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
        return WarcraftLogsConstants.BASE_URL +
                String.format(WarcraftLogsConstants.API_CALENDAR, this.region.name(), this.realm,
                        getUrlEncoded(this.guild));
    }

    public String getCalendarUrl(String guildName) {
        return WarcraftLogsConstants.BASE_URL +
                String.format(WarcraftLogsConstants.API_CALENDAR, this.region.name(), this.realm,
                        getUrlEncoded(guildName));
    }

    public String getReportsUrl(String apiKey) {
        return WarcraftLogsConstants.BASE_URL +
                String.format(WarcraftLogsConstants.API_REPORTS, this.guild, this.realm,
                        this.region.name()) + "?api_key=" + apiKey;
    }

    public String getReportsUrl(String apiKey, String guildName) {
        return WarcraftLogsConstants.BASE_URL +
                String.format(WarcraftLogsConstants.API_REPORTS, guildName, this.realm, this.region.name()) +
                "?api_key=" + apiKey;
    }

    private String getUrlEncoded(String string) {
        return string.replace(" ", "%20");
    }

}
