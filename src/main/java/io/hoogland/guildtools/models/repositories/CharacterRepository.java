package io.hoogland.guildtools.models.repositories;

import io.hoogland.guildtools.models.domain.Character;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CharacterRepository extends JpaRepository<Character, Long> {
    Optional<Character> findByNameAndGuildId(String name, long guildId);

    List<Character> findAllByUserIdAndGuildId(long userId, long guildId);
}
