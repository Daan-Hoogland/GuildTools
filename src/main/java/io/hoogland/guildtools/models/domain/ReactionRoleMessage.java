package io.hoogland.guildtools.models.domain;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "reaction_role_message", uniqueConstraints = {@UniqueConstraint(columnNames = {"guildId", "messageId", "channelId"})})
public class ReactionRoleMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long guildId, messageId, channelId;
    private String directLink;

    @OneToMany(mappedBy = "reactionRoleMessage", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<ReactionRole> roles;

    public ReactionRoleMessage() {
    }

    public ReactionRoleMessage(long guildId) {
        this.guildId = guildId;
        this.roles = new ArrayList<>();
    }
}
