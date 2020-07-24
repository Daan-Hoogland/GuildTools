package io.hoogland.guildtools.commands.logs;

import io.hoogland.guildtools.App;
import io.hoogland.guildtools.constants.Constants;
import io.hoogland.guildtools.constants.WarcraftLogsConstants;
import io.hoogland.guildtools.models.Metric;
import io.hoogland.guildtools.models.Region;
import io.hoogland.guildtools.models.WarcraftLogsRanking;
import io.hoogland.guildtools.models.domain.GuildSettings;
import io.hoogland.guildtools.models.repositories.GuildSettingsRepository;
import io.hoogland.guildtools.services.RestService;
import io.hoogland.guildtools.utils.*;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;

import java.util.*;

@Slf4j
public class PLogsListener extends ListenerAdapter {

    private GuildSettingsRepository guildSettingsRepository = BeanUtils.getBean(GuildSettingsRepository.class);
    private RestService restService = BeanUtils.getBean(RestService.class);
    private String token;

    public PLogsListener(String token) {
        this.token = token;
    }

    public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent event) {
        if (!event.getMember().getUser().isBot()) {
            if (event.getReactionEmote().isEmote() &&
                    (EmojiUtils.emoteToDiscordEmoji(event.getReactionEmote().getEmote()).equalsIgnoreCase(WarcraftLogsConstants.ONYXIA_EMOJI) ||
                            EmojiUtils.emoteToDiscordEmoji(event.getReactionEmote().getEmote()).equalsIgnoreCase(WarcraftLogsConstants.MOLTEN_CORE_EMOJI) ||
                            EmojiUtils.emoteToDiscordEmoji(event.getReactionEmote().getEmote()).equalsIgnoreCase(WarcraftLogsConstants.BLACKWING_LAIR_EMOJI) ||
                    EmojiUtils.emoteToDiscordEmoji(event.getReactionEmote().getEmote()).equalsIgnoreCase(WarcraftLogsConstants.ZUL_GURUB_EMOJI) ||
                    EmojiUtils.emoteToDiscordEmoji(event.getReactionEmote().getEmote()).equalsIgnoreCase(WarcraftLogsConstants.AHN_QIRAJ_20_EMOJI) ||
                    EmojiUtils.emoteToDiscordEmoji(event.getReactionEmote().getEmote()).equalsIgnoreCase(WarcraftLogsConstants.AHN_QIRAJ_40_EMOJI))) {
                event.getChannel().retrieveMessageById(event.getMessageIdLong()).queue(
                        message -> {
                            if (message.getAuthor().getIdLong() == App.jda.getSelfUser().getIdLong()) {
                                MessageEmbed embed = message.getEmbeds().get(0);
                                if (embed.getTitle().contains("rankings for ")) {
                                    String metric = embed.getFooter().getText().substring(embed.getFooter().getText().lastIndexOf(": ") + 2);
                                    String characterName = embed.getTitle().replace(metric + " rankings for ", "");

                                    int oldZone = Integer.parseInt(
                                            embed.getFooter().getText().substring(embed.getFooter().getText().indexOf("Zone: "))
                                                    .replaceAll("[^0-9]", ""));
                                    int zone;
                                    switch (event.getReactionEmote().getEmote().getName()) {
                                        case "mc":
                                            zone = 1000;
                                            break;
                                        case "onyxia":
                                            zone = 1001;
                                            break;
                                        case "bwl":
                                            zone = 1002;
                                            break;
                                        case "zg":
                                            zone = 1003;
                                            break;
                                        case "aq20":
                                            zone = 1004;
                                            break;
                                        case "aq40":
                                            zone = 1005;
                                            break;
                                        case "naxx":
                                            zone = 1006;
                                            break;
                                        default:
                                            zone = 1000;
                                            break;
                                    }

                                    Optional<GuildSettings> optionalSettings = guildSettingsRepository.findByGuildId(event.getGuild().getIdLong());
                                    if (optionalSettings.isPresent()) {
                                        ResponseEntity<WarcraftLogsRanking[]> response = restService.getRestTemplate()
                                                .getForEntity(
                                                        WarcraftLogsUtils.buildRankingsUrl(characterName,
                                                                optionalSettings.get().getWarcraftLogSettings().getRealm(), Region.EU,
                                                                Metric.valueOf(metric), zone, token), WarcraftLogsRanking[].class);

                                        Map<String, List<WarcraftLogsRanking>> rankingMap = new HashMap<>();
                                        for (WarcraftLogsRanking ranking : response.getBody()) {
                                            if (rankingMap.containsKey(ranking.getSpec())) {
                                                rankingMap.get(ranking.getSpec()).add(ranking);
                                            } else {
                                                List<WarcraftLogsRanking> tempList = new ArrayList<>();
                                                tempList.add(ranking);
                                                rankingMap.put(ranking.getSpec(), tempList);
                                            }
                                        }

                                        List<WarcraftLogsRanking> mainRankings = null;
                                        String role = null;
                                        int maxLen = 0;
                                        for (Map.Entry<String, List<WarcraftLogsRanking>> e : rankingMap.entrySet()) {
                                            int len = e.getValue().size();

                                            if (role == null || len > maxLen) {
                                                role = e.getKey();
                                                mainRankings = e.getValue();
                                                maxLen = len;
                                            }
                                        }

                                        HashMap<Integer, HashMap<String, String>> zoneMap = (HashMap<Integer, HashMap<String, String>>) ConfigUtils
                                                .getConfig().get("zones");

                                        MessageEmbed editedMsg = EmbedUtils
                                                .createEmbed(metric + " rankings for " + StringUtils.capitalize(characterName.toLowerCase()),
                                                        String.format(Constants.LINK, "Click here to visit the rankings page",
                                                                WarcraftLogsConstants.BASE_URL +
                                                                        String.format(WarcraftLogsConstants.WARCRAFTLOGS_RANKINGS, Region.EU,
                                                                                optionalSettings.get().getWarcraftLogSettings().getRealm(),
                                                                                characterName)), WarcraftLogsUtils.getFieldsForRankings(mainRankings),
                                                        null, "Zone: " + zone + " | Metric: " + metric, null, zoneMap.get(zone).get("image"));
                                        message.editMessage(editedMsg).queue(
                                                success -> {
                                                    success.addReaction(EmojiUtils.discordEmojiToUnicode(WarcraftLogsUtils.getEmojiForZone(oldZone)))
                                                            .queue();
                                                    success.removeReaction(event.getReactionEmote().getEmote()).queue();
                                                    success.removeReaction(event.getReactionEmote().getEmote(), event.getUser()).queue();
                                                }
                                        );
                                    }
                                }
                            }
                        });
            }
        }
    }
}
