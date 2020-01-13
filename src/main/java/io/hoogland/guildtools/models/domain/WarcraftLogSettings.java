package io.hoogland.guildtools.models.domain;

import io.hoogland.guildtools.constants.WarcraftLogsConstants;
import io.hoogland.guildtools.models.Region;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "warcraftlogs_settings")
public class WarcraftLogSettings implements Serializable {

    @Id
    private long id;

    private String guild;

    private String realm;

    @OneToOne
    @MapsId
    private GuildSettings guildSettings;

    @Enumerated(EnumType.STRING)
    private Region region = Region.EU;

    public String getCalendarUrl() {
        return WarcraftLogsConstants.WARCRAFTLOGS_API_URL +
                String.format(WarcraftLogsConstants.WARCRAFTLOGS_API_CALENDAR, this.region.name(), this.realm, this.guild.replace(" ", "%20"));
    }

    public String getCalendarUrl(String guildName) {
        return WarcraftLogsConstants.WARCRAFTLOGS_API_URL +
                String.format(WarcraftLogsConstants.WARCRAFTLOGS_API_CALENDAR, this.region.name(), this.realm, guildName.replace(" ", "%20"));
    }

    public String getReportsUrl(String apiKey) {
        return WarcraftLogsConstants.WARCRAFTLOGS_API_URL +
                String.format(WarcraftLogsConstants.WARCRAFTLOGS_API_REPORTS, this.guild, this.realm, this.region.name()) + "?api_key=" + apiKey;
    }

    public String getReportsUrl(String apiKey, String guildName) {
        return WarcraftLogsConstants.WARCRAFTLOGS_API_URL +
                String.format(WarcraftLogsConstants.WARCRAFTLOGS_API_REPORTS, guildName, this.realm, this.region.name()) + "?api_key=" + apiKey;
    }

}
