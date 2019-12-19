package io.hoogland.guildtools.models.repositories;

import io.hoogland.guildtools.models.ReactionRoleMessage;
import io.hoogland.guildtools.models.ReactionRoleMessageDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ReactionRoleMessageRepository extends JpaRepository<ReactionRoleMessage, Long> {
    Optional<ReactionRoleMessage> findDistinctByMessageIdAndChannelIdAndGuildId(String messageId, String channelId, String guildId);

    @Query("SELECT new io.hoogland.guildtools.models.ReactionRoleMessageDto(rr.roleId, rrm.directLink, rr.creatorId, rr.type) " +
            "FROM ReactionRoleMessage rrm JOIN ReactionRole rr ON rrm.id = rr.reactionRoleMessage WHERE rrm.messageId = ?1 AND rr.emojiId = ?2")
    Optional<ReactionRoleMessageDto> findByMessageIdAndEmojiId(String messageId, String emojiId);
}
