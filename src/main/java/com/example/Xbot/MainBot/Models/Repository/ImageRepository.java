package com.example.Xbot.MainBot.Models.Repository;

import com.example.Xbot.MainBot.Models.Enum.MyDayOfWeek;
import com.example.Xbot.MainBot.Models.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageRepository extends JpaRepository<Image, Integer> {
    Image findImageById(Integer id);
}
