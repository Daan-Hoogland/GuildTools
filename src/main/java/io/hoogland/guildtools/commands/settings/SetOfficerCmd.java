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

import java.util.List;
import java.util.Optional;

@Slf4j
public class SetOfficerCmd extends Command {

    private GuildSettingsRepository guildSettingsRepository = BeanUtils.getBean(GuildSettingsRepository.class);

    public SetOfficerCmd() {
        this.name = "setofficer";
        this.aliases = new String[]{"setofficer", "officer"};
        this.help = "sends a rank request to the officers of the Discord.";
    }

    @Override
    protected void execute(CommandEvent event) {
        if (event.getAuthor().getIdLong() == event.getGuild().getOwnerIdLong()) {
            List<Role> roles = event.getMessage().getMentionedRoles();
            if (!roles.isEmpty()) {
                Optional<GuildSettings> settingsOptional = guildSettingsRepository.findByGuildId(event.getGuild().getIdLong());
                GuildSettings settings;
                if (settingsOptional.isPresent()) {
                    settings = settingsOptional.get();
                    settings.setAdminRoleId(roles.get(0).getIdLong());
                } else {
                    settings = new GuildSettings();
                    settings.setGuildId(event.getGuild().getIdLong());
                    settings.setAdminRoleId(roles.get(0).getIdLong());
                }
                guildSettingsRepository.saveAndFlush(settings);
                MessageEmbed success = EmbedUtils.createEmbed("Officer role saved",
                        "Officer role saved as " + String.format(Constants.MENTION_ROLE, settings.getAdminRoleId()), null, Constants.COLOR_OK, null,
                        null,
                        null);
                event.getChannel().sendMessage(success).queue();
            } else {
                MessageEmbed error = EmbedUtils.createErrorEmbed(null, null, "No roles mentioned.", "");
                event.getChannel().sendMessage(error).queue();
            }
            event.getMessage().delete().queue();
        }
    }
}
