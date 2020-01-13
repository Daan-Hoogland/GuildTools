package io.hoogland.guildtools.commands.loot;

import io.hoogland.guildtools.models.domain.DKPStanding;
import io.hoogland.guildtools.models.domain.EPGPStanding;
import io.hoogland.guildtools.models.domain.LootAllMessage;
import io.hoogland.guildtools.models.repositories.DKPStandingRepository;
import io.hoogland.guildtools.models.repositories.EPGPStandingRepository;
import io.hoogland.guildtools.models.repositories.LootAllMessageRepository;
import io.hoogland.guildtools.models.repositories.LootImportRepository;
import io.hoogland.guildtools.utils.BeanUtils;
import io.hoogland.guildtools.utils.DKPUtils;
import io.hoogland.guildtools.utils.EPGPUtils;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.Optional;

@Slf4j
public class LootAllReactionListener extends ListenerAdapter {

    private LootAllMessageRepository lootAllMessageRepository = BeanUtils.getBean(LootAllMessageRepository.class);
    private DKPStandingRepository dkpStandingRepository = BeanUtils.getBean(DKPStandingRepository.class);
    private LootImportRepository lootImportRepository = BeanUtils.getBean(LootImportRepository.class);
    private EPGPStandingRepository epgpStandingRepository = BeanUtils.getBean(EPGPStandingRepository.class);

    public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent event) {
        if (!event.getUser().isBot() && (event.getReaction().getReactionEmote().getName().equals("⬅") ||
                event.getReaction().getReactionEmote().getName().equals("➡"))) {
            Optional<LootAllMessage> lootAllMessage = lootAllMessageRepository
                    .findByMessageIdAndAndChannelIdAndGuildId(event.getMessageIdLong(), event.getChannel().getIdLong(), event.getGuild().getIdLong());
            if (lootAllMessage.isPresent()) {
                event.getChannel().retrieveMessageById(lootAllMessage.get().getMessageId()).queue(
                        success -> {
                            int page = 0;
                            if (event.getReaction().getReactionEmote().getEmoji().equals("⬅") && lootAllMessage.get().getPage() - 1 >= 0) {
                                page = lootAllMessage.get().getPage() - 1;
                            } else if (event.getReaction().getReactionEmote().getEmoji().equals("➡")) {
                                page = lootAllMessage.get().getPage() + 1;
                            }

                            if (lootAllMessage.get().getType().equalsIgnoreCase("dkp")) {

                                Page<DKPStanding> guildStanding = dkpStandingRepository
                                        .findByGuildId(event.getGuild().getIdLong(),
                                                PageRequest.of(page, 20, Sort.by("dkp").descending()));

                                if (!guildStanding.getContent().isEmpty() && page - 1 < guildStanding.getTotalPages()) {
                                    success.editMessage(DKPUtils.getDKPAllEmbed(event.getGuild().getName(),
                                            lootImportRepository.findTopByGuildIdAndTypeOrderByCreatedDateDesc(event.getGuild().getIdLong(), "dkp"),
                                            guildStanding,
                                            page)).queue();
                                    LootAllMessage newMessage = lootAllMessage.get();
                                    newMessage.setPage(page);
                                    lootAllMessageRepository.saveAndFlush(newMessage);
                                }
                            } else if (lootAllMessage.get().getType().equalsIgnoreCase("epgp")) {
                                Page<EPGPStanding> guildStanding = epgpStandingRepository
                                        .findByGuildId(event.getGuild().getIdLong(), PageRequest.of(page, 20, Sort.by("pr").descending()));

                                if (!guildStanding.getContent().isEmpty() && page - 1 < guildStanding.getTotalPages()) {
                                    success.editMessage(EPGPUtils.getAllEmbed(event.getGuild().getName(),
                                            lootImportRepository.findTopByGuildIdAndTypeOrderByCreatedDateDesc(event.getGuild().getIdLong(), "epgp"),
                                            guildStanding, page)).queue();
                                    LootAllMessage newMessage = lootAllMessage.get();
                                    newMessage.setPage(page);
                                    lootAllMessageRepository.saveAndFlush(newMessage);
                                }
                            }
                        }
                );
                event.getReaction().removeReaction(event.getUser()).queue();
            }
        }
    }
}
