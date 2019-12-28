package io.hoogland.guildtools.utils;

import io.hoogland.guildtools.constants.EmojiConstants;
import io.hoogland.guildtools.models.DKPImport;
import io.hoogland.guildtools.models.DKPStanding;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.springframework.data.domain.Page;
import org.springframework.util.StringUtils;

import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Slf4j
public class DKPUtils {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

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

    public static MessageEmbed getDKPAllEmbed(String messageTitle, Optional<DKPImport> dkpImport, Page<DKPStanding> guildStanding, int page) {
        EmbedBuilder embed = new EmbedBuilder();

        embed.setTitle(messageTitle);
        embed.setThumbnail("https://i.imgur.com/5gzgA0B.png");
        if (guildStanding.getTotalPages() > 1) {
            embed.setFooter("Page " + (page + 1) + "/" + guildStanding.getTotalPages() +
                    "\nTo see the next page, react with the arrow emojis. | Last updated: " + dkpImport.get().getCreatedDate().format(formatter), "https://i.imgur.com/pZf0MvC.png");
        } else if (dkpImport.isPresent()) {
            embed.setFooter("Last updated: " + dkpImport.get().getCreatedDate().format(formatter), "https://i.imgur.com/pZf0MvC.png");
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
