package io.hoogland.guildtools.commands.dkp.classes;

import io.hoogland.guildtools.commands.dkp.DKPGenericClassCmd;

public class DKPHunterCmd extends DKPGenericClassCmd {
    public DKPHunterCmd() {
        this.name = "hunter";
        this.help = "shows the dkp values for all " + this.name + " in guild.";
    }
}
