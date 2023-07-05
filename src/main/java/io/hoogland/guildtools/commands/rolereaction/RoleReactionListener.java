package io.hoogland.guildtools.commands.rolereaction;

import com.vdurmont.emoji.EmojiManager;
import io.hoogland.guildtools.constants.ReactionRoleConstants;
import io.hoogland.guildtools.models.ReactionRoleMessageDto;
import io.hoogland.guildtools.models.repositories.ReactionRoleMessageRepository;
import io.hoogland.guildtools.utils.BeanUtils;
import io.hoogland.guildtools.utils.EmbeddedUtils;
import io.hoogland.guildtools.utils.RoleUtils;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.react.GenericGuildMessageReactionEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Optional;

@Slf4j
public class RoleReactionListener extends ListenerAdapter {

    private ReactionRoleMessageRepository reactionRoleMessageRepository = BeanUtils.getBean(ReactionRoleMessageRepository.class);

    public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent event) {
        if (!event.getUser().isBot()) {
            String emoji = getEmojiStringFromEvent(event);
            Optional<ReactionRoleMessageDto> dto = reactionRoleMessageRepository.findByMessageIdAndEmojiId(event.getMessageIdLong(), emoji);
            if (dto.isPresent()) {

                boolean canInteract = RoleUtils.canInteractWithRole(event.getGuild().getSelfMember().getRoles(),
                        event.getGuild().getRoleById(dto.get().getRoleId()));


                log.trace("Reaction added");
                boolean hasRole = false;
                for (Role role : event.getMember().getRoles()) {
                    if (role.getIdLong() == dto.get().getRoleId()) {
                        log.error("user already has role.");
                        hasRole = true;
                    }
                }
                if (hasRole && canInteract && dto.get().getType() == 2) {
                    log.trace("has role");
                    event.getGuild().removeRoleFromMember(event.getMember(), event.getGuild().getRoleById(dto.get().getRoleId())).queue(
                            success -> {
                            },
                            failure -> {
                                log.error(failure.getMessage());
                                event.getJDA().getUserById(dto.get().getCreatorId()).openPrivateChannel().queue(
                                        success -> {
                                            MessageEmbed embed = EmbeddedUtils.buildErrorEmbed(ReactionRoleConstants.ERROR_REMOVE_ROLE_TITLE,
                                                    String.format(ReactionRoleConstants.ERROR_REMOVE_ROLE_DESCRIPTION, dto.get().getRoleId(),
                                                            dto.get().getDirectLink()), failure.getMessage(), null);
                                            success.sendMessage(embed).queue();
                                        },
                                        fail -> {
                                            log.error("Unable to open private message channel RoleReactionListener.class: " + fail.getMessage());
                                        }
                                );
                            }
                    );
                } else if (canInteract) {
                    log.trace("doesnt have role");
                    event.getGuild().addRoleToMember(event.getMember(), event.getGuild().getRoleById(dto.get().getRoleId())).queue(
                            success -> {
                                log.trace("added role");
                            },
                            failure -> {
                                log.error(failure.getMessage());
                                event.getJDA().getUserById(dto.get().getCreatorId()).openPrivateChannel().queue(
                                        success -> {
                                            MessageEmbed embed = EmbeddedUtils.buildErrorEmbed(ReactionRoleConstants.ERROR_ASSIGN_ROLE_TITLE,
                                                    String.format(ReactionRoleConstants.ERROR_ASSIGN_ROLE_DESCRIPTION, dto.get().getRoleId(),
                                                            dto.get().getDirectLink()), failure.getMessage(), null);
                                            success.sendMessage(embed).queue();
                                        },
                                        fail -> {
                                            log.error(fail.getMessage());
                                        }
                                );
                            });
                } else {
                    //cannot assign role
                    event.getJDA().getUserById(dto.get().getCreatorId()).openPrivateChannel().queue(
                            success -> {
                                MessageEmbed embed = EmbeddedUtils.buildErrorEmbed(ReactionRoleConstants.ERROR_ASSIGN_ROLE_TITLE,
                                        String.format(ReactionRoleConstants.ERROR_ASSIGN_ROLE_DESCRIPTION, dto.get().getRoleId(),
                                                dto.get().getDirectLink()), "Insufficient permissions (`MANAGE_ROLES` or role hierarchy)", null);
                                success.sendMessage(embed).queue();
                            },
                            fail -> {
                                log.error(fail.getMessage());
                            }
                    );
                }
                if (dto.get().getType() == 2) {
                    event.getReaction().removeReaction(event.getUser()).queue();
                }
            }
        }
    }

    public void onGuildMessageReactionRemove(GuildMessageReactionRemoveEvent event) {
        if (!event.getUser().isBot()) {
            String emoji = getEmojiStringFromEvent(event);
            Optional<ReactionRoleMessageDto> dto = reactionRoleMessageRepository.findByMessageIdAndEmojiId(event.getMessageIdLong(), emoji);
            if (dto.isPresent() && dto.get().getType() == 1) {
                log.trace("Reaction removed");
                boolean canInteract = RoleUtils.canInteractWithRole(event.getGuild().getSelfMember().getRoles(),
                        event.getGuild().getRoleById(dto.get().getRoleId()));
                if (canInteract) {
                    event.getGuild().removeRoleFromMember(event.getMember(), event.getGuild().getRoleById(dto.get().getRoleId())).queue(
                            success -> {
                            },
                            failure -> {
                                log.error(failure.getMessage());
                                event.getJDA().getUserById(dto.get().getCreatorId()).openPrivateChannel().queue(
                                        success -> {
                                            MessageEmbed embed = EmbeddedUtils.buildErrorEmbed(ReactionRoleConstants.ERROR_REMOVE_ROLE_TITLE,
                                                    String.format(ReactionRoleConstants.ERROR_REMOVE_ROLE_DESCRIPTION, dto.get().getRoleId(),
                                                            dto.get().getDirectLink()), failure.getMessage(), null);
                                            success.sendMessage(embed).queue();
                                        },
                                        fail -> {
                                            log.error("Unable to open private message channel RoleReactionListener.class: " + fail.getMessage());
                                        }
                                );
                            }
                    );
                }
            }
        }
    }

    private String getEmojiStringFromEvent(GenericGuildMessageReactionEvent event) {
        return EmojiManager.isEmoji(event.getReactionEmote().getName()) ? event.getReactionEmote().getName() :
                String.format(ReactionRoleConstants.MENTION_EMOJI, event.getReactionEmote().getEmote().getName(),
                        event.getReactionEmote().getEmote().getId());
    }
}
