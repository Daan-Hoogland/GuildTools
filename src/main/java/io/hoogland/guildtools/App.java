package io.hoogland.guildtools;

import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import io.hoogland.guildtools.commands.*;
import io.hoogland.guildtools.commands.dkp.DKPAllReactionListener;
import io.hoogland.guildtools.commands.dkp.DKPCmd;
import io.hoogland.guildtools.commands.rolereaction.RoleReactionCmd;
import io.hoogland.guildtools.commands.rolereaction.RoleReactionListener;
import io.hoogland.guildtools.utils.ConfigUtils;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.security.auth.login.LoginException;
import java.util.HashMap;

@SpringBootApplication
@EnableJpaRepositories("io.hoogland.guildtools.models.repositories")
@Slf4j
public class App implements CommandLineRunner {

    public static final EventWaiter waiter = new EventWaiter();
    public static JDA jda;

    public static void main(String[] args) {
        ConfigUtils.loadConfiguration();
        SpringApplication.run(App.class, args);
    }

    @Override
    public void run(String... args) throws LoginException {
        HashMap configTokens = (HashMap) ConfigUtils.getConfig().get("tokens");

        CommandClientBuilder builder = new CommandClientBuilder();

        builder.setPrefix(ConfigUtils.getConfig().get("prefix").toString());
        builder.setOwnerId(configTokens.get("owner").toString());
        //todo change to prefix from yaml
        builder.setActivity(Activity.listening("!help"));
        builder.addCommands(new RoleReactionCmd(waiter));
        builder.addCommands(new DKPCmd(waiter));
        builder.addCommands(new ShutdownCmd());

        CommandClient client = builder.build();

        jda = new JDABuilder(configTokens.get("discord").toString())
                .addEventListeners(client)
                .addEventListeners(new RoleReactionListener())
                .addEventListeners(new DKPAllReactionListener())
                .addEventListeners(waiter)
                .build();
    }
}
