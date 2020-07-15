package io.hoogland.guildtools.constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WarcraftLogsConstants {

    public static final String ICON_LINK = "https://dmszsuqyoe6y6.cloudfront.net/img/warcraft/favicon.png";


    public static final String BASE_URL = "https://classic.warcraftlogs.com";

    // guild - realm - region
    public static final String API_REPORTS = "/v1/reports/guild/%s/%s/%s";

    // report id
    public static final String WARCRAFTLOGS_REPORT = "/reports/%s";

    // region - realm - guild
    public static final String API_CALENDAR = "/guild/%s/%s/%s";

    // character - realm - region
    public static final String API_RANKINGS = "/v1/rankings/character/%s/%s/%s";

    // region - realm - character
    public static final String WARCRAFTLOGS_RANKINGS = "/character/%s/%s/%s";

    public static final String PARSE_TOP = "\\*\\***%d**\\*\\*";
    public static final String PARSE_LEGENDARY = "\\***%d**\\*";
    public static final String PARSE_EPIC = "_**%d**_";
    public static final String PARSE_RARE = "**%d**";
    public static final String PARSE_UNCOMMON = "_%d_";
    public static final String PARSE_COMMON = "%d";

    public static final String MOLTEN_CORE_EMOJI = "<:mc:668932236695044125>";
    public static final List<String> MOLTEN_CORE = Collections.unmodifiableList(
            new ArrayList<String>() {{
                add("Lucifron");
                add("Magmadar");
                add("Gehennas");
                add("Garr");
                add("Shazzrah");
                add("Baron Geddon");
                add("Sulfuron Harbinger");
                add("Golemagg the Incinerator");
                add("Majordomo Executus");
                add("Ragnaros");
            }});

    public static final String ONYXIA_EMOJI = "<:onyxia:668932297072050209>";
    public static final List<String> ONYXIA = Collections.unmodifiableList(
            new ArrayList<String>() {{
                add("Onyxia");
            }});

    public static final String BLACKWING_LAIR_EMOJI = "<:bwl:668932269125271573>";
    public static final List<String> BLACKWING_LAIR = Collections.unmodifiableList(
            new ArrayList<String>() {{
                add("Razorgore the Untamed");
                add("Vaelstrasz the Corrupt");
                add("Broodlord Lashlayer");
                add("Firemaw");
                add("Ebonrock");
                add("Flamegor");
                add("Chromaggus");
                add("Nefarian");
            }});

    public static final String ZUL_GURUB_EMOJI = "<:zg:732770048149815418>";
    public static final List<String> ZUL_GURUB = Collections.unmodifiableList(
            new ArrayList<String>() {{
                add("High Priestess Jeklik");
                add("High Priest Venoxis");
                add("High Priestess Mar'li");
                add("Bloodlord Mandokir");
                add("Gri'lek");
                add("Hazza'rah");
                add("Rentaki");
                add("Wushoolay");
                add("Gahz'ranka");
                add("High Priest Thekal");
                add("High Priestess Arlokk");
                add("Jin'do the Hexxer");
                add("Hakkar");
            }});

    public static final String AHN_QIRAJ_20_EMOJI = "<:aq20:732769544191606856>";
    public static final List<String> AHN_QIRAJ_20 = Collections.unmodifiableList(
            new ArrayList<String>() {{
                add("Kurinnaxx");
                add("General Rajaxx");
                add("Moam");
                add("Buru the Gorger");
                add("Ayamiss the Hunter");
                add("Ossirian the Unscarred");
            }});

    public static final String AHN_QIRAJ_40_EMOJI = "<:aq40:732769553196777523>";
    public static final List<String> AHN_QIRAJ = Collections.unmodifiableList(
            new ArrayList<String>() {{
                add("The Prophet Skeram");
                add("Bug Trio");
                add("Battleguard Sartura");
                add("Frankriss the Unyielding");
                add("Viscidus");
                add("Princess Huhuran");
                add("Twin Emperors");
                add("Ouro");
                add("C'thun");
            }});

    public static final List<String> NAXXRAMAS = Collections.unmodifiableList(
            new ArrayList<String>() {{
                add("Anub'Rekhan");
                add("Grand Widow Faerlina");
                add("Maexxna");
                add("Noth the Plaguebringer");
                add("Heigan the Unclean");
                add("Loatheb");
                add("Instructor Razuvious");
                add("Gothik the Harvester");
                add("The Four Horsemen");
                add("Patchwerk");
                add("Grobbulus");
                add("Cluth");
                add("Thaddius");
                add("Sapphiron");
                add("Kel'Thuzad");
            }});
}
