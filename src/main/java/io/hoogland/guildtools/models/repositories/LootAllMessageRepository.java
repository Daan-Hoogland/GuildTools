package io.hoogland.guildtools.models.repositories;

import io.hoogland.guildtools.models.domain.LootAllMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LootAllMessageRepository extends JpaRepository<LootAllMessage, Long> {
    Optional<LootAllMessage> findByMessageIdAndAndChannelIdAndGuildId(long messageId, long channelId, long guildId);
}
