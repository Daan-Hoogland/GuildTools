package io.hoogland.guildtools.models.domain;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "reaction_role", uniqueConstraints = {@UniqueConstraint(columnNames = {"roleId", "emojiId", "reaction_role_message_id"})})
public class ReactionRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "reaction_role_message_id")
    private ReactionRoleMessage reactionRoleMessage;

    private Long roleId;
    private String emojiId;
    private int type;
    private Long creatorId;

    public ReactionRole() {
    }

    public ReactionRole(long guildId) {
        if (null == this.reactionRoleMessage)
            this.reactionRoleMessage = new ReactionRoleMessage(guildId);
    }

    public String getEmojiIdSanitized() {
        return this.emojiId.startsWith("<") && this.emojiId.endsWith(">") ? this.emojiId
                .substring(this.emojiId.indexOf(":", this.emojiId.indexOf(":")), this.emojiId.length() - 1) : this.emojiId;
    }

}
