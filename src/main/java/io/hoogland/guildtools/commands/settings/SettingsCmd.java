package io.hoogland.guildtools.commands.settings;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import io.hoogland.guildtools.constants.Constants;
import io.hoogland.guildtools.models.GuildSettings;
import io.hoogland.guildtools.models.repositories.GuildSettingsRepository;
import io.hoogland.guildtools.utils.BeanUtils;
import io.hoogland.guildtools.utils.EmbedUtils;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SettingsCmd extends Command {

    private GuildSettingsRepository guildSettingsRepository = BeanUtils.getBean(GuildSettingsRepository.class);

    public SettingsCmd() {
        this.name = "settings";
        this.aliases = new String[]{"settings", "setting"};
        this.arguments = "[none|setofficer|setapplication]";
        this.help = "shows the settings set for this Discord server.";
        this.children = new Command[]{new SetOfficerCmd(), new SetApplicationCmd()};
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
                MessageEmbed success = EmbedUtils.createEmbed("Guild settings", null, getFields(optionalSettings.get()));
                event.getChannel().sendMessage(success).queue();
                event.getMessage().delete().queue();
            }
        } else {
            MessageEmbed invalid = EmbedUtils.createErrorEmbed("Invalid settings", null, "No settings found for this Discord server.", "");
            event.getChannel().sendMessage(invalid).queue();
            event.getMessage().delete().queue();
        }
    }

    private List<MessageEmbed.Field> getFields(GuildSettings guildSettings) {
        List<MessageEmbed.Field> fields = new ArrayList<>();

        fields.add(new MessageEmbed.Field("Officer role",
                guildSettings.getAdminRoleId() != null ? String.format(Constants.MENTION_ROLE, guildSettings.getAdminRoleId()) : "*Not set*", false));
        fields.add(new MessageEmbed.Field("Application channel", guildSettings.getApplicationChannelId() != null ? String
                .format(Constants.MENTION_CHANNEL, guildSettings.getApplicationChannelId()) : "*Not set*",
                false));

        return fields;
    }
}
