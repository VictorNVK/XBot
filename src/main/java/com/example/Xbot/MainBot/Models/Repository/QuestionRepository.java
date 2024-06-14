package com.example.Xbot.MainBot.Models.Repository;

import com.example.Xbot.MainBot.Models.Quest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionRepository extends JpaRepository<Quest, Integer> {
    Quest findQuestById(Integer id);
}
