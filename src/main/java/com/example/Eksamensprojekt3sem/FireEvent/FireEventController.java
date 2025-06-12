package com.example.Eksamensprojekt3sem.FireEvent;

import com.example.Eksamensprojekt3sem.Siren.SirenModel;
import com.example.Eksamensprojekt3sem.Siren.SirenService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/fire-events")
public class FireEventController {

    private final FireEventService fireEventService;
    private final SirenService sirenService;

    public FireEventController(FireEventService fireEventService,  SirenService sirenService) {
        this.fireEventService = fireEventService;
        this.sirenService = sirenService;
    }

    @GetMapping
    public ResponseEntity<List<FireEventModel>> findAll() {
        List<FireEventModel> all = fireEventService.findAll();
        // 200 OK + liste (tom liste er fint)
        return ResponseEntity.ok(all);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable int id) {
        try {
            FireEventModel found = fireEventService.findById(id);
            // 200 OK + fireEvent
            return ResponseEntity.ok(found);
        } catch (IllegalArgumentException e) {
            // 404 hvis ikke fundet
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Fire event not found with id = " + id);
        }
    }

    @PostMapping
    public ResponseEntity<?> createFireEvent(@RequestBody FireEventModel fireEventModel) {
        try {
            // Afvis hvis klienten allerede angiver en ID
            if (fireEventModel.getFireEventId() != 0) {
                return ResponseEntity
                        .badRequest()
                        .body("Cannot specify fireEventId for create; it is auto-generated");
            }

            // Sæt timestamp, hvis ikke angivet
            if (fireEventModel.getTimestamp() == null) {
                fireEventModel.setTimestamp(LocalDateTime.now());
            }

            // Gem fire event
            FireEventModel saved = fireEventService.save(fireEventModel);

            // Byg Location-URI: /api/fires/{id}
            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest()               // fx http://localhost:8080/api/fires
                    .path("/{id}")                // tilføj "/{id}"
                    .buildAndExpand(saved.getFireEventId()) // sæt det nye id ind
                    .toUri();

            // Returner 201 Created + Location + body
            return ResponseEntity
                    .created(location)
                    .body(saved);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .badRequest()
                    .body("Error creating fire event: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateFireEvent(
            @PathVariable int id,
            @RequestBody FireEventModel requestBody
    ) {
        try {
            FireEventModel existing = fireEventService.findById(id);

            // Opdater simple felter
            existing.setLatitude(requestBody.getLatitude());
            existing.setLongitude(requestBody.getLongitude());
            existing.setTimestamp(requestBody.getTimestamp());
            existing.setClosed(requestBody.isClosed());

            // Hvis klienten sender siren-IDs, så opdater relationen:
            if (requestBody.getSirens() != null) {
                // Hent den Set, der allerede er knyttet til eventet
                Set<SirenModel> sirenRelation = existing.getSirens();
                // Tøm den
                sirenRelation.clear();
                // Fyld den op igen med DB-entiteter
                for (SirenModel s : requestBody.getSirens()) {
                    SirenModel dbSiren = sirenService.findById(s.getSirenId());
                    sirenRelation.add(dbSiren);
                }
            }

            FireEventModel saved = fireEventService.save(existing);
            return ResponseEntity.ok(saved);

        } catch (IllegalArgumentException iae) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(iae.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .badRequest()
                    .body("Error updating fire event: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFireEvent(@PathVariable int id) {
        try {
            fireEventService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException iae) {
            // fx hvis service kaster ved ikke-fundet
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(iae.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .badRequest()
                    .body("Error deleting fire event: " + e.getMessage());
        }
    }

}
