package com.example.Eksamensprojekt3sem.Siren;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SirenServiceTest {

    private SirenService sirenService;

    @BeforeEach
    void setUp() {
        sirenService = new SirenService(null);
    }

    @Test
    void distanceBetweenVeniceBeachAndSantaMonicaShouldBeLessThan1km() {
        double lat1 = 34.0094, lon1 = -118.4973; // Venice Beach
        double lat2 = 34.0090, lon2 = -118.4969; // Santa Monica Pier

        double distance = sirenService.calculateDistance(lat1, lon1, lat2, lon2);

        assertTrue(distance < 1.0,
                () -> "Forventet afstand < 1 km, men var: " + distance + " km");
    }

    @Test
    void distanceBetweenSamePointShouldBeZero() {
        double lat = 34.0094, lon = -118.4973;

        double distance = sirenService.calculateDistance(lat, lon, lat, lon);

        assertTrue(distance < 1e-6,
                () -> "Forventet afstand ≈ 0 km, men var: " + distance + " km");
    }

    @Test
    void distanceBetweenOppositePointsIsHalfEarthCircumference() {
        // Punkt A (0°,0°) ↔ Punkt B (0°,180°)
        double distance = sirenService.calculateDistance(0, 0, 0, 180);
        assertEquals(Math.PI * 6371, distance, 1.0,
                () -> "Forventet ≈" + (Math.PI * 6371) + " men var: " + distance);
    }

    @Test
    void distanceBetweenCopenhagenAndAarhusShouldBeAbout157km() {
        double cphLat = 55.6761, cphLon = 12.5683;   // København
        double aarLat = 56.1629, aarLon = 10.2039;   // Aarhus

        double distance = sirenService.calculateDistance(cphLat, cphLon, aarLat, aarLon);

        // Assertér at afstanden ligger tæt på 157 km (±2 km tolerance)
        assertEquals(157, distance, 2.0,
                () -> "Forventet ≈157 km, men var: " + distance + " km");
    }
}
