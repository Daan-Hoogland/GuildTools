package io.hoogland.guildtools.models.repositories;

import io.hoogland.guildtools.models.DKPImport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface DKPImportRepository extends JpaRepository<DKPImport, Long> {
    @Transactional
    Optional<DKPImport> findTopByGuildIdOrderByCreatedDateDesc(long guildId);

    @Transactional
    void deleteAllByGuildId(long guildId);
}
