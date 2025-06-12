package com.example.Eksamensprojekt3sem.Config;

import com.example.Eksamensprojekt3sem.Enum.Status;
import com.example.Eksamensprojekt3sem.Siren.SirenModel;
import com.example.Eksamensprojekt3sem.Siren.SirenRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initSirens(SirenRepository sirenRepo) {
        return args -> {
            // Undgå dobbeltindsættelse
            if (sirenRepo.count() > 0) {
                return;
            }

            List<SirenModel> sirens = new ArrayList<>();

            // Opret sirener via helper-metoden
            sirens.add(createSiren(34.0074, -118.4973, Status.FRED, false));
            sirens.add(createSiren(34.0094, -118.4953, Status.FARLIG, false));
            sirens.add(createSiren(34.0094, -118.4993, Status.FRED, false));
            sirens.add(createSiren(34.0114, -118.4953, Status.FARLIG, false));
            sirens.add(createSiren(34.0114, -118.4993, Status.FRED, false));
            sirens.add(createSiren(34.0074, -118.4953, Status.FRED, false));
            sirens.add(createSiren(34.0074, -118.4993, Status.FARLIG, false));
            sirens.add(createSiren(34.0094, -118.4933, Status.FRED, false));
            sirens.add(createSiren(34.0094, -118.5013, Status.FRED, false));

            sirenRepo.saveAll(sirens);
            System.out.println("Seeded sirens: " + sirens.size());
        };
    }

    private SirenModel createSiren(double latitude, double longitude, Status status, boolean disabled) {
        SirenModel siren = new SirenModel();
        siren.setLatitude(latitude);
        siren.setLongitude(longitude);
        siren.setStatus(status);
        siren.setDisabled(disabled);
        return siren;
    }
}