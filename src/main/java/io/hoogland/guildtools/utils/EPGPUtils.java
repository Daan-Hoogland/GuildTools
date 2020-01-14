package io.hoogland.guildtools.utils;

import io.hoogland.guildtools.constants.Constants;
import io.hoogland.guildtools.models.domain.EPGPStanding;
import io.hoogland.guildtools.models.domain.LootImport;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.springframework.data.domain.Page;
import org.springframework.util.StringUtils;

import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Slf4j
public class EPGPUtils {

    public static List<EPGPStanding> getDuplicates(final List<EPGPStanding> dkpStandings) {
        return getDuplicatesMap(dkpStandings).values().stream()
                .filter(duplicates -> duplicates.size() > 1)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private static Map<String, List<EPGPStanding>> getDuplicatesMap(List<EPGPStanding> epgpStandings) {
        return epgpStandings.stream().collect(groupingBy(EPGPStanding::getPlayer));
    }

    public static List<MessageEmbed.Field> getStandingFields(Page<EPGPStanding> guildStanding, int page) {
        List<MessageEmbed.Field> fields = new ArrayList<>();
        StringBuilder nameRanking = new StringBuilder();
        StringBuilder rank = new StringBuilder();
        StringBuilder pr = new StringBuilder();
        int ranking = page * 20 + 1;
        for (EPGPStanding standing : guildStanding) {
            nameRanking.append(ranking).append(". ").append(StringUtils.capitalize(standing.getPlayer().toLowerCase()))
                    .append("<:empty:660151903753076751>").append("\n");
            rank.append(standing.getGuildRank()).append("<:empty:660151903753076751>")
                    .append("\n");
            pr.append(standing.getPr()).append("\t(").append(EmojiUtils.getChangeEmoji(standing.getPr().subtract(standing.getPreviousPr())))
                    .append(standing.getPr().subtract(standing.getPreviousPr()))
                    .append(")")
                    .append("\n");
            ranking = ranking + 1;
        }
        fields.add(new MessageEmbed.Field("Player", nameRanking.toString(), true));
        fields.add(new MessageEmbed.Field("Rank", rank.toString(), true));
        fields.add(new MessageEmbed.Field("PR (change)", pr.toString(), true));

        return fields;
    }

    public static MessageEmbed getAllEmbed(String messageTitle, Optional<LootImport> epgpImport, Page<EPGPStanding> guildStanding, int page) {
        EmbedBuilder embed = new EmbedBuilder();

        embed.setTitle(messageTitle);
        embed.setThumbnail("https://i.imgur.com/XBRSzXh.png");
        if (guildStanding.getTotalPages() > 1) {
            embed.setFooter("Page " + (page + 1) + "/" + guildStanding.getTotalPages() +
                            "\nTo see the next page, react with the arrow emojis. | Last updated: " + epgpImport.get().getCreatedDate().format(
                    Constants.DATE_TIME_FORMATTER),
                    "https://i.imgur.com/pZf0MvC.png");
        } else if (epgpImport.isPresent()) {
            embed.setFooter("Last updated: " + epgpImport.get().getCreatedDate().format(Constants.DATE_TIME_FORMATTER),
                    "https://i.imgur.com/pZf0MvC.png");
        } else {
            log.debug("no dkpimport found?");
        }

        StringBuilder nameRanking = new StringBuilder();
        StringBuilder rank = new StringBuilder();
        StringBuilder pr = new StringBuilder();
        int ranking = page * 20 + 1;
        for (EPGPStanding standing : guildStanding) {
            nameRanking.append(ranking).append(". ").append(StringUtils.capitalize(standing.getPlayer().toLowerCase()))
                    .append("<:empty:660151903753076751>").append("\n");
            rank.append(standing.getGuildRank()).append("<:empty:660151903753076751>")
                    .append("\n");
            pr.append(standing.getPr()).append("\t(")
                    .append(EmojiUtils.getChangeEmoji(standing.getPr().subtract(standing.getPreviousPr()).setScale(2, RoundingMode.HALF_UP)))
                    .append(standing.getPr().subtract(standing.getPreviousPr()).setScale(2, RoundingMode.HALF_UP))
                    .append(")")
                    .append("\n");
            ranking = ranking + 1;
        }
        embed.addField("Player", nameRanking.toString(), true);
        embed.addField("Rank", rank.toString(), true);
        embed.addField("PR (change)", pr.toString(), true);

        return embed.build();
    }
}
