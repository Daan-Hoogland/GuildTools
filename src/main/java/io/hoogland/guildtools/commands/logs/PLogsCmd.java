package io.hoogland.guildtools.commands.logs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import io.hoogland.guildtools.App;
import io.hoogland.guildtools.constants.Constants;
import io.hoogland.guildtools.constants.EmojiConstants;
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
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
public class PLogsCmd extends Command {

    private GuildSettingsRepository guildSettingsRepository = BeanUtils.getBean(GuildSettingsRepository.class);
    private RestService restService = BeanUtils.getBean(RestService.class);
    private String token;

    public PLogsCmd(String token) {
        this.name = "plogs";
        this.aliases = new String[]{"plog"};
        this.help = "links the rankings page of a player.";
        this.token = token;
    }

    @Override
    protected void execute(CommandEvent event) {
        if (!event.getArgs().isBlank()) {
            String[] cmdArgs = event.getArgs().split(" ");
            if (cmdArgs.length < 2) {
                MessageEmbed errorEmbed = EmbedUtils.createErrorEmbed("Invalid command",
                        "Missing required argument.\n\nExample usage: `" + App.client.getPrefix() + "plogs [dps/hps] [character]`",
                        "Missing argument", null);
                event.getMessage().delete().queue();
                event.getChannel().sendMessage(errorEmbed).queue();
                log.debug("too short");
            } else {
                Metric metric = Metric.DPS;
                try {
                    metric = Metric.valueOf(cmdArgs[0].toUpperCase());
                } catch (IllegalArgumentException e) {
                    MessageEmbed invalidMetricEmbed = EmbedUtils
                            .createErrorEmbed("Invalid command", "Invalid metric. Choose either DPS or HPS.", "Invalid metric (DPS/HPS)", null);
                    event.getMessage().delete().queue();
                    event.getChannel().sendMessage(invalidMetricEmbed).queue();
                    return;
                }
                String characterName = cmdArgs[1];

                Optional<GuildSettings> optionalSettings = guildSettingsRepository.findByGuildId(event.getGuild().getIdLong());
                if (optionalSettings.isPresent()) {
                    if (optionalSettings.get().getWarcraftLogSettings() != null &&
                            optionalSettings.get().getWarcraftLogSettings().getRealm() != null) {
                        MessageEmbed msg = EmbedUtils.createEmbed(metric + " rankings for " + StringUtils.capitalize(characterName.toLowerCase()),
                                String.format(Constants.LINK, "Click here to visit the rankings page",
                                        String.format(WarcraftLogsConstants.WARCRAFTLOGS_RANKINGS,
                                                optionalSettings.get().getWarcraftLogSettings().getRegion(),
                                                optionalSettings.get().getWarcraftLogSettings().getRealm(), characterName)) + "\n\n" +
                                        EmojiConstants.EMOJI_LOADING, null, null, null, null, WarcraftLogsConstants.ICON_LINK);
                        Metric finalMetric = metric;
                        event.getChannel().sendMessage(msg).queue(
                                sendMsg -> {
                                    try {

                                        ResponseEntity<WarcraftLogsRanking[]> response = restService.getRestTemplate()
                                                .getForEntity(
                                                        WarcraftLogsUtils.buildRankingsUrl(characterName,
                                                                optionalSettings.get().getWarcraftLogSettings().getRealm(), Region.EU,
                                                                finalMetric, token), WarcraftLogsRanking[].class);

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

                                        int zone;
                                        if(mainRankings == null) {
                                            zone = 0;
                                        } else {
                                            zone = WarcraftLogsUtils.getZoneForBoss(mainRankings.get(0).getEncounterName());
                                        }

                                        MessageEmbed editedMsg = EmbedUtils
                                                .createEmbed(
                                                        finalMetric.name() + " rankings for " + StringUtils.capitalize(characterName.toLowerCase()),
                                                        String.format(Constants.LINK, "Click here to visit the rankings page",
                                                                WarcraftLogsConstants.BASE_URL +
                                                                        String.format(WarcraftLogsConstants.WARCRAFTLOGS_RANKINGS, Region.EU,
                                                                                optionalSettings.get().getWarcraftLogSettings().getRealm(),
                                                                                characterName)), WarcraftLogsUtils.getFieldsForRankings(mainRankings),
                                                        null,
                                                        "Zone: " + zone + " | Metric: " + finalMetric, null,
                                                        (zone == 0 ? WarcraftLogsConstants.ICON_LINK : zoneMap.get(zone).get("image")));
                                        sendMsg.editMessage(editedMsg).queue(
                                                success -> {
                                                    if(zone != 0) {
                                                        WarcraftLogsUtils.getEmojisForZone(zone).forEach(emoji -> {
                                                            success.addReaction(EmojiUtils.discordEmojiToUnicode(emoji)).queue();
                                                        });
                                                    }
                                                }
                                        );
                                    } catch (HttpClientErrorException exception) {
                                        ObjectMapper mapper = new ObjectMapper();
                                        try {
                                            JsonNode actualObj = mapper
                                                    .readTree(exception.getMessage()
                                                            .substring(exception.getMessage().indexOf("{"), exception.getMessage().indexOf("}") + 1));

                                            MessageEmbed successEmbed = EmbedUtils.createEmbed(
                                                    finalMetric.name() + " rankings for " + StringUtils.capitalize(characterName.toLowerCase()),
                                                    "Error while searching for rankings.\n\n⚠ " + exception.getRawStatusCode() + " - " +
                                                            actualObj.findValue("error").asText(), null, Constants.COLOR_NOT_OK, null, null,
                                                    "https://dmszsuqyoe6y6.cloudfront.net/img/warcraft/favicon.png");
                                            sendMsg.editMessage(successEmbed).queue();
                                        } catch (JsonProcessingException e) {
                                            log.error("Error parsing JSON response", e);
                                        }
                                    }
                                });
                    } else {
                        MessageEmbed error = EmbedUtils
                                .createEmbed("Invalid WarcraftLogs settings", "The logs command must first be configured by an admin using `" +
                                                App.client.getPrefix() + "setup logs`\n\n⚠ WarcraftLogs command not configured", null, Constants.COLOR_NOT_OK,
                                        null, null, WarcraftLogsConstants.ICON_LINK);
                        event.getChannel().sendMessage(error).queue(
                                success -> {
                                    success.delete().queueAfter(20, TimeUnit.SECONDS);
                                }
                        );
                    }
                } else {
                    MessageEmbed invalid = EmbedUtils.createErrorEmbed("Invalid settings", null, "No settings found for this Discord server.", "");
                    event.getChannel().sendMessage(invalid).queue(
                            success -> {
                                success.delete().queueAfter(20, TimeUnit.SECONDS);
                            }
                    );
                }
                event.getMessage().delete().queue();
            }
        } else {
            MessageEmbed errorEmbed = EmbedUtils.createErrorEmbed("Invalid command",
                    "Missing required argument.\n\nExample usage: `" + App.client.getPrefix() + "plogs [dps/hps] [character]`",
                    "Missing argument", null);
            event.getMessage().delete().queue();
            event.getChannel().sendMessage(errorEmbed).queue();
        }
    }
}
