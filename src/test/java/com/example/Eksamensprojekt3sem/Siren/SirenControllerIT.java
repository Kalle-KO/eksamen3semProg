package com.example.Eksamensprojekt3sem.Siren;

import com.example.Eksamensprojekt3sem.Enum.Status;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Integrationstest:
 * Opretter Sirene (post),
 * Henter liste og finder id,
 * opdaterer via put,
 * sletter via delete
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SirenControllerIT {

    @Autowired
    private TestRestTemplate rest;

    @Test
    void fullCrudFlow() {
        // Create
        SirenModel toCreate = new SirenModel();
        toCreate.setLatitude(55.0);
        toCreate.setLongitude(12.0);
        toCreate.setStatus(Status.NEUTRAL);
        toCreate.setDisabled(false);

        ResponseEntity<SirenModel> postRes =
                rest.postForEntity("/api/sirens", toCreate, SirenModel.class); // HTTP-POST kald med objekt der skal gemmes
        assertEquals(HttpStatus.CREATED, postRes.getStatusCode()); // 201
        int id = postRes.getBody().getSirenId();

        // Read
        ResponseEntity<SirenModel[]> listRes =
                rest.getForEntity("/api/sirens", SirenModel[].class); // HTTP-GET kald, 200 ok og array m. sirener
        assertTrue(Arrays.stream(listRes.getBody()) // AssertTrue - fejler, hvis ikke id er med
                .anyMatch(s -> s.getSirenId() == id)); // konverterer sirene-array til stream og kontrollere id.

        // Update
        SirenModel updated = postRes.getBody();
        updated.setDisabled(true);
        rest.put("/api/sirens/{id}", updated, id);

        SirenModel fetched = rest.getForObject("/api/sirens/{id}", SirenModel.class, id);
        assertTrue(fetched.isDisabled());

        // Delete
        rest.delete("/api/sirens/{id}", id);
        ResponseEntity<String> afterDel = rest.getForEntity("/api/sirens/{id}", String.class, id);
        assertEquals(HttpStatus.NOT_FOUND, afterDel.getStatusCode());
    }
}
