package com.example.Eksamensprojekt3sem.Siren;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SirenRepository extends JpaRepository<SirenModel, Integer> {
}
