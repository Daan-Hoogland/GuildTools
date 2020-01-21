package io.hoogland.guildtools.models.domain;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "character", uniqueConstraints = {@UniqueConstraint(columnNames = {"name", "guildId"})})
public class Character extends AuditedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
    @Column(name = "class")
    private String clazz;
    private long userId;
    private long guildId;

    public Character() {
    }

    public Character(String name, long userId, long guildId) {
        this.name = name;
        this.userId = userId;
        this.guildId = guildId;
    }
}
