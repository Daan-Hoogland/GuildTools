package io.hoogland.guildtools.models;

import lombok.Data;
import lombok.Getter;

import javax.persistence.*;

@Data
public class Character {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name, userId;

    @Column(name = "class")
    private String clazz;

    @OneToOne
    @JoinColumn(name = "dkp_standing_id")
    private DKPStanding dkpStanding;

}
