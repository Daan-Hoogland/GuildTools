package io.hoogland.guildtools.commands.settings;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import io.hoogland.guildtools.models.domain.GuildSettings;
import io.hoogland.guildtools.models.repositories.GuildSettingsRepository;
import io.hoogland.guildtools.utils.BeanUtils;
import io.hoogland.guildtools.utils.EmbedUtils;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class SettingsCmd extends Command {

    private GuildSettingsRepository guildSettingsRepository = BeanUtils.getBean(GuildSettingsRepository.class);

    public SettingsCmd() {
        this.name = "settings";
        this.aliases = new String[]{"settings", "setting"};
        this.arguments = "[none|setofficer|setapplication]";
        this.help = "shows the settings set for this Discord server.";
        this.children = new Command[]{new SetOfficerCmd(), new SetApplicationCmd(), new SetLootCmd()};
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
                event.getChannel().sendMessage(optionalSettings.get().getMessageEmbed()).queue();
                event.getMessage().delete().queue();
            }
        } else {
            MessageEmbed invalid = EmbedUtils.createErrorEmbed("Invalid settings", null, "No settings found for this Discord server.", "");
            event.getChannel().sendMessage(invalid).queue( success -> {
                success.delete().queueAfter(20, TimeUnit.SECONDS);
            });
            event.getMessage().delete().queue();
        }
    }
}
