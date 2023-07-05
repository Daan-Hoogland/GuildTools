package io.hoogland.guildtools.constants;

import java.time.format.DateTimeFormatter;

public class Constants {
    public static final String MENTION_EMOJI = "<:%s:%s>";
    public static final String MENTION_CHANNEL = "<#%s>";
    public static final String MENTION_ROLE = "<@&%d>";
    public static final String MENTION_USER = "<@%d>";
    public static final String LINK = "[%s](%s)";

    public static final String COLOR_OK = "43b581";
    public static final String COLOR_NOT_OK = "ff0000";

    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
    public static final DateTimeFormatter DATE_TIME_FORMATTER_DATE = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    public static final DateTimeFormatter DATE_TIME_FORMATTER_TIME = DateTimeFormatter.ofPattern("HH:mm");
    public static final DateTimeFormatter DATE_TIME_FORMATTER_PRECISE = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

}
