package io.hoogland.guildtools.models;

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

    @Column(unique = true, nullable = false)
//    @CsvBindByPosition(position = 0, required = true)
    @CsvBindByName(column = "player", required = true)
    private String player;

    @Column(nullable = false)
//    @CsvBindByPosition(position = 1, required = true)
    @CsvBindByName(column = "class", required = true)
    private String clazz;

    @Column(nullable = false)
//    @CsvBindByPosition(position = 2)
    @CsvBindByName(column = "DKP", required = true)
    private long dkp;

    @Column(nullable = false)
//    @CsvBindByPosition(position = 3)
    @CsvBindByName(column = "previousDKP", required = true)
    private long previous;

    @Column(nullable = false)
    @CsvIgnore
    private long dkpChange;

    @Column(nullable = false)
//    @CsvBindByPosition(position = 4)
    @CsvBindByName(column = "lifetimeGained", required = true)
    private long lifetimeGained;

    @Column(nullable = false)
//    @CsvBindByPosition(position = 5)
    @CsvBindByName(column = "lifetimeSpent", required = true)
    private long lifetimeSpent;

    public DKPStanding() {
    }

}
