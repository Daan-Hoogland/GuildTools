package io.hoogland.guildtools.models;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "dkp_import")
public class DKPImport extends AuditedEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private long uploader;

    private long guildId;

    @Lob
    @Column(name = "imported_text")
    private String importedText;
}
