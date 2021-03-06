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

import java.util.Optional;

@Slf4j
public class SetLootCmd extends Command {

    private GuildSettingsRepository guildSettingsRepository = BeanUtils.getBean(GuildSettingsRepository.class);

    public SetLootCmd() {
        this.name = "setloot";
        this.aliases = new String[]{"loot"};
        this.help = "sets the loot style for the server.";
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

                String description = "";
                log.debug(String.valueOf(event.getArgs().equalsIgnoreCase("epgp") || event.getArgs().equalsIgnoreCase("dkp")));
                if (!event.getArgs().isEmpty() && (event.getArgs().equalsIgnoreCase("epgp") || event.getArgs().equalsIgnoreCase("dkp"))) {
                    if (event.getArgs().equalsIgnoreCase("dkp")) {
                        log.debug("dkp");
                        settings.setDkp(true);
                        description = "DKP";
                    } else {
                        log.debug("epgp");
                        settings.setEpgp(true);
                        description = "EPGP";
                    }
                    guildSettingsRepository.saveAndFlush(settings);
                    MessageEmbed success = EmbedUtils.createEmbed("Loot system saved",
                            "Loot setting saved as " + description, null, Constants.COLOR_OK, null,
                            null,
                            null);
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
