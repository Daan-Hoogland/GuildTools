package io.hoogland.guildtools.utils;

import io.hoogland.guildtools.constants.Constants;
import io.hoogland.guildtools.constants.DKPConstants;
import io.hoogland.guildtools.constants.EmojiConstants;
import io.hoogland.guildtools.models.domain.DKPStanding;
import io.hoogland.guildtools.models.domain.LootImport;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.springframework.data.domain.Page;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Slf4j
public class DKPUtils {

    public static List<DKPStanding> getDuplicates(final List<DKPStanding> dkpStandings) {
        return getDuplicatesMap(dkpStandings).values().stream()
                .filter(duplicates -> duplicates.size() > 1)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private static Map<String, List<DKPStanding>> getDuplicatesMap(List<DKPStanding> dkpStandings) {
        return dkpStandings.stream().collect(groupingBy(DKPStanding::getPlayer));
    }

    public static String getDkpChangeEmoji(long dkpChange) {
        return (Long.signum(dkpChange) < 0) ? EmojiConstants.EMOJI_DOWN : EmojiConstants.EMOJI_UP;
    }

    public static List<MessageEmbed.Field> getStandingFields(DKPStanding standing) {
        List<MessageEmbed.Field> fields = new ArrayList<>();

        fields.add(new MessageEmbed.Field(DKPConstants.DKP_CURRENT_TITLE,
                String.format(DKPConstants.DKP_CURRENT_VALUE, standing.getDkp(), getDkpChangeEmoji(standing.getDkpChange()), standing.getDkpChange()),
                false));
        fields.add(new MessageEmbed.Field(DKPConstants.DKP_PREVIOUS, String.valueOf(standing.getPrevious()), false));
        fields.add(new MessageEmbed.Field(DKPConstants.DKP_LIFETIME_GAINED, String.valueOf(standing.getLifetimeGained()), true));
        fields.add(new MessageEmbed.Field(DKPConstants.DKP_LIFETIME_SPENT, String.valueOf(standing.getLifetimeSpent()), true));

        return fields;
    }

    public static List<MessageEmbed.Field> getStandingFields(Page<DKPStanding> guildStanding, int page) {
        List<MessageEmbed.Field> fields = new ArrayList<>();
        StringBuilder nameRanking = new StringBuilder();
        StringBuilder dkp = new StringBuilder();
        int ranking = page * 20 + 1;
        for (DKPStanding standing : guildStanding) {
            nameRanking.append(ranking).append(". ").append(StringUtils.capitalize(standing.getPlayer().toLowerCase()))
                    .append("<:empty:660151903753076751>").append("\n");
            dkp.append(standing.getDkp()).append("\t(").append(DKPUtils.getDkpChangeEmoji(standing.getDkpChange())).append(standing.getDkpChange())
                    .append(")")
                    .append("\n");
            ranking = ranking + 1;
        }
        fields.add(new MessageEmbed.Field("Player", nameRanking.toString(), true));
        fields.add(new MessageEmbed.Field("DKP (change)", dkp.toString(), true));

        return fields;
    }

    public static MessageEmbed createDkpEmbed(DKPStanding standing) {
        HashMap classInfo = (HashMap) ((HashMap) ConfigUtils.getConfig().get("icons")).get(standing.getClazz().toLowerCase());
        return EmbedUtils
                .createEmbed(DKPConstants.DKP_EMBED_TITLE, String.format(DKPConstants.DKP_EMBED_DESCRIPTION, StringUtils
                                .capitalize(standing.getPlayer().toLowerCase())), DKPUtils.getStandingFields(standing),
                        classInfo.get("color").toString(), String.format(DKPConstants.DKP_FOOTER, standing.getModifiedDate().format(
                                Constants.DATE_TIME_FORMATTER)), null, classInfo.get("icon").toString());
    }

    public static String getFooter(Optional<LootImport> dkpImport, Page<DKPStanding> guildStanding, int page) {
        if (guildStanding.getTotalPages() > 1) {
            return "Page " + (page + 1) + "/" + guildStanding.getTotalPages() +
                    "\nTo see the next page, react with the arrow emojis. | Last updated: " +
                    dkpImport.get().getCreatedDate().format(Constants.DATE_TIME_FORMATTER);
        } else if (dkpImport.isPresent()) {
            return "Last updated: " + dkpImport.get().getCreatedDate().format(Constants.DATE_TIME_FORMATTER);
        } else {
            return "Unknown update time";
        }
    }

    public static MessageEmbed getDKPAllEmbed(String messageTitle, Optional<LootImport> dkpImport, Page<DKPStanding> guildStanding, int page) {
        EmbedBuilder embed = new EmbedBuilder();

        embed.setTitle(messageTitle);
        embed.setThumbnail("https://i.imgur.com/5gzgA0B.png");
        if (guildStanding.getTotalPages() > 1) {
            embed.setFooter("Page " + (page + 1) + "/" + guildStanding.getTotalPages() +
                            "\nTo see the next page, react with the arrow emojis. | Last updated: " +
                            dkpImport.get().getCreatedDate().format(Constants.DATE_TIME_FORMATTER),
                    "https://i.imgur.com/pZf0MvC.png");
        } else if (dkpImport.isPresent()) {
            embed.setFooter("Last updated: " + dkpImport.get().getCreatedDate().format(Constants.DATE_TIME_FORMATTER),
                    "https://i.imgur.com/pZf0MvC.png");
        } else {
            log.debug("no dkpimport found?");
        }

        StringBuilder nameRanking = new StringBuilder();
        StringBuilder dkp = new StringBuilder();
        int ranking = page * 20 + 1;
        for (DKPStanding standing : guildStanding) {
            nameRanking.append(ranking).append(". ").append(StringUtils.capitalize(standing.getPlayer().toLowerCase()))
                    .append("<:empty:660151903753076751>").append("\n");
            dkp.append(standing.getDkp()).append("\t(").append(DKPUtils.getDkpChangeEmoji(standing.getDkpChange())).append(standing.getDkpChange())
                    .append(")")
                    .append("\n");
            ranking = ranking + 1;
        }
        embed.addField("Player", nameRanking.toString(), true);
        embed.addField("DKP (change)", dkp.toString(), true);

        return embed.build();
    }
}
