package io.hoogland.guildtools.commands.loot.dkp.classes;

import io.hoogland.guildtools.commands.loot.dkp.DKPGenericClassCmd;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DKPWarlockCmd extends DKPGenericClassCmd {
    public DKPWarlockCmd() {
        this.name = "warlock";
        this.help = "shows the dkp values for all " + this.name + " in guild.";
    }
}
