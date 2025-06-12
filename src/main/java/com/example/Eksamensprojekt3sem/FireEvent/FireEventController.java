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

@CrossOrigin(origins = "*")
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
        return ResponseEntity.ok(fireEventService.findAll());
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
    public ResponseEntity<?> createFireEvent(@RequestBody FireEventModel fireEventModel) {
        if (fireEventModel.getFireEventId() != 0)
            return ResponseEntity.badRequest().body("Cannot specify fireEventId for create; it is auto-generated");

        if (fireEventModel.getTimestamp() == null)
            fireEventModel.setTimestamp(LocalDateTime.now());

        FireEventModel saved = fireEventService.save(fireEventModel);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(saved.getFireEventId())
                .toUri();
        return ResponseEntity.created(location).body(saved);
    }

    @PostMapping("/register")
    public ResponseEntity<FireEventModel> registerFireEvent(
            @RequestParam("latitude") double latitude,
            @RequestParam("longitude") double longitude
    ) {
        FireEventModel evt = fireEventService.registerEvent(latitude, longitude);
        return ResponseEntity.status(HttpStatus.CREATED).body(evt);
    }

    // **Her er det, du mangler:**
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

            if (requestBody.getSirens() != null) {
                Set<com.example.Eksamensprojekt3sem.Siren.SirenModel> rel = existing.getSirens();
                rel.clear();
                for (var s : requestBody.getSirens()) {
                    rel.add(sirenService.findById(s.getSirenId()));
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
