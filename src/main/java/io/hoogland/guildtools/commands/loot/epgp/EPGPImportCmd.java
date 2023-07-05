package io.hoogland.guildtools.commands.loot.epgp;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.opencsv.CSVReader;
import com.opencsv.bean.CsvToBeanBuilder;
import io.hoogland.guildtools.App;
import io.hoogland.guildtools.constants.Constants;
import io.hoogland.guildtools.constants.DKPConstants;
import io.hoogland.guildtools.constants.EmojiConstants;
import io.hoogland.guildtools.models.domain.EPGPStanding;
import io.hoogland.guildtools.models.domain.GuildSettings;
import io.hoogland.guildtools.models.domain.LootImport;
import io.hoogland.guildtools.models.repositories.EPGPStandingRepository;
import io.hoogland.guildtools.models.repositories.GuildSettingsRepository;
import io.hoogland.guildtools.models.repositories.LootImportRepository;
import io.hoogland.guildtools.utils.*;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.io.StringReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
public class EPGPImportCmd extends Command {

    private LootImportRepository lootImportRepository = BeanUtils.getBean(LootImportRepository.class);
    private EPGPStandingRepository epgpStandingRepository = BeanUtils.getBean(EPGPStandingRepository.class);
    private GuildSettingsRepository guildSettingsRepository = BeanUtils.getBean(GuildSettingsRepository.class);

    public EPGPImportCmd() {
        this.name = "import";
        this.help = "imports EPGP standings from a csv file.";
    }

    @Override
    protected void execute(CommandEvent event) {
        if (!event.getAuthor().isBot()) {
            Optional<GuildSettings> optionalSettings = guildSettingsRepository.findByGuildId(event.getGuild().getIdLong());
            if (optionalSettings.isPresent()) {
                if (RoleUtils.hasRoleWithId(optionalSettings.get().getAdminRoleId(), event.getMember().getRoles()) ||
                        event.getGuild().getOwnerIdLong() == event.getMember().getIdLong()) {
                    if (optionalSettings.get().isEpgp()) {
                        event.getChannel().sendMessage(EmbedUtils.createEmbed("Processing data", EmojiConstants.EMOJI_LOADING, null))
                                .queue(processingMessage -> {
                                    StringBuilder builder = new StringBuilder();

                                    builder = AttachmentUtils.getAttachmentContent(event);

                                    List<EPGPStanding> importedStandings;
                                    try {
                                        CSVReader reader = new CSVReader(new StringReader(builder.toString()));

                                        importedStandings = new CsvToBeanBuilder(reader)
                                                .withType(EPGPStanding.class).build().parse();
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

                                    if (EPGPUtils.getDuplicates(importedStandings).size() > 0 && !importedStandings.isEmpty()) {
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

                                    LootImport epgpImport = new LootImport();
                                    epgpImport.setUploader(event.getAuthor().getIdLong());
                                    epgpImport.setGuildId(event.getGuild().getIdLong());
                                    epgpImport.setImportedText(builder.toString());
                                    epgpImport.setType("epgp");
                                    LootImport savedImport = lootImportRepository.saveAndFlush(epgpImport);


                                    List<MessageEmbed.Field> fields = new ArrayList<>() {{
                                        add(new MessageEmbed.Field("Latest update",
                                                savedImport.getModifiedDate().format(Constants.DATE_TIME_FORMATTER),
                                                true));
                                        add(new MessageEmbed.Field("Author", String.format(Constants.MENTION_USER, savedImport.getUploader()), true));
                                    }};
                                    processingMessage.editMessage(EmbeddedUtils
                                            .buildGenericEmbed("Imported EPGP values",
                                                    "EPGP values have been imported. New values are now available through the EPGP commands.", fields,
                                                    null,
                                                    "64a266")).queue(
                                            success -> {
                                                success.delete().queueAfter(20, TimeUnit.SECONDS);
                                                event.getMessage().addReaction("✅").queueAfter(20, TimeUnit.SECONDS);
                                            }
                                    );
                                });
                    } else {
                        MessageEmbed error = EmbedUtils
                                .createErrorEmbed("Invalid import",
                                        "Bot is currently not set to EPGP as the selected loot system.\n\nUse `" + App.client.getPrefix() +
                                                "settings` to see what the loot system is currently set to.",
                                        "Loot system not set as EPGP.", "");
                        event.getChannel().sendMessage(error).queue();
                    }
                }

            } else {
                MessageEmbed error = EmbedUtils.createErrorEmbed("Invalid settings", "Bot is not configured", "Officer role not set.", "");
                event.getChannel().sendMessage(error).queue();
            }
        }
    }

    private void saveImportedStandings(List<EPGPStanding> importedStandings, long guildId) {
        List<EPGPStanding> toBeRemoved = new ArrayList<>();
        for (EPGPStanding importedStanding : importedStandings) {
            if (!importedStanding.getGuildRank().equalsIgnoreCase("social") && importedStanding.getEp() != 0) {
                Optional<EPGPStanding> optionalEPGPStanding = epgpStandingRepository
                        .findByPlayerAndGuildId(importedStanding.getPlayer().toUpperCase(), guildId);

                if (optionalEPGPStanding.isPresent()) {
                    EPGPStanding newEPGPStanding = optionalEPGPStanding.get();
                    newEPGPStanding.setPreviousEp(newEPGPStanding.getEp());
                    newEPGPStanding.setPreviousGp(newEPGPStanding.getGp());
                    newEPGPStanding.setPreviousPr(newEPGPStanding.getPr() == null ? BigDecimal.ZERO : newEPGPStanding.getPr());

                    newEPGPStanding.setEp(importedStanding.getEp());
                    newEPGPStanding.setGp(importedStanding.getGp());
                    newEPGPStanding.setPr(importedStanding.getPr());
                    newEPGPStanding.setGuildRank(importedStanding.getGuildRank());

                    epgpStandingRepository.saveAndFlush(newEPGPStanding);
                } else {
                    importedStanding.setPlayer(importedStanding.getPlayer().toUpperCase());
                    importedStanding.setClazz(importedStanding.getClazz().toUpperCase());
                    importedStanding.setGuildId(guildId);
                    importedStanding.setPreviousPr(BigDecimal.ZERO);
                    importedStanding.setPreviousEp(0);
                    importedStanding.setPreviousGp(0);
                    epgpStandingRepository.saveAndFlush(importedStanding);
                }
            }
        }
    }
}
