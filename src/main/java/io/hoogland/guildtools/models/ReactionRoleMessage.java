package io.hoogland.guildtools.models;

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
    private long id;
    private String guildId, messageId, directLink, channelId;

    @OneToMany(mappedBy = "reactionRoleMessage", fetch = FetchType.EAGER)
    private List<ReactionRole> roles;

    public ReactionRoleMessage() {
    }

    public ReactionRoleMessage(String guildId) {
        this.guildId = guildId;
        this.roles = new ArrayList<>();
    }

    public String getChannelIdSanitized() {
        return this.channelId.substring(2, this.channelId.length() - 1);
    }
}
