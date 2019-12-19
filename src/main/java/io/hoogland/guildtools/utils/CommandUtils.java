package io.hoogland.guildtools.utils;

import com.jagrosh.jdautilities.command.CommandEvent;

import java.util.Arrays;
import java.util.Map;

public class CommandUtils {

    private static Map config = ConfigUtils.getConfig();

    public static boolean isValidCommand(CommandEvent event, String[] aliases) {
        String[] split = event.getMessage().getContentRaw().split("\\s+");
        return Arrays.stream(aliases).map(s -> config.get("prefix") + s).anyMatch(split[0]::equals);
    }
}
