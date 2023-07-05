package io.hoogland.guildtools.models.domain;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvIgnore;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "dkp_standing", uniqueConstraints = {@UniqueConstraint(columnNames = {"guildId", "player"})})
public class DKPStanding extends AuditedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @CsvIgnore
    private Long id;

    @CsvIgnore
    private long guildId;

    @Column(nullable = false)
    @CsvBindByName(column = "player", required = true)
    private String player;

    @Column(nullable = false)
    @CsvBindByName(column = "class", required = true)
    private String clazz;

    @Column(nullable = false)
    @CsvBindByName(column = "DKP", required = true)
    private long dkp;

    @Column(nullable = false)
    @CsvBindByName(column = "previousDKP", required = true)
    private long previous;

    @Column(nullable = false)
    @CsvIgnore
    private long dkpChange;

    @Column(nullable = false)
    @CsvBindByName(column = "lifetimeGained", required = true)
    private long lifetimeGained;

    @Column(nullable = false)
    @CsvBindByName(column = "lifetimeSpent", required = true)
    private long lifetimeSpent;

    public DKPStanding() {
    }

}
