package io.hoogland.guildtools.commands.application;

import io.hoogland.guildtools.constants.Constants;
import io.hoogland.guildtools.models.domain.RoleApplication;
import io.hoogland.guildtools.models.repositories.RoleApplicationRepository;
import io.hoogland.guildtools.utils.BeanUtils;
import io.hoogland.guildtools.utils.EmbedUtils;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
public class ApplyListener extends ListenerAdapter {

    private RoleApplicationRepository roleApplicationRepository = BeanUtils.getBean(RoleApplicationRepository.class);

    public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent event) {
        if (!event.getMember().getUser().isBot()) {
            if (event.getReactionEmote().isEmoji() &&
                    (event.getReactionEmote().getEmoji().equals("✅") || event.getReactionEmote().getEmoji().equals("❌"))) {
                Optional<RoleApplication> optionalRoleApplication = roleApplicationRepository
                        .findByMessageIdAndGuildId(event.getMessageIdLong(), event.getGuild().getIdLong());

                if (optionalRoleApplication.isPresent()) {
                    List<Role> assignedRoles = new ArrayList<>();
                    optionalRoleApplication.get().getRoles().forEach(role -> {
                        assignedRoles.add(event.getGuild().getRoleById(role));
                    });
                    Member member = event.getGuild().getMemberById(optionalRoleApplication.get().getUserId());
                    if (event.getReactionEmote().getEmoji().equals("✅")) {
                        List<String> confirmedList = new ArrayList<>();

                        for (Role assignedRole : assignedRoles) {
                            event.getGuild().addRoleToMember(member, assignedRole).complete();
                            confirmedList.add("**" + assignedRole.getName() + "**");
                        }

                        MessageEmbed privateMsg = EmbedUtils.createEmbed("Application approved",
                                String.format("Application approved on server %s.\n\nThe following roles have been granted: %s",
                                        event.getGuild().getName(), String.join(", ", confirmedList)), null, Constants.COLOR_OK,
                                String.format("Approved on %s at %s",
                                        LocalDateTime.now().format(Constants.DATE_TIME_FORMATTER_DATE),
                                        LocalDateTime.now().format(Constants.DATE_TIME_FORMATTER_TIME)), null, event.getGuild().getIconUrl());

                        member.getUser().openPrivateChannel().queue(
                                success -> {
                                    success.sendMessage(privateMsg).queue();
                                });

                        event.getChannel().retrieveMessageById(event.getMessageIdLong()).queue(
                                success -> {
                                    success.removeReaction(event.getReactionEmote().getEmoji(), event.getUser()).queue();
                                    success.removeReaction("✅").queue();
                                    success.removeReaction("❌").queue();

                                    StringBuilder descriptionBuilder = new StringBuilder();
                                    descriptionBuilder
                                            .append("The user " + String.format(Constants.MENTION_USER, optionalRoleApplication.get().getUserId()) +
                                                    " has been assigned the following role(s): ");
                                    assignedRoles.forEach(requestedRole -> {
                                        descriptionBuilder.append(String.format(Constants.MENTION_ROLE, requestedRole.getIdLong()) + "\n");
                                    });
                                    descriptionBuilder.append("\n\nRequest handled by: " +
                                            String.format(Constants.MENTION_USER, event.getMember().getIdLong()));
                                    MessageEmbed editedMsg = EmbedUtils.createEmbed("Role Request | Approved", descriptionBuilder.toString(), null);
                                    success.editMessage(editedMsg).queue();

                                }
                        );
                        roleApplicationRepository.delete(optionalRoleApplication.get());

                    } else if (event.getReactionEmote().getEmoji().equals("❌")) {
                        List<String> deniedList = new ArrayList<>();

                        for (Role assignedRole : assignedRoles) {
                            deniedList.add("**" + assignedRole.getName() + "**");
                        }

                        MessageEmbed deniedMsg = EmbedUtils
                                .createEmbed("Application denied",
                                        String.format("Application denied on server %s.\n\nThe following roles were part of your request: %s",
                                                event.getGuild().getName(), String.join(", ", deniedList)), null,
                                        Constants.COLOR_NOT_OK, String.format("Approved on %s at %s",
                                                LocalDateTime.now().format(Constants.DATE_TIME_FORMATTER_DATE),
                                                LocalDateTime.now().format(Constants.DATE_TIME_FORMATTER_TIME)), null, event.getGuild().getIconUrl());

                        member.getUser().openPrivateChannel().queue(
                                success -> {
                                    success.sendMessage(deniedMsg).queue();
                                });

                        event.getChannel().retrieveMessageById(event.getMessageIdLong()).queue(
                                success -> {
                                    success.removeReaction(event.getReactionEmote().getEmoji(), event.getUser()).queue();
                                    success.removeReaction("✅").queue();
                                    success.removeReaction("❌").queue();
                                    StringBuilder descriptionBuilder = new StringBuilder();
                                    descriptionBuilder
                                            .append("The user " + String.format(Constants.MENTION_USER, optionalRoleApplication.get().getUserId()) +
                                                    " has been denied the following role(s):\n\nRequest handled by: " +
                                                    String.format(Constants.MENTION_USER, event.getMember().getIdLong()));
                                    assignedRoles.forEach(requestedRole -> {
                                        descriptionBuilder.append(String.format(Constants.MENTION_ROLE, requestedRole.getIdLong()) + "\n");
                                    });
                                    MessageEmbed editedMsg = EmbedUtils.createEmbed("Role Request | Denied", descriptionBuilder.toString(), null);
                                    success.editMessage(editedMsg).queue();
                                }
                        );
                        roleApplicationRepository.delete(optionalRoleApplication.get());
                    }
                }
            }
        }
    }
}
