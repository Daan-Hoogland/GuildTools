package io.hoogland.guildtools.utils;

import com.vdurmont.emoji.EmojiParser;
import io.hoogland.guildtools.constants.DKPConstants;
import io.hoogland.guildtools.constants.EmojiConstants;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Message;

import java.math.BigDecimal;
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

    public static String getEmojiForClass(String clazz) {
        if (clazz == null) {
            return null;
        }
        switch (clazz.toUpperCase()) {
            case "WARRIOR":
                return EmojiConstants.EMOJI_WARRIOR;
            case "PRIEST":
                return EmojiConstants.EMOJI_PRIEST;
            case "WARLOCK":
                return EmojiConstants.EMOJI_WARLOCK;
            case "MAGE":
                return EmojiConstants.EMOJI_MAGE;
            case "PALADIN":
                return EmojiConstants.EMOJI_PALADIN;
            case "ROGUE":
                return EmojiConstants.EMOJI_ROGUE;
            case "DRUID":
                return EmojiConstants.EMOJI_DRUID;
            case "HUNTER":
                return EmojiConstants.EMOJI_HUNTER;
            case "SHAMAN":
                return EmojiConstants.EMOJI_SHAMAN;
            default:
                return EmojiConstants.EMOJI_BLANK;
        }
    }

    public static String discordEmojiToUnicode(String discordEmoji) {
        return discordEmoji.substring(2, discordEmoji.length() - 1);
    }

    public static String emoteToDiscordEmoji(Emote emote) {
        return "<:" + emote.getName() + ":" + emote.getIdLong() + ">";
    }

    public static List<String> getAllClassEmojisButOne(String clazz) {
        List<String> emojis = new ArrayList<>();
        List<String> classes = new ArrayList<>(DKPConstants.CLASSES);
        classes.remove(clazz.toUpperCase());
        classes.forEach(c -> {
            emojis.add(getEmojiForClass(c));
        });
        return emojis;
    }

    public static String getChangeEmoji(long dkpChange) {
        return (Long.signum(dkpChange) < 0) ? EmojiConstants.EMOJI_DOWN : EmojiConstants.EMOJI_UP;
    }

    public static String getChangeEmoji(int epgpChange) {
        return (Integer.signum(epgpChange) < 0) ? EmojiConstants.EMOJI_DOWN : EmojiConstants.EMOJI_UP;
    }

    public static String getChangeEmoji(BigDecimal prChange) {
        return (prChange.signum() < 0) ? EmojiConstants.EMOJI_DOWN : EmojiConstants.EMOJI_UP;
    }
}
