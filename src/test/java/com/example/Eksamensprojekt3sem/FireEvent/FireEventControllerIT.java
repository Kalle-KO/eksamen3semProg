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

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FireEventControllerIT {

    @Autowired
    private TestRestTemplate rest;

    @Autowired
    private SirenRepository sirenRepository;

    @Test
    void registerAndCloseFlow() {
        // Seed en sirene præcis på lat=10, lon=10
        SirenModel s = sirenRepository.save(new SirenModel(10, 10, Status.FRED, false));

        // Register event
        ResponseEntity<FireEventModel> reg =
                rest.postForEntity("/api/fire-events/register?latitude=10&longitude=10",
                        null, FireEventModel.class); // Body null - alle parametre i query string
        assertEquals(HttpStatus.CREATED, reg.getStatusCode()); // 201
        int evtId = reg.getBody().getFireEventId();

        // Siren skal nu være FARLIG
        SirenModel reloaded = rest.getForObject("/api/sirens/{id}", SirenModel.class, s.getSirenId()); // hent sirene på id
        assertEquals(Status.FARLIG, reloaded.getStatus()); // kontroller at status nu er farlig

        // Luk event
        ResponseEntity<FireEventModel> closeRes =
                rest.postForEntity("/api/fire-events/{id}/close", null,
                        FireEventModel.class, evtId); // vi sender POST - brand bør være slukket nu
        assertEquals(HttpStatus.OK, closeRes.getStatusCode()); // 200

        // Siren skal være FRED igen
        SirenModel after = rest.getForObject("/api/sirens/{id}", SirenModel.class, s.getSirenId()); // brand er slukket - sirene tilbage til fred
        assertEquals(Status.FRED, after.getStatus());
    }
}
