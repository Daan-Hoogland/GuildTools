package io.hoogland.guildtools.commands;

import io.hoogland.guildtools.models.domain.LootAllMessage;
import io.hoogland.guildtools.models.repositories.LootAllMessageRepository;
import io.hoogland.guildtools.models.repositories.ReactionRoleMessageRepository;
import io.hoogland.guildtools.utils.BeanUtils;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.message.guild.GuildMessageDeleteEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Optional;

@Slf4j
public class DeleteMessageListener extends ListenerAdapter {

    private ReactionRoleMessageRepository reactionRoleMessageRepository = BeanUtils.getBean(ReactionRoleMessageRepository.class);
    private LootAllMessageRepository lootAllMessageRepository = BeanUtils.getBean(LootAllMessageRepository.class);

    @Override
    public void onGuildMessageDelete(GuildMessageDeleteEvent event) {
        log.debug("deleted msg " + event.getMessageId());
        reactionRoleMessageRepository.deleteAll(reactionRoleMessageRepository.findAllByMessageId(event.getMessageIdLong()));
        Optional<LootAllMessage> lootAllMsg = lootAllMessageRepository
                .findByMessageIdAndAndChannelIdAndGuildId(event.getMessageIdLong(), event.getChannel().getIdLong(), event.getGuild().getIdLong());
        if (lootAllMsg.isPresent()) {
            lootAllMessageRepository.delete(lootAllMsg.get());
        }
    }
}
