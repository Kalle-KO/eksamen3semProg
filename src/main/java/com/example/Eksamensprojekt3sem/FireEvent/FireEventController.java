package com.example.Eksamensprojekt3sem.FireEvent;

import com.example.Eksamensprojekt3sem.Siren.SirenService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@CrossOrigin(origins = "*") // Bruges ikke mere - kun til at lave python server
@RestController
@RequestMapping("/api/fire-events")
public class FireEventController {

    private final FireEventService fireEventService;
    private final SirenService sirenService;

    public FireEventController(FireEventService fireEventService, SirenService sirenService) {
        this.fireEventService = fireEventService;
        this.sirenService = sirenService;
    }

    @GetMapping
    public ResponseEntity<List<FireEventModel>> findAll() {
        return ResponseEntity.ok(fireEventService.findAll()); // ResponseEntity.ok returnerer både data og statuskode (200 ok)
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable int id) {
        try {
            return ResponseEntity.ok(fireEventService.findById(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Fire event not found with id = " + id);
        }
    }

    @PostMapping
    public ResponseEntity<?> createFireEvent(@RequestBody FireEventModel fireEventModel) { // Bør egentlig være FireEventModel i stedet for "?"
        if (fireEventModel.getFireEventId() != 0)
            return ResponseEntity.badRequest().body("Cannot specify fireEventId for create; it is auto-generated");

        if (fireEventModel.getTimestamp() == null)
            fireEventModel.setTimestamp(LocalDateTime.now());

        FireEventModel saved = fireEventService.save(fireEventModel);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest() // REST best practice. Location-header vises i POSTMAN
                .path("/{id}")
                .buildAndExpand(saved.getFireEventId())
                .toUri(); // Tager aktuelle request-URL, tilføjer dynamisk del (id), indsætter id'et i URL'en, konverterer hele stien til et URI-objekt
        return ResponseEntity.created(location).body(saved); // gemmer stien og det nye objekt
    }

    @PostMapping("/register")
    public ResponseEntity<FireEventModel> registerFireEvent(
            @RequestParam("latitude") double latitude,
            @RequestParam("longitude") double longitude // Bruges til bare at hente to koordinater: POST /api/fire-events/register?latitude=55.6761&longitude=12.5683
    ) {
        FireEventModel evt = fireEventService.registerEvent(latitude, longitude);
        return ResponseEntity.status(HttpStatus.CREATED).body(evt);
    }

    @PostMapping("/{id}/close")
    public ResponseEntity<FireEventModel> closeFireEvent(@PathVariable int id) {
        FireEventModel closed = fireEventService.closeEvent(id);
        return ResponseEntity.ok(closed);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateFireEvent(
            @PathVariable int id,
            @RequestBody FireEventModel requestBody
    ) {
        try {
            FireEventModel existing = fireEventService.findById(id);
            existing.setLatitude(requestBody.getLatitude());
            existing.setLongitude(requestBody.getLongitude());
            existing.setTimestamp(requestBody.getTimestamp());
            existing.setClosed(requestBody.isClosed());

            if (requestBody.getSirens() != null) { // Hvis FireEvent er tilknyttet sirener så...
                Set<com.example.Eksamensprojekt3sem.Siren.SirenModel> rel = existing.getSirens(); // Henter nuværende sirener
                rel.clear(); // Sletter gamle liste af sirener - altså relationen
                for (var s : requestBody.getSirens()) { // Looper igennem de sirener der blev sendt med requesten, altså de nye sirener.
                    rel.add(sirenService.findById(s.getSirenId())); // Henter nye sirener
                }
            }

            return ResponseEntity.ok(fireEventService.save(existing));
        } catch (IllegalArgumentException iae) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(iae.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFireEvent(@PathVariable int id) {
        try {
            fireEventService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException iae) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(iae.getMessage());
        }
    }
}
