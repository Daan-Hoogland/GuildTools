package io.hoogland.guildtools.models.repositories;

import io.hoogland.guildtools.models.DKPAllMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DKPAllMessageRepository extends JpaRepository<DKPAllMessage, Long> {
    Optional<DKPAllMessage> findByMessageIdAndAndChannelIdAndGuildId(long messageId, long channelId, long guildId);
}
