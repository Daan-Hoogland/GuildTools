package io.hoogland.guildtools.constants;

import java.util.ArrayList;
import java.util.List;

public class DKPConstants {

    public static final String DKP_CLASS_TITLE = "%s | %s";

    public static final String DKP_EMBED_TITLE = "DKP standings";
    public static final String DKP_EMBED_DESCRIPTION = "DKP standings for %s";

    public static final String DKP_CURRENT_TITLE = "**Current DKP**";
    public static final String DKP_CURRENT_VALUE = "%d %s (%s)";
    public static final String DKP_PREVIOUS = "Previous DKP";
    public static final String DKP_LIFETIME_GAINED = "Lifetime gained";
    public static final String DKP_LIFETIME_SPENT = "Lifetime spent";

    public static final String DKP_FOOTER = "Last updated at: %s";

    public static final String DKP_SENDING_ERROR_TITLE = "Error sending DKP response";
    public static final String DKP_SENDING_ERROR_DESCRIPTION = "There was an error sending DKP standings to <@%s> as a PM. Make sure <@%s> don't have the bot blocked or PMs disabled.";

    public static final String DKP_ERROR_FILE_TITLE = "Error reading file";
    public static final String DKP_ERROR_FILE_DESCRIPTION = "An error occurred while reading the attached file.";

    public static final String DKP_IMPORT_TITLE = "Imported DKP values";
    public static final String DKP_IMPORT_DESCRIPTION = "DKP values have been imported. New values are now available through the DKP commands.";

    public static final String DKP_IMPORT_ERROR_TITLE = "Error importing DKP CSV data";
    public static final String DKP_IMPORT_ERROR_DESCRIPTION = "An error occurred while parsing the CSV data. Make sure the CSV data is valid.";

    public static final String DKP_IMPORT_DUPLICATES_TITLE = "Duplicate entries found";
    public static final String DKP_IMPORT_DUPLICATES_DESCRIPTION = "There were duplicate DKP entries found in the CSV file uploaded. Please export the DKP standings again and remove the duplicate entries.";

    public static final List<String> CLASSES = new ArrayList<>() {
        {
            add("DRUID");
            add("HUNTER");
            add("MAGE");
            add("PALADIN");
            add("PRIEST");
            add("ROGUE");
            add("SHAMAN");
            add("WARLOCK");
            add("WARRIOR");
        }
    };

}
