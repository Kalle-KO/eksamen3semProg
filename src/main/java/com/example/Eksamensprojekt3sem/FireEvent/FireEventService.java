package com.example.Eksamensprojekt3sem.FireEvent;

import com.example.Eksamensprojekt3sem.Enum.Status;
import com.example.Eksamensprojekt3sem.Siren.SirenModel;
import com.example.Eksamensprojekt3sem.Siren.SirenRepository;
import com.example.Eksamensprojekt3sem.Siren.SirenService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
public class FireEventService {

    private final FireEventRepository fireEventRepository;
    private final SirenRepository sirenRepository;
    private final SirenService sirenService;

    private static final double ACTIVATION_RADIUS_KM = 10.0;

    public FireEventService(FireEventRepository fireEventRepository,  SirenRepository sirenRepository,  SirenService sirenService) {
        this.fireEventRepository = fireEventRepository;
        this.sirenRepository = sirenRepository;
        this.sirenService = sirenService;
    }

    public List<FireEventModel> findAll() {
        return fireEventRepository.findAll();
    }

    public FireEventModel findById(int id) {
        return fireEventRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("FireEvent not found: " + id));
    }

    public FireEventModel save(FireEventModel fireEventModel) {
        return fireEventRepository.save(fireEventModel);
    }

    public FireEventModel closeEvent(int id) {
        FireEventModel event = findById(id);

        // 1) Luk event
        event.setClosed(true);

        // 2) Gå alle sirener på eventet igennem og sæt dem tilbage til FRED
        Set<SirenModel> sirens = event.getSirens();
        if (sirens != null) {
            for (SirenModel s : sirens) {
                s.setStatus(Status.FRED);
            }
            // Gem opdaterede sirener
            sirenRepository.saveAll(sirens);
        }

        // 3) Gem eventet (så closed‐flaget bliver ved med at stå)
        return fireEventRepository.save(event);
    }

    public void deleteById(int id) {
        fireEventRepository.deleteById(id);
    }

    /**
     * Opretter en nyt FireEvent, finder sirener inden for 10 km og aktiverer dem.
     */
    public FireEventModel registerEvent(double lat, double lon) {
        // 1) Opret event og gem — nu er det managed og får et ID
        FireEventModel event = new FireEventModel();
        event.setLatitude(lat);
        event.setLongitude(lon);
        event.setTimestamp(LocalDateTime.now());
        event.setClosed(false);
        event = fireEventRepository.save(event);

        // 2) Hent alle sirener og tjek afstand
        List<SirenModel> allSirens = sirenRepository.findAll();
        for (SirenModel s : allSirens) {
            if (s.isDisabled()) continue;

            double dist = sirenService.calculateDistance(
                    lat, lon,
                    s.getLatitude(), s.getLongitude()
            );

            if (dist <= ACTIVATION_RADIUS_KM) {
                s.setStatus(Status.FARLIG);
                event.getSirens().add(s);
            }
        }

        // 3) Gem opdaterede sirener (status) og join‐tabel entry
        sirenRepository.saveAll(event.getSirens());
        return fireEventRepository.save(event);
    }
}
