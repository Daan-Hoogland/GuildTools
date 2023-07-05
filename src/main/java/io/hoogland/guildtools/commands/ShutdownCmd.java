package io.hoogland.guildtools.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

public class ShutdownCmd extends Command {

    public ShutdownCmd() {
        this.name = "shutdown";
        this.aliases = new String[]{"shutdown", "disconnect"};
        this.help = "Turns off the bot.";
        this.ownerCommand = true;
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        commandEvent.getMessage().addReaction("\uD83D\uDC4C").queue();
        commandEvent.getJDA().shutdown();
        System.exit(0);
    }
}
