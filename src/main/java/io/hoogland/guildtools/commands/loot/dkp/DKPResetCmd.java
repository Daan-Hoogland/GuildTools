package io.hoogland.guildtools.commands.loot.dkp;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import io.hoogland.guildtools.constants.Constants;
import io.hoogland.guildtools.models.repositories.DKPStandingRepository;
import io.hoogland.guildtools.models.repositories.GuildSettingsRepository;
import io.hoogland.guildtools.models.repositories.LootImportRepository;
import io.hoogland.guildtools.utils.BeanUtils;
import io.hoogland.guildtools.utils.EmbedUtils;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.MessageEmbed;

@Slf4j
public class DKPResetCmd extends Command {
    private GuildSettingsRepository guildSettingsRepository = BeanUtils.getBean(GuildSettingsRepository.class);
    private DKPStandingRepository dkpStandingRepository = BeanUtils.getBean(DKPStandingRepository.class);
    private LootImportRepository lootImportRepository = BeanUtils.getBean(LootImportRepository.class);

    public DKPResetCmd() {
        this.name = "reset";
        this.aliases = new String[]{"reset", "clear"};
        this.help = "resets all DKP standings for this server.";
    }

    @Override
    protected void execute(CommandEvent event) {
        if (event.getGuild().getOwnerIdLong() == event.getMember().getIdLong()) {
            dkpStandingRepository.deleteAllByGuildId(event.getGuild().getIdLong());
            lootImportRepository.deleteAllByGuildId(event.getGuild().getIdLong());
            MessageEmbed success = EmbedUtils
                    .createEmbed("Reset DKP", "DKP entries and imports have been reset.", null, Constants.COLOR_OK, null, null, null);
            event.getChannel().sendMessage(success).queue();
            event.getMessage().delete().queue();
        }
    }
}
