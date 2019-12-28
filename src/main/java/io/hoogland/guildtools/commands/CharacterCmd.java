package io.hoogland.guildtools.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import io.hoogland.guildtools.utils.CommandUtils;
import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;

@Slf4j
public class CharacterCmd extends Command {

    private EventWaiter waiter;

    public CharacterCmd(EventWaiter waiter) {
        this.name = "character";
        this.aliases = new String[]{"character", "char", "link"};
        this.help = "links the users Discord account to their in-game character.";
        this.waiter = waiter;
    }

    @Override
    protected void execute(CommandEvent event) {
        if (CommandUtils.isValidCommand(event, aliases)) {

        }
    }
}
