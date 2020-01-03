package io.hoogland.guildtools.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

public class ApplyCmd extends Command {

    public ApplyCmd() {
        this.name = "apply";
        this.aliases = new String[]{"apply"};
        this.help = "sends a rank request to the officers of the Discord.";
    }

    @Override
    protected void execute(CommandEvent event) {

    }
}
