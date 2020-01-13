package io.hoogland.guildtools.utils;

import io.hoogland.guildtools.models.domain.Character;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.springframework.util.StringUtils;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class LinkUtils {
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

    public static List<MessageEmbed.Field> getResultFields(List<String> success, List<String> error, boolean linking) {
        List<MessageEmbed.Field> fields = new ArrayList<>();
        fields.add(new MessageEmbed.Field(linking ? "✅ Linked" : "✅ Unlinked", success.isEmpty() ? "*None*" : String.join(", ", success), false));
        fields.add(new MessageEmbed.Field(linking ? "⚠ Not linked" : "⚠ Not unlinked", error.isEmpty() ? "*None*" : String.join(", ", error), false));
        return fields;
    }
}
