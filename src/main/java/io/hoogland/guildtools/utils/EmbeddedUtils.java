package io.hoogland.guildtools.utils;

import io.hoogland.guildtools.constants.Constants;
import io.hoogland.guildtools.constants.DKPConstants;
import io.hoogland.guildtools.constants.EmojiConstants;
import io.hoogland.guildtools.constants.ReactionRoleConstants;
import io.hoogland.guildtools.models.domain.Character;
import io.hoogland.guildtools.models.domain.DKPStanding;
import io.hoogland.guildtools.models.domain.ReactionRole;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.springframework.util.StringUtils;

import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
public class EmbeddedUtils {

    public static MessageEmbed buildReactionRoleEmbed(String title, String description, ReactionRole role, String error) {
        return buildReactionRoleEmbed(title, description, role, error, null);
    }

    public static MessageEmbed buildGenericEmbed(String title, String description, List<MessageEmbed.Field> fields, String customFooter,
                                                 String color) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(title);
        embedBuilder.setDescription(description);
        embedBuilder.setFooter(customFooter);
        fields.forEach(embedBuilder::addField);
        if (color != null) {
            if (!color.startsWith("#")) {
                color = "#" + color;
            }
            embedBuilder.setColor(Color.decode(color));
        }
        return embedBuilder.build();
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
            embedBuilder.setFooter(ReactionRoleConstants.ADD_REACTION_FOOTER);
        }

        if (null != error) {
            embedBuilder.setColor(Color.RED);
            embedBuilder.addField("⚠", error, false);
        } else {
            embedBuilder.setColor(Color.decode("#43b581"));
        }

        boolean inline = true;
        if (title.equals(ReactionRoleConstants.ADD_REACT_TITLE_SUMMARY)) {
            inline = false;
        }

        if (role != null) {
            if (role.getId() != null)
                embedBuilder.addField(ReactionRoleConstants.ID, String.valueOf(role.getId()), inline);
            if (role.getReactionRoleMessage().getChannelId() != null)
                embedBuilder.addField(ReactionRoleConstants.CHANNEL,
                        String.format(ReactionRoleConstants.MENTION_CHANNEL, role.getReactionRoleMessage().getChannelId()), inline);
            if (role.getReactionRoleMessage().getMessageId() != null)
                embedBuilder.addField(ReactionRoleConstants.MESSAGE,
                        String.format(ReactionRoleConstants.LINK, role.getReactionRoleMessage().getMessageId(),
                                role.getReactionRoleMessage().getDirectLink()), inline);
            if (role.getEmojiId() != null)
                embedBuilder.addField(ReactionRoleConstants.EMOJI, role.getEmojiId(), inline);
            if (role.getRoleId() != null)
                embedBuilder.addField(ReactionRoleConstants.ROLE, String.format(ReactionRoleConstants.MENTION_ROLE, role.getRoleId()), inline);
            if (role.getType() != 0) {
                embedBuilder.addField(ReactionRoleConstants.TYPE, String.valueOf(role.getType()), inline);
            }
        }

        return embedBuilder.build();
    }

    public static MessageEmbed createDkpEmbed(DKPStanding dkpStanding) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(DKPConstants.DKP_EMBED_TITLE);
        embedBuilder.setDescription(String.format(DKPConstants.DKP_EMBED_DESCRIPTION, StringUtils.capitalize(dkpStanding.getPlayer().toLowerCase())));

        //todo add image for class

        String standing = "";

        if (Long.signum(dkpStanding.getDkpChange()) >= 0) {
            standing = EmojiConstants.EMOJI_UP;
        } else {
            standing = EmojiConstants.EMOJI_DOWN;
        }
        embedBuilder.addField(DKPConstants.DKP_CURRENT_TITLE,
                String.format(DKPConstants.DKP_CURRENT_VALUE, dkpStanding.getDkp(), standing, dkpStanding.getDkpChange()),
                false);
        embedBuilder.addField(DKPConstants.DKP_PREVIOUS, String.valueOf(dkpStanding.getPrevious()), false);
        embedBuilder.addField(DKPConstants.DKP_LIFETIME_GAINED, String.valueOf(dkpStanding.getLifetimeGained()), true);
        embedBuilder.addField(DKPConstants.DKP_LIFETIME_SPENT, String.valueOf(dkpStanding.getLifetimeSpent()), true);

        HashMap classInfo = (HashMap) ((HashMap) ConfigUtils.getConfig().get("icons")).get(dkpStanding.getClazz().toLowerCase());
        embedBuilder.setThumbnail(classInfo.get("icon").toString());
        embedBuilder.setColor(Color.decode("#" + classInfo.get("color").toString()));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        embedBuilder.setFooter(String.format(DKPConstants.DKP_FOOTER, dkpStanding.getModifiedDate().format(formatter)));

        return embedBuilder.build();
    }

    public static MessageEmbed buildLinkMessageEmbed(boolean linked, List<String> errors, List<String> confirmed) {
        EmbedBuilder embed = new EmbedBuilder();
        String titleLinked = "Character linking";
        String titleUnlinked = "Character unlinking";

        String descriptionLinked = "Below you can find the character names successfully linked, and the ones that failed.\n" +
                "The failed ones most likely failed because that character is already linked with someone else. " +
                "Use `!whois <name>` to find out who the character is currently linked to.";
        String descriptionUnlinked = "Below you can find the character names successfully unlinked, and the ones that failed.\n" +
                "The failed ones most likely failed because that character is already linked with someone else, or it was never linked to you to begin with. " +
                "Use `!whois <name>` to find out who the character is currently linked to.";

        embed.setTitle(linked ? titleLinked : titleUnlinked);
        embed.setDescription(linked ? descriptionLinked : descriptionUnlinked);

        embed.addField(linked ? "✅ Linked" : "✅ Unlinked", confirmed.isEmpty() ? "*None*" : String.join(", ", confirmed), false);
        embed.addField(linked ? "⚠ Not linked" : "⚠ Not unlinked", errors.isEmpty() ? "*None*" : String.join(", ", errors), false);


        return embed.build();
    }


    public static MessageEmbed buildLinkedCharacterUserEmbed(String userName, long userId, List<Character> allCharacters,
                                                             DateTimeFormatter formatter) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle(String.format("Who is @%s?", userName));
        builder.setDescription(
                "Below the characters linked to " + String.format(Constants.MENTION_USER, userId) + " can be found.");

        getCharacterFields(allCharacters, formatter).forEach(builder::addField);


        return builder.build();
    }

    public static MessageEmbed buildLinkedUserCharacterEmbed(String playerName, long userId, List<Character> allCharacters,
                                                             DateTimeFormatter formatter) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle(String.format("Who is %s?", playerName));
        builder.setDescription(String.format("The user linked to %s is %s\n\nOther characters linked to %s", playerName,
                String.format(Constants.MENTION_USER, userId), String.format(Constants.MENTION_USER, userId)));

        getCharacterFields(allCharacters, formatter).forEach(builder::addField);

        return builder.build();
    }

    public static List<MessageEmbed.Field> getCharacterFields(List<Character> allCharacters, DateTimeFormatter formatter) {
        List<MessageEmbed.Field> fields = new ArrayList<>();
        if (allCharacters.isEmpty()) {
            fields.add(new MessageEmbed.Field("⚠ No characters linked", "", false));
        } else {
            allCharacters.forEach(character -> {
                String emoji = EmojiUtils.getEmojiForClass(character.getClazz());

                fields.add(new MessageEmbed.Field(
                        String.format("%s %s", emoji != null ? emoji : "", StringUtils.capitalize(character.getName().toLowerCase())),
                        String.format("Linked since: %s", character.getCreatedDate().format(formatter)), false));
            });
        }

        return fields;
    }
}
