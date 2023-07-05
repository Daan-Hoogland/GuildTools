package io.hoogland.guildtools.commands.loot.dkp.classes;

import io.hoogland.guildtools.commands.loot.dkp.DKPGenericClassCmd;

public class DKPDruidCmd extends DKPGenericClassCmd {
    public DKPDruidCmd() {
        this.name = "druid";
        this.help = "shows the dkp values for all " + this.name + " in guild.";
    }
}
