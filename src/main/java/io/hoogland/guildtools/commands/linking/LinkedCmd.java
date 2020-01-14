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

import java.util.List;
import java.util.concurrent.TimeUnit;

public class LinkedCmd extends Command {

    private CharacterRepository characterRepository = BeanUtils.getBean(CharacterRepository.class);

    public LinkedCmd() {
        this.name = "linked";
        this.arguments = "[player]";
        this.aliases = new String[]{"linked", "characters"};
        this.help = "lists the characters linked to the users Discord account.";
    }

    @Override
    protected void execute(CommandEvent event) {
        List<Character> allCharacters = characterRepository
                .findAllByUserIdAndGuildId(event.getAuthor().getIdLong(), event.getGuild().getIdLong());

        String description = String.format(CharacterConstants.CHARACTER_LINKED_DESCRIPTION, String.format(
                Constants.MENTION_USER, event.getAuthor().getIdLong()));

        event.getMessage().delete().queue();
        event.getChannel().sendMessage(EmbedUtils
                .createEmbed(CharacterConstants.CHARACTER_LINKED, description,
                        LinkUtils.getCharacterFields(allCharacters, Constants.DATE_TIME_FORMATTER_DATE)))
                .queue(
                        success -> {
                            success.delete().queueAfter(1, TimeUnit.MINUTES);
                        }
                );
    }
}
