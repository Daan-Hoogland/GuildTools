package io.hoogland.guildtools.commands.loot.epgp;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import io.hoogland.guildtools.commands.loot.epgp.classes.*;
import io.hoogland.guildtools.constants.EPGPConstants;
import io.hoogland.guildtools.models.domain.Character;
import io.hoogland.guildtools.models.domain.EPGPStanding;
import io.hoogland.guildtools.models.repositories.CharacterRepository;
import io.hoogland.guildtools.models.repositories.EPGPStandingRepository;
import io.hoogland.guildtools.utils.BeanUtils;
import io.hoogland.guildtools.utils.EmbedUtils;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class EPGPCmd extends Command {

    private EPGPStandingRepository epgpStandingRepository = BeanUtils.getBean(EPGPStandingRepository.class);
    private CharacterRepository characterRepository = BeanUtils.getBean(CharacterRepository.class);

    public EPGPCmd() {
        this.name = "epgp";
        this.help = "informs the user of their epgp standing based on input. No input will return all linked characters.";
        this.arguments = "[none|player|class|all]";
        this.children = new Command[]{new EPGPAllCmd(), new EPGPImportCmd(), new EPGPResetCmd(), new EPGPDruidCmd(), new EPGPHunterCmd(),
                new EPGPMageCmd(), new EPGPPaladinCmd(), new EPGPPriestCmd(), new EPGPRogueCmd(), new EPGPShamanCmd(), new EPGPWarlockCmd(),
                new EPGPWarriorCmd(), new EPGPDeleteCmd()};
    }

    @Override
    protected void execute(CommandEvent event) {
        if (!event.getAuthor().isBot()) {
            String[] split = event.getArgs().split(" ");

            if (split.length >= 1 && !split[0].trim().isBlank()) {
                for (String charName : split) {
                    Optional<EPGPStanding> epgpStanding = epgpStandingRepository
                            .findByPlayerAndGuildId(charName.toUpperCase(), event.getGuild().getIdLong());
                    MessageEmbed msg;
                    if (epgpStanding.isPresent()) {
                        msg = epgpStanding.get().getMessageEmbed();
                    } else {
                        msg = EmbedUtils
                                .createErrorEmbed(null, "Unable to retrieve EPGP standings.", String.format("No EPGP entry found for `%s`",
                                        StringUtils.capitalize(charName.toLowerCase())),
                                        null);
                    }

                    sendPrivateMessage(event, msg);
                }

            } else {
                List<Character> linkedCharacters = characterRepository
                        .findAllByUserIdAndGuildId(event.getAuthor().getIdLong(), event.getGuild().getIdLong());

                if (!linkedCharacters.isEmpty()) {
                    List<EPGPStanding> standings = new ArrayList<>();
                    linkedCharacters.forEach(character -> {
                        Optional<EPGPStanding> standing = epgpStandingRepository
                                .findByPlayerAndGuildId(character.getName(), event.getGuild().getIdLong());
                        if (standing.isPresent()) {
                            standings.add(standing.get());
                        }
                    });

                    standings.forEach(standing -> {
                        sendPrivateMessage(event, standing.getMessageEmbed());
                    });
                } else {
                    sendPrivateMessage(event, EmbedUtils
                            .createErrorEmbed(null, "Unable to retrieve EPGP standings.", "No linked characters", ""));
                }
            }
            event.getMessage().delete().queue();
        }
    }

    private void sendPrivateMessage(CommandEvent event, MessageEmbed embed) {
        event.getAuthor().openPrivateChannel().queue(
                sucesss -> {
                    sucesss.sendMessage(embed).queue(
                            success -> {
                            },
                            failure -> {
                                MessageEmbed errorEmbed = EmbedUtils
                                        .createErrorEmbed(EPGPConstants.STANDINGS_TITLE_ERROR,
                                                String.format(EPGPConstants.STANDINGS_DESCRIPTION_ERROR, event.getAuthor().getId(),
                                                        event.getAuthor().getId()), failure.getMessage(), null);

                                event.getChannel().sendMessage(errorEmbed).queue(
                                        success -> {
                                            success.delete().queueAfter(20, TimeUnit.SECONDS);
                                        }
                                );
                            }
                    );
                },
                failure -> {
                    MessageEmbed errorEmbed = EmbedUtils
                            .createErrorEmbed(EPGPConstants.STANDINGS_TITLE_ERROR,
                                    String.format(EPGPConstants.STANDINGS_DESCRIPTION_ERROR, event.getAuthor().getId(),
                                            event.getAuthor().getId()), failure.getMessage(), null);

                    event.getChannel().sendMessage(errorEmbed).queue(
                            success -> {
                                success.delete().queueAfter(20, TimeUnit.SECONDS);
                            }
                    );
                }
        );
    }
}
