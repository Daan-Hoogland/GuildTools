package io.hoogland.guildtools.models.repositories;

import io.hoogland.guildtools.models.domain.LootImport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface LootImportRepository extends JpaRepository<LootImport, Long> {
    @Transactional
    Optional<LootImport> findTopByGuildIdAndTypeOrderByCreatedDateDesc(long guildId, String type);

    @Transactional
    void deleteAllByGuildId(long guildId);

    @Transactional
    void deleteAllByGuildIdAndType(long guildId, String type);
}
