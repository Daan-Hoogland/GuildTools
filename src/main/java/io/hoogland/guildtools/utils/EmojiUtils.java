package io.hoogland.guildtools.utils;

import com.vdurmont.emoji.EmojiParser;
import net.dv8tion.jda.api.entities.Message;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class EmojiUtils {

    public static List<String> extractEmojisFromMessage(Message message) {
        String content = message.getContentRaw();
        List<String> emojis = EmojiParser.extractEmojis(content);
        List<String> customEmoji = message.getEmotes().stream()
                .map((emote) -> emote.getName() + ":" + emote.getId())
                .collect(Collectors.toList());

        // Create merged list
        List<String> merged = new ArrayList<>();
        merged.addAll(emojis);
        merged.addAll(customEmoji);

        // Sort based on index in message to preserve order
        merged.sort(Comparator.comparingInt(content::indexOf));

        return merged;
    }
}
