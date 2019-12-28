package io.hoogland.guildtools.commands.dkp;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import io.hoogland.guildtools.models.DKPAllMessage;
import io.hoogland.guildtools.models.DKPStanding;
import io.hoogland.guildtools.models.repositories.DKPAllMessageRepository;
import io.hoogland.guildtools.models.repositories.DKPImportRepository;
import io.hoogland.guildtools.models.repositories.DKPStandingRepository;
import io.hoogland.guildtools.utils.BeanUtils;
import io.hoogland.guildtools.utils.DKPUtils;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.managers.EmoteManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.time.format.DateTimeFormatter;

@Slf4j
public class DKPAllCmd extends Command {

    private EventWaiter waiter;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private DKPAllMessageRepository dkpAllMessageRepository = BeanUtils.getBean(DKPAllMessageRepository.class);
    private DKPStandingRepository dkpStandingRepository = BeanUtils.getBean(DKPStandingRepository.class);
    private DKPImportRepository dkpImportRepository = BeanUtils.getBean(DKPImportRepository.class);


    public DKPAllCmd(EventWaiter waiter) {
        this.name = "all";
        this.help = "links the users Discord account to their in-game character.";
        this.waiter = waiter;
//        this.cooldown = 60;
    }

    @Override
    protected void execute(CommandEvent event) {
        if (!event.getAuthor().isBot()) {
            //todo save message as DKPAllMessage and fix pagination and fix embeddedmessage
            Page<DKPStanding> guildStanding = dkpStandingRepository
                    .findByGuildId(event.getGuild().getIdLong(), PageRequest.of(0, 20, Sort.by("dkp").descending()));

            log.debug(String.valueOf(guildStanding.getTotalPages()));
            event.getChannel().sendMessage(DKPUtils.getDKPAllEmbed(event.getGuild().getName(),
                    dkpImportRepository.findTopByGuildIdOrderByCreatedDateDesc(event.getGuild().getIdLong()), guildStanding, 0)).queue(
                    success -> {
                        if (guildStanding.getContent().size() > 19) {
                            DKPAllMessage dkpAllMessage = new DKPAllMessage(success, 0);
                            dkpAllMessageRepository.saveAndFlush(dkpAllMessage);
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
