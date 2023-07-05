package io.hoogland.guildtools.commands.loot.epgp.classes;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import io.hoogland.guildtools.constants.Constants;
import io.hoogland.guildtools.models.repositories.EPGPStandingRepository;
import io.hoogland.guildtools.models.repositories.LootImportRepository;
import io.hoogland.guildtools.utils.BeanUtils;
import io.hoogland.guildtools.utils.EmbedUtils;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.MessageEmbed;

@Slf4j
public class EPGPResetCmd extends Command {
    private EPGPStandingRepository epgpStandingRepository = BeanUtils.getBean(EPGPStandingRepository.class);
    private LootImportRepository lootImportRepository = BeanUtils.getBean(LootImportRepository.class);

    public EPGPResetCmd() {
        this.name = "reset";
        this.aliases = new String[]{"reset", "clear"};
        this.help = "resets all EPGP standings for this server.";
    }

    @Override
    protected void execute(CommandEvent event) {
        if (event.getGuild().getOwnerIdLong() == event.getMember().getIdLong()) {
            epgpStandingRepository.deleteAllByGuildId(event.getGuild().getIdLong());
            lootImportRepository.deleteAllByGuildIdAndType(event.getGuild().getIdLong(), "epgp");
            MessageEmbed success = EmbedUtils
                    .createEmbed("Reset EPGP", "EPGP entries and imports have been reset.", null, Constants.COLOR_OK, null, null, null);
            event.getChannel().sendMessage(success).queue();
            event.getMessage().delete().queue();
        }
    }
}
