package io.hoogland.guildtools.commands.dkp;

import io.hoogland.guildtools.models.DKPAllMessage;
import io.hoogland.guildtools.models.DKPStanding;
import io.hoogland.guildtools.models.repositories.DKPAllMessageRepository;
import io.hoogland.guildtools.models.repositories.DKPImportRepository;
import io.hoogland.guildtools.models.repositories.DKPStandingRepository;
import io.hoogland.guildtools.utils.BeanUtils;
import io.hoogland.guildtools.utils.DKPUtils;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.Optional;

@Slf4j
public class DKPAllReactionListener extends ListenerAdapter {

    private DKPAllMessageRepository dkpAllMessageRepository = BeanUtils.getBean(DKPAllMessageRepository.class);
    private DKPStandingRepository dkpStandingRepository = BeanUtils.getBean(DKPStandingRepository.class);
    private DKPImportRepository dkpImportRepository = BeanUtils.getBean(DKPImportRepository.class);

    public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent event) {
        if (!event.getUser().isBot() && (event.getReaction().getReactionEmote().getName().equals("⬅") ||
                event.getReaction().getReactionEmote().getName().equals("➡"))) {
            Optional<DKPAllMessage> dkpAllMessage = dkpAllMessageRepository
                    .findByMessageIdAndAndChannelIdAndGuildId(event.getMessageIdLong(), event.getChannel().getIdLong(), event.getGuild().getIdLong());
            if (dkpAllMessage.isPresent()) {
                //todo update message with new values
                event.getChannel().retrieveMessageById(dkpAllMessage.get().getMessageId()).queue(
                        success -> {
                            int page = 0;
                            if (event.getReaction().getReactionEmote().getEmoji().equals("⬅") && dkpAllMessage.get().getPage() - 1 >= 0) {
                                page = dkpAllMessage.get().getPage() - 1;
                            } else if (event.getReaction().getReactionEmote().getEmoji().equals("➡")) {
                                page = dkpAllMessage.get().getPage() + 1;
                            }
                            Page<DKPStanding> guildStanding = dkpStandingRepository
                                    .findByGuildId(event.getGuild().getIdLong(),
                                            PageRequest.of(page, 20, Sort.by("dkp").descending()));

                            if (!guildStanding.getContent().isEmpty() && page - 1 < guildStanding.getTotalPages()) {
                                success.editMessage(DKPUtils.getDKPAllEmbed(event.getGuild().getName(),
                                        dkpImportRepository.findTopByGuildIdOrderByCreatedDateDesc(event.getGuild().getIdLong()), guildStanding,
                                        page)).queue();
                                DKPAllMessage newMessage = dkpAllMessage.get();
                                newMessage.setPage(page);
                                dkpAllMessageRepository.saveAndFlush(newMessage);
                            }
                        }
                );
                event.getReaction().removeReaction(event.getUser()).queue();
            }
        }
    }
}
