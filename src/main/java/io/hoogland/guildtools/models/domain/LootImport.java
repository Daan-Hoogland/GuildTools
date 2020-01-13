package io.hoogland.guildtools.models.domain;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "loot_import")
public class LootImport extends AuditedEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private long uploader;

    private long guildId;

    @Lob
    @Column(name = "imported_text")
    private String importedText;

    private String type;
}
