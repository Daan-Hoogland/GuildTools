package io.hoogland.guildtools.commands.loot.dkp;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import io.hoogland.guildtools.commands.loot.dkp.classes.*;
import io.hoogland.guildtools.constants.DKPConstants;
import io.hoogland.guildtools.models.domain.Character;
import io.hoogland.guildtools.models.domain.DKPStanding;
import io.hoogland.guildtools.models.repositories.CharacterRepository;
import io.hoogland.guildtools.models.repositories.DKPStandingRepository;
import io.hoogland.guildtools.utils.BeanUtils;
import io.hoogland.guildtools.utils.DKPUtils;
import io.hoogland.guildtools.utils.EmbedUtils;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
public class DKPCmd extends Command {

    private EventWaiter waiter;
    private DKPStandingRepository dkpStandingRepository = BeanUtils.getBean(DKPStandingRepository.class);
    private CharacterRepository characterRepository = BeanUtils.getBean(CharacterRepository.class);

    public DKPCmd(EventWaiter waiter) {
        this.name = "dkp";
        this.help = "informs the user of their dkp standing based on input. No input will return all linked characters.";
        this.arguments = "[none|player|class|all]";
        this.waiter = waiter;
        this.children = new Command[]{new DKPAllCmd(), new DKPImportCmd(waiter),
                new DKPWarriorCmd(), new DKPRogueCmd(), new DKPDruidCmd(), new DKPHunterCmd(),
                new DKPMageCmd(), new DKPPriestCmd(), new DKPPaladinCmd(), new DKPShamanCmd(), new DKPWarlockCmd(), new DKPResetCmd(), new DKPDeleteCmd()};
    }

    @Override
    protected void execute(CommandEvent event) {
        if (!event.getAuthor().isBot()) {
            String[] split = event.getMessage().getContentRaw().split(" ");

            if (split.length > 1 && split[1] != null) {
                Optional<DKPStanding> dkpStanding = dkpStandingRepository
                        .findByPlayerAndGuildId(split[1].toUpperCase(), event.getGuild().getIdLong());
                MessageEmbed msg;
                if (dkpStanding.isPresent()) {
                    msg = DKPUtils.createDkpEmbed(dkpStanding.get());
                } else {
                    msg = EmbedUtils
                            .createErrorEmbed(null, "Unable to retrieve DKP standings.", String.format("No DKP entry found for `%s`", split[1]),
                                    null);
                }

                sendPrivateMessage(event, msg);
            } else {
                List<Character> linkedCharacters = characterRepository
                        .findAllByUserIdAndGuildId(event.getAuthor().getIdLong(), event.getGuild().getIdLong());

                if (!linkedCharacters.isEmpty()) {
                    List<DKPStanding> standings = new ArrayList<>();
                    linkedCharacters.forEach(character -> {
                        Optional<DKPStanding> standing = dkpStandingRepository
                                .findByPlayerAndGuildId(character.getName(), event.getGuild().getIdLong());
                        if (standing.isPresent()) {
                            standings.add(standing.get());
                        }
                    });

                    standings.forEach(standing -> {
                        sendPrivateMessage(event, DKPUtils.createDkpEmbed(standing));
                    });
                } else {
                    sendPrivateMessage(event, EmbedUtils
                            .createErrorEmbed(null, "Unable to retrieve DKP standings.", "No linked characters", ""));
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
                                        .createErrorEmbed(DKPConstants.DKP_SENDING_ERROR_TITLE,
                                                String.format(DKPConstants.DKP_SENDING_ERROR_DESCRIPTION, event.getAuthor().getId(),
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
                            .createErrorEmbed(DKPConstants.DKP_SENDING_ERROR_TITLE,
                                    String.format(DKPConstants.DKP_SENDING_ERROR_DESCRIPTION, event.getAuthor().getId(),
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
