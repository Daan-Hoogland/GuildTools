package io.hoogland.guildtools.commands.application;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import io.hoogland.guildtools.constants.Constants;
import io.hoogland.guildtools.models.domain.GuildSettings;
import io.hoogland.guildtools.models.domain.RoleApplication;
import io.hoogland.guildtools.models.repositories.GuildSettingsRepository;
import io.hoogland.guildtools.models.repositories.RoleApplicationRepository;
import io.hoogland.guildtools.utils.BeanUtils;
import io.hoogland.guildtools.utils.EmbedUtils;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ApplyCmd extends Command {

    private GuildSettingsRepository guildSettingsRepository = BeanUtils.getBean(GuildSettingsRepository.class);
    private RoleApplicationRepository roleApplicationRepository = BeanUtils.getBean(RoleApplicationRepository.class);

    public ApplyCmd() {
        this.name = "apply";
        this.aliases = new String[]{"apply"};
        this.help = "sends a rank request to the officers of the Discord.";
    }

    @Override
    protected void execute(CommandEvent event) {
        if (!event.getMember().getUser().isBot()) {
            Optional<RoleApplication> optionalRoleApplication = roleApplicationRepository
                    .findByUserIdAndGuildId(event.getAuthor().getIdLong(), event.getGuild().getIdLong());
            if (optionalRoleApplication.isPresent()) {
                MessageEmbed errorMsg = EmbedUtils
                        .createErrorEmbed("Application pending", "There is already an application pending for user " +
                                String.format(Constants.MENTION_USER, event.getAuthor().getIdLong()) + ".", "Application pending", "");
                event.getChannel().sendMessage(errorMsg).queue(success -> {
                    success.delete().queueAfter(20, TimeUnit.SECONDS);
                    event.getMessage().addReaction("❌").queueAfter(20, TimeUnit.SECONDS);
                });
                return;
            }

            List<Role> requestedRoles = event.getGuild().getRolesByName(event.getArgs(), true);
            if (requestedRoles.isEmpty()) {
                MessageEmbed errorMsg = EmbedUtils
                        .createErrorEmbed("Invalid roles", "No matching roles found", "Invalid roles", "");
                event.getChannel().sendMessage(errorMsg).queue(success -> {
                    success.delete().queueAfter(20, TimeUnit.SECONDS);
                    event.getMessage().addReaction("❌").queueAfter(20, TimeUnit.SECONDS);
                });
                return;
            }

            boolean alreadyAssigned = false;
            for (Role requestedRole : requestedRoles) {
                if (event.getMember().getRoles().contains(requestedRole)) {
                    alreadyAssigned = true;
                }
            }
            Optional<GuildSettings> optionalSettings = guildSettingsRepository.findByGuildId(event.getGuild().getIdLong());
            if (!optionalSettings.isPresent() || (optionalSettings.isPresent() && optionalSettings.get().getApplicationChannelId() == null)) {
                MessageEmbed error = EmbedUtils.createErrorEmbed("Invalid configuration", "", "Missing application channel", "");
                event.getChannel().sendMessage(error).queue(success -> {
                    success.delete().queueAfter(20, TimeUnit.SECONDS);
                    event.getMessage().addReaction("❌").queueAfter(20, TimeUnit.SECONDS);
                });
                return;
            }

            if (alreadyAssigned) {
                event.getAuthor().openPrivateChannel().queue(opened -> {
                    MessageEmbed errorMsg = EmbedUtils
                            .createErrorEmbed("Duplicate role", "You applied to a role you are already assigned.", "Role already assigned", "");
                    opened.sendMessage(errorMsg).queue();
                });
            } else {
                StringBuilder descriptionBuilder = new StringBuilder();
                descriptionBuilder.append("The user " + String.format(Constants.MENTION_USER, event.getMember().getIdLong()) +
                        " has requested the following role(s):\n");
                requestedRoles.forEach(requestedRole -> {
                    descriptionBuilder.append(String.format(Constants.MENTION_ROLE, requestedRole.getIdLong()) + "\n");
                });
                descriptionBuilder.append("\nTo accept this request, react with the ✅ emoji. To decline, react with ❌.");
                MessageEmbed msg = EmbedUtils.createEmbed("Role Request", descriptionBuilder.toString(), null);

                event.getGuild().getTextChannelById(optionalSettings.get().getApplicationChannelId()).sendMessage(msg).queue(
                        success -> {
                            RoleApplication roleApplication = new RoleApplication();
                            roleApplication.setGuildId(event.getGuild().getIdLong());
                            roleApplication.setMessageId(success.getIdLong());
                            roleApplication.setUserId(event.getAuthor().getIdLong());
                            roleApplication.setRoles(new ArrayList<>());
                            requestedRoles.forEach(role -> {
                                roleApplication.getRoles().add(role.getId());
                            });

                            roleApplicationRepository.saveAndFlush(roleApplication);
                            success.addReaction("✅").queue();
                            success.addReaction("❌").queue();
                        }
                );
            }
            event.getMessage().delete().queue();
        }
    }
}