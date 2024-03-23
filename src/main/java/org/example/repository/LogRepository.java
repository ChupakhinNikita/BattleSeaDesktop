package org.example.repository;

import org.example.entity.Log;
import org.springframework.data.jpa.repository.JpaRepository;


public interface LogRepository extends JpaRepository<Log, Integer> {
}
