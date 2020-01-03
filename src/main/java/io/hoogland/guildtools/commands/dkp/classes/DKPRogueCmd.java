package io.hoogland.guildtools.commands.dkp.classes;

import io.hoogland.guildtools.commands.dkp.DKPGenericClassCmd;

public class DKPRogueCmd extends DKPGenericClassCmd {
    public DKPRogueCmd() {
        this.name = "rogue";
        this.help = "shows the dkp values for all " + this.name + " in guild.";
    }
}
