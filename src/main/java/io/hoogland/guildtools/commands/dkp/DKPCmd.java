package io.hoogland.guildtools.commands.dkp;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import io.hoogland.guildtools.constants.DKPConstants;
import io.hoogland.guildtools.models.DKPStanding;
import io.hoogland.guildtools.models.repositories.DKPStandingRepository;
import io.hoogland.guildtools.utils.BeanUtils;
import io.hoogland.guildtools.utils.EmbeddedUtils;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
public class DKPCmd extends Command {

    private EventWaiter waiter;
    private DKPStandingRepository dkpStandingRepository = BeanUtils.getBean(DKPStandingRepository.class);

    public DKPCmd(EventWaiter waiter) {
        this.name = "dkp";
        this.help = "informs the user of their dkp standing. Use without name to get information about character linked to";
        this.arguments = "<name>, all";
        this.waiter = waiter;
        this.children = new Command[]{new DKPAllCmd(waiter), new DKPImportCmd(waiter)};
    }

    @Override
    protected void execute(CommandEvent event) {
        if (!event.getAuthor().isBot()) {
            String[] split = event.getMessage().getContentRaw().split(" ");

            if (split.length > 1 && split[1] != null) {
                Optional<DKPStanding> dkpStanding = dkpStandingRepository
                        .findByPlayerAndGuildId(split[1].toUpperCase(), event.getGuild().getIdLong());
                MessageEmbed msg;
                if (dkpStanding.isPresent()) {
                    msg = EmbeddedUtils.createDkpEmbed(dkpStanding.get());
                } else {
                    msg = EmbeddedUtils.buildErrorEmbed("Invalid DKP entry", null, String.format("No DKP entry found for `%s`", split[1]), null);
                }
                log.debug("replying");

                event.getAuthor().openPrivateChannel().queue(
                        sucesss -> {
                            sucesss.sendMessage(msg).queue(
                                    success -> {
                                    },
                                    failure -> {
                                        log.error("failure 1");
                                        log.error(failure.getMessage());
                                        event.getChannel().sendMessage(EmbeddedUtils.buildErrorEmbed(DKPConstants.DKP_SENDING_ERROR_TITLE,
                                                String.format(DKPConstants.DKP_SENDING_ERROR_DESCRIPTION, event.getAuthor().getId(),
                                                        event.getAuthor().getId()),
                                                failure.getMessage(), null)).queue(
                                                success -> {
                                                    success.delete().queueAfter(20, TimeUnit.SECONDS);
                                                }
                                        );
                                    }
                            );
                        },
                        failure -> {
                            log.error(failure.getMessage());
                            event.getChannel().sendMessage(EmbeddedUtils.buildErrorEmbed(DKPConstants.DKP_SENDING_ERROR_TITLE,
                                    String.format(DKPConstants.DKP_SENDING_ERROR_DESCRIPTION, event.getAuthor().getId(), event.getAuthor().getId()),
                                    failure.getMessage(), null)).queue(
                                    success -> {
                                        success.delete().queueAfter(20, TimeUnit.SECONDS);
                                    }
                            );
                        }
                );
                event.getMessage().delete().queue();
            } else {
                //todo look for character bound to discord acc
                log.warn("character linking NYI");
            }
        }
    }
}
