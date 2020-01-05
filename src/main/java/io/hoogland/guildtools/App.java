package io.hoogland.guildtools;

import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import io.hoogland.guildtools.commands.application.ApplyCmd;
import io.hoogland.guildtools.commands.RulesCmd;
import io.hoogland.guildtools.commands.ShutdownCmd;
import io.hoogland.guildtools.commands.application.ApplyListener;
import io.hoogland.guildtools.commands.dkp.DKPAllReactionListener;
import io.hoogland.guildtools.commands.dkp.DKPClassReactionListener;
import io.hoogland.guildtools.commands.dkp.DKPCmd;
import io.hoogland.guildtools.commands.linking.LinkCmd;
import io.hoogland.guildtools.commands.linking.LinkedCmd;
import io.hoogland.guildtools.commands.linking.UnlinkCmd;
import io.hoogland.guildtools.commands.linking.WhoisCmd;
import io.hoogland.guildtools.commands.rolereaction.RoleReactionCmd;
import io.hoogland.guildtools.commands.rolereaction.RoleReactionListener;
import io.hoogland.guildtools.commands.settings.SettingsCmd;
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

    @Value("${prefix}")
    private String prefix;
    @Value("${tokens.discord}")
    private String discordToken;
    @Value("${tokens.owner}")
    private String ownerId;

    public static void main(String[] args) {
        ConfigUtils.loadConfiguration();
        SpringApplication.run(App.class, args);
    }

    @Override
    public void run(String... args) throws LoginException {
        CommandClientBuilder builder = new CommandClientBuilder();

        builder.setPrefix(prefix);
        builder.setOwnerId(ownerId);
        builder.setActivity(Activity.listening(prefix + "help"));
        builder.addCommands(new RoleReactionCmd(waiter));
        builder.addCommands(new DKPCmd(waiter));
        builder.addCommands(new SettingsCmd());
        builder.addCommands(new LinkCmd());
        builder.addCommands(new UnlinkCmd());
        builder.addCommands(new LinkedCmd());
        builder.addCommands(new WhoisCmd());
        builder.addCommands(new ShutdownCmd());
        builder.addCommands(new ApplyCmd());
        builder.addCommands(new RulesCmd());

        CommandClient client = builder.build();

        jda = new JDABuilder(discordToken)
                .addEventListeners(client)
                .addEventListeners(new RoleReactionListener())
                .addEventListeners(new DKPAllReactionListener())
                .addEventListeners(new DKPClassReactionListener())
                .addEventListeners(new ApplyListener())
                .addEventListeners(waiter)
                .build();
    }
}
