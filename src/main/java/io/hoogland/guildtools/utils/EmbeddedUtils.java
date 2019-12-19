package io.hoogland.guildtools.utils;

import io.hoogland.guildtools.constants.EmbedConstants;
import io.hoogland.guildtools.models.ReactionRole;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;

public class EmbeddedUtils {

    public static MessageEmbed buildReactionRoleEmbed(String title, String description, ReactionRole role, String error) {
        return buildReactionRoleEmbed(title, description, role, error, null);
    }

    public static MessageEmbed buildErrorEmbed(String title, String description, String reason, String customFooter) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(title);
        embedBuilder.setDescription(description);
        embedBuilder.setColor(Color.RED);

        if (null != customFooter) {
            embedBuilder.setFooter(customFooter);
        }

        if (reason != null)
            embedBuilder.addField("⚠", reason, false);


        return embedBuilder.build();
    }

    public static MessageEmbed buildReactionRoleEmbed(String title, String description, ReactionRole role, String error, String customFooter) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(title);
        embedBuilder.setDescription(description);
        if (null != customFooter) {
            embedBuilder.setFooter(customFooter);
        } else {
            embedBuilder.setFooter(EmbedConstants.ADD_REACTION_FOOTER);
        }

        if (null != error) {
            embedBuilder.setColor(Color.RED);
            embedBuilder.addField("⚠", error, false);
        } else {
            embedBuilder.setColor(Color.decode("#43b581"));
        }

        boolean inline = true;
        if (title.equals(EmbedConstants.ADD_REACT_TITLE_SUMMARY)) {
            inline = false;
        }

        if (role != null) {
            if (role.getId() != null)
                embedBuilder.addField(EmbedConstants.ID, String.valueOf(role.getId()), inline);
            if (role.getReactionRoleMessage().getChannelId() != null)
                embedBuilder.addField(EmbedConstants.CHANNEL, role.getReactionRoleMessage().getChannelId(), inline);
            if (role.getReactionRoleMessage().getMessageId() != null)
                embedBuilder.addField(EmbedConstants.MESSAGE,
                        String.format(EmbedConstants.LINK, role.getReactionRoleMessage().getMessageId(),
                                role.getReactionRoleMessage().getDirectLink()), inline);
            if (role.getEmojiId() != null)
                embedBuilder.addField(EmbedConstants.EMOJI, role.getEmojiId(), inline);
            if (role.getRoleId() != null)
                embedBuilder.addField(EmbedConstants.ROLE, String.format(EmbedConstants.MENTION_ROLE, role.getRoleId()), inline);
            if (role.getType() != 0) {
                embedBuilder.addField(EmbedConstants.TYPE, String.valueOf(role.getType()), inline);
            }
        }

        return embedBuilder.build();
    }
}
