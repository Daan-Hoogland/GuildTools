package io.hoogland.guildtools.commands.loot.dkp.classes;

import io.hoogland.guildtools.commands.loot.dkp.DKPGenericClassCmd;

public class DKPWarriorCmd extends DKPGenericClassCmd {
    public DKPWarriorCmd() {
        this.name = "warrior";
        this.help = "shows the dkp values for all " + this.name + " in guild.";
    }
}
