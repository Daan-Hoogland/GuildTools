package io.hoogland.guildtools.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import io.hoogland.guildtools.constants.Constants;
import io.hoogland.guildtools.models.ReactionRole;
import io.hoogland.guildtools.models.ReactionRoleMessage;
import io.hoogland.guildtools.models.repositories.ReactionRoleMessageRepository;
import io.hoogland.guildtools.models.repositories.ReactionRoleRepository;
import io.hoogland.guildtools.utils.BeanUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;

import java.util.ArrayList;
import java.util.List;

public class RulesCmd extends Command {

    private ReactionRoleMessageRepository reactionRoleMessageRepository = BeanUtils.getBean(ReactionRoleMessageRepository.class);
    private ReactionRoleRepository reactionRoleRepository = BeanUtils.getBean(ReactionRoleRepository.class);

    public RulesCmd() {
        this.name = "rules";
        this.help = "displays the rules.";
        this.ownerCommand = true;
    }

    @Override
    protected void execute(CommandEvent event) {
        if (!event.getMessage().getMentionedRoles().isEmpty()) {
            event.getChannel().sendMessage(getRulesEmbed()).queue(
                    success -> {
                        ReactionRole role = new ReactionRole();
                        role.setRoleId(event.getMessage().getMentionedRoles().get(0).getIdLong());
                        role.setCreatorId(event.getMember().getIdLong());
                        role.setType(2);
                        role.setEmojiId("✅");

                        List<ReactionRole> roles = new ArrayList<>();
                        ReactionRole savedRole = reactionRoleRepository.saveAndFlush(role);
                        roles.add(savedRole);

                        ReactionRoleMessage message = new ReactionRoleMessage();
                        message.setChannelId(event.getChannel().getIdLong());
                        message.setGuildId(event.getGuild().getIdLong());
                        message.setMessageId(success.getIdLong());
                        message.setDirectLink(success.getJumpUrl());
                        message.setRoles(roles);

                        success.addReaction("✅").queue(
                                succ -> {
                                    ReactionRoleMessage savedMsg = reactionRoleMessageRepository.saveAndFlush(message);
                                    savedRole.setReactionRoleMessage(savedMsg);
                                    reactionRoleRepository.saveAndFlush(savedRole);
                                }
                        );
                    }
            );
            event.getMessage().delete().queue();
        }
    }

    private MessageEmbed getRulesEmbed() {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("<buying gf> Guild Rules");

        embed.addField("Loot",
                "DKP is used to distribute loot within the guild. People are awarded DKP for attending raids and participating in guild events.\n\n" +
                        "When a piece of loot drop, everyone bids a part of their DKP. The highest bidder will receive the item, and the DKP will be deducted from that players total.\n\n" +
                        "The loot priority is as follows:\n`Core Raider => Raider > Trial`\nThis means that trials cannot get items unless raiders pass on the item.\n\n" +
                        "Pugs can win 1 item per raid. If a member of the guild wins the roll, the item is distributed using DKP.\n\n" +
                        "**Class loot priority can be found " + String.format(Constants.LINK, "here",
                        "https://docs.google.com/spreadsheets/d/1EFN6py9TkeaKVMr8wTERduMnOb93enmsx8nLGxxcyCo/edit?usp=sharing") + "**",
                false);

        embed.addField("Extra rewards & punishments", "There are multiple ways to get extra DKP rewards besides killing bosses.\n\n" +
                "- For every minute the raid is cleared faster than the previous record, everyone part of that raid will receive 1 DKP.\n" +
                "- Participating in guild events will receive DKP based on what kind of event it is. Got an idea for an event? Message an officer about it." +
                "- Occasionally extra DKP may be given out for good performance.\n\n" +
                "Just like rewards, there are also situations where DKP may be taken away from a player. These situations include:\n" +
                "- Consistent bad performance.\n" +
                "- Consistently not using consumables, even after an officer has warned the player.\n" +
                "- Consistently wiping the raid or failing on a mechanic.\n", false);

        embed.addField("Raids",
                "Raid sign-ups are available in the Discord channel for the given day. If you do not sign up for the raid **we cannot guarantee you a spot**, as we might've already found a pug to replace you for that raid.\n\n" +
                        "If you consistently don't show up to raids and don't sign absent, or sign up and don't show up, you'll be given a -50 DKP penalty.\n\n" +
                        "If as a raider your performance is below what we expect from you, or consistently lower than other players of your class, you *may* be demoted back to trial until your performance has increased.\n\n" +
                        "We expect all raiders to come prepared with the relevant consumables for their class along with enchanted gear.\n\n" +
                        "Our current raid times are\n" +
//                        "```" +
                        "Wednesday 20:00-23:00 ST (invites 19:30)\n" +
                        "Sunday 19:00-22:00 ST (invites 18:30)\n" +
                        "Monday 20:00-22:00 ST (during progress, invites 19:30)",
//                        "```",
                false);

        embed.addField("Required addons",
                "- " + String.format(Constants.LINK, "Details", "https://www.curseforge.com/wow/addons/details-damage-meter-classic-wow") + "\n" +
                        "- " + String.format(Constants.LINK, "DBM", "https://www.curseforge.com/wow/addons/deadly-boss-mods") + "\n" +
                        "- " + String.format(Constants.LINK, "ClassicCastbars", "https://www.curseforge.com/wow/addons/classiccastbars") + "\n" +
                        "- " + String.format(Constants.LINK, "Monolith DKP", "https://www.curseforge.com/wow/addons/monolith-dkp") + "\n" +
                        "- " + String.format(Constants.LINK, "HealComm", "https://www.wowace.com/projects/libhealcomm-4-0") + " or any addon that includes HealComm (Healers)\n" +
                        "- " + String.format(Constants.LINK, "Decursive", "https://www.curseforge.com/wow/addons/decursive") + " (Healers, useful)\n"
                ,
                false);

        embed.addField("Trials",
                "Trials will last for 2 main raid clears and 3 Onyxia's. During this time, the trial is expected to bring relevant consumables for every boss. A list of this consumables can be found in the " +
                        String.format(Constants.MENTION_CHANNEL, "649302512394043392") +
                        " channel. During the trial period you're expected to attend every raid, unless you've informed an officer about your absence. " +
                        "Before getting promoted to raider, we also expect you to have either full pre-raid BiS, or at least having shown you put effort into obtaining your pre-raid BiS.\n\n" +
                        "Don't be afraid to ask an officer how your trial is progressing, or how you can improve. ",
                false);

        embed.addField("Core Raiders",
                "Core Raider is the rank above regular raider. People with this rank will have priority on certain rare/powerful items over regular raiders. " +
                        "The requirements to get promoted to core raider are different for every role and class. For specifics, ask an officer. The general requirements can be found below.\n\n" +
                        "- Consumables for every fight\n" +
                        "- Consistently joining raids\n" +
                        "- Competitive performance for your item level",
                false);

        embed.addField("Guild Bank",
                "All BoEs and materials will be put in the guild bank, with the exception of tier pieces. This includes both rares and epics. The rare BoE drops are purchasable for DKP, with a starting bid of 25 DKP unless stated otherwise by an officer.\n\n" +
                        "The gold in the guild bank will be used to purchase recipes to craft consumables for specific classes/bosses, buying items for legendaries and repair bots in raids.\n\n" +
                        "Any other item in the guild bank is also available for purchase at 50-75% of AH price depending on the item. The guild bank can be accessed " +
                        String.format(Constants.LINK, "by visiting this website",
                                "https://classicguildbank.com/#/guild/readonly/UMwgBTbSLkaouGQyWuEcmA") +
                        "\n\nIn addition, the Onyxia bag is distributed using gold DKP, meaning you bid gold in 5 gold increments and the highest bidder wins. This gold goes to the guild bank as well.",
                false);

        embed.addField("",
                "**Any updates to these rules will be announced in the " + String.format(Constants.MENTION_CHANNEL, "631952246657122306") + " channel.**\n\n" +
                        "**To join the raid voice channel, and to sign up for the raids, you must agree with the rules. To do so, react with the ✅ emoji.**",
                false);

        embed.setFooter("Last updated on 5-1-2020");

        embed.setThumbnail("https://i.imgur.com/nytjlah.jpg");
        return embed.build();
    }
}
