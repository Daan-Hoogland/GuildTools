package io.hoogland.guildtools.utils;

import io.hoogland.guildtools.constants.WarcraftLogsConstants;
import io.hoogland.guildtools.models.Metric;
import io.hoogland.guildtools.models.Region;
import io.hoogland.guildtools.models.WarcraftLogsRanking;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.ArrayList;
import java.util.List;

public class WarcraftLogsUtils {

    public static int getZoneForBoss(String bossName) {
        if (WarcraftLogsConstants.ONYXIA.contains(bossName)) {
            return 1001;
        } else if (WarcraftLogsConstants.MOLTEN_CORE.contains(bossName)) {
            return 1000;
        } else if (WarcraftLogsConstants.BLACKWING_LAIR.contains(bossName)) {
            return 1002;
        } else if (WarcraftLogsConstants.ZUL_GURUB.contains(bossName)) {
            return 1003;
        } else if (WarcraftLogsConstants.AHN_QIRAJ_20.contains(bossName)) {
            return 1004;
        } else if (WarcraftLogsConstants.AHN_QIRAJ.contains(bossName)) {
            return 1005;
        } else if (WarcraftLogsConstants.NAXXRAMAS.contains(bossName)) {
            return 1006;
        } else {
            return 1000;
        }
    }

    public static List<String> getEmojisForZone(int zone) {
        List<String> emojiList = new ArrayList<>();

        if (zone != 1000) {
            emojiList.add(WarcraftLogsConstants.MOLTEN_CORE_EMOJI);
        }
        if (zone != 1001) {
            emojiList.add(WarcraftLogsConstants.ONYXIA_EMOJI);
        }
        if (zone != 1002) {
            emojiList.add(WarcraftLogsConstants.BLACKWING_LAIR_EMOJI);
        }
        if (zone != 1003) {
            emojiList.add(WarcraftLogsConstants.ZUL_GURUB_EMOJI);
        }
        if (zone != 1004) {
            emojiList.add(WarcraftLogsConstants.AHN_QIRAJ_20_EMOJI);
        }
        if (zone != 1005) {
            emojiList.add(WarcraftLogsConstants.AHN_QIRAJ_40_EMOJI);
        }
        return emojiList;
    }

    public static String getEmojiForZone(int zone) {
        switch (zone) {
            case 1000:
                return WarcraftLogsConstants.MOLTEN_CORE_EMOJI;
            case 1001:
                return WarcraftLogsConstants.ONYXIA_EMOJI;
            case 1002:
                return WarcraftLogsConstants.BLACKWING_LAIR_EMOJI;
            case 1003:
                return WarcraftLogsConstants.ZUL_GURUB_EMOJI;
            case 1004:
                return WarcraftLogsConstants.AHN_QIRAJ_20_EMOJI;
            case 1005:
                return WarcraftLogsConstants.AHN_QIRAJ_40_EMOJI;
            case 1006:
                return WarcraftLogsConstants.MOLTEN_CORE_EMOJI;
            default:
                return WarcraftLogsConstants.MOLTEN_CORE_EMOJI;
        }
    }

    public static String buildRankingsUrl(String playerName, String realm, Region region, Metric metric, String token) {
        return WarcraftLogsConstants.BASE_URL + String.format(WarcraftLogsConstants.API_RANKINGS, playerName, realm, region.name()) + "?api_key=" +
                token + "&metric=" + metric.name().toLowerCase() + "&timeframe=historical";
    }

    public static String buildRankingsUrl(String playerName, String realm, Region region, Metric metric, int zone, String token) {
        return buildRankingsUrl(playerName, realm, region, metric, token) + "&zone=" + zone;
    }

    public static List<MessageEmbed.Field> getFieldsForRankings(List<WarcraftLogsRanking> rankings) {
        List<MessageEmbed.Field> fields = new ArrayList<>();

        StringBuilder bossList = new StringBuilder();
        StringBuilder ilvlList = new StringBuilder();
        StringBuilder percentileList = new StringBuilder();

        if (rankings != null && !rankings.isEmpty()) {
            rankings.forEach(ranking -> {
                bossList.append(ranking.getEncounterName() + "\n");
                ilvlList.append(ranking.getIlvlKeyOrPatch() + "\n");
                percentileList.append(ranking.getFormattedPercentile() + "\n");
            });
        } else {
            bossList.append("_No kills found_");
        }

        fields.add(new MessageEmbed.Field("Boss", bossList.toString(), true));
        fields.add(new MessageEmbed.Field("ilvl", ilvlList.toString(), true));
        fields.add(new MessageEmbed.Field("Percentile", percentileList.toString(), true));

        return fields;
    }
}
