package io.hoogland.guildtools.models;

import lombok.Data;

@Data
public class ReactionRoleMessageDto {

    private String roleId;
    private String directLink;
    private long creatorId;
    private int type;

    public ReactionRoleMessageDto(String roleId, String directLink, long creatorId, int type) {
        this.roleId = roleId;
        this.directLink = directLink;
        this.creatorId = creatorId;
        this.type = type;
    }
}
