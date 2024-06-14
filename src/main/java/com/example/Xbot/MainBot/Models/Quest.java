package com.example.Xbot.MainBot.Models;

import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "quest")
public class Quest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @JoinColumn(name = "client_id")
    @ManyToOne
    private User owner;

    @Column
    private String ownerName;

    @Column
    private String quest;
}
