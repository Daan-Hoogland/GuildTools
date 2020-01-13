package io.hoogland.guildtools.models.repositories;

import io.hoogland.guildtools.models.domain.DKPStanding;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface DKPStandingRepository extends JpaRepository<DKPStanding, Long> {
    List<DKPStanding> findAllByClazz(String clazz);

    Optional<DKPStanding> findByPlayerAndGuildId(String player, long guildId);

    Page<DKPStanding> findByGuildId(long guildId, Pageable pageable);

    Page<DKPStanding> findByGuildIdAndClazz(long guildId, String clazz, Pageable pageable);

    @Transactional
    void deleteAllByGuildId(long guildId);
}
