package io.hoogland.guildtools.models.repositories;

import io.hoogland.guildtools.models.domain.ReactionRole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReactionRoleRepository extends JpaRepository<ReactionRole, Long> {
}
