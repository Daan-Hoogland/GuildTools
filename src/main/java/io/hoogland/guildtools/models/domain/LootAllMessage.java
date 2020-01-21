package io.hoogland.guildtools.models.domain;

import lombok.Data;
import net.dv8tion.jda.api.entities.Message;

import javax.persistence.*;

@Data
@Entity
@Table(name = "loot_all_message")
public class LootAllMessage extends AuditedEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private long guildId;
    private long channelId;
    private long messageId;
    private String type;
    private int page;

    public LootAllMessage() {
    }

    public LootAllMessage(Message message, String type, int page) {
        this.guildId = message.getGuild().getIdLong();
        this.channelId = message.getChannel().getIdLong();
        this.messageId = message.getIdLong();
        this.type = type;
        this.page = page;
    }
}
