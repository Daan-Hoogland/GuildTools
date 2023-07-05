package io.hoogland.guildtools.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import io.hoogland.guildtools.constants.Constants;
import io.hoogland.guildtools.models.domain.ReactionRole;
import io.hoogland.guildtools.models.domain.ReactionRoleMessage;
import io.hoogland.guildtools.models.repositories.ReactionRoleMessageRepository;
import io.hoogland.guildtools.models.repositories.ReactionRoleRepository;
import io.hoogland.guildtools.utils.BeanUtils;
import net.dv8tion.jda.api.EmbedBuilder;

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
                            "**Trial**\nWhen first joining the guild you will be subject to a trial period lasting a minimum of 3 weeks (3 raid resets).\n\n" +
                            "During this period of time we'll look at your performance and preparation for each raid, and how well you are able to take on various tasks. " +
                            "During this period you have a lower priority on loot vs existing members, although this will change once you get to your last week of the trial." +
                            "At the end of the trial period you'll be promoted to member, be demoted to social, or be told that your trial period is extended, and what you can improve on during the extended trial.\n\n" +
                            "If you're wondering about how you're doing at any point during your trial, message any of the officers or class leaders.\n\n" +
                            "**Member & Veteran**\nMember rank is the rank you'll be promoted to after you pass a trial. This will give you equal priority on loot to everyone else.\n\n" +
                            "Veteran rank is for the people that have been in the guild for an extended period of time (roughly 2 months). This rank offers **no benefit** over being a Member.");
            ranksEmbed.setThumbnail("https://wow.zamimg.com/images/wow/icons/large/inv_misc_tabardpvp_03.jpg");
            ranksEmbed.setColor(Color.decode("#00ADEF"));

            event.getChannel().sendMessage(ranksEmbed.build()).complete();

            EmbedBuilder lootEmbed = new EmbedBuilder();
            lootEmbed.setTitle("Loot");
            lootEmbed.setDescription(
                    "Loot is distributed based on the decision of a loot council. This loot council consists of all the class leaders, officers and guild master together. " +
                            "While the individual officers can't possibly know all the ins and outs of all classes, the class leaders will provide sufficient info for them to make an informed decision.\n\n" +

                            "These decisions are made based on class, role, attendance, world buffs and general performance in raids (not slowing the raid down, dying on purpose etc.).\n\n" +
                            "If the user is still missing pre-raid BiS gear they **will not** be considered for an item. " +
                            "Spending a lot of time getting upgrades outside of raids (PvP, Rep etc) will **not** lower your prio on any item, since it increases the amount of effort you put into improving your character.\n\n" +
                            "Getting an item will not necessarily lower your priority on other items, depending on how powerful the received item is. " +
                            "Raid preparation includes consumables, protection potions and world buffs. World buffs are tracked by the officers in a spreadsheet which will be available to everyone once its finished.\n\n" +

                            "Attendance plays a big factor in loot decisions as well. People not showing up to farm raids where others might still need gear that benefits the raid as a whole will be given a lower priority on loot in regards to the attendance factor. " +
                            "Old raids are boring, but cleared faster when everyone helps out properly.\n\n" +
                            "Offspec and PvP items are rolled off, unless there is a case to be made for a specific person to receive said item. Such as someone that often offtanks on trash receiving a key tank item before others.\n\n" +

                            "The loot priority (rank wise) is as follows:\n```Veteran/Member > Trial > Veteran/Member offspec = Alt```\n" +

                            "Besides the factors mentioned above, there is also a general class priority. This class priority might be ignored by the council if they feel like someone else will benefit from it more.\n\n" +
                            String.format(Constants.LINK, "**The class priority for loot can be found by clicking this link.**",
                                    "https://docs.google.com/spreadsheets/d/1EFN6py9TkeaKVMr8wTERduMnOb93enmsx8nLGxxcyCo") + "\n\n");

            lootEmbed.addField("Recipes/Formulas",
                    "Recipes and formulas will be given to the designated guild crafter/enchanter for that specific profession. Any drops following the first drop are rolled between everyone with the profession.",
                    false);
            lootEmbed.addField("Issues",
                    "Any problems regarding loot can be brought up to any council member in private. Any suggestions in regards to class priority can be posted in " +
                            String.format(Constants.MENTION_CHANNEL, "665926785086521344") + " or " +
                            String.format(Constants.MENTION_CHANNEL, "669648077681852418") + ", or to any council member in private.", false);
            lootEmbed.setThumbnail("https://wow.zamimg.com/images/wow/icons/large/inv_misc_ornatebox.jpg");
            lootEmbed.setColor(Color.decode("#a335ee"));

            event.getChannel().sendMessage(lootEmbed.build()).complete();

            EmbedBuilder raidEmbed = new EmbedBuilder();
            raidEmbed.setTitle("Raids");
            raidEmbed.setDescription(
                    "Raid sign-ups are available in the Discord channel for the given day. If you do not sign up for the raid we cannot guarantee you a spot. **Raid signups close 24 hours before the raid, after which the roster will be posted.**\n\n" +
                            "If as a member your performance is below what we expect from you, or consistently lower than other players of your class, you *may* be demoted back to trial until your performance has increased.\n\n" +
                            "We expect all members to come prepared with the relevant consumables for their class along with enchanted gear and hold an acceptable attendance level. " +
                            "Alts are welcome in raids specified by the raid leader and have to fit in with the composition. Alts will also be held to the same standard as raiders, which means consumes and world buffs. Alts can be declined based on these expectations not being met.\n\n" +
                            "Our current raid times are\n" +
                            ":small_orange_diamond: Wednesday 20:00-23:00 ST\n" +
                            ":small_orange_diamond: Sunday 20:00-23:00 ST\n" +
                            "_Additional raids may be organized in the events of a speed run or new raid release._\n\n" +
                            "Invites will start 30 minutes early, at 19:30. If we're doing MC/BWL we will fly at 19:45 from **Stormwind** to **Morgans Vigil, Burning Steppes**.\nAQ flight information will be added once we know how we'll get to AQ.\n\n" +
                            "Additionally, if you've been inactive for an extended period of time (1+ month) and wish to partake in raids again, you will be subject to a new trial period lasting 2 weeks.");
            raidEmbed.setThumbnail("https://wow.zamimg.com/images/wow/icons/large/inv_misc_head_dragon_01.jpg");
            raidEmbed.setColor(Color.decode("#e8412e"));

            event.getChannel().sendMessage(raidEmbed.build()).complete();

            EmbedBuilder addonEmbed = new EmbedBuilder();
            addonEmbed.setTitle("Required addons");
            addonEmbed.setDescription("Below is a list of all the addons we require to raid. DBM and Details should be self explanatory. " +
                    "RCLootCouncil is used for loot, and without it you will **not** be eligible for loot off the bosses.\n\n" +
                    ":small_orange_diamond: " +
                    String.format(Constants.LINK, "RCLootCouncil", "https://www.curseforge.com/wow/addons/rclootcouncil-classic") + "\n" +
                    ":small_orange_diamond: " +
                    String.format(Constants.LINK, "Details", "https://www.curseforge.com/wow/addons/details-damage-meter-classic-wow") +
                    "\n" +
                    ":small_orange_diamond: " + String.format(Constants.LINK, "DBM", "https://www.curseforge.com/wow/addons/deadly-boss-mods") +
                    "\n" +
                    ":small_orange_diamond: " + String.format(Constants.LINK, "Salad_Cthun", "https://www.curseforge.com/wow/addons/salad_cthun") +
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
                    "If the items drop from a raid then only the people that actively participate in said raid are eligible to purchase the item at a discounted rate.\n\n" +
                    "Uncommon items will be disenchanted. Enchanting mats are also available from the guild bank with a discount.\n\n" +
                    "The gold in the guild bank will be used to purchase any recipes we're missing as a guild, providing consumables to guildies " +
                    "at a discounted rate if the prices soar too high on the AH, or reimbursements for flasks or other specific consumables during a speedrun *(only when indicated by an officer)*.\n\n" +
                    "In addition to all of this, there are 2 items that are gold bid by default. These items are the Onyxia bag and the bag from the panther boss in ZG. " +
                    "Bids are placed with 5 gold increments to speed up the process.\n\n" +
                    String.format(Constants.LINK, "**The guild bank contents can be found here.**", "https://classicguildbank.com/#/guild/readonly/UMwgBTbSLkaouGQyWuEcmA"));
            gbEmbed.setThumbnail("https://wow.zamimg.com/images/wow/icons/large/inv_box_02.jpg");
            gbEmbed.setColor(Color.decode("#fcba03"));
            gbEmbed.setFooter("Rules last updated on 14-11");


            event.getChannel().sendMessage(gbEmbed.build()).queue(
                    success -> {
                        ReactionRole role = new ReactionRole();
                        role.setRoleId(event.getMessage().getMentionedRoles().get(0).getIdLong());
                        role.setCreatorId(event.getMember().getIdLong());
                        role.setType(2);
                        role.setEmojiId("✅");

                        List<ReactionRole> roles = new ArrayList<>();
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
}
