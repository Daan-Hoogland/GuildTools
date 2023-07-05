package io.hoogland.guildtools.models.repositories;

import io.hoogland.guildtools.models.domain.WarcraftLogSettings;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WarcraftlogSettingsRepository extends JpaRepository<WarcraftLogSettings, Long> {
}
