package io.hoogland.guildtools.commands.loot;

import io.hoogland.guildtools.App;
import io.hoogland.guildtools.models.domain.LootAllMessage;
import io.hoogland.guildtools.models.repositories.LootAllMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LootMessageScheduler {

    @Autowired
    private LootAllMessageRepository lootAllMessageRepository;

    @Scheduled(cron = "0 * * * *")
    public void removeExpiredMessages() {
        List<LootAllMessage> allMessages = lootAllMessageRepository.findAll();
        allMessages.forEach(message -> {
            if (message.getCreatedDate().isBefore(message.getCreatedDate().minusDays(1))) {
                App.jda.getGuildById(message.getGuildId()).getTextChannelById(message.getChannelId()).retrieveMessageById(message.getMessageId())
                        .queue(
                                success -> {
                                    success.delete().queue(deleteSuccess -> {
                                        lootAllMessageRepository.delete(message);
                                    });
                                }
                        );
            }
        });
    }
}
