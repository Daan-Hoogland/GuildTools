package io.hoogland.guildtools.models.domain;

import io.hoogland.guildtools.constants.Constants;
import io.hoogland.guildtools.utils.EmbedUtils;
import lombok.Data;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "guild_settings")
public class GuildSettings implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private Long guildId;

    private Long adminRoleId;

    private Long applicationChannelId;

    private boolean dkp;
    private boolean epgp;

    @OneToOne(mappedBy = "guildSettings", cascade = CascadeType.ALL)
    private WarcraftLogSettings warcraftLogSettings;

    public void setDkp(boolean dkp) {
        this.epgp = !dkp;
        this.dkp = dkp;
    }

    public void setEpgp(boolean epgp) {
        this.epgp = epgp;
        this.dkp = !epgp;
    }

    public MessageEmbed getMessageEmbed() {
        List<MessageEmbed.Field> fields = new ArrayList<>();

        fields.add(new MessageEmbed.Field("Officer role",
                this.adminRoleId != null ? String.format(Constants.MENTION_ROLE, this.adminRoleId) : "*Not set*", false));
        fields.add(new MessageEmbed.Field("Application channel", this.applicationChannelId != null ? String
                .format(Constants.MENTION_CHANNEL, this.applicationChannelId) : "*Not set*",
                false));
        String loot = "";
        if (this.epgp) {
            loot = "EPGP";
        } else if (this.dkp) {
            loot = "DKP";
        } else {
            loot = "*Not set*";
        }
        fields.add(new MessageEmbed.Field("Loot system", loot,
                false));

        if(warcraftLogSettings != null) {
            fields.add(new MessageEmbed.Field("WarcraftLogs settings", "**Guild**: " + warcraftLogSettings.getGuild() + "\n" +
                    "**Realm**: " + StringUtils.capitalize(warcraftLogSettings.getRealm().toLowerCase()) + "\n" +
                    "**Region**: " + warcraftLogSettings.getRegion().name(), false));
        } else {
            fields.add(new MessageEmbed.Field("WarcraftLogs settings", "*Not configured*", false));
        }

        return EmbedUtils.createEmbed("Guild settings", null, fields);
    }
}
