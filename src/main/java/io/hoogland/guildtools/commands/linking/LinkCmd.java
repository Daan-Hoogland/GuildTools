package io.hoogland.guildtools.commands.linking;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import io.hoogland.guildtools.constants.CharacterConstants;
import io.hoogland.guildtools.models.domain.Character;
import io.hoogland.guildtools.models.domain.DKPStanding;
import io.hoogland.guildtools.models.repositories.CharacterRepository;
import io.hoogland.guildtools.models.repositories.DKPStandingRepository;
import io.hoogland.guildtools.utils.BeanUtils;
import io.hoogland.guildtools.utils.EmbedUtils;
import io.hoogland.guildtools.utils.LinkUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
public class LinkCmd extends Command {

    private CharacterRepository characterRepository = BeanUtils.getBean(CharacterRepository.class);
    private DKPStandingRepository dkpStandingRepository = BeanUtils.getBean(DKPStandingRepository.class);


    public LinkCmd() {
        this.name = "link";
        this.arguments = "[player]";
        this.aliases = new String[]{"character", "char", "link"};
        this.help = "links the users Discord account to their in-game character.";
    }

    @Override
    protected void execute(CommandEvent event) {
        String[] splitArgs = event.getArgs().split(" ");
        if (splitArgs.length > 0 && !splitArgs[0].trim().isEmpty()) {
            long guildId = event.getGuild().getIdLong();
            List<String> confirmedList = new ArrayList<>();
            List<String> errorList = new ArrayList<>();

            for (String characterName : splitArgs) {
                Optional<Character> existingChar = characterRepository.findByNameAndGuildId(characterName.toUpperCase(), guildId);
                if (existingChar.isPresent()) {
                    errorList.add("`" + StringUtils.capitalize(characterName) + "`");
                    log.debug("character already exists!");
                } else {
                    Character character = new Character(characterName.toUpperCase(), event.getAuthor().getIdLong(), guildId);

                    Optional<DKPStanding> standing = dkpStandingRepository
                            .findByPlayerAndGuildId(characterName.toUpperCase(), event.getGuild().getIdLong());

                    if (standing.isPresent()) {
                        character.setClazz(standing.get().getClazz());
                    }

                    characterRepository.saveAndFlush(character);
                    confirmedList.add("`" + StringUtils.capitalize(characterName) + "`");
                }
            }
            event.getMessage().delete().queue();
            event.getChannel().sendMessage(EmbedUtils
                    .createEmbed(CharacterConstants.CHARACTER_LINK, errorList.isEmpty() ? CharacterConstants.CHARACTER_LINK_DESCRIPTION :
                                    CharacterConstants.CHARACTER_LINK_DESCRIPTION + CharacterConstants.CHARACTER_LINK_DESCRIPTION_ERROR,
                            LinkUtils.getResultFields(confirmedList, errorList, true))).queue(
                    success -> {
                        success.delete().queueAfter(20, TimeUnit.SECONDS);
                    }
            );
        } else {
            log.debug("invalid");
        }
    }
}
