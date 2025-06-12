package com.example.Eksamensprojekt3sem.FireEvent;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FireEventService {

    private final FireEventRepository fireEventRepository;

    public FireEventService(FireEventRepository fireEventRepository) {
        this.fireEventRepository = fireEventRepository;
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
        event.setClosed(true);
        return fireEventRepository.save(event);
    }
}
