package io.hoogland.guildtools.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import io.hoogland.guildtools.App;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;

@Slf4j
public class HelpCmd extends Command {

    public HelpCmd() {
        this.name = "help";
        this.aliases = new String[]{"info"};
        this.help = "shows this help text.";
    }

    @Override
    protected void execute(CommandEvent event) {
        String prefix = App.client.getPrefix();

        EmbedBuilder builder = new EmbedBuilder();

        builder.setTitle(event.getSelfUser().getName() + " commands");
        builder.setDescription("Below all the commands along with sub-commands can be found.");

        builder.addField("DKP", "`" + prefix + "dkp` - sends the dkp standings for the characters linked to user.\n" +
                "`" + prefix + "dkp all` - displays all DKP standings for characters in the current server.\n" +
                "`" + prefix + "dkp [name]` - sends the DKP standings for the name mentioned.\n" +
                "`" + prefix + "dkp [class]` - displays the DKP standings for all characters for a certain class.", false);
        builder.addField("Linking", "`" + prefix + "link [character]` - links your Discord to the character.\n" +
                "`" + prefix + "unlink [character]` - unlinks your Discord from the character.\n" +
                "`" + prefix + "whois [@user|player]` - shows who is linked to what Discord account/character.\n" +
                "`" + prefix + "linked` - shows a list of characters you are currently linked to.\n", false);
        builder.addField("Settings", "`" + prefix + "settings` - shows settings for the current Discord server.\n" +
                "`" + prefix +
                "settings setofficer [@role]` - sets @role as the officer role for the bot. This allows for editing DKP values & approving applications.\n" +
                "`" + prefix + "settings setapplication [#channel]` - sets #channel as the channel the applications will be posted in.\n", false);
        builder.addField("Misc", "`" + prefix + "apply [role]` - sends an application to the officers of the server to be assigned role `role`.",
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
