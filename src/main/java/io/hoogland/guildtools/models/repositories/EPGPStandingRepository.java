package io.hoogland.guildtools.models.repositories;

import io.hoogland.guildtools.models.domain.EPGPStanding;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface EPGPStandingRepository extends JpaRepository<EPGPStanding, Long> {
    List<EPGPStanding> findAllByClazz(String clazz);

    Optional<EPGPStanding> findByPlayerAndGuildId(String player, long guildId);

    Page<EPGPStanding> findByGuildId(long guildId, Pageable pageable);

    Page<EPGPStanding> findByGuildIdAndClazz(long guildId, String clazz, Pageable pageable);

    @Transactional
    void deleteAllByGuildId(long guildId);
}
