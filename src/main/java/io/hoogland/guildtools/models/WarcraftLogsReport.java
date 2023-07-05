package io.hoogland.guildtools.models;

import io.hoogland.guildtools.constants.WarcraftLogsConstants;
import lombok.Data;

@Data
public class WarcraftLogsReport {
    private String id;
    private String title;
    private String owner;
    private long start;
    private long end;
    private int zone;

    public String getUrl() {
        return WarcraftLogsConstants.BASE_URL + String.format(WarcraftLogsConstants.WARCRAFTLOGS_REPORT, this.id);
    }
}
