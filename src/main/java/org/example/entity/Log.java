package org.example.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "Log")
@Getter @Setter
public class Log {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "first_player_name", nullable = false)
    private String first_player_name;

    @Column(name = "second_player_name", nullable = false)
    private String second_player_name;

    @Column(name = "winner_name", nullable = false)
    private String winner_name;

    @Column(name = "port_game", nullable = false)
    private String port_game;

    @Column(name = "data", nullable = false)
    private String data;

    @Column(name = "time_start_game", nullable = false)
    private String time_start_game;

    @Column(name = "time_end_game", nullable = false)
    private String time_end_game;

    @Column(name = "duration_game", nullable = false)
    private String duration_game;

}