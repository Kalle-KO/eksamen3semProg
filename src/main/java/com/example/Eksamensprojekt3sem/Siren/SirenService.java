// src/main/java/com/example/Eksamensprojekt3sem/Siren/SirenService.java
package com.example.Eksamensprojekt3sem.Siren;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SirenService {

    private static final double EARTH_RADIUS_KM = 6371.0;
    private final SirenRepository sirenRepository;

    public SirenService(SirenRepository sirenRepository) {
        this.sirenRepository = sirenRepository;
    }

    /**
     * Beregn afstanden i kilometer mellem to punkter (lat1,lon1) og (lat2,lon2) med haversine-formlen.
     */
    public double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        // 1) Konverter forskellen i grader til radianer
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        // 2) Omregn start- og slut-latitude til radianer
        double radLat1 = Math.toRadians(lat1);
        double radLat2 = Math.toRadians(lat2);

        // 3) Anvend haversine-formlen
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(radLat1) * Math.cos(radLat2)
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        // 4) Den “store” vinkel mellem punkterne
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        // 5) Afstand = jordens radius * vinkel i radianer
        return EARTH_RADIUS_KM * c;
    }

    public List<SirenModel> findAll() {
        return sirenRepository.findAll();
    }

    public SirenModel findById(int id) {
        return sirenRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Siren id " + id + " not found"));
    }

    public SirenModel save(SirenModel sirenModel) {
        return sirenRepository.save(sirenModel);
    }

    public void deleteById(int id) {
        sirenRepository.deleteById(id);
    }
}
