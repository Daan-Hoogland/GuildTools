package io.hoogland.guildtools.commands.loot.epgp;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import io.hoogland.guildtools.models.domain.EPGPStanding;
import io.hoogland.guildtools.models.repositories.EPGPStandingRepository;
import io.hoogland.guildtools.utils.*;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;

@Slf4j
public abstract class EPGPGenericClassCmd extends Command {

    private EPGPStandingRepository epgpStandingRepository = BeanUtils.getBean(EPGPStandingRepository.class);

    public EPGPGenericClassCmd() {
        this.help = "shows the dkp values for all " + this.name + " in guild.";
    }

    @Override
    protected void execute(CommandEvent event) {
        if (!event.getAuthor().isBot()) {
            Page<EPGPStanding> guildStanding = epgpStandingRepository
                    .findByGuildIdAndClazz(event.getGuild().getIdLong(),
                            this.name.toUpperCase(), PageRequest.of(0, 20, Sort.by("pr").descending()));

            HashMap classInfo = (HashMap) ((HashMap) ConfigUtils.getConfig().get("icons")).get(this.name.toLowerCase());

            MessageEmbed embed = EmbedUtils
                    .createEmbed(StringUtils.capitalize(this.name.toLowerCase()) + " | " + event.getGuild().getName(), null,
                            EPGPUtils.getStandingFields(guildStanding, 0), (String) classInfo.get("color"), "React with a class emote to view other classes.",
                            "https://i.imgur.com/pZf0MvC.png", (String) classInfo.get("icon"));

            event.getChannel().sendMessage(embed).queue(
                    success -> {
                        List<String> classEmojis = EmojiUtils.getAllClassEmojisButOne(this.name.toLowerCase());
                        classEmojis.forEach(emoji -> {
                            success.addReaction(EmojiUtils.discordEmojiToUnicode(emoji)).queue();
                        });
                    },
                    failure -> {
                        log.debug("failure");
                    }
            );
            event.getMessage().delete().queue();
        }
    }
}
