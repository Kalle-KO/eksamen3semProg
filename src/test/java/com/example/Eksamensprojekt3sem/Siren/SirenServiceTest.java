package com.example.Eksamensprojekt3sem.Siren;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class SirenServiceTest {

    private SirenService sirenService;

    @BeforeEach
    void setUp() {
        sirenService = new SirenService(null);
    }

    @Test
    void distanceBetweenVeniceBeachAndSantaMonicaShouldBeLessThan1km() {
        // Venice Beach
        double lat1 = 34.0094;
        double lon1 = -118.4973;
        // Santa Monica Pier
        double lat2 = 34.0090;
        double lon2 = -118.4969;

        double distance = sirenService.calculateDistance(lat1, lon1, lat2, lon2);

        // Vi ved, at de ligger meget tæt – derfor < 1 km
        assertTrue(distance < 1.0,
                () -> "Forventet afstand < 1 km, men var: " + distance + " km");
    }

    @Test
    void distanceBetweenSamePointShouldBeZero() {
        double lat = 34.0094;
        double lon = -118.4973;

        double distance = sirenService.calculateDistance(lat, lon, lat, lon);

        assertTrue(distance < 1e-6,
                () -> "Forventet afstand ≈ 0 km, men var: " + distance + " km");
    }
}
