package io.hoogland.guildtools.constants;

public class CharacterConstants {
    public static final String WHOIS_TITLE = "Who is %s?";
    public static final String WHOIS_DESCRIPTION_DISCORD = "Below the characters linked to %s can be found.";
    public static final String WHOIS_DESCRIPTION_CHARACTER = "The user linked to %s is %s\n\nOther characters linked to %s";
    public static final String CHARACTER_LINK = "Character linking";
    public static final String CHARACTER_UNLINK = "Character unlinking";
    public static final String CHARACTER_LINKED = "Linked characters";
    public static final String CHARACTER_LINK_DESCRIPTION =
            "Below you can find the character names successfully linked, and the ones that failed.";
    public static final String CHARACTER_LINK_DESCRIPTION_ERROR =
            "\n\nThe failed ones most likely failed because that character is already linked with someone else. " +
                    "Use `!whois <name>` to find out who the character is currently linked to.";
    public static final String CHARACTER_UNLINK_DESCRIPTION =
            "Below you can find the character names successfully unlinked, and the ones that failed.";
    public static final String CHARACTER_UNLINK_DESCRIPTION_ERROR =
            "\n\nThe failed ones most likely failed because that character is already linked with someone else, or it was never linked to you to begin with. " +
                    "Use `!whois <name>` to find out who the character is currently linked to.";
    public static final String CHARACTER_LINKED_DESCRIPTION = WHOIS_DESCRIPTION_DISCORD;
}
