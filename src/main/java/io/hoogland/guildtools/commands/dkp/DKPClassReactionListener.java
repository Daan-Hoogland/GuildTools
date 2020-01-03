package io.hoogland.guildtools.commands.dkp;

import io.hoogland.guildtools.App;
import io.hoogland.guildtools.constants.Constants;
import io.hoogland.guildtools.constants.DKPConstants;
import io.hoogland.guildtools.models.DKPImport;
import io.hoogland.guildtools.models.DKPStanding;
import io.hoogland.guildtools.models.repositories.DKPAllMessageRepository;
import io.hoogland.guildtools.models.repositories.DKPImportRepository;
import io.hoogland.guildtools.models.repositories.DKPStandingRepository;
import io.hoogland.guildtools.utils.*;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Slf4j
public class DKPClassReactionListener extends ListenerAdapter {

    private DKPAllMessageRepository dkpAllMessageRepository = BeanUtils.getBean(DKPAllMessageRepository.class);
    private DKPStandingRepository dkpStandingRepository = BeanUtils.getBean(DKPStandingRepository.class);
    private DKPImportRepository dkpImportRepository = BeanUtils.getBean(DKPImportRepository.class);

    public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent event) {
        if (!event.getUser().isBot() && event.getReactionEmote().isEmote()) {
            if (String.format(Constants.MENTION_EMOJI, event.getReactionEmote().getEmote().getName(), event.getReactionEmote().getEmote().getIdLong())
                    .equals(EmojiUtils.getEmojiForClass(event.getReactionEmote().getEmote().getName()))) {

                event.getReaction().getChannel().retrieveMessageById(event.getMessageIdLong()).queue(
                        success -> {
                            if (success.getAuthor().getIdLong() == App.jda.getSelfUser().getIdLong() && !success.getEmbeds().isEmpty()) {
                                if (success.getEmbeds().get(0).getTitle() != null) {
                                    if (success.getEmbeds().get(0).getTitle().contains("| " + event.getGuild().getName())) {
                                        Page<DKPStanding> guildStanding = dkpStandingRepository
                                                .findByGuildIdAndClazz(event.getGuild().getIdLong(),
                                                        DKPConstants.CLASSES
                                                                .get(DKPConstants.CLASSES
                                                                        .indexOf(event.getReactionEmote().getEmote().getName().toUpperCase())),
                                                        PageRequest.of(0, 20, Sort.by("dkp").descending()));
                                        String[] previousMessageClass = success.getEmbeds().get(0).getTitle().split("\\s+");

                                        List<MessageEmbed.Field> standingFields = DKPUtils.getStandingFields(guildStanding, 0);
                                        Optional<DKPImport> dkpImport = dkpImportRepository
                                                .findTopByGuildIdOrderByCreatedDateDesc(event.getGuild().getIdLong());
                                        String footer = String
                                                .format(DKPConstants.DKP_FOOTER, dkpImport.isPresent() ? dkpImport.get().getCreatedDate()
                                                        .format(Constants.DATE_TIME_FORMATTER) : "unknown");
                                        String title = String.format(DKPConstants.DKP_CLASS_TITLE,
                                                StringUtils.capitalize(event.getReactionEmote().getEmote().getName()), event.getGuild().getName());
                                        HashMap classInfo = (HashMap) ((HashMap) ConfigUtils.getConfig().get("icons"))
                                                .get(event.getReactionEmote().getEmote().getName());
                                        success.editMessage(
                                                EmbedUtils.createEmbed(title, "", standingFields, classInfo.get("color").toString(), footer, null,
                                                        classInfo.get("icon").toString())).queue(
                                                msgEdited -> {
                                                    msgEdited.addReaction(
                                                            EmojiUtils.discordEmojiToUnicode(EmojiUtils.getEmojiForClass(previousMessageClass[0])))
                                                            .queue();
                                                    event.getReaction().removeReaction(event.getUser()).queue();
                                                    event.getReaction().removeReaction().queue();
                                                }
                                        );
                                    }
                                }
                            }
                        });
            }
        }
    }
}
