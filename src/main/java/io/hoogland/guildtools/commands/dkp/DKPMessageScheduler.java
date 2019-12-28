package io.hoogland.guildtools.commands.dkp;

import io.hoogland.guildtools.App;
import io.hoogland.guildtools.models.DKPAllMessage;
import io.hoogland.guildtools.models.repositories.DKPAllMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DKPMessageScheduler {

    @Autowired
    private DKPAllMessageRepository dkpAllMessageRepository;

    @Scheduled(cron = "0 * * * *")
    public void removeExpiredMessages() {
        List<DKPAllMessage> allMessages = dkpAllMessageRepository.findAll();
        allMessages.forEach(message -> {
            if (message.getCreatedDate().isBefore(message.getCreatedDate().minusDays(1))) {
                //todo delete message
                App.jda.getGuildById(message.getGuildId()).getTextChannelById(message.getChannelId()).retrieveMessageById(message.getMessageId())
                        .queue(
                                success -> {
                                    success.delete().queue(deleteSuccess -> {
                                        dkpAllMessageRepository.delete(message);
                                    });
                                }
                        );
            }
        });
    }
}
