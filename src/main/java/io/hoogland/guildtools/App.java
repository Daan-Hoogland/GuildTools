package io.hoogland.guildtools;

import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import io.hoogland.guildtools.commands.*;
import io.hoogland.guildtools.commands.application.ApplyCmd;
import io.hoogland.guildtools.commands.application.ApplyListener;
import io.hoogland.guildtools.commands.linking.LinkCmd;
import io.hoogland.guildtools.commands.linking.LinkedCmd;
import io.hoogland.guildtools.commands.linking.UnlinkCmd;
import io.hoogland.guildtools.commands.linking.WhoisCmd;
import io.hoogland.guildtools.commands.logs.LogsCmd;
import io.hoogland.guildtools.commands.logs.PLogsCmd;
import io.hoogland.guildtools.commands.logs.PLogsListener;
import io.hoogland.guildtools.commands.loot.LootAllReactionListener;
import io.hoogland.guildtools.commands.loot.LootClassReactionListener;
import io.hoogland.guildtools.commands.loot.dkp.DKPCmd;
import io.hoogland.guildtools.commands.loot.epgp.EPGPCmd;
import io.hoogland.guildtools.commands.rolereaction.RoleReactionCmd;
import io.hoogland.guildtools.commands.rolereaction.RoleReactionListener;
import io.hoogland.guildtools.commands.settings.LootIgnoreRankCmd;
import io.hoogland.guildtools.commands.settings.SettingsCmd;
import io.hoogland.guildtools.commands.settings.SetupCmd;
import io.hoogland.guildtools.listeners.WebhookListener;
import io.hoogland.guildtools.utils.ConfigUtils;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.security.auth.login.LoginException;

@SpringBootApplication(scanBasePackages = "io.hoogland.guildtools.*")
@EnableJpaRepositories("io.hoogland.guildtools.models.repositories")
@Slf4j
public class App implements CommandLineRunner {

    public static final EventWaiter waiter = new EventWaiter();
    public static JDA jda;
    public static CommandClient client;

    @Value("${prefix}")
    private String prefix;
    @Value("${tokens.discord}")
    private String discordToken;
    @Value("${tokens.owner}")
    private String ownerId;
    @Value("${tokens.warcraftlogs}")
    private String warcraftlogsToken;

    public static void main(String[] args) {
        ConfigUtils.loadConfiguration();
        SpringApplication.run(App.class, args);
    }

    @Override
    public void run(String... args) throws LoginException {
        CommandClientBuilder builder = new CommandClientBuilder();

        builder.useHelpBuilder(false);
        builder.setPrefix(prefix);
        builder.setOwnerId(ownerId);
        builder.setActivity(Activity.listening(prefix + "help"));
        builder.addCommands(new RoleReactionCmd(waiter), new DKPCmd(waiter), new EPGPCmd(), new SettingsCmd(), new LinkCmd(), new UnlinkCmd(),
                new LinkedCmd(), new WhoisCmd(), new ShutdownCmd(), new ApplyCmd(), new RulesCmd(), new HelpCmd(),
                new SetupCmd(waiter, warcraftlogsToken), new LogsCmd(warcraftlogsToken), new PLogsCmd(warcraftlogsToken), new ConsumablesCmd());

        client = builder.build();

        jda = JDABuilder.createDefault(discordToken)
                .addEventListeners(client)
                .addEventListeners(new RoleReactionListener())
                .addEventListeners(new LootAllReactionListener())
                .addEventListeners(new LootClassReactionListener())
                .addEventListeners(new PLogsListener(warcraftlogsToken))
                .addEventListeners(new ApplyListener())
                .addEventListeners(new DeleteMessageListener())
                .addEventListeners(new WebhookListener(warcraftlogsToken))
                .addEventListeners(waiter)
                .build();
    }
}
