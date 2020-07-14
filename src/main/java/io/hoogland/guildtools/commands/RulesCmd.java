package io.hoogland.guildtools.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import io.hoogland.guildtools.App;
import io.hoogland.guildtools.constants.Constants;
import io.hoogland.guildtools.models.domain.ReactionRole;
import io.hoogland.guildtools.models.domain.ReactionRoleMessage;
import io.hoogland.guildtools.models.repositories.ReactionRoleMessageRepository;
import io.hoogland.guildtools.models.repositories.ReactionRoleRepository;
import io.hoogland.guildtools.utils.BeanUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;
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

            EmbedBuilder ranksEmbed = new EmbedBuilder();
            ranksEmbed.setTitle("Ranks");
            ranksEmbed.setDescription(
                    "There are 3 relevant ranks to a raider. Trial, Member and Veteran.\n\n" +
                            "**Trial**\nWhen first joining the guild you will be subject to a trial period lasting a minimum of 2 weeks (2 raid resets).\n\n" +
                            "During this period of time we'll look at your performance and preparation for each raid, and how well you are able to take on various tasks. " +
                            "At the end of the trial period you'll be promoted to member or be told that your trial period is extended, and what you can improve on during the extended trial.\n\n" +
                            "If you're wondering about how you're doing at any point during your trial, message any of the officers or class leaders.\n\n" +
                            "**Member & Veteran**\nMember rank is the rank you'll be promoted to after you pass a trial. This will give you equal priority on loot to everyone else.\n\n" +
                            "Veteran rank is for the people that have been in the guild for an extended period of time (2+ months). This rank offers **no benefit** over being a Member.");
            ranksEmbed.setThumbnail("https://wow.zamimg.com/images/wow/icons/large/inv_misc_tabardpvp_03.jpg");
            ranksEmbed.setColor(Color.decode("#00ADEF"));

            event.getChannel().sendMessage(ranksEmbed.build()).complete();

            EmbedBuilder lootEmbed = new EmbedBuilder();
            lootEmbed.setTitle("Loot");
            lootEmbed.setDescription(
                    "Loot is distributed based on the decision of a loot council. This loot council consists of all the class leaders, officers and guild master together.\n\n" +
                            "These decisions are made based on class, role, your performance, current piece in that gear slot and overall effort you put into preparation of raids. " +
                            "Raid preparation includes consumables, protection potions and world buffs. World buffs are tracked by the officers in a spreadsheet available <TODO HERE>.\n\n" +
                            "Besides the factors mentioned above, there is also a general class priority. This class priority might be ignored by the council if they feel like someone else will benefit from it more.\n\n" +
                            "**Offspec/PvP**\nOffspec items are also loot counciled. This is based on if the person actually plays the offspec in raids. PvP items are given to people that actively PvP outside of raids or are planning to rank.\n\n" +
                            "**Issues**\nAny problems regarding loot can be brought up to any council member in private. Any suggestions in regards to class priority can be posted in "
                            + String.format(Constants.MENTION_CHANNEL, "665926785086521344") + " or " + String.format(Constants.MENTION_CHANNEL, "669648077681852418") + ", or to any council member in private.");
            lootEmbed.setThumbnail("https://wow.zamimg.com/images/wow/icons/large/inv_misc_ornatebox.jpg");
            lootEmbed.setColor(Color.decode("#a335ee"));

            event.getChannel().sendMessage(lootEmbed.build()).complete();


            EmbedBuilder raidEmbed = new EmbedBuilder();
            raidEmbed.setTitle("Raids");
            raidEmbed.setDescription(
                    "Raid sign-ups are available in the Discord channel for the given day. If you do not sign up for the raid we cannot guarantee you a spot. **Raid signups close 24 hours before the raid, after which the roster will be posted.**\n\n" +
                            "If as a member your performance is below what we expect from you, or consistently lower than other players of your class, you *may* be demoted back to trial until your performance has increased.\n\n" +
                            "We expect all members to come prepared with the relevant consumables for their class along with enchanted gear.\n\n" +
                            "Our current raid times are\n" +
                            ":small_orange_diamond: Wednesday 20:00-23:00 ST\n" +
                            ":small_orange_diamond: Sunday 20:00-23:00 ST\n" +
                            "Invites will start 30 minutes early, at 19:30. If we're doing MC/BWL we will fly at 19:45 from **Stormwind** to **Morgans Vigil, Burning Steppes**.\nAQ flight information will be added once we know how we'll get to AQ.\n\n" +
                            "Additionally, if you've been inactive for an extended period of time (1+ month) and wish to partake in raids again, you will be subject to a new trial period lasting 2 weeks.");
            raidEmbed.setThumbnail("https://wow.zamimg.com/images/wow/icons/large/inv_misc_head_dragon_01.jpg");
            raidEmbed.setColor(Color.decode("#e8412e"));

            event.getChannel().sendMessage(raidEmbed.build()).complete();

//            EmbedBuilder rankEmbed = new EmbedBuilder();
//            rankEmbed.setTitle("Ranks");
//            rankEmbed.setDescription(
//                    "There are 3 relevant ranks for a raider. These ranks are Trial, Member and Veteran. Trial is described above");
//            rankEmbed.setThumbnail("https://wow.zamimg.com/images/wow/icons/large/inv_misc_tabardpvp_03.jpg");
//            rankEmbed.setColor(Color.decode("#00ADEF"));

//            event.getChannel().sendMessage(rankEmbed.build()).complete();

            EmbedBuilder addonEmbed = new EmbedBuilder();
            addonEmbed.setTitle("Required addons");
            addonEmbed.setDescription("Below is a list of all the addons we require to raid. DBM and Details should be self explanatory. " +
                    "RCLootCouncil is used for loot, and without it you will **not** be eligible for loot off the bosses.\n\n" +
                    ":small_orange_diamond: " +
                    String.format(Constants.LINK, "RCLootCouncil", "https://www.curseforge.com/wow/addons/rclootcouncil") + "\n" +
                    ":small_orange_diamond: " +
                    String.format(Constants.LINK, "Details", "https://www.curseforge.com/wow/addons/details-damage-meter-classic-wow") +
                    "\n" +
                    ":small_orange_diamond: " + String.format(Constants.LINK, "DBM", "https://www.curseforge.com/wow/addons/deadly-boss-mods") +
                    "\n" +
                    ":small_orange_diamond: " +
                    String.format(Constants.LINK, "ClassicCastbars", "https://www.curseforge.com/wow/addons/classiccastbars") + "\n" +
                    ":small_orange_diamond: " + String.format(Constants.LINK, "WeakAuras 2", "https://www.curseforge.com/wow/addons/weakauras-2") +
                    "\n" +
                    ":small_orange_diamond: " + String.format(Constants.LINK, "HealComm", "https://www.wowace.com/projects/libhealcomm-4-0") +
                    " or any addon that includes HealComm (Healers)\n" +
                    ":small_orange_diamond: " + String.format(Constants.LINK, "Decursive", "https://www.curseforge.com/wow/addons/decursive") +
                    " (Healers, useful)\n" +
                    ":small_orange_diamond: " + String.format(Constants.LINK, "Showmemyheals", "https://www.curseforge.com/wow/addons/showmemyheal") +
                    " (Healers, useful)\n"
            );
            addonEmbed.setThumbnail("https://wow.zamimg.com/images/wow/icons/large/inv_misc_wrench_01.jpg");
            addonEmbed.setColor(Color.decode("#919191"));

            event.getChannel().sendMessage(addonEmbed.build()).complete();


            EmbedBuilder gbEmbed = new EmbedBuilder();
            gbEmbed.setTitle("Guild Bank");
            gbEmbed.setDescription("All BoE, materials and recipes will be put into the guild bank. This includes both rare and epics. " +
                    "The rare BoE drops are purchasable for a significant discount compared to the auction house depending on the item. " +
                    "Uncommon items will be disenchanted. Enchanting mats are also available from the guild bank with a discount.\n\n" +
                    "The gold in the guild bank will be used to purchase any recipes we're missing as a guild, providing consumables to guildies " +
                    "at a discounted rate if the prices soar too high on the AH, or reimbursements for flasks or other specific consumables during a speedrun *(only when indicated by an officer)*.\n\n" +
                    "In addition to all of this, there are 2 items that are gold bid by default. These items are the Onyxia bag and the bag from the panther boss in ZG. " +
                    "Bids are placed with 5 gold increments to speed up the process.");
            gbEmbed.setThumbnail("https://wow.zamimg.com/images/wow/icons/large/inv_box_02.jpg");
            gbEmbed.setColor(Color.decode("#fcba03"));
            gbEmbed.setFooter("Rules last updated on 10-7-2020");


            event.getChannel().sendMessage(gbEmbed.build()).queue(
                    success -> {
                        ReactionRole role = new ReactionRole();
                        role.setRoleId(event.getMessage().getMentionedRoles().get(0).getIdLong());
                        role.setCreatorId(event.getMember().getIdLong());
                        role.setType(2);
                        role.setEmojiId("✅");

                        List<ReactionRole> roles = new ArrayList<>();
//                        ReactionRole savedRole = reactionRoleRepository.saveAndFlush(role);
                        roles.add(role);

                        ReactionRoleMessage message = new ReactionRoleMessage();
                        message.setChannelId(event.getChannel().getIdLong());
                        message.setGuildId(event.getGuild().getIdLong());
                        message.setMessageId(success.getIdLong());
                        message.setDirectLink(success.getJumpUrl());
                        message.setRoles(roles);

                        success.addReaction("✅").queue(
                                succ -> {
                                    ReactionRoleMessage savedMsg = reactionRoleMessageRepository.saveAndFlush(message);
                                    role.setReactionRoleMessage(savedMsg);
                                    reactionRoleRepository.saveAndFlush(role);
                                }
                        );
                    }
            );
            event.getMessage().delete().queue();
        }
    }

    private MessageEmbed getRulesEmbed() {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("<GOTEM> Guild Rules");

        embed.addField("Loot",
                "EPGP is used to distribute most loot within the guild. People are awarded EP for attending raids and participating in guild events.\n\n" +
                        "The person with the highest ratio (calculated as EP/GP) will receive the item, and the GP cost for the item will be added to that players total, causing their ratio to drop. Once a week both EP and GP are decayed by 10%.\n" +
                        "More information about EPGP can be found " + String.format(Constants.LINK, "here", "http://www.epgpweb.com/help/system") +
                        "\n\n" +
                        "Certain items will be distributed through a loot council. These items are marked as LC in the Class priority spreadsheet below and will be awarded based on performance, rank and attendance.\n\n" +
                        "The loot priority is as follows:\n`Core Raider >= Raider > Trial > Alt = Raider OS`\nThis means that trials cannot get items unless raiders pass on the item.\n" +
                        "\n\n" +
                        "**Class loot priority can be found " + String.format(Constants.LINK, "here",
                        "https://tinyurl.com/guildclassprio") + "**",
                false);

        embed.addField("Extra rewards & punishments", "There are multiple ways to get extra EP rewards besides killing bosses.\n\n" +
                        "- For every minute the raid is cleared faster than the previous record, everyone part of that raid will receive 1 EP.\n" +
                        "- Participating in guild events will receive EP based on what kind of event it is. Got an idea for an event? Message an officer about it." +
                        "- Occasionally extra EP may be given out for extraordinary performance.\n\n" +
                        "Just like rewards, there are also situations where EP may be taken away from a player, or additional GP added. These situations include:\n" +
                        "- Not flying with the raid when we start (-attendance EP)\n" +
                        "- Consistent bad performance.\n" +
                        "- Consistently not using consumables, even after an officer has warned the player.\n" +
                        "- Long absence without a heads up to one of the officers.\n" +
                        "- Consistently wiping the raid or failing on a mechanic.",
                false);

        embed.addField("Raids",
                "Raid sign-ups are available in the Discord channel for the given day. If you do not sign up for the raid we cannot guarantee you a spot. **Raid signups close 24 hours before the raid, after which the roster will be posted.**\n\n" +
                        "If as a raider your performance is below what we expect from you, or consistently lower than other players of your class, you *may* be demoted back to trial until your performance has increased.\n\n" +
                        "We expect all raiders to come prepared with the relevant consumables for their class along with enchanted gear.\n\n" +
                        "Our current raid times are\n" +
                        "Wednesday 20:00-23:00 ST (invites 19:30)\n" +
                        "Sunday 19:00-22:00 ST (invites 18:30)\n" +
                        "Monday 20:00-22:00 ST (during progress, invites 19:30)",
                false);

        embed.addField("Required addons",
                "- " + String.format(Constants.LINK, "CEPGP", "https://www.curseforge.com/wow/addons/cepgp") + "\n" +
                        "- " + String.format(Constants.LINK, "Details", "https://www.curseforge.com/wow/addons/details-damage-meter-classic-wow") +
                        "\n" +
                        "- " + String.format(Constants.LINK, "DBM", "https://www.curseforge.com/wow/addons/deadly-boss-mods") + "\n" +
                        "- " + String.format(Constants.LINK, "ClassicCastbars", "https://www.curseforge.com/wow/addons/classiccastbars") + "\n" +
                        "- " + String.format(Constants.LINK, "WeakAuras 2", "https://www.curseforge.com/wow/addons/weakauras-2") + "\n" +
                        "- " + String.format(Constants.LINK, "HealComm", "https://www.wowace.com/projects/libhealcomm-4-0") +
                        " or any addon that includes HealComm (Healers)\n" +
                        "- " + String.format(Constants.LINK, "Decursive", "https://www.curseforge.com/wow/addons/decursive") + " (Healers, useful)\n"
                ,
                false);

        embed.addField("Trials",
                "Trials will last for 2 full resets (2 BWL, 2 MC, 3 Ony). During this time, the trial is expected to bring relevant consumables for every boss. A list of this consumables can be found in the " +
                        String.format(Constants.MENTION_CHANNEL, "649302512394043392") +
                        " channel. If a trial does not use consumables, they will be given a GP penalty based on how many bosses they have not used consumables on." +
                        " During the trial period you're expected to attend every raid, unless you've informed an officer about your absence. " +
                        "Before getting promoted to raider, we also expect you to have either full pre-raid BiS, or at least having shown you put effort into obtaining your pre-raid BiS.\n\n" +
                        "Don't be afraid to ask an officer how your trial is progressing, or how you can improve. ",
                false);

        embed.addField("Core Raiders",
                "Core Raider is the rank above regular raider. People with this rank will be _higher_ on the priority list for the items marked as LC. " +
                        "The requirements to get promoted to core raider are different for every role and class. " +
                        "For example, healers are expected to have Corehound Belt/Hide of the Wild, while warriors are expected to have Lionheart and Edgemasters if they're non-human. The general requirements can be found below.\n\n" +
                        "- Consumables for every fight\n" +
                        "- Consistently joining raids\n" +
                        "- Competitive performance for your item level",
                false);

        embed.addField("Inactivity",
                "When coming back from a long inactive period, you will be subject to a 1 week trial period no matter what previous rank you held. Exceptions are made for absence the officers are aware of, like holidays etc.\n\n" +
                        "You also may or may not be subject to a penalty in the form of EP or GP, whatever the officers think is fitting.",
                false);

        embed.addField("Guild Bank",
                "All BoEs and materials will be put in the guild bank, with the exception of tier pieces. This includes both rares and epics. The rare BoE drops are purchasable for GP, with a GP value decided on by an officer.\n\n" +
                        "The gold in the guild bank will be used to purchase recipes to craft consumables for specific classes/bosses, buying items for legendaries and repair bots in raids.\n\n" +
                        "Any other item in the guild bank is also available for purchase at 50-75% of AH price depending on the item. The guild bank can be accessed " +
                        String.format(Constants.LINK, "by visiting this website",
                                "https://classicguildbank.com/#/guild/readonly/UMwgBTbSLkaouGQyWuEcmA") +
                        "\n\nIn addition, the Onyxia bag is distributed using gold DKP, meaning you bid gold in 5 gold increments and the highest bidder wins. This gold goes to the guild bank as well.",
                false);

        embed.addField("",
                "**Any updates to these rules will be announced in the " + String.format(Constants.MENTION_CHANNEL, "631952246657122306") +
                        " channel.**\n\n" +
                        "**Missing trial/raider rank? Use the `" + App.client.getPrefix() +
                        "apply [rank]` (without brackets) command to request it.**\n\n" +
                        "**To join the raid voice channel, and to sign up for the raids, you must agree with the rules. To do so, react with the ✅ emoji.**",
                false);

        embed.setFooter("Last updated on 20-3-2020");

        embed.setThumbnail("https://discordemoji.com/assets/emoji/4882_gotem.png");
        return embed.build();
    }
}
