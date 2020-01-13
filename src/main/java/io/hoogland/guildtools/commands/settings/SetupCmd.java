package io.hoogland.guildtools.commands.settings;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import io.hoogland.guildtools.models.repositories.GuildSettingsRepository;
import io.hoogland.guildtools.models.repositories.WarcraftlogSettingsRepository;
import io.hoogland.guildtools.services.RestService;
import io.hoogland.guildtools.utils.BeanUtils;

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
