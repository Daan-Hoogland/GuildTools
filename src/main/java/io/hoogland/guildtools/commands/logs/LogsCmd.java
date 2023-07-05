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
import io.hoogland.guildtools.models.WarcraftLogsReport;
import io.hoogland.guildtools.models.domain.GuildSettings;
import io.hoogland.guildtools.models.domain.WarcraftLogSettings;
import io.hoogland.guildtools.models.repositories.GuildSettingsRepository;
import io.hoogland.guildtools.services.RestService;
import io.hoogland.guildtools.utils.BeanUtils;
import io.hoogland.guildtools.utils.ConfigUtils;
import io.hoogland.guildtools.utils.EmbedUtils;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
public class LogsCmd extends Command {

    private GuildSettingsRepository guildSettingsRepository = BeanUtils.getBean(GuildSettingsRepository.class);
    private RestService restService = BeanUtils.getBean(RestService.class);
    private String token;

    public LogsCmd(String token) {
        this.name = "logs";
        this.aliases = new String[]{"log", "warcraftlogs"};
        this.help = "shows the latest logs from WarcraftLogs.";
        this.token = token;
    }

    @Override
    protected void execute(CommandEvent event) {
        Optional<GuildSettings> optionalSettings = guildSettingsRepository.findByGuildId(event.getGuild().getIdLong());
        if (optionalSettings.isPresent()) {
            WarcraftLogSettings settings = optionalSettings.get().getWarcraftLogSettings();
            if (settings != null) {
                event.getMessage().delete().queue();

                String guildName;
                if (event.getArgs().isEmpty()) {
                    guildName = settings.getGuild();
                } else {
                    guildName = event.getArgs();
                }

                MessageEmbed msg = EmbedUtils.createEmbed("<" + guildName + "> logs",
                        String.format(Constants.LINK, "Click here to visit the calendar", settings.getCalendarUrl(guildName)) + "\n\n" +
                                EmojiConstants.EMOJI_LOADING, null, null, null, null, WarcraftLogsConstants.ICON_LINK);
                event.getChannel().sendMessage(msg).queue(
                        sendMsg -> {
                            try {
                                ResponseEntity<WarcraftLogsReport[]> response = restService.getRestTemplate()
                                        .getForEntity(settings.getReportsUrl(token, guildName), WarcraftLogsReport[].class);

                                List<MessageEmbed.Field> fields = new ArrayList<>();

                                HashMap<Integer, HashMap<String, String>> zoneMap = (HashMap<Integer, HashMap<String, String>>) ConfigUtils.getConfig().get("zones");

                                WarcraftLogsReport[] reports = response.getBody();
                                StringBuilder reportNames = new StringBuilder();
                                StringBuilder dates = new StringBuilder();
                                StringBuilder zoneNames = new StringBuilder();

                                if (reports.length > 0) {
                                    for (WarcraftLogsReport report : (reports.length > 6 ? Arrays.copyOfRange(reports, 0, 6) : reports)) {
                                        reportNames.append(String.format(Constants.LINK, report.getTitle(), report.getUrl())).append("\n");
                                        dates.append(
                                                LocalDateTime.ofInstant(Instant.ofEpochMilli(report.getStart()), ZoneId.systemDefault())
                                                        .format(Constants.DATE_TIME_FORMATTER_DATE)).append("\n");
                                        zoneNames.append(report.getZone() == -1 ? "Invalid zone" : zoneMap.get(report.getZone()).get("name")).append("\n");
                                    }
                                    fields.add(new MessageEmbed.Field("Reports", reportNames.toString(), true));
                                    fields.add(new MessageEmbed.Field("Date", dates.toString(), true));
                                    fields.add(new MessageEmbed.Field("Zone", zoneNames.toString(), true));
                                } else {
                                    fields.add(new MessageEmbed.Field("No logs found.", "", false));
                                }

                                MessageEmbed completedMsg = EmbedUtils.createEmbed("<" + guildName + "> logs",
                                        String.format(Constants.LINK, "Click here to visit the calendar", settings.getCalendarUrl(guildName)), fields,
                                        Constants.COLOR_OK, null, null, WarcraftLogsConstants.ICON_LINK);
                                sendMsg.editMessage(completedMsg).queue();
                            } catch (HttpClientErrorException exception) {
                                ObjectMapper mapper = new ObjectMapper();
                                try {
                                    JsonNode actualObj = mapper
                                            .readTree(exception.getMessage().substring(exception.getMessage().indexOf("{"), exception.getMessage().indexOf("}") + 1));

                                    MessageEmbed successEmbed = EmbedUtils.createEmbed(guildName + " logs",
                                            "Error while searching for reports.\n\n⚠ " + exception.getRawStatusCode() + " - " +
                                                    actualObj.findValue("error").asText(), null, Constants.COLOR_NOT_OK, null, null,
                                            "https://dmszsuqyoe6y6.cloudfront.net/img/warcraft/favicon.png");
                                    sendMsg.editMessage(successEmbed).queue();
                                } catch (JsonProcessingException e) {
                                    log.error("Error parsing JSON response", e);
                                }
                            }
                        }
                );
            } else {
                MessageEmbed error = EmbedUtils.createEmbed("Invalid WarcraftLogs settings", "The logs command must first be configured by an admin using `" +
                        App.client.getPrefix() + "setup logs`\n\n⚠ WarcraftLogs command not configured", null, Constants.COLOR_NOT_OK, null, null,WarcraftLogsConstants.ICON_LINK);
                event.getChannel().sendMessage(error).queue(
                        success -> {
                            success.delete().queueAfter(20, TimeUnit.SECONDS);
                        }
                );
                event.getMessage().delete().queue();
            }
        } else {
            MessageEmbed invalid = EmbedUtils.createErrorEmbed("Invalid settings", null, "No settings found for this Discord server.", "");
            event.getChannel().sendMessage(invalid).queue(
                    success -> {
                        success.delete().queueAfter(20, TimeUnit.SECONDS);
                    }
            );
            event.getMessage().delete().queue();
        }
    }
}
