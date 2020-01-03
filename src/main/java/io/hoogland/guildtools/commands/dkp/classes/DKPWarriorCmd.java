package io.hoogland.guildtools.commands.dkp.classes;

import io.hoogland.guildtools.commands.dkp.DKPGenericClassCmd;

public class DKPWarriorCmd extends DKPGenericClassCmd {
    public DKPWarriorCmd() {
        this.name = "warrior";
        this.help = "shows the dkp values for all " + this.name + " in guild.";
    }
}
