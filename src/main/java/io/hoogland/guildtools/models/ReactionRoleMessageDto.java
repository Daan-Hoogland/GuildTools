package io.hoogland.guildtools.models;

import lombok.Data;

@Data
public class ReactionRoleMessageDto {

    private long roleId;
    private String directLink;
    private long creatorId;
    private int type;

    public ReactionRoleMessageDto(long roleId, String directLink, long creatorId, int type) {
        this.roleId = roleId;
        this.directLink = directLink;
        this.creatorId = creatorId;
        this.type = type;
    }
}
