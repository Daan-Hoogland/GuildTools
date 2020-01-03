package io.hoogland.guildtools.commands.dkp.classes;

import io.hoogland.guildtools.commands.dkp.DKPGenericClassCmd;

public class DKPMageCmd extends DKPGenericClassCmd {
    public DKPMageCmd() {
        this.name = "mage";
        this.help = "shows the dkp values for all " + this.name + " in guild.";
    }
}
