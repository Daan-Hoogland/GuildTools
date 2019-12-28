package io.hoogland.guildtools.utils;

import com.jagrosh.jdautilities.command.CommandEvent;
import io.hoogland.guildtools.constants.DKPConstants;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class AttachmentUtils {

    public static StringBuilder getAttachmentContent(CommandEvent event) {
        return getAttachmentContent(event, null);
    }

    public static StringBuilder getAttachmentContent(GuildMessageReceivedEvent event) {
        return getAttachmentContent(null, event);
    }

    private static StringBuilder getAttachmentContent(CommandEvent e1, GuildMessageReceivedEvent e2) {
        List<Message.Attachment> attachmentList;
        MessageChannel channel;
        Message message;
        if (e1 != null && e2 == null) {
            attachmentList = e1.getMessage().getAttachments();
            channel = e1.getChannel();
            message = e1.getMessage();
        } else if (e1 == null && e2 != null) {
            attachmentList = e2.getMessage().getAttachments();
            channel = e2.getChannel();
            message = e2.getMessage();
        } else {
            return null;
        }
        StringBuilder builder = new StringBuilder();
        attachmentList.get(0).retrieveInputStream().thenAccept(
                inputStream -> {
                    try {
                        byte[] buf = new byte[1024];
                        int count = 0;
                        while ((count = inputStream.read(buf)) > 0) {
                            builder.append(new String(buf, 0, count));
                        }

                        inputStream.close();
                    } catch (IOException e) {
                        channel.sendMessage(EmbeddedUtils.buildErrorEmbed(DKPConstants.DKP_ERROR_FILE_TITLE,
                                DKPConstants.DKP_ERROR_FILE_DESCRIPTION,
                                e.getMessage(), null)).queue(
                                success -> {
                                    success.delete().queueAfter(20, TimeUnit.SECONDS);
                                    message.addReaction("❌").queueAfter(20, TimeUnit.SECONDS);
                                }
                        );
                        return;
                    }
                }).join();
        return builder;
    }
}
