package io.hoogland.guildtools.commands.rolereaction;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.sun.istack.NotNull;
import com.vdurmont.emoji.EmojiParser;
import io.hoogland.guildtools.constants.Constants;
import io.hoogland.guildtools.constants.ReactionRoleConstants;
import io.hoogland.guildtools.models.domain.ReactionRole;
import io.hoogland.guildtools.models.domain.ReactionRoleMessage;
import io.hoogland.guildtools.models.repositories.ReactionRoleMessageRepository;
import io.hoogland.guildtools.models.repositories.ReactionRoleRepository;
import io.hoogland.guildtools.utils.BeanUtils;
import io.hoogland.guildtools.utils.EmbeddedUtils;
import io.hoogland.guildtools.utils.RoleUtils;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
public class RoleReactionCmd extends Command {

    private ReactionRoleRepository reactionRoleRepository = BeanUtils.getBean(ReactionRoleRepository.class);
    private ReactionRoleMessageRepository reactionRoleMessageRepository = BeanUtils.getBean(ReactionRoleMessageRepository.class);

    private EventWaiter waiter;

    public RoleReactionCmd(EventWaiter waiter) {
        this.name = "addreaction";
        this.aliases = new String[]{"addreaction", "addreact", "reactionrole"};
        this.help = "allows the bot to assign roles based on reactions to a message.";
        this.waiter = waiter;
    }

    @Override
    protected void execute(CommandEvent event) {
        if (event.getMember().hasPermission(Permission.MANAGE_ROLES)) {
            if (event.getSelfMember().hasPermission(Permission.MANAGE_ROLES) &&
                    event.getSelfMember().hasPermission(Permission.MESSAGE_ADD_REACTION)) {
                MessageEmbed embed = EmbeddedUtils
                        .buildReactionRoleEmbed(ReactionRoleConstants.ADD_REACT_TITLE_1, ReactionRoleConstants.ADD_REACT_DESCRIPTION_CHANNEL, null,
                                null);

                event.getMessage().delete().queue();

                Message message = event.getChannel().sendMessage(embed).complete();
                ReactionRole role = new ReactionRole(event.getGuild().getIdLong());

                waitForChannelId(event, message, role);
            } else {
                MessageEmbed embed = EmbeddedUtils.buildReactionRoleEmbed(ReactionRoleConstants.ADD_REACT_INSUFFICIENT_PERMISSIONS_TITLE,
                        ReactionRoleConstants.ADD_REACT_DESCRIPTION__INSUFFICIENT_PERMISSIONS, null, "Insufficient Permissions");
                execute(event);
            }
        }
    }

    private void cancelCommand(@NotNull Message userMessage, @NotNull Message embeddedMessage) {
        MessageEmbed embed = EmbeddedUtils.buildReactionRoleEmbed(ReactionRoleConstants.ADD_REACT_TITLE_CANCEL,
                null, null, ReactionRoleConstants.CANCEL_ERROR, "Cancelled at " + LocalDateTime.now().format(Constants.DATE_TIME_FORMATTER_PRECISE));
        embeddedMessage.editMessage(embed).queue();
        userMessage.delete().queue();
    }

    private void waitForChannelId(CommandEvent event, Message message, ReactionRole role) {
        waiter.waitForEvent(GuildMessageReceivedEvent.class,
                e -> e.getAuthor().equals(event.getAuthor()) && e.getChannel().equals(event.getChannel()),
                e -> {
                    if (e.getMessage().getContentRaw().equalsIgnoreCase(ReactionRoleConstants.CANCEL_COMMAND)) {
                        cancelCommand(e.getMessage(), message);
                    } else {
                        if (!e.getMessage().getMentionedChannels().isEmpty()) {
                            role.getReactionRoleMessage().setChannelId(e.getMessage().getMentionedChannels().get(0).getIdLong());
                            MessageEmbed embed = EmbeddedUtils.buildReactionRoleEmbed(ReactionRoleConstants.ADD_REACT_TITLE_2,
                                    ReactionRoleConstants.ADD_REACT_DESCRIPTION_MESSAGE, role, null);
                            message.editMessage(embed).queue();
                            e.getMessage().delete().queue();

                            waitForMessageId(event, message, role);
                        } else {
                            MessageEmbed embed = EmbeddedUtils.buildReactionRoleEmbed(ReactionRoleConstants.ADD_REACT_TITLE_1,
                                    ReactionRoleConstants.ADD_REACT_DESCRIPTION_CHANNEL, null, "No channel found in your response.");
                            message.editMessage(embed).queue();
                            e.getMessage().delete().queue();
                            waitForChannelId(event, message, role);
                        }
                    }
                },
                // if the user takes more than a minute, time out
                2, TimeUnit.MINUTES, () -> {
                    event.getMessage().getChannel().sendMessage("Sorry, you took too long.").queue();
                });
    }

    private void waitForMessageId(CommandEvent event, Message message, ReactionRole role) {
        waiter.waitForEvent(GuildMessageReceivedEvent.class,
                e -> e.getAuthor().equals(event.getAuthor()) && e.getChannel().equals(event.getChannel()),
                e -> {
                    if (e.getMessage().getContentRaw().equalsIgnoreCase(ReactionRoleConstants.CANCEL_COMMAND)) {
                        cancelCommand(e.getMessage(), message);
                    } else {
                        try {
                            long messageId = Long.parseLong(e.getMessage().getContentRaw());
                            event.getGuild().getTextChannelById(role.getReactionRoleMessage().getChannelId())
                                    .retrieveMessageById(Long.parseLong(e.getMessage().getContentRaw())).queue(
                                    success -> {
                                        long msgId = Long.parseLong(e.getMessage().getContentRaw());

                                        Optional<ReactionRoleMessage> optionalReactionRoleMessage = reactionRoleMessageRepository
                                                .findDistinctByMessageIdAndChannelIdAndGuildId(msgId, role.getReactionRoleMessage().getChannelId(),
                                                        message.getGuild().getIdLong());
                                        if (optionalReactionRoleMessage.isPresent()) {
                                            log.debug("using present object");
                                            role.setReactionRoleMessage(optionalReactionRoleMessage.get());
                                        } else {
                                            log.debug("continueing with new object");
                                            role.getReactionRoleMessage().setMessageId(msgId);
                                            role.getReactionRoleMessage().setDirectLink(success.getJumpUrl());
                                            role.setReactionRoleMessage(reactionRoleMessageRepository.save(role.getReactionRoleMessage()));
                                        }

                                        MessageEmbed embed = EmbeddedUtils.buildReactionRoleEmbed(ReactionRoleConstants.ADD_REACT_TITLE_3,
                                                ReactionRoleConstants.ADD_REACT_DESCRIPTION_EMOJI, role, null);
                                        message.editMessage(embed).queue();
                                        e.getMessage().delete().queue();
                                        waitForEmoji(event, message, role);
                                    },
                                    failure -> {
                                        MessageEmbed embed = EmbeddedUtils.buildReactionRoleEmbed(ReactionRoleConstants.ADD_REACT_TITLE_1,
                                                ReactionRoleConstants.ADD_REACT_DESCRIPTION_CHANNEL, null,
                                                String.format(ReactionRoleConstants.MESSAGE_ERROR, failure.getMessage()));
                                        message.editMessage(embed).queue();
                                        e.getMessage().delete().queue();
                                        waitForMessageId(event, message, role);
                                    });
                        } catch (NumberFormatException exception) {
                            MessageEmbed embed = EmbeddedUtils.buildReactionRoleEmbed(ReactionRoleConstants.ADD_REACT_TITLE_1,
                                    ReactionRoleConstants.ADD_REACT_DESCRIPTION_CHANNEL, null,
                                    String.format(ReactionRoleConstants.MESSAGE_ERROR, exception.getMessage()));
                            message.editMessage(embed).queue();
                            e.getMessage().delete().queue();
                            waitForMessageId(event, message, role);
                        }
                    }

                }, 2, TimeUnit.MINUTES, () -> {
                    event.getMessage().getChannel().sendMessage("Sorry, you took too long.").queue();
                });
    }

    private void waitForEmoji(CommandEvent event, Message message, ReactionRole role) {
        waiter.waitForEvent(GuildMessageReceivedEvent.class,
                e -> e.getAuthor().equals(event.getAuthor()) && e.getChannel().equals(event.getChannel()),
                e -> {
                    if (e.getMessage().getContentRaw().equalsIgnoreCase(ReactionRoleConstants.CANCEL_COMMAND)) {
                        cancelCommand(e.getMessage(), message);
                    } else {
                        //todo only checks for discord emojis, not regular ones.
                        List<Emote> discordEmoteList = e.getMessage().getEmotes();
                        List<String> regularEmoteList = EmojiParser.extractEmojis(e.getMessage().getContentRaw());

                        if (discordEmoteList.size() + regularEmoteList.size() != 1) {
                            //no emoji or too many
                            MessageEmbed embed = EmbeddedUtils.buildReactionRoleEmbed(ReactionRoleConstants.ADD_REACT_TITLE_3,
                                    ReactionRoleConstants.ADD_REACT_DESCRIPTION_EMOJI, null,
                                    "Either your message didn't contain any emoji's, or it contained more than one. Please respond with only one emoji.");
                            message.editMessage(embed).queue();
                            e.getMessage().delete().queue();
                            waitForEmoji(event, message, role);
//                        } else if (!discordEmoteList.isEmpty() && discordEmoteList.get(0).isFake()) {
//                            //if the emoji isn't usable by the bot
//                            //todo let use as long as user adds it himself first.
//
//                            System.out.println(discordEmoteList.get(0).getName());
//                            role.setEmojiId(discordEmoteList.get(0).getName());
//
//                            MessageEmbed embed = EmbeddedUtils.buildReactionRoleEmbed(EmbedConstants.ADD_REACT_TITLE_3,
//                                    EmbedConstants.ADD_REACT_DESCRIPTION_EMOJI, null, "The bot does not have access to that specific emoji. Please use one that the bot has access to, or make sure that the bot has access to the mentioned emoji.");
//                            message.editMessage(embed).queue();
//                            e.getMessage().delete().queue();
//                            waitForEmoji(event, message, role);
                        } else {
                            String emoji = discordEmoteList.isEmpty() ?
                                    regularEmoteList.get(0) : String
                                    .format(ReactionRoleConstants.MENTION_EMOJI, discordEmoteList.get(0).getName(), discordEmoteList.get(0).getId());

                            if (!role.getReactionRoleMessage().getRoles().isEmpty()) {
                                boolean inUse = false;
                                ReactionRole duplicateReactionRole = null;
                                for (ReactionRole reactionRole : role.getReactionRoleMessage().getRoles()) {
                                    if (reactionRole.getEmojiId().equals(emoji)) {
                                        inUse = true;
                                        duplicateReactionRole = reactionRole;
                                        break;
                                    }
                                }
                                if (inUse) {
                                    log.warn("emoji already in use for that message");

                                    //todo: carry on to edit, cancel! to cancel all
                                    MessageEmbed embed = EmbeddedUtils.buildReactionRoleEmbed(ReactionRoleConstants.ADD_REACT_TITLE_3_EDIT,
                                            ReactionRoleConstants.ADD_REACT_EMOJI_DUPLICATE, role, null);
                                    message.editMessage(embed).queue();

                                    e.getMessage().delete().queue();
                                    waitForEditConfirmation(event, message, role, duplicateReactionRole);

                                    return;
                                }
                            }
                            role.setEmojiId(emoji);

                            MessageEmbed embed = EmbeddedUtils.buildReactionRoleEmbed(ReactionRoleConstants.ADD_REACT_TITLE_4,
                                    ReactionRoleConstants.ADD_REACT_DESCRIPTION_ROLE, role, null);
                            message.editMessage(embed).queue();

                            e.getMessage().delete().queue();
                            waitForRole(event, message, role);

                        }

                    }
                }, 2, TimeUnit.MINUTES, () -> {
                    event.getMessage().getChannel().sendMessage("Sorry, you took too long.").queue();
                });
    }

    //todo add warning for role that already exists on another emoji
    private void waitForRole(CommandEvent event, Message message, ReactionRole role) {
        waiter.waitForEvent(GuildMessageReceivedEvent.class,
                e -> e.getAuthor().equals(event.getAuthor()) && e.getChannel().equals(event.getChannel()),
                e -> {
                    if (e.getMessage().getContentRaw().equalsIgnoreCase(ReactionRoleConstants.CANCEL_COMMAND)) {
                        cancelCommand(e.getMessage(), message);
                    } else {
                        if (e.getMessage().getMentionedRoles().size() != 1) {
                            MessageEmbed embed = EmbeddedUtils.buildReactionRoleEmbed(ReactionRoleConstants.ADD_REACT_TITLE_4,
                                    ReactionRoleConstants.ADD_REACT_DESCRIPTION_ROLE, null,
                                    "Either no roles are mentioned in your message or you mentioned multiple. Please mention only one role. If you want to set up multiple roles for the same emoji, run this setup again.");
                            message.editMessage(embed).queue();
                            e.getMessage().delete().queue();
                            waitForRole(event, message, role);
                        } else {
                            boolean canInteract = RoleUtils.canInteractWithRole(event.getSelfMember().getRoles(),
                                    event.getGuild().getRoleById(e.getMessage().getMentionedRoles().get(0).getId()));

                            if (canInteract) {
                                //can assign
                                log.debug("can assign role");
                                role.setRoleId(e.getMessage().getMentionedRoles().get(0).getIdLong());
                                System.out.println(e.getMessage().getMentionedRoles().get(0).getId());

                                MessageEmbed embed = EmbeddedUtils.buildReactionRoleEmbed(ReactionRoleConstants.ADD_REACT_TITLE_5,
                                        ReactionRoleConstants.ADD_REACT_DESCRIPTION_TYPE, role, null);
                                message.editMessage(embed).queue();

                                e.getMessage().delete().queue();
                                waitForType(event, message, role);
                            } else {
                                //cant assign
                                log.debug("cant assign role due to hierarchy");
                                MessageEmbed embed = EmbeddedUtils.buildReactionRoleEmbed(ReactionRoleConstants.ADD_REACT_TITLE_4,
                                        ReactionRoleConstants.ADD_REACT_DESCRIPTION_EMOJI, null,
                                        "The bot cannot assign this role to a user, because the role is higher in the role hierarchy.\nDrag the role of the bot above the mentioned role, assign the bot a different role or choose a new role to assign.");
                                message.editMessage(embed).queue();
                                e.getMessage().delete().queue();
                                waitForRole(event, message, role);
                            }
                        }
                    }
                }, 2, TimeUnit.MINUTES, () -> {
                    event.getMessage().getChannel().sendMessage("Sorry, you took too long.").queue();
                });
    }

    private void waitForType(CommandEvent event, Message message, ReactionRole role) {
        waiter.waitForEvent(GuildMessageReceivedEvent.class,
                e -> e.getAuthor().equals(event.getAuthor()) && e.getChannel().equals(event.getChannel()),
                e -> {
                    if (e.getMessage().getContentRaw().equalsIgnoreCase(ReactionRoleConstants.CANCEL_COMMAND)) {
                        cancelCommand(e.getMessage(), message);
                    } else {
                        try {
                            int selection = Integer.parseInt(e.getMessage().getContentRaw());

                            if (selection != 1 && selection != 2)
                                throw new NumberFormatException();

                            role.setType(selection);
                            role.setCreatorId(e.getAuthor().getIdLong());
                            reactionRoleRepository.save(role);

                            //todo replace localtime.now with localdate from repo
                            MessageEmbed embed = EmbeddedUtils.buildReactionRoleEmbed(ReactionRoleConstants.ADD_REACT_TITLE_SUMMARY,
                                    ReactionRoleConstants.ADD_REACT_DESCRIPTION_SUMMARY, role, null,
                                    "Reaction role created at " + LocalDateTime.now().format(Constants.DATE_TIME_FORMATTER_PRECISE));
                            message.editMessage(embed).queue();
                            e.getMessage().delete().queue();

                            addEmoji(event, message, role);
                        } catch (NumberFormatException ex) {
                            MessageEmbed embed = EmbeddedUtils.buildReactionRoleEmbed(ReactionRoleConstants.ADD_REACT_TITLE_5,
                                    ReactionRoleConstants.ADD_REACT_DESCRIPTION_TYPE, null, "Please select either 1 or 2.");
                            message.editMessage(embed).queue();
                            e.getMessage().delete().queue();
                            waitForType(event, message, role);
                        }
                    }
                }, 2, TimeUnit.MINUTES, () -> {
                    event.getMessage().getChannel().sendMessage("Sorry, you took too long.").queue();
                });
    }

    private void waitForEditConfirmation(CommandEvent event, Message message, ReactionRole role, ReactionRole duplicate) {
        waiter.waitForEvent(GuildMessageReceivedEvent.class,
                e -> e.getAuthor().equals(event.getAuthor()) && e.getChannel().equals(event.getChannel()),
                e -> {
                    if (e.getMessage().getContentRaw().equalsIgnoreCase(ReactionRoleConstants.CANCEL_COMMAND + "!")) {
                        // Cancel creation of reaction role
                        cancelCommand(e.getMessage(), message);
                    } else if (e.getMessage().getContentRaw().equalsIgnoreCase("cancel")) {
                        // Do not edit and pick new emoji
                        //todo just allow user to respond with new emoji
                        MessageEmbed embed = EmbeddedUtils.buildReactionRoleEmbed(ReactionRoleConstants.ADD_REACT_TITLE_3,
                                ReactionRoleConstants.ADD_REACT_DESCRIPTION_EMOJI, role, null);
                        message.editMessage(embed).queue();
                        e.getMessage().delete().queue();
                        waitForEmoji(event, message, role);
                    } else if (e.getMessage().getContentRaw().equalsIgnoreCase("yes")) {
                        //todo: continue editing
                        duplicate.setRoleId(null);
                        duplicate.setType(0);

                        MessageEmbed embed = EmbeddedUtils.buildReactionRoleEmbed(ReactionRoleConstants.ADD_REACT_TITLE_4,
                                ReactionRoleConstants.ADD_REACT_DESCRIPTION_ROLE, duplicate, null);
                        message.editMessage(embed).queue();
                        e.getMessage().delete().queue();

                        waitForRole(event, message, duplicate);
                    } else {
                        MessageEmbed embed = EmbeddedUtils.buildReactionRoleEmbed(ReactionRoleConstants.ADD_REACT_TITLE_4,
                                ReactionRoleConstants.ADD_REACT_DESCRIPTION_EMOJI, null,
                                "Respond with either `yes` to continue editing this reaction role, or `cancel` to select a new emoji. `cancel!` can be used to abort the entire process.");
                        message.editMessage(embed).queue();
                        e.getMessage().delete().queue();
                        waitForEditConfirmation(event, message, role, duplicate);
                    }

                }, 2, TimeUnit.MINUTES, () -> {
                    event.getMessage().getChannel().sendMessage("Sorry, you took too long.").queue();
                });
    }

    private void addEmoji(CommandEvent event, Message message, ReactionRole role) {
        Message msgWithReaction = event.getJDA().getGuildById(role.getReactionRoleMessage().getGuildId())
                .getTextChannelById(role.getReactionRoleMessage().getChannelId())
                .retrieveMessageById(role.getReactionRoleMessage().getMessageId()).complete();

        msgWithReaction.addReaction(role.getEmojiIdSanitized()).queue(
                success -> {
                    log.info("Emoji successfully added");
                },
                failure -> {
                    MessageEmbed embed = EmbeddedUtils.buildReactionRoleEmbed(ReactionRoleConstants.ADD_REACT_TITLE_SUMMARY,
                            ReactionRoleConstants.ADD_REACT_DESCRIPTION_SUMMARY, role,
                            "Could not add emoji to the message. To allow people to react to obtain the role, manually add the emoji.");
                    message.editMessage(embed).queue();
                });
    }
}
