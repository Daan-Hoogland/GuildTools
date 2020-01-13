package io.hoogland.guildtools.commands.loot;

import io.hoogland.guildtools.App;
import io.hoogland.guildtools.constants.Constants;
import io.hoogland.guildtools.constants.DKPConstants;
import io.hoogland.guildtools.models.domain.DKPStanding;
import io.hoogland.guildtools.models.domain.EPGPStanding;
import io.hoogland.guildtools.models.domain.LootImport;
import io.hoogland.guildtools.models.repositories.DKPStandingRepository;
import io.hoogland.guildtools.models.repositories.EPGPStandingRepository;
import io.hoogland.guildtools.models.repositories.LootImportRepository;
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
public class LootClassReactionListener extends ListenerAdapter {

    private DKPStandingRepository dkpStandingRepository = BeanUtils.getBean(DKPStandingRepository.class);
    private LootImportRepository lootImportRepository = BeanUtils.getBean(LootImportRepository.class);
    private EPGPStandingRepository epgpStandingRepository = BeanUtils.getBean(EPGPStandingRepository.class);

    public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent event) {
        if (!event.getUser().isBot() && event.getReactionEmote().isEmote()) {
            if (String.format(Constants.MENTION_EMOJI, event.getReactionEmote().getEmote().getName(), event.getReactionEmote().getEmote().getIdLong())
                    .equals(EmojiUtils.getEmojiForClass(event.getReactionEmote().getEmote().getName()))) {

                event.getReaction().getChannel().retrieveMessageById(event.getMessageIdLong()).queue(
                        success -> {
                            if (success.getAuthor().getIdLong() == App.jda.getSelfUser().getIdLong() && !success.getEmbeds().isEmpty()) {
                                if (success.getEmbeds().get(0).getTitle() != null) {
                                    if (success.getEmbeds().get(0).getTitle().contains("| " + event.getGuild().getName())) {
                                        String[] previousMessageClass = success.getEmbeds().get(0).getTitle().split("\\s+");
                                        String clazz = DKPConstants.CLASSES
                                                .get(DKPConstants.CLASSES
                                                        .indexOf(event.getReactionEmote().getEmote().getName().toUpperCase()));
                                        HashMap classInfo = (HashMap) ((HashMap) ConfigUtils.getConfig().get("icons"))
                                                .get(event.getReactionEmote().getEmote().getName());


                                        String title = String.format(DKPConstants.DKP_CLASS_TITLE,
                                                StringUtils.capitalize(event.getReactionEmote().getEmote().getName()),
                                                event.getGuild().getName());

                                        if (success.getEmbeds().get(0).getFields().get(1).getName().equalsIgnoreCase("rank")) {
                                            Optional<LootImport> lootImport = lootImportRepository
                                                    .findTopByGuildIdAndTypeOrderByCreatedDateDesc(event.getGuild().getIdLong(), "epgp");
                                            Page<EPGPStanding> guildStanding = epgpStandingRepository
                                                    .findByGuildIdAndClazz(event.getGuild().getIdLong(),
                                                            clazz, PageRequest.of(0, 20, Sort.by("pr").descending()));

                                            String footer = String
                                                    .format(DKPConstants.DKP_FOOTER, lootImport.isPresent() ? lootImport.get().getCreatedDate()
                                                            .format(Constants.DATE_TIME_FORMATTER) : "unknown");

                                            List<MessageEmbed.Field> standingFields = EPGPUtils.getStandingFields(guildStanding, 0);

                                            success.editMessage(
                                                    EmbedUtils.createEmbed(title, "", standingFields, classInfo.get("color").toString(), footer, null,
                                                            classInfo.get("icon").toString())).queue(
                                                    msgEdited -> {
                                                        msgEdited.addReaction(
                                                                EmojiUtils
                                                                        .discordEmojiToUnicode(EmojiUtils.getEmojiForClass(previousMessageClass[0])))
                                                                .queue();
                                                        event.getReaction().removeReaction(event.getUser()).queue();
                                                        event.getReaction().removeReaction().queue();
                                                    }
                                            );
                                        } else {
                                            Optional<LootImport> lootImport = lootImportRepository
                                                    .findTopByGuildIdAndTypeOrderByCreatedDateDesc(event.getGuild().getIdLong(), "dkp");

                                            Page<DKPStanding> guildStanding = dkpStandingRepository
                                                    .findByGuildIdAndClazz(event.getGuild().getIdLong(),
                                                            clazz, PageRequest.of(0, 20, Sort.by("dkp").descending()));

                                            List<MessageEmbed.Field> standingFields = DKPUtils.getStandingFields(guildStanding, 0);

                                            String footer = String
                                                    .format(DKPConstants.DKP_FOOTER, lootImport.isPresent() ? lootImport.get().getCreatedDate()
                                                            .format(Constants.DATE_TIME_FORMATTER) : "unknown");


                                            success.editMessage(
                                                    EmbedUtils.createEmbed(title, "", standingFields, classInfo.get("color").toString(), footer, null,
                                                            classInfo.get("icon").toString())).queue(
                                                    msgEdited -> {
                                                        msgEdited.addReaction(
                                                                EmojiUtils
                                                                        .discordEmojiToUnicode(EmojiUtils.getEmojiForClass(previousMessageClass[0])))
                                                                .queue();
                                                        event.getReaction().removeReaction(event.getUser()).queue();
                                                        event.getReaction().removeReaction().queue();
                                                    }
                                            );
                                        }
                                    }
                                }
                            }
                        });
            }
        }
    }
}
