package com.example.Xbot.MainBot.Models;

import com.example.Xbot.MainBot.Models.Enum.UserState;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Table(name = "clients")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @Column
    private Long id;

    @Column
    private String username;

    @Enumerated
    @Column
    private UserState userState;


}
