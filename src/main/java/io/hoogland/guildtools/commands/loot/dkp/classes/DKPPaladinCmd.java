package io.hoogland.guildtools.commands.loot.dkp.classes;

import io.hoogland.guildtools.commands.loot.dkp.DKPGenericClassCmd;

public class DKPPaladinCmd extends DKPGenericClassCmd {
    public DKPPaladinCmd() {
        this.name = "paladin";
        this.help = "shows the dkp values for all " + this.name + " in guild.";
    }
}
