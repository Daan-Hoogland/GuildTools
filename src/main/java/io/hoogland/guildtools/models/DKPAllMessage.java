package io.hoogland.guildtools.models;

import lombok.Data;
import net.dv8tion.jda.api.entities.Message;

import javax.persistence.*;

@Data
@Entity
@Table(name = "dkp_all_messages")
public class DKPAllMessage extends AuditedEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private long guildId;
    private long channelId;
    private long messageId;
    private int page;

    public DKPAllMessage() {
    }

    public DKPAllMessage(Message message, int page) {
        this.guildId = message.getGuild().getIdLong();
        this.channelId = message.getChannel().getIdLong();
        this.messageId = message.getIdLong();
        this.page = page;
    }
}
