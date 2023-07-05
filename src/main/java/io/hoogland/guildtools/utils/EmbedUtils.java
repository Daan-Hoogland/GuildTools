package io.hoogland.guildtools.utils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;
import java.util.List;

public class EmbedUtils {

    public static MessageEmbed createErrorEmbed(String title, String description, String error, String footer) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle(title);
        embed.setDescription(description);
        embed.setColor(Color.RED);

        if (error != null)
            embed.addField("", "⚠ " + error, false);

        return embed.build();
    }

    public static MessageEmbed createEmbed(String title, String description, List<MessageEmbed.Field> fields) {
        return createEmbed(title, description, fields, null, null, null, null);
    }

    public static MessageEmbed createEmbed(String title, String description, List<MessageEmbed.Field> fields, String hexColor, String footer,
                                           String footerIcon, String thumbnail) {
        EmbedBuilder builder = new EmbedBuilder();

        builder.setTitle(title);
        builder.setDescription(description);
        builder.setFooter(footer, footerIcon);
        builder.setThumbnail(thumbnail);
        if (hexColor != null)
            builder.setColor(Color.decode("#" + hexColor));

        if (fields != null)
            fields.forEach(builder::addField);

        return builder.build();
    }
}
