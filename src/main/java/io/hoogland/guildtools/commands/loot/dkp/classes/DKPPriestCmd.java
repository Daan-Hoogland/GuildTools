package io.hoogland.guildtools.commands.loot.dkp.classes;

import io.hoogland.guildtools.commands.loot.dkp.DKPGenericClassCmd;

public class DKPPriestCmd extends DKPGenericClassCmd {
    public DKPPriestCmd() {
        this.name = "priest";
        this.help = "shows the dkp values for all " + this.name + " in guild.";
    }
}
