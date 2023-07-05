package io.hoogland.guildtools.models.repositories;

import io.hoogland.guildtools.models.domain.ReactionRole;
import io.hoogland.guildtools.models.domain.ReactionRoleMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReactionRoleRepository extends JpaRepository<ReactionRole, Long> {

    void deleteAllByReactionRoleMessage(ReactionRoleMessage reactionRoleMessage);

}
