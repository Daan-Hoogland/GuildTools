package io.hoogland.guildtools.constants;

public class ReactionRoleConstants {

    public static final String ADD_REACT_INSUFFICIENT_PERMISSIONS_TITLE = "Reaction Role Setup - Insufficient Permissions";
    public static final String ADD_REACT_TITLE_1 = "Reaction Role Setup - Channel";
    public static final String ADD_REACT_TITLE_2 = "Reaction Role Setup - Message";
    public static final String ADD_REACT_TITLE_3 = "Reaction Role Setup - Emoji";
    public static final String ADD_REACT_TITLE_3_EDIT = "Reaction Role Setup - Emoji - Edit";
    public static final String ADD_REACT_TITLE_4 = "Reaction Role Setup - Role";
    public static final String ADD_REACT_TITLE_5 = "Reaction Role Setup - Type";
    public static final String ADD_REACT_TITLE_SUMMARY = "Reaction Role Setup - Summary";
    public static final String ADD_REACT_TITLE_CANCEL = "Action cancelled";

    public static final String ADD_REACT_DESCRIPTION__INSUFFICIENT_PERMISSIONS = "The bot has insufficient permissions to carry out this command. Please make sure that the bot has a role that can both manage roles and add reactions.";
    public static final String ADD_REACT_DESCRIPTION_CHANNEL = "Reply with the channel the message will appear in.\nMention it using the # prefix, like #general.";
    public static final String ADD_REACT_DESCRIPTION_MESSAGE = "Reply with the `message ID` of the message you want the reactions to be added to.\n\nInformation so far:";
    public static final String ADD_REACT_DESCRIPTION_ROLE = "Tag the role you would like to assign to this emoji using the @ symbol. Make sure the role is mentionable in the role settings.";
    public static final String ADD_REACT_DESCRIPTION_EMOJI = "Reply with the emoji you'd like people to react with.";
    public static final String ADD_REACT_DESCRIPTION_TYPE = "The way of handling the reactions.\nSelecting `1` will allow the user to react to the message and get the role, and then unreact to remove the role.\nSelecting `2` will make it so that if the user reacts to the message, his reaction is removed shortly after and he will be able to react again to remove the role.\n\nRespond with either `1` or `2`";
    public static final String ADD_REACT_DESCRIPTION_SUMMARY = "The following information has been saved. Users can now start reacting to the message to receive their roles. To edit this rule, use the `!editreact` command.";
    public static final String ADD_REACT_EMOJI_DUPLICATE = "There is already a reaction role set up for that emoji.\nTo edit that reaction role, respond with **`yes`**. \nTo go back and select a different emoji, respond with **`cancel`**. \nTo abort the reaction role creation, respond with **`cancel!`**";

    public static final String ADD_REACTION_FOOTER = "After 2 minutes this setup will be automatically cancelled. To manually cancel it reply with cancel.";

    public static final String CANCEL_COMMAND = "cancel";
    public static final String CANCEL_ERROR = "The command has been cancelled.";
    public static final String CHANNEL = "**Channel**";
    public static final String MESSAGE = "**Message**";
    public static final String MESSAGE_ERROR = "No message found with that ID.\n\nError: %s";
    public static final String EMOJI = "**Emoji**";
    public static final String ROLE = "**Role**";
    public static final String TYPE = "**Type**";
    public static final String ID = "**Rule ID**";

    public static final String MENTION_EMOJI = "<:%s:%s>";
    public static final String MENTION_CHANNEL = "<#%s>";
    public static final String MENTION_ROLE = "<@&%s>";
    public static final String LINK = "[%s](%s)";

    public static final String ERROR_ASSIGN_ROLE_DESCRIPTION = "The bot was unable to assign a role.\n\n**Role**\n%s\n**Message** %s";
    public static final String ERROR_ASSIGN_ROLE_TITLE = "Unable to assign role.";


    public static final String ERROR_REMOVE_ROLE_DESCRIPTION = "The bot was unable to assign a role.\n\n**Role**\n%s\n**Message** %s";
    public static final String ERROR_REMOVE_ROLE_TITLE = "Unable to remove role.";
}
