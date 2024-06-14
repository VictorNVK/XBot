package com.example.Xbot.AdminBot.model.entity;

import com.example.Xbot.AdminBot.model.adminEnum.AdminState;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Table(name = "moderators")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer adminId;

    @Column(unique = true)
    private Long id;

    @Column(unique = true)
    private String username;

    @Enumerated
    @Column
    private AdminState adminState;

}
