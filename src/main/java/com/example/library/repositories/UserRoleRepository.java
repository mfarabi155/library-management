package com.example.library.repositories;

import com.example.library.models.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;


@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {

}

