package com.example.Xbot.MainBot;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@Getter
@Setter
public final class Resources {

    private List<String> times = Arrays.asList("21:00", "21:30", "22:00", "22:30", "23:00", "23:30", "00:00"
    , "00:30", "01:00", "01:30", "02:00", "02:30", "03:00", "03:30","04:00", "04:30", "05:00");

    private List<Integer> tables = Arrays.asList(1,2,3,4,5,6,7,8,9,11,12,13);
}
