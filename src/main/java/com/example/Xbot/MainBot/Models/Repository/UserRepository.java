package com.example.Xbot.MainBot.Models.Repository;

import com.example.Xbot.MainBot.Models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    User findById(Long id);
}
