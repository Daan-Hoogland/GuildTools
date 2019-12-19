package io.hoogland.guildtools.utils;

import net.dv8tion.jda.api.entities.Role;

import java.util.List;

public class RoleUtils {

    public static boolean canInteractWithRole(List<Role> selfRoles, Role targetRole) {
        boolean canInteract = false;
        for (Role selfRole : selfRoles) {
            if (!canInteract) {
                canInteract = selfRole.canInteract(targetRole);
            }
        }
        return canInteract;
    }
}
