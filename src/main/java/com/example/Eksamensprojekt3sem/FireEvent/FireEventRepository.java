package com.example.Eksamensprojekt3sem.FireEvent;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FireEventRepository extends JpaRepository<FireEventModel, Integer> {
}
