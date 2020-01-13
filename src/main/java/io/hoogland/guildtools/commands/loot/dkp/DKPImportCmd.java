package io.hoogland.guildtools.commands.loot.dkp;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.opencsv.CSVReader;
import com.opencsv.bean.CsvToBeanBuilder;
import io.hoogland.guildtools.App;
import io.hoogland.guildtools.constants.Constants;
import io.hoogland.guildtools.constants.DKPConstants;
import io.hoogland.guildtools.constants.EmojiConstants;
import io.hoogland.guildtools.models.domain.DKPStanding;
import io.hoogland.guildtools.models.domain.GuildSettings;
import io.hoogland.guildtools.models.domain.LootImport;
import io.hoogland.guildtools.models.repositories.DKPStandingRepository;
import io.hoogland.guildtools.models.repositories.GuildSettingsRepository;
import io.hoogland.guildtools.models.repositories.LootImportRepository;
import io.hoogland.guildtools.utils.*;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
public class DKPImportCmd extends Command {

    private EventWaiter waiter;
    private LootImportRepository lootImportRepository = BeanUtils.getBean(LootImportRepository.class);
    private DKPStandingRepository dkpStandingRepository = BeanUtils.getBean(DKPStandingRepository.class);
    private GuildSettingsRepository guildSettingsRepository = BeanUtils.getBean(GuildSettingsRepository.class);

    public DKPImportCmd(EventWaiter waiter) {
        this.name = "import";
        this.help = "links the users Discord account to their in-game character.";
        this.waiter = waiter;
    }

    @Override
    protected void execute(CommandEvent event) {
        log.trace("importing");
        if (!event.getAuthor().isBot()) {
            log.trace("not bot");
            Optional<GuildSettings> optionalSettings = guildSettingsRepository.findByGuildId(event.getGuild().getIdLong());
            if (optionalSettings.isPresent()) {
                log.trace("is present");
                if ((RoleUtils.hasRoleWithId(optionalSettings.get().getAdminRoleId(), event.getMember().getRoles()) ||
                        event.getGuild().getOwnerIdLong() == event.getMember().getIdLong())) {
                    if (optionalSettings.get().isDkp()) {
                        event.getChannel().sendMessage(EmbedUtils.createEmbed("Processing data", EmojiConstants.EMOJI_LOADING, null))
                                .queue(processingMessage -> {
                                    StringBuilder builder = new StringBuilder();
                                    if (event.getMessage().getAttachments().isEmpty()) {
                                        log.debug("NYI accept csv in message.");

                                        event.getMessage().delete().queue();
                                        waitForCsv(event);
                                    } else {
                                        builder = AttachmentUtils.getAttachmentContent(event);

                                        List<DKPStanding> importedStandings;
                                        try {
                                            CSVReader reader = new CSVReader(new StringReader(builder.toString()));

                                            importedStandings = new CsvToBeanBuilder(reader)
                                                    .withType(DKPStanding.class).build().parse();
                                        } catch (Exception e) {
                                            processingMessage.editMessage(EmbeddedUtils.buildErrorEmbed(DKPConstants.DKP_ERROR_FILE_TITLE,
                                                    DKPConstants.DKP_ERROR_FILE_DESCRIPTION,
                                                    e.getMessage(), null)).queue(
                                                    success -> {
                                                        success.delete().queueAfter(20, TimeUnit.SECONDS);
                                                        event.getMessage().addReaction("❌").queueAfter(20, TimeUnit.SECONDS);
                                                    }
                                            );
                                            return;
                                        }

                                        log.debug(String.valueOf(importedStandings.size()));

                                        if (DKPUtils.getDuplicates(importedStandings).size() > 0 && !importedStandings.isEmpty()) {
                                            processingMessage.editMessage(EmbeddedUtils
                                                    .buildErrorEmbed(DKPConstants.DKP_IMPORT_DUPLICATES_TITLE,
                                                            DKPConstants.DKP_IMPORT_DUPLICATES_DESCRIPTION,
                                                            "Duplicate entries found.", null)).queue(
                                                    success -> {
                                                        success.delete().queueAfter(20, TimeUnit.SECONDS);
                                                        event.getMessage().addReaction("❌").queueAfter(20, TimeUnit.SECONDS);
                                                    }
                                            );
                                            return;
                                        } else if (importedStandings.isEmpty()) {
                                            log.debug("importedStandings empty, bad");
                                            processingMessage.editMessage(EmbeddedUtils
                                                    .buildErrorEmbed(DKPConstants.DKP_IMPORT_ERROR_TITLE, DKPConstants.DKP_IMPORT_ERROR_DESCRIPTION,
                                                            "Missing CSV data",
                                                            null))
                                                    .queue(success -> {
                                                        success.delete().queueAfter(20, TimeUnit.SECONDS);
                                                        event.getMessage().addReaction("❌").queueAfter(20, TimeUnit.SECONDS);
                                                    });
                                            return;
                                        }

                                        saveImportedStandings(importedStandings, event.getGuild().getIdLong());

                                        LootImport dkpImport = new LootImport();
                                        dkpImport.setUploader(event.getAuthor().getIdLong());
                                        dkpImport.setGuildId(event.getGuild().getIdLong());
                                        dkpImport.setImportedText(builder.toString());
                                        dkpImport.setType("dkp");
                                        LootImport savedImport = lootImportRepository.saveAndFlush(dkpImport);


                                        List<MessageEmbed.Field> fields = new ArrayList<>() {{
                                            add(new MessageEmbed.Field("Latest update",
                                                    savedImport.getModifiedDate().format(Constants.DATE_TIME_FORMATTER),
                                                    true));
                                            add(new MessageEmbed.Field("Author", String.format(Constants.MENTION_USER, savedImport.getUploader()),
                                                    true));
                                        }};
                                        processingMessage.editMessage(EmbeddedUtils
                                                .buildGenericEmbed(DKPConstants.DKP_IMPORT_TITLE, DKPConstants.DKP_IMPORT_DESCRIPTION, fields, null,
                                                        "64a266"))
                                                .queue(
                                                        success -> {
                                                            success.delete().queueAfter(20, TimeUnit.SECONDS);
                                                            event.getMessage().addReaction("✅").queueAfter(20, TimeUnit.SECONDS);
                                                        }
                                                );
                                    }
                                });
                    } else {
                        MessageEmbed error = EmbedUtils
                                .createErrorEmbed("Invalid import",
                                        "Bot is currently not set to DKP as the selected loot system.\n\nUse `" + App.client.getPrefix() +
                                                "settings` to see what the loot system is currently set to.",
                                        "Loot system not set as DKP.", "");
                        event.getChannel().sendMessage(error).queue();
                    }
                }

            } else {
                MessageEmbed error = EmbedUtils
                        .createErrorEmbed("Invalid settings", "Bot is not configured.", "Officer role or loot method not set.", "");
                event.getChannel().sendMessage(error).queue();
            }
        }
    }

    private void waitForCsv(CommandEvent event) {
        //todo message asking for csv text or file

        waiter.waitForEvent(GuildMessageReceivedEvent.class,
                e -> e.getAuthor().equals(event.getAuthor()) && e.getChannel().equals(event.getChannel()),
                e -> {
                    if (e.getMessage().getAttachments().isEmpty()) {
                        log.debug(e.getMessage().getContentRaw());
                    } else {
                        StringBuilder builder = AttachmentUtils.getAttachmentContent(e);
                    }
                }, 2, TimeUnit.MINUTES, () -> {
                    event.getMessage().getChannel().sendMessage("Sorry, you took too long.").queue();
                });
    }

    private void saveImportedStandings(List<DKPStanding> importedStandings, long guildId) {
        importedStandings.forEach(importedStanding -> {
            Optional<DKPStanding> optionalDKPStanding = dkpStandingRepository
                    .findByPlayerAndGuildId(importedStanding.getPlayer().toUpperCase(), guildId);
            if (optionalDKPStanding.isPresent()) {
                DKPStanding newDkpStanding = optionalDKPStanding.get();
                newDkpStanding.setPlayer(importedStanding.getPlayer().toUpperCase());
                newDkpStanding.setClazz(importedStanding.getClazz());
                newDkpStanding.setDkp(importedStanding.getDkp());
                newDkpStanding.setGuildId(importedStanding.getGuildId());
                newDkpStanding.setPrevious(importedStanding.getPrevious());
                newDkpStanding.setDkpChange(importedStanding.getDkp() - importedStanding.getPrevious());
                newDkpStanding.setLifetimeGained(importedStanding.getLifetimeGained());
                newDkpStanding.setLifetimeSpent(importedStanding.getLifetimeSpent());
                newDkpStanding.setGuildId(importedStanding.getGuildId());
                dkpStandingRepository.saveAndFlush(newDkpStanding);
            } else {
                importedStanding.setPlayer(importedStanding.getPlayer().toUpperCase());
                importedStanding.setDkpChange(importedStanding.getDkp() - importedStanding.getPrevious());
                importedStanding.setGuildId(guildId);
                dkpStandingRepository.saveAndFlush(importedStanding);
            }
            log.debug(importedStanding.toString());
        });
    }
}
