package io.hoogland.guildtools.commands.dkp.classes;

import io.hoogland.guildtools.commands.dkp.DKPGenericClassCmd;

public class DKPPaladinCmd extends DKPGenericClassCmd {
    public DKPPaladinCmd() {
        this.name = "paladin";
        this.help = "shows the dkp values for all " + this.name + " in guild.";
    }
}
