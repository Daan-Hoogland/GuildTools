package io.hoogland.guildtools.commands.loot.dkp;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import io.hoogland.guildtools.models.domain.DKPStanding;
import io.hoogland.guildtools.models.domain.GuildSettings;
import io.hoogland.guildtools.models.repositories.DKPStandingRepository;
import io.hoogland.guildtools.models.repositories.GuildSettingsRepository;
import io.hoogland.guildtools.utils.BeanUtils;
import io.hoogland.guildtools.utils.EmbedUtils;
import io.hoogland.guildtools.utils.RoleUtils;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
public class DKPDeleteCmd extends Command {

    private DKPStandingRepository dkpStandingRepository = BeanUtils.getBean(DKPStandingRepository.class);
    private GuildSettingsRepository guildSettingsRepository = BeanUtils.getBean(GuildSettingsRepository.class);

    public DKPDeleteCmd() {
        this.name = "delete";
        this.aliases = new String[]{"delete", "remove"};
        this.help = "deletes the DKP standing for specified user(s).";
    }

    @Override
    protected void execute(CommandEvent event) {
        Optional<GuildSettings> optionalSettings = guildSettingsRepository.findByGuildId(event.getGuild().getIdLong());
        if (optionalSettings.isPresent()) {
            if (RoleUtils.hasRoleWithId(optionalSettings.get().getAdminRoleId(), event.getMember().getRoles()) ||
                    event.getGuild().getOwnerIdLong() == event.getMember().getIdLong()) {
                String[] args = event.getArgs().split("\\s+");
                List<String> success = new ArrayList<>();
                List<String> error = new ArrayList<>();

                for (String player : args) {
                    Optional<DKPStanding> standing = dkpStandingRepository.findByPlayerAndGuildId(player.toUpperCase(), event.getGuild().getIdLong());
                    if (standing.isPresent()) {
                        dkpStandingRepository.delete(standing.get());
                        success.add("`" + StringUtils.capitalize(player.toLowerCase()) + "`");
                    } else {
                        error.add("`" + StringUtils.capitalize(player.toLowerCase()) + "`");
                    }
                }

                MessageEmbed msg = EmbedUtils
                        .createEmbed("Deleted DKP standings", "The following changes have been made.", getFields(success, error));
                event.getChannel().sendMessage(msg).queue();
                event.getMessage().delete().queue();
            }
        }
    }

    private List<MessageEmbed.Field> getFields(List<String> success, List<String> error) {
        List<MessageEmbed.Field> fields = new ArrayList<>();

        String joinError = String.join(", ", error);

        fields.add(new MessageEmbed.Field("Deleted", success.isEmpty() ? "*None*" : String.join(", ", success), false));
        fields.add(new MessageEmbed.Field("Not deleted", error.isEmpty() ? "*None*" : String.join(", ", error), false));

        return fields;
    }
}
