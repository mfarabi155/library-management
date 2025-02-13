package com.example.library.repositories;

import com.example.library.models.Variable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface VariableRepository extends JpaRepository<Variable, Long> {
    Optional<Variable> findByName(String name);
}
