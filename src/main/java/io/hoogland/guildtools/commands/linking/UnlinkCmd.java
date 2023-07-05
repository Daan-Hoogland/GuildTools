package io.hoogland.guildtools.commands.linking;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import io.hoogland.guildtools.constants.CharacterConstants;
import io.hoogland.guildtools.models.domain.Character;
import io.hoogland.guildtools.models.repositories.CharacterRepository;
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
public class UnlinkCmd extends Command {

    private CharacterRepository characterRepository = BeanUtils.getBean(CharacterRepository.class);

    public UnlinkCmd() {
        this.name = "unlink";
        this.arguments = "[player]";
        this.aliases = new String[]{"unlink", "ulink", "dchar", "dcharacter"};
        this.help = "unlinks the users Discord account from their in-game character.";
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
                    confirmedList.add("`" + StringUtils.capitalize(characterName) + "`");
                    characterRepository.delete(existingChar.get());
                } else {
                    errorList.add("`" + StringUtils.capitalize(characterName) + "`");
                }
            }
            event.getMessage().delete().queue();
            event.getChannel().sendMessage(EmbedUtils
                    .createEmbed(CharacterConstants.CHARACTER_UNLINK, errorList.isEmpty() ? CharacterConstants.CHARACTER_UNLINK_DESCRIPTION :
                                    CharacterConstants.CHARACTER_UNLINK_DESCRIPTION + CharacterConstants.CHARACTER_UNLINK_DESCRIPTION_ERROR,
                            LinkUtils.getResultFields(confirmedList, errorList, false))).queue(
                                    success -> {
                                        success.delete().queueAfter(20, TimeUnit.SECONDS);

                                    }
            );
        } else {
            log.debug("invalid");
        }
    }
}
