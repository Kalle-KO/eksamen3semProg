package com.example.Eksamensprojekt3sem.Siren;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/sirens")
public class SirenController {

    private final SirenService sirenService;

    public SirenController(SirenService sirenService) {
        this.sirenService = sirenService;
    }

    @GetMapping
    public ResponseEntity<List<SirenModel>> findAll() {
        List<SirenModel> all = sirenService.findAll();
        // 200 OK + liste (tom liste er fint)
        return ResponseEntity.ok(all);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable int id) {
        try {
            SirenModel found = sirenService.findById(id);
            // 200 OK + siren
            return ResponseEntity.ok(found);
        } catch (IllegalArgumentException e) {
            // 404 hvis ikke fundet
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Siren not found with id = " + id);
        }
    }

    @PostMapping
    public ResponseEntity<?> createSiren(@RequestBody SirenModel sirenModel) {
        try {
            // 1) Afvis hvis klienten allerede angiver en ID
            if (sirenModel.getSirenId() != 0) {
                return ResponseEntity
                        .badRequest()
                        .body("Cannot specify sirenId for create; it is auto-generated");
            }

            // 2) Gem sirenen
            SirenModel saved = sirenService.save(sirenModel);

            // 3) Byg Location-URI: /api/sirens/{id}
            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest()               // fx http://localhost:8080/api/sirens
                    .path("/{id}")                // tilføj "/{id}"
                    .buildAndExpand(saved.getSirenId()) // sæt det nye id ind
                    .toUri();

            // 4) Returner 201 Created + Location + body
            return ResponseEntity
                    .created(location)
                    .body(saved);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .badRequest()
                    .body("Error creating siren: " + e.getMessage());
        }

    }


    @PutMapping("/{id}")
    public ResponseEntity<?> updateSiren(
            @PathVariable int id,
            @RequestBody SirenModel requestBody
    ) {
        try {
            // Denne kaster IllegalArgumentException, hvis id ikke findes
            SirenModel existing = sirenService.findById(id);

            existing.setLatitude(requestBody.getLatitude());
            existing.setLongitude(requestBody.getLongitude());
            existing.setStatus(requestBody.getStatus());
            existing.setDisabled(requestBody.isDisabled());

            SirenModel saved = sirenService.save(existing);
            return ResponseEntity.ok(saved);

        } catch (IllegalArgumentException iae) {
            // 404 hvis ikke fundet
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(iae.getMessage());
        } catch (Exception e) {
            // anden fejl
            return ResponseEntity.badRequest()
                    .body("Error updating siren: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSiren(@PathVariable int id) {
        try {
        sirenService.deleteById(id);
        return ResponseEntity.noContent().build();
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}
