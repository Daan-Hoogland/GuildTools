package io.hoogland.guildtools.models.domain;

import io.hoogland.guildtools.models.converters.StringListConverter;
import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Table(name = "role_application", uniqueConstraints = {@UniqueConstraint(columnNames = {"userId", "guildId"})})
public class RoleApplication extends AuditedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private long userId;

    @Convert(converter = StringListConverter.class)
    private List<String> roles;

    private long messageId;
    private long guildId;
}
