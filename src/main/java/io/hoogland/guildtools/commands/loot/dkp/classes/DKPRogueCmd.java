package io.hoogland.guildtools.commands.loot.dkp.classes;

import io.hoogland.guildtools.commands.loot.dkp.DKPGenericClassCmd;

public class DKPRogueCmd extends DKPGenericClassCmd {
    public DKPRogueCmd() {
        this.name = "rogue";
        this.help = "shows the dkp values for all " + this.name + " in guild.";
    }
}
