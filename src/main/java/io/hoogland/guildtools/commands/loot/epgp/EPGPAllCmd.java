package io.hoogland.guildtools.commands.loot.epgp;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import io.hoogland.guildtools.models.domain.EPGPStanding;
import io.hoogland.guildtools.models.domain.LootAllMessage;
import io.hoogland.guildtools.models.repositories.EPGPStandingRepository;
import io.hoogland.guildtools.models.repositories.LootAllMessageRepository;
import io.hoogland.guildtools.models.repositories.LootImportRepository;
import io.hoogland.guildtools.utils.BeanUtils;
import io.hoogland.guildtools.utils.EPGPUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@Slf4j
public class EPGPAllCmd extends Command {

    private LootAllMessageRepository lootAllMessageRepository = BeanUtils.getBean(LootAllMessageRepository.class);
    private EPGPStandingRepository epgpStandingRepository = BeanUtils.getBean(EPGPStandingRepository.class);
    private LootImportRepository lootImportRepository = BeanUtils.getBean(LootImportRepository.class);


    public EPGPAllCmd() {
        this.name = "all";
        this.help = "links the users Discord account to their in-game character.";
    }

    @Override
    protected void execute(CommandEvent event) {
        if (!event.getAuthor().isBot()) {
            Page<EPGPStanding> guildStanding = epgpStandingRepository
                    .findByGuildId(event.getGuild().getIdLong(), PageRequest.of(0, 20, Sort.by("pr").descending()));

            event.getChannel().sendMessage(EPGPUtils.getAllEmbed(event.getGuild().getName(),
                    lootImportRepository.findTopByGuildIdAndTypeOrderByCreatedDateDesc(event.getGuild().getIdLong(), "epgp"), guildStanding, 0))
                    .queue(
                            success -> {
                                if (guildStanding.getContent().size() > 19) {
                                    LootAllMessage lootAllMessage = new LootAllMessage(success, "epgp", 0);
                                    lootAllMessageRepository.saveAndFlush(lootAllMessage);
                                    success.addReaction("⬅").queue();
                                    success.addReaction("➡").queue();
                                }
                                log.debug("success");
                            },
                            failure -> {
                                log.debug("failure");
                            }
                    );
            event.getMessage().delete().queue();
        }
    }
}
