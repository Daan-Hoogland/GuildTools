package io.hoogland.guildtools.commands.settings;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import io.hoogland.guildtools.commands.logs.SetupLogsCmd;

public class SetupCmd extends Command {

    private EventWaiter waiter;
    private String token;

    public SetupCmd(EventWaiter waiter, String token) {
        this.name = "setup";
        this.waiter = waiter;
        this.token = token;
        this.children = new Command[]{new SetupLogsCmd(waiter, token)};
    }

    @Override
    protected void execute(CommandEvent commandEvent) {

    }
}
