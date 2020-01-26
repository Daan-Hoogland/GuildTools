package io.hoogland.guildtools.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import io.hoogland.guildtools.App;
import io.hoogland.guildtools.models.domain.GuildSettings;
import io.hoogland.guildtools.models.repositories.GuildSettingsRepository;
import io.hoogland.guildtools.utils.BeanUtils;
import io.hoogland.guildtools.utils.RoleUtils;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;
import java.util.Optional;

@Slf4j
public class HelpCmd extends Command {

    private GuildSettingsRepository guildSettingsRepository = BeanUtils.getBean(GuildSettingsRepository.class);

    public HelpCmd() {
        this.name = "help";
        this.aliases = new String[]{"info"};
        this.help = "shows this help text.";
    }

    @Override
    protected void execute(CommandEvent event) {
        Optional<GuildSettings> optionalSettings = guildSettingsRepository.findByGuildId(event.getGuild().getIdLong());
        boolean epgp = false;
        boolean dkp = false;
        boolean both = true;
        if (optionalSettings.isPresent()) {
            epgp = optionalSettings.get().isEpgp();
            dkp = optionalSettings.get().isDkp();
            if (dkp || epgp)
                both = false;
        }

        String prefix = App.client.getPrefix();

        EmbedBuilder builder = new EmbedBuilder();

        builder.setTitle(event.getSelfUser().getName() + " commands");
        builder.setDescription("Below all the commands along with sub-commands can be found.");
        builder.setThumbnail("https://discordemoji.com/assets/emoji/4882_gotem.png");
        builder.setColor(Color.decode("#ffdc5d"));
        if (dkp || both) {
            builder.addField("DKP", "`" + prefix + "dkp` - sends the dkp standings for the characters linked to user.\n" +
                    "`" + prefix + "dkp all` - displays all DKP standings for characters in the current server.\n" +
                    "`" + prefix + "dkp [name]` - sends the DKP standings for the name mentioned.\n" +
                    "`" + prefix + "dkp [class]` - displays the DKP standings for all characters for a certain class.", false);
        }
        if (epgp || both) {
            builder.addField("EPGP", "`" + prefix + "epgp` - sends the EPGP standings for the characters linked to user.\n" +
                    "`" + prefix + "epgp all` - displays all EPGP standings for characters in the current server.\n" +
                    "`" + prefix + "epgp [name]` - sends the EPGP standings for the name mentioned.\n" +
                    "`" + prefix + "epgp [class]` - displays the EPGP standings for all characters for a certain class.", false);
        }

        builder.addField("Linking", "`" + prefix + "link [character]` - links your Discord to the character.\n" +
                "`" + prefix + "unlink [character]` - unlinks your Discord from the character.\n" +
                "`" + prefix + "whois [@user|player]` - shows who is linked to what Discord account/character.\n" +
                "`" + prefix + "linked` - shows a list of characters you are currently linked to.\n", false);
        if (optionalSettings.isPresent() && !(event.getGuild().getOwnerIdLong() == event.getMember().getIdLong() && !event.getArgs().isBlank())) {
            if (RoleUtils.hasRoleWithId(optionalSettings.get().getAdminRoleId(), event.getMember().getRoles())) {
                builder.addField("Settings", "`" + prefix + "settings` - shows settings for the current Discord server.\n" +
                                "`" + prefix +
                                "settings setofficer [@role]` - sets @role as the officer role for the bot. This allows for editing loot values & approving applications.\n" +
                                "`" + prefix + "settings setapplication [#channel]` - sets #channel as the channel the applications will be posted in.\n" +
                                "`" + prefix + "setup logs` - sets up the required variables to enable the `" + prefix + "logs` command.\n",
                        false);
            }
        }
        builder.addField("Warcraft Logs", "`" + prefix + "logs` - shows the latest logs of the guild.\n" +
                "`" + prefix + "logs [guildname]` - shows the latest logs of the mentioned guild.\n" +
                "`" + prefix + "plogs [dps/hps] [character]` - shows the rankings of the mentioned character for the latest raid.", false);

        builder.addField("Misc", "`" + prefix + "apply [role]` - sends an application to the officers of the server to be assigned role `role`.\n" +
                        "`" + prefix +
                        "consumables [class]` - shows all the required consumables for a raid for the given class (all classes if left empty).",
                false);

        builder.addField("", "For additional info contact **Dan**#3377 or **Bigdan**-Noggenfogger EU", false);

        if (event.getGuild().getOwnerIdLong() == event.getMember().getIdLong() && !event.getArgs().isBlank()) {
            event.getChannel().sendMessage(builder.build()).queue();
        } else {
            event.getAuthor().openPrivateChannel().queue(
                    success -> {
                        success.sendMessage(builder.build()).queue();
                    });
        }
        event.getMessage().delete().queue();
    }
}
