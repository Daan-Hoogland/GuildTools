package io.hoogland.guildtools.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.hoogland.guildtools.constants.WarcraftLogsConstants;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class WarcraftLogsRanking implements Serializable {
    @JsonProperty(value = "class")
    private String clazz;
    private String encounterName;
    private String spec;
    @JsonProperty(value = "reportID")
    private String reportId;
    private int percentile;
    private int ilvlKeyOrPatch;

    public String getFormattedPercentile() {
        if (0 <= this.percentile && this.percentile < 25) {
            return String.format(WarcraftLogsConstants.PARSE_COMMON, this.percentile);
        } else if (25 <= this.percentile && this.percentile < 50) {
            return String.format(WarcraftLogsConstants.PARSE_UNCOMMON, this.percentile);
        } else if (50 <= this.percentile && this.percentile < 75) {
            return String.format(WarcraftLogsConstants.PARSE_RARE, this.percentile);
        } else if (75 <= this.percentile && this.percentile < 95) {
            return String.format(WarcraftLogsConstants.PARSE_EPIC, this.percentile);
        } else if (95 <= this.percentile && this.percentile < 99) {
            return String.format(WarcraftLogsConstants.PARSE_LEGENDARY, this.percentile);
        } else if (99 <= this.percentile && this.percentile <= 100) {
            return String.format(WarcraftLogsConstants.PARSE_TOP, this.percentile);
        }
        return String.format(WarcraftLogsConstants.PARSE_COMMON, this.percentile);
    }
}
