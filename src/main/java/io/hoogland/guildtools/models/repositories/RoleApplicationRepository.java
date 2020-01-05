package io.hoogland.guildtools.models.repositories;

import io.hoogland.guildtools.models.Character;
import io.hoogland.guildtools.models.RoleApplication;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoleApplicationRepository extends JpaRepository<RoleApplication, Long> {
    Optional<RoleApplication> findByMessageIdAndGuildId(long messageId, long guildId);

    Optional<RoleApplication> findByUserIdAndGuildId(long userId, long guildId);
}
