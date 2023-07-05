package io.hoogland.guildtools.commands.settings;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import io.hoogland.guildtools.constants.Constants;
import io.hoogland.guildtools.models.domain.GuildSettings;
import io.hoogland.guildtools.models.repositories.GuildSettingsRepository;
import io.hoogland.guildtools.utils.BeanUtils;
import io.hoogland.guildtools.utils.EmbedUtils;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.List;
import java.util.Optional;

@Slf4j
public class SetApplicationCmd extends Command {

    private GuildSettingsRepository guildSettingsRepository = BeanUtils.getBean(GuildSettingsRepository.class);

    public SetApplicationCmd() {
        this.name = "setapplication";
        this.aliases = new String[]{"setapplication", "setapplicationchannel", "applicationchannel", "channel", "application", "setapplications"};
        this.help = "sets the channel for rank applications.";
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
                log.debug("allowed true");
                GuildSettings settings = optionalSettings.get();
                List<TextChannel> channels = event.getMessage().getMentionedChannels();

                if (!channels.isEmpty()) {
                    settings.setApplicationChannelId(channels.get(0).getIdLong());
                    guildSettingsRepository.saveAndFlush(settings);
                    MessageEmbed success = EmbedUtils.createEmbed("Application channel saved",
                            "Application channel saved as " + String.format(Constants.MENTION_CHANNEL, settings.getApplicationChannelId()), null,
                            Constants.COLOR_OK, null, null, null);
                    event.getChannel().sendMessage(success).queue();
                } else {
                    MessageEmbed error = EmbedUtils.createErrorEmbed("Invalid channels", null, "No channels mentioned", "");
                    event.getChannel().sendMessage(error).queue();
                }
                event.getMessage().delete().queue();
            }
        } else {
            MessageEmbed error = EmbedUtils.createErrorEmbed("Invalid settings", null, "No settings found for server", "");
            event.getChannel().sendMessage(error).queue();
            event.getMessage().delete().queue();
        }
    }
}
