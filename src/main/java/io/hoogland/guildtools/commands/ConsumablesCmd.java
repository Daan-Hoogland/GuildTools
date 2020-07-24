package io.hoogland.guildtools.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import io.hoogland.guildtools.constants.Constants;
import io.hoogland.guildtools.utils.EmbedUtils;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.ArrayList;

@Slf4j
public class ConsumablesCmd extends Command {

    public ConsumablesCmd() {
        this.name = "consumables";
        this.aliases = new String[]{"consumes"};
        this.help = "shows the list of consumables.";
    }

    @Override
    protected void execute(CommandEvent event) {
        if (event.getArgs().isBlank()) {
            MessageEmbed embed = getAllConsumablesFields();
            event.getChannel().sendMessage(embed).queue();
        } else {
            ArrayList<MessageEmbed.Field> fields = new ArrayList<>();
            fields.add(getGenericConsumables());
            String title = "";
            switch (event.getArgs().toUpperCase()) {
                case "TANK":
                case "TANKS":
                case "WARRIOR":
                    title = (
                            event.getArgs().equalsIgnoreCase("tank") || event.getArgs().equalsIgnoreCase("tanks") ? "Tank" : "Warrior");
                    fields.add(getMeleeConsumables());
                    fields.add(getWarriorConsumables());
                    fields.add(getTankingConsumables());
                    break;
                case "CASTER":
                case "CASTERS":
                    title = "Caster";
                    break;
                case "HEAL":
                case "HEALER":
                    title = "Healer";
                    fields.add(getHealerConsumables());
                    break;
                case "WARLOCK":
                    title = "Warlock";
                    fields.add(getCasterConsumables());
                    fields.add(getWarlockConsumables());
                    break;
                case "DRUID":
                    title = "Druid";
                    fields.add(getMeleeConsumables());
                    fields.add(getFeralConsumables());
                    fields.add(getHealerConsumables());
                    break;
                case "ROGUE":
                    title = "Rogue";
                    fields.add(getMeleeConsumables());
                    fields.add(getRogueConsumables());
                    break;
                case "PALADIN":
                    title = "Paladin";
                    fields.add(getMeleeConsumables());
                    fields.add(getPaladinConsumables());
                    fields.add(getHealerConsumables());
                    break;
                case "RET":
                case "RETRIBUTION":
                    title = "Paladin";
                    fields.add(getMeleeConsumables());
                    fields.add(getPaladinConsumables());
                    break;
                case "PRIEST":
                case "SHADOW":
                    title = "Priest";
                    fields.add(getHealerConsumables());
                    fields.add(getCasterConsumables());
                    fields.add(getShadowPriestConsumables());
                    break;
                case "SHAMAN":
                    title = "Shaman";
                    fields.add(new MessageEmbed.Field("", "**Wrong faction buddy**", false));
                    break;
                case "HUNTER":
                    title = "Hunter";
                    fields.add(getHunterConsumables());
                    break;
                case "MAGE":
                    title = "Mage";
                    fields.add(getCasterConsumables());
                    fields.add(getMageConsumables());
                    break;
                default:
                    fields.add(new MessageEmbed.Field("", "**No valid class specified**", false));
                    break;
            }

            MessageEmbed msg = EmbedUtils.createEmbed(title + " Consumables",
                    "The following fields contain the minimum required consumables. Optional ones are written in _cursive_.", fields, "e3992e", null,
                    null, "https://classicdb.ch/images/icons/large/inv_misc_food_08.jpg");
            event.getChannel().sendMessage(msg).queue();
        }

        event.getMessage().delete().queue();
    }

    private MessageEmbed getAllConsumablesFields() {
        ArrayList<MessageEmbed.Field> fields = new ArrayList<>();
        fields.add(getGenericConsumables());
        fields.add(getHunterConsumables());
        fields.add(getMeleeConsumables());
        fields.add(getWarriorConsumables());
        fields.add(getRogueConsumables());
        fields.add(getPaladinConsumables());
        fields.add(getFeralConsumables());
        fields.add(getTankingConsumables());
        fields.add(new MessageEmbed.Field("", "", true));

        fields.add(getCasterConsumables());
        fields.add(getMageConsumables());
        fields.add(getWarlockConsumables());

        fields.add(getHealerConsumables());

        return EmbedUtils
                .createEmbed("All consumables",
                        "The following fields contain the minimum required consumables. Optional ones are written in _cursive_ and labeled by a :small_blue_diamond: in front of the link. Consumables prefixed with :small_orange_diamond: are required.",
                        fields, "e3992e", null, null, "https://classicdb.ch/images/icons/large/inv_misc_food_08.jpg");
    }

    private MessageEmbed.Field getGenericConsumables() {
        StringBuilder builder = new StringBuilder();
        builder.append("The following consumables are **required** for **everyone**").append("\n");
        builder.append(":small_orange_diamond: ");
        builder.append(String.format(Constants.LINK, "Zanza Potion", "https://classicdb.ch/?search=zanza")).append(" *(melee go for Blasted Lands buffs)*\n");
        builder.append(":small_orange_diamond: ");
        builder.append(String.format(Constants.LINK, "Greater Nature Protection Potion", "https://classicdb.ch/?item=13458")).append(" *(AQ40)*\n");
        builder.append(":small_orange_diamond: ");
        builder.append(String.format(Constants.LINK, "Hourglass Sand", "https://classicdb.ch/?item=19183")).append(" *(BWL)*\n");
        builder.append(":small_orange_diamond: ");
        builder.append(String.format(Constants.LINK, "Limited Invulnerability Potion", "https://classicdb.ch/?item=3387")).append(" *(Fire Mage/Rogue/Warrior DPS)*");
        return new MessageEmbed.Field("Generic Consumables", builder.toString(), false);
    }

    private MessageEmbed.Field getCasterConsumables() {
        StringBuilder builder = new StringBuilder();
        builder.append("The following consumables are required for **all caster DPS classes**").append("\n");
        builder.append(":small_orange_diamond: ");
        builder.append(String.format(Constants.LINK, "Greater Arcane Elixir", "https://classicdb.ch/?item=13454")).append("\n");
        builder.append(":small_orange_diamond: ");
        builder.append(String.format(Constants.LINK, "Brilliant Wizard Oil", "https://classicdb.ch/?item=20749")).append("\n");
        builder.append(":small_orange_diamond: ");
        builder.append(String.format(Constants.LINK, "Major Mana Potion", "https://classicdb.ch/?item=13444")).append("\n");
        builder.append(":small_blue_diamond: ");
        builder.append(String.format(Constants.LINK, "_Dark Rune_*", "https://classicdb.ch/?item=20520")).append("\n\n");
        builder.append("_* required for Boomkins & Shadow Priests_");

        return new MessageEmbed.Field("Caster Consumables", builder.toString(), false);
    }

    private MessageEmbed.Field getMageConsumables() {
        StringBuilder builder = new StringBuilder();
        builder.append(":small_orange_diamond: ");
        builder.append("Frost: " + String.format(Constants.LINK, "Elixir of Frost Power", "https://classicdb.ch/?item=17708")).append("\n");
        builder.append(":small_orange_diamond: ");
        builder.append("Fire: " + String.format(Constants.LINK, "Elixir of Greater Firepower", "https://classicdb.ch/?item=21546"));
        return new MessageEmbed.Field("Mage", builder.toString(), true);
    }

    private MessageEmbed.Field getWarlockConsumables() {
        StringBuilder builder = new StringBuilder();
        builder.append(":small_orange_diamond: ");
        builder.append(String.format(Constants.LINK, "Elixir of Shadow Power", "https://classicdb.ch/?item=9264"));
        return new MessageEmbed.Field("Warlock", builder.toString(), true);
    }

    private MessageEmbed.Field getShadowPriestConsumables() {
        StringBuilder builder = new StringBuilder();
        builder.append(":small_orange_diamond: ");
        builder.append(String.format(Constants.LINK, "Elixir of Shadow Power", "https://classicdb.ch/?item=9264"));
        return new MessageEmbed.Field("Shadow Priest", builder.toString(), true);
    }

    private MessageEmbed.Field getMeleeConsumables() {
        StringBuilder builder = new StringBuilder();
        builder.append("The following consumables are required for **all melee DPS classes**").append("\n");
        builder.append(":small_orange_diamond: ");
        builder.append(String.format(Constants.LINK, "Elixir of the Mongoose", "https://classicdb.ch/?item=13452")).append("\n");
        builder.append(":small_orange_diamond: ");
        builder.append(String.format(Constants.LINK, "Juju Power", "https://classicdb.ch/?item=12451")).append("/")
                .append(String.format(Constants.LINK, "Elixir of Giants", "https://classicdb.ch/?item=9206")).append("\n");
        builder.append(":small_orange_diamond: ");
        builder.append(String.format(Constants.LINK, "Winterfall Firewater", "https://classicdb.ch/?item=12820")).append("/")
                .append(String.format(Constants.LINK, "Juju Might", "https://classicdb.ch/?item=12460")).append("\n");
        builder.append(":small_orange_diamond: ");
        builder.append(String.format(Constants.LINK, "Free Action Potion", "https://classicdb.ch/?item=5634")).append("\n\n");
        builder.append(":small_blue_diamond: ");
        builder.append("_" + String.format(Constants.LINK, "Ground Scorpok Assay ", "https://classicdb.ch/?item=8412") + "_").append("\n");
        builder.append(":small_blue_diamond: ");
        builder.append("_" + String.format(Constants.LINK, "R.O.I.D.S.", "https://classicdb.ch/?item=8410") + "_").append("\n");
        builder.append(":small_blue_diamond: ");
        builder.append("_" + String.format(Constants.LINK, "Restorative Potion", "https://classicdb.ch/?item=9030") + "_")
                .append("\n");
        builder.append(":small_blue_diamond: ");
        builder.append("_" + String.format(Constants.LINK, "Major Healing Potion", "https://classicdb.ch/?item=13446") + "_");
        return new MessageEmbed.Field("Melee Consumables", builder.toString(), false);
    }

    private MessageEmbed.Field getWarriorConsumables() {
        StringBuilder builder = new StringBuilder();
        builder.append(":small_orange_diamond: ");
        builder.append(String.format(Constants.LINK, "Mighty Rage Potion", "https://classicdb.ch/?item=13442")).append("\n");
        builder.append(":small_orange_diamond: ");
        builder.append(String.format(Constants.LINK, "Smoked Desert Dumplings", "https://classicdb.ch/?item=20452")).append("\n");
        builder.append(":small_orange_diamond: ");
        builder.append(String.format(Constants.LINK, "Elemental Sharpening Stone", "https://classicdb.ch/?item=18262"));
        return new MessageEmbed.Field("Warrior", builder.toString(), true);
    }

    private MessageEmbed.Field getRogueConsumables() {
        StringBuilder builder = new StringBuilder();
        builder.append(":small_orange_diamond: ");
        builder.append(String.format(Constants.LINK, "Grilled Squid", "https://classicdb.ch/?item=13928")).append("/");
        builder.append(String.format(Constants.LINK, "Smoked Desert Dumplings", "https://classicdb.ch/?item=20452")).append("\n");
        builder.append(":small_orange_diamond: ");
        builder.append(String.format(Constants.LINK, "Instant Poison VI", "https://classicdb.ch/?item=8928")).append("\n");
        builder.append(":small_blue_diamond: ");
        builder.append("_" + String.format(Constants.LINK, "Thistle Tea", "https://classicdb.ch/?item=7676") + "_");
        return new MessageEmbed.Field("Rogue", builder.toString(), true);
    }

    private MessageEmbed.Field getHunterConsumables() {
        StringBuilder builder = new StringBuilder();
        builder.append(":small_orange_diamond: ");
        builder.append(String.format(Constants.LINK, "Elixir of the Mongoose", "https://classicdb.ch/?item=13452")).append("\n");
        builder.append(":small_orange_diamond: ");
        builder.append(String.format(Constants.LINK, "Grilled Squid", "https://classicdb.ch/?item=13928")).append("\n");
        builder.append(":small_orange_diamond: ");
        builder.append(String.format(Constants.LINK, "Thorium bullets/arrows", "https://classicdb.ch/?item=15997")).append("\n");
        builder.append(":small_orange_diamond: ");
        builder.append(String.format(Constants.LINK, "Major Mana Potion", "https://classicdb.ch/?item=13444"));
        return new MessageEmbed.Field("Hunter", builder.toString(), false);
    }

    private MessageEmbed.Field getPaladinConsumables() {
        StringBuilder builder = new StringBuilder();
        builder.append(":small_orange_diamond: ");
        builder.append(String.format(Constants.LINK, "Greater Arcane Elixir", "https://classicdb.ch/?item=13454")).append("\n");
        builder.append(":small_orange_diamond: ");
        builder.append(String.format(Constants.LINK, "Blessed Sunfruit", "https://classicdb.ch/?item=13810"));
        return new MessageEmbed.Field("Retribution", builder.toString(), true);
    }

    private MessageEmbed.Field getHealerConsumables() {
        StringBuilder builder = new StringBuilder();
        builder.append("The following consumables are required for **all healing classes**").append("\n");
        builder.append(":small_orange_diamond: ");
        builder.append(String.format(Constants.LINK, "Mageblood Potion", "https://classicdb.ch/?item=20007")).append("\n");
        builder.append(":small_orange_diamond: ");
        builder.append(String.format(Constants.LINK, "Nightfin Soup", "https://classicdb.ch/?item=13931")).append("\n");
        builder.append(":small_orange_diamond: ");
        builder.append(String.format(Constants.LINK, "Brilliant Mana Oil", "https://classicdb.ch/?item=20748")).append("\n");
        builder.append(":small_orange_diamond: ");
        builder.append(String.format(Constants.LINK, "Major Mana Potion", "https://classicdb.ch/?item=13444")).append("\n");
        builder.append(":small_blue_diamond: ");
        builder.append(String.format(Constants.LINK, "_Dark Rune_", "https://classicdb.ch/?item=20520"));
        return new MessageEmbed.Field("Healer Consumables", builder.toString(), false);
    }

    private MessageEmbed.Field getTankingConsumables() {
        StringBuilder builder = new StringBuilder();
        builder.append(":small_orange_diamond: ");
        builder.append(String.format(Constants.LINK, "Mighty Rage Potion (Warrior)", "https://classicdb.ch/?item=13442")).append("\n");
        builder.append(":small_orange_diamond: ");
        builder.append(String.format(Constants.LINK, "Greater Stoneshield Potion", "https://classicdb.ch/?item=13455")).append("\n");
        builder.append(":small_orange_diamond: ");
        builder.append(String.format(Constants.LINK, "Elixir of Fortitude", "https://classicdb.ch/?item=3825")).append("\n");
        builder.append(":small_orange_diamond: ");
        builder.append(String.format(Constants.LINK, "Elixir of Superior Defense", "https://classicdb.ch/?item=13445"));
        return new MessageEmbed.Field("Tanks", builder.toString(), true);
    }

    private MessageEmbed.Field getFeralConsumables() {
        StringBuilder builder = new StringBuilder();
        builder.append(":small_orange_diamond: ");
        builder.append(String.format(Constants.LINK, "Grilled Squid", "https://classicdb.ch/?item=13928"));
        return new MessageEmbed.Field("Feral", builder.toString(), true);
    }
}
