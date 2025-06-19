package com.example.Eksamensprojekt3sem.Config;

import com.example.Eksamensprojekt3sem.Enum.Status;
import com.example.Eksamensprojekt3sem.Siren.SirenModel;
import com.example.Eksamensprojekt3sem.Siren.SirenRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class DataInitializer { // Backup - Data ligger nu dynamisk i MySQL database

    /**
     * Initiates different sirens for a basic starter-pack of sirens
     * Uses the different variables in SirenModel
     * @param sirenRepo
     * @return
     */
    @Bean
    public CommandLineRunner initSirens(SirenRepository sirenRepo) {
        return args -> {
            // Undgå dobbeltindsættelse
            if (sirenRepo.count() > 0) {
                return;
            }

            List<SirenModel> sirens = List.of(

            // Opret sirener via helper-metoden
            createSiren(34.0074, -118.4973, Status.NEUTRAL, false),
            createSiren(34.0094, -118.4953, Status.EMERGENCY, false),
            createSiren(34.0094, -118.4993, Status.NEUTRAL, false),
            createSiren(34.0114, -118.4953, Status.EMERGENCY, false),
            createSiren(34.0114, -118.4993, Status.NEUTRAL, false),
            createSiren(34.0074, -118.4953, Status.NEUTRAL, false),
            createSiren(34.0074, -118.4993, Status.EMERGENCY, false),
            createSiren(34.0094, -118.4933, Status.NEUTRAL, false),
            createSiren(34.0094, -118.5013, Status.NEUTRAL, false)

            );
            sirenRepo.saveAll(sirens);
            System.out.println("Seeded sirens: " + sirens.size());
        };
    }

    /**
     * Creates a siren object
     * @param latitude
     * @param longitude
     * @param status
     * @param disabled
     * @return
     */
    private SirenModel createSiren(double latitude, double longitude, Status status, boolean disabled) {
        SirenModel siren = new SirenModel();
        siren.setLatitude(latitude);
        siren.setLongitude(longitude);
        siren.setStatus(status);
        siren.setDisabled(disabled);
        return siren;
    }
}