package io.hoogland.guildtools.commands.dkp.classes;

import io.hoogland.guildtools.commands.dkp.DKPGenericClassCmd;

public class DKPPriestCmd extends DKPGenericClassCmd {
    public DKPPriestCmd() {
        this.name = "priest";
        this.help = "shows the dkp values for all " + this.name + " in guild.";
    }
}
