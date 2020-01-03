package io.hoogland.guildtools.commands.dkp;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import io.hoogland.guildtools.constants.DKPConstants;
import io.hoogland.guildtools.models.DKPStanding;
import io.hoogland.guildtools.models.repositories.DKPAllMessageRepository;
import io.hoogland.guildtools.models.repositories.DKPImportRepository;
import io.hoogland.guildtools.models.repositories.DKPStandingRepository;
import io.hoogland.guildtools.utils.BeanUtils;
import io.hoogland.guildtools.utils.DKPUtils;
import io.hoogland.guildtools.utils.EmojiUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
public class DKPClassCmd extends Command {

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private DKPAllMessageRepository dkpAllMessageRepository = BeanUtils.getBean(DKPAllMessageRepository.class);
    private DKPStandingRepository dkpStandingRepository = BeanUtils.getBean(DKPStandingRepository.class);
    private DKPImportRepository dkpImportRepository = BeanUtils.getBean(DKPImportRepository.class);

    public DKPClassCmd() {
        this.name = "dkpclass";
        this.help = "links the users Discord account to their in-game character.";
    }

    @Override
    protected void execute(CommandEvent event) {
        if (!event.getAuthor().isBot()) {
            if (!event.getArgs().isBlank() && DKPConstants.CLASSES.contains(event.getArgs().toUpperCase())) {
                Page<DKPStanding> guildStanding = dkpStandingRepository
                        .findByGuildIdAndClazz(event.getGuild().getIdLong(),
                                DKPConstants.CLASSES.get(DKPConstants.CLASSES.indexOf(event.getArgs().toUpperCase())),
                                PageRequest.of(0, 20, Sort.by("dkp").descending()));

                event.getChannel().sendMessage(
                        DKPUtils.getDKPAllEmbed(StringUtils.capitalize(event.getArgs().toLowerCase()) + " | " + event.getGuild().getName(),
                                dkpImportRepository.findTopByGuildIdOrderByCreatedDateDesc(event.getGuild().getIdLong()), guildStanding, 0)).queue(
                        success -> {
                            List<String> classEmojis = EmojiUtils.getAllClassEmojisButOne(event.getArgs().toUpperCase());
                            classEmojis.forEach(emoji -> {
                                success.addReaction(EmojiUtils.discordEmojiToUnicode(emoji)).queue();
                            });
                        }
                );
                event.getMessage().delete().queue();
            } else {

            }
        }
    }
}
