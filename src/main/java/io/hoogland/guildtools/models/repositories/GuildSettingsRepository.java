package io.hoogland.guildtools.models.repositories;

import io.hoogland.guildtools.models.domain.GuildSettings;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GuildSettingsRepository extends JpaRepository<GuildSettings, Long> {
    Optional<GuildSettings> findByGuildId(Long guildId);
}
