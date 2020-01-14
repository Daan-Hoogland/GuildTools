package io.hoogland.guildtools.commands.linking;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import io.hoogland.guildtools.constants.CharacterConstants;
import io.hoogland.guildtools.constants.Constants;
import io.hoogland.guildtools.models.domain.Character;
import io.hoogland.guildtools.models.repositories.CharacterRepository;
import io.hoogland.guildtools.utils.BeanUtils;
import io.hoogland.guildtools.utils.EmbedUtils;
import io.hoogland.guildtools.utils.LinkUtils;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
public class WhoisCmd extends Command {

    private CharacterRepository characterRepository = BeanUtils.getBean(CharacterRepository.class);

    public WhoisCmd() {
        this.name = "whois";
        this.arguments = "[@user]";
        this.aliases = new String[]{"whois"};
        this.help = "lists the character names linked to a Discord account or the other way around.";
    }

    @Override
    protected void execute(CommandEvent event) {
        if (event.getArgs().isBlank()) {

            List<Character> allCharacters = characterRepository
                    .findAllByUserIdAndGuildId(event.getAuthor().getIdLong(), event.getGuild().getIdLong());

            String description = String.format(CharacterConstants.CHARACTER_LINKED_DESCRIPTION, String.format(
                    Constants.MENTION_USER, event.getAuthor().getIdLong()));

            event.getChannel().sendMessage(EmbedUtils
                    .createEmbed(CharacterConstants.CHARACTER_LINKED, description,
                            LinkUtils.getCharacterFields(allCharacters, Constants.DATE_TIME_FORMATTER_DATE)))
                    .queue();

        } else if (!event.getMessage().getMentionedMembers().isEmpty()) {
            List<Character> characterList = characterRepository
                    .findAllByUserIdAndGuildId(event.getMessage().getMentionedMembers().get(0).getUser().getIdLong(),
                            event.getGuild().getIdLong());

            List<MessageEmbed.Field> characterFields = LinkUtils.getCharacterFields(characterList, Constants.DATE_TIME_FORMATTER_DATE);

            String title = String
                    .format(CharacterConstants.WHOIS_TITLE, "@" + event.getMessage().getMentionedMembers().get(0).getUser().getName());

            String description = String.format(CharacterConstants.WHOIS_DESCRIPTION_DISCORD,
                    String.format(Constants.MENTION_USER, event.getMessage().getMentionedMembers().get(0).getUser().getIdLong()));

            event.getChannel().sendMessage(
                    EmbedUtils.createEmbed(title, description, LinkUtils.getCharacterFields(characterList, Constants.DATE_TIME_FORMATTER_DATE)))
                    .queue();
        } else {
            Optional<Character> matchingChar = characterRepository
                    .findByNameAndGuildId(event.getArgs().toUpperCase(), event.getGuild().getIdLong());
            if (matchingChar.isPresent()) {
                long userId = matchingChar.get().getUserId();
                List<Character> characterList = characterRepository
                        .findAllByUserIdAndGuildId(userId, matchingChar.get().getGuildId());

                String title = String
                        .format(CharacterConstants.WHOIS_TITLE, StringUtils.capitalize(event.getArgs().toLowerCase()));

                String description = String
                        .format(CharacterConstants.WHOIS_DESCRIPTION_CHARACTER, StringUtils.capitalize(event.getArgs().toLowerCase()),
                                String.format(Constants.MENTION_USER, userId),
                                String.format(Constants.MENTION_USER, userId));

                event.getMessage().delete().queue();
                event.getChannel().sendMessage(EmbedUtils
                        .createEmbed(title, description, LinkUtils.getCharacterFields(characterList, Constants.DATE_TIME_FORMATTER_DATE)))
                        .queue( success -> {
                            success.delete().queueAfter(1, TimeUnit.MINUTES);
                        });
            } else {
                log.debug("nothing found");
            }
        }
    }
}
