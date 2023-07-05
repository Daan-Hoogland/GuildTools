package io.hoogland.guildtools.commands.loot.dkp.classes;

import io.hoogland.guildtools.commands.loot.dkp.DKPGenericClassCmd;

public class DKPShamanCmd extends DKPGenericClassCmd {
    public DKPShamanCmd() {
        this.name = "shaman";
        this.help = "shows the dkp values for all " + this.name + " in guild.";
    }
}
