package io.hoogland.guildtools.models;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class GuildSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private Long guildId;

    private Long adminRoleId;

    private Long applicationChannelId;
}
