package com.example.Xbot.AdminBot.model.repository;

import com.example.Xbot.AdminBot.model.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Integer> {
    Boolean existsAdminByUsername(String username);
    Admin findAdminById(Long id);
    Admin findAdminByUsername(String username);
    List<Admin> findAdminsByUsername(String username);
}
