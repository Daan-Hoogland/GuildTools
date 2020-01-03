package io.hoogland.guildtools.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HelpCmd extends Command {

    public HelpCmd(EventWaiter waiter) {
        this.name = "help";
        this.aliases = new String[]{"help", "info"};
        this.help = "links the users Discord account to their in-game character.";
    }

    @Override
    protected void execute(CommandEvent event) {
        event.getJDA().getEventManager().getRegisteredListeners().forEach(obj -> {
            log.debug(obj.getClass().getName());
        });
    }
}
