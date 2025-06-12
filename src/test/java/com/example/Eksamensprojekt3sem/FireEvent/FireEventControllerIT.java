package com.example.Eksamensprojekt3sem.FireEvent;

import com.example.Eksamensprojekt3sem.Enum.Status;
import com.example.Eksamensprojekt3sem.Siren.SirenModel;
import com.example.Eksamensprojekt3sem.Siren.SirenRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FireEventControllerIT {

    @Autowired
    private TestRestTemplate rest;

    @Autowired
    private SirenRepository sirenRepository;

    @Test
    void registerAndCloseFlow() {
        // Seed en sirene
        SirenModel s = sirenRepository.save(new SirenModel(10, 10, Status.NEUTRAL, false));

        // 1) Register event, men få body som Map i stedet for FireEventModel
        ResponseEntity<Map> reg =
                rest.postForEntity("/api/fire-events/register?latitude=10&longitude=10",
                        null, Map.class);
        assertEquals(HttpStatus.CREATED, reg.getStatusCode());

        // Hent evtId ud af Map’en
        Integer evtId = (Integer) reg.getBody().get("fireEventId");

        // 2) Tjek sirenestatus
        SirenModel reloaded = rest
                .getForObject("/api/sirens/{id}", SirenModel.class, s.getSirenId());
        assertEquals(Status.EMERGENCY, reloaded.getStatus());

        // 3) Luk event – igen som Map
        ResponseEntity<Map> closeRes =
                rest.postForEntity("/api/fire-events/{id}/close", null, Map.class, evtId);
        assertEquals(HttpStatus.OK, closeRes.getStatusCode());

        // 4) Siren skal tilbage til NEUTRAL
        SirenModel after = rest
                .getForObject("/api/sirens/{id}", SirenModel.class, s.getSirenId());
        assertEquals(Status.NEUTRAL, after.getStatus());
    }
}
