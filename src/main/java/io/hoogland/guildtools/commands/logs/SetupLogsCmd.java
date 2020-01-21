package io.hoogland.guildtools.commands.logs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.sun.istack.NotNull;
import io.hoogland.guildtools.constants.Constants;
import io.hoogland.guildtools.constants.EmojiConstants;
import io.hoogland.guildtools.constants.ReactionRoleConstants;
import io.hoogland.guildtools.constants.WarcraftLogsConstants;
import io.hoogland.guildtools.models.Region;
import io.hoogland.guildtools.models.WarcraftLogsReport;
import io.hoogland.guildtools.models.domain.GuildSettings;
import io.hoogland.guildtools.models.domain.WarcraftLogSettings;
import io.hoogland.guildtools.models.repositories.GuildSettingsRepository;
import io.hoogland.guildtools.models.repositories.WarcraftlogSettingsRepository;
import io.hoogland.guildtools.services.RestService;
import io.hoogland.guildtools.utils.BeanUtils;
import io.hoogland.guildtools.utils.EmbedUtils;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
public class SetupLogsCmd extends Command {

    private GuildSettingsRepository guildSettingsRepository = BeanUtils.getBean(GuildSettingsRepository.class);
    private RestService restService = BeanUtils.getBean(RestService.class);
    private WarcraftlogSettingsRepository warcraftlogSettingsRepository = BeanUtils.getBean(WarcraftlogSettingsRepository.class);
    private EventWaiter waiter;
    private String token;
    private final String color = "001525";

    public SetupLogsCmd(EventWaiter waiter, String token) {
        this.name = "logs";
        this.aliases = new String[]{"log"};
        this.help = "configures the values to retrieve data from WarcraftLogs.";
        this.waiter = waiter;
        this.token = token;
    }

    @Override
    protected void execute(CommandEvent event) {
        Optional<GuildSettings> optionalSettings = guildSettingsRepository.findByGuildId(event.getGuild().getIdLong());
        if (optionalSettings.isPresent()) {
            boolean isAllowed = false;
            for (Role role : event.getMember().getRoles()) {
                if (role.getIdLong() == optionalSettings.get().getAdminRoleId()) {
                    isAllowed = true;
                }
            }

            if (isAllowed) {
                WarcraftLogSettings settings;
                if (optionalSettings.get().getWarcraftLogSettings() == null) {
                    settings = new WarcraftLogSettings();
                } else {
                    settings = optionalSettings.get().getWarcraftLogSettings();
                }
                event.getMessage().delete().queue();
                event.getAuthor().openPrivateChannel().queue(
                        openedChannel -> {
                            MessageEmbed msg = EmbedUtils.createEmbed("WarcraftLogs Setup - Region",
                                    "This is the start of the WarcraftLogs setup. Please respond with your Region.\n\nRegions to choose from: `EU, US, KR, TW, CH`",
                                    null, color, "Respond with cancel to cancel setup", null, WarcraftLogsConstants.ICON_LINK);
                            openedChannel.sendMessage(msg).queue(success -> {
                                waitForRegion(event, success, settings);
                            });
                        }
                );
            }
        } else {
            MessageEmbed error = EmbedUtils.createErrorEmbed("Invalid settings", null, "No settings found for server", "");
            event.getChannel().sendMessage(error).queue(
                    success -> {
                        success.delete().queueAfter(20, TimeUnit.SECONDS);
                    }
            );
        }
    }

    private void cancelCommand(@NotNull Message userMessage, @NotNull PrivateChannel channel) {
        MessageEmbed embed = EmbedUtils.createErrorEmbed("WarcraftLogs Setup - Cancelled",
                "The WarcraftLog setup has been cancelled. Your previous values have not been saved.", "",
                "Cancelled at " + LocalDateTime.now().format(Constants.DATE_TIME_FORMATTER_PRECISE));
        channel.sendMessage(embed).queue();
    }

    private void waitForRegion(CommandEvent originalEvent, Message message, WarcraftLogSettings settings) {
        waiter.waitForEvent(PrivateMessageReceivedEvent.class, event -> !event.getAuthor().isBot(),
                e -> {
                    if (e.getMessage().getContentRaw().equalsIgnoreCase(ReactionRoleConstants.CANCEL_COMMAND)) {
                        cancelCommand(e.getMessage(), e.getChannel());
                    } else {
                        if (EnumUtils.isValidEnum(Region.class, e.getMessage().getContentRaw().toUpperCase())) {
                            settings.setRegion(Region.valueOf(e.getMessage().getContentRaw().toUpperCase()));
                            waitForGuildName(originalEvent, e.getChannel(), settings);
                        } else {
                            log.warn("invalid enum selected");
                            e.getChannel().sendMessage(EmbedUtils.createErrorEmbed("WarcraftLogs Setup - Region",
                                    "This is the start of the WarcraftLogs setup. Please respond with your Region.\n\nRegions to choose from: `EU, US, KR, TW, CH`",
                                    "Invalid region", "Respond with cancel to cancel setup")).queue();
                            waitForRegion(originalEvent, message, settings);
                        }
                    }
                });
    }

    private void waitForGuildName(CommandEvent originalEvent, PrivateChannel channel, WarcraftLogSettings settings) {
        MessageEmbed msg = EmbedUtils
                .createEmbed("WarcraftLogs Setup - Guild Name", "Respond with your guild name.", null, color, "Respond with cancel to cancel setup",
                        null, WarcraftLogsConstants.ICON_LINK);
        channel.sendMessage(msg).queue(success -> {
            waiter.waitForEvent(PrivateMessageReceivedEvent.class, event -> !event.getAuthor().isBot(),
                    e -> {
                        if (e.getMessage().getContentRaw().equalsIgnoreCase(ReactionRoleConstants.CANCEL_COMMAND)) {
                            cancelCommand(e.getMessage(), e.getChannel());
                        } else {
                            settings.setGuild(e.getMessage().getContentRaw());
                            waitForServer(originalEvent, e.getChannel(), settings);
                        }
                    });
        });
    }

    private void waitForServer(CommandEvent originalEvent, PrivateChannel channel, WarcraftLogSettings settings) {
        MessageEmbed msg = EmbedUtils
                .createEmbed("WarcraftLogs Setup - Server Name", "Respond with the server your guild is located on.", null, color,
                        "Respond with cancel to cancel setup", null, WarcraftLogsConstants.ICON_LINK);
        channel.sendMessage(msg).queue(success -> {
            waiter.waitForEvent(PrivateMessageReceivedEvent.class, event -> !event.getAuthor().isBot(),
                    e -> {
                        if (e.getMessage().getContentRaw().equalsIgnoreCase(ReactionRoleConstants.CANCEL_COMMAND)) {
                            cancelCommand(e.getMessage(), e.getChannel());
                        } else {
                            settings.setRealm(e.getMessage().getContentRaw());
                            waitForNumberOfLogs(originalEvent, e.getChannel(), settings);
                        }
                    });
        });
    }

    private void waitForNumberOfLogs(CommandEvent originalEvent, PrivateChannel channel, WarcraftLogSettings settings) {
        MessageEmbed msg = EmbedUtils.createEmbed("WarcraftLogs Setup - Testing configuration", EmojiConstants.EMOJI_LOADING, null, color,
                "Respond with cancel to cancel setup", null, WarcraftLogsConstants.ICON_LINK);
        channel.sendMessage(msg).queue(success -> {
            try {
                ResponseEntity<WarcraftLogsReport[]> response = restService.getRestTemplate()
                        .getForEntity(settings.getReportsUrl(token), WarcraftLogsReport[].class);

                Optional<GuildSettings> optionalSettings = guildSettingsRepository.findByGuildId(originalEvent.getGuild().getIdLong());
                settings.setGuildSettings(optionalSettings.get());
                optionalSettings.get().setWarcraftLogSettings(settings);
                guildSettingsRepository.saveAndFlush(optionalSettings.get());

                MessageEmbed successEmbed = EmbedUtils.createEmbed("WarcraftLogs Setup - Testing configuration",
                        "Testing OK, configuration saved.\n\n" + response.getBody().length + " reports found.",
                        null, Constants.COLOR_OK, null, null, "https://dmszsuqyoe6y6.cloudfront.net/img/warcraft/favicon.png");
                success.editMessage(successEmbed).queue();
            } catch (HttpClientErrorException exception) {
                ObjectMapper mapper = new ObjectMapper();
                try {
                    JsonNode actualObj = mapper
                            .readTree(exception.getMessage().substring(exception.getMessage().indexOf("{"), exception.getMessage().indexOf("}") + 1));

                    MessageEmbed successEmbed = EmbedUtils.createEmbed("WarcraftLogs Setup - Testing configuration",
                            "Testing not OK, settings discarded.\n\n⚠ " + exception.getRawStatusCode() + " - " +
                                    actualObj.findValue("error").asText(), null, Constants.COLOR_NOT_OK, null, null,
                            "https://dmszsuqyoe6y6.cloudfront.net/img/warcraft/favicon.png");
                    success.editMessage(successEmbed).queue();
                } catch (JsonProcessingException e) {
                    log.error("Error parsing JSON response", e);
                }
            }
        });
    }
}
