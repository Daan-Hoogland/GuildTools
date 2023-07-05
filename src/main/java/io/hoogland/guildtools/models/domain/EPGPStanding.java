package io.hoogland.guildtools.models.domain;

import com.opencsv.bean.CsvBindByPosition;
import com.opencsv.bean.CsvIgnore;
import io.hoogland.guildtools.constants.Constants;
import io.hoogland.guildtools.constants.DKPConstants;
import io.hoogland.guildtools.constants.EPGPConstants;
import io.hoogland.guildtools.utils.ConfigUtils;
import io.hoogland.guildtools.utils.EmbedUtils;
import io.hoogland.guildtools.utils.EmojiUtils;
import lombok.Data;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Data
@Entity
@Table(name = "epgp_standing", uniqueConstraints = {@UniqueConstraint(columnNames = {"guildId", "player"})})
public class EPGPStanding extends AuditedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @CsvIgnore
    private Long id;

    @CsvIgnore
    private long guildId;

    @CsvBindByPosition(position = 0, required = true)
    private String player;
    @CsvBindByPosition(position = 1, required = true)
    private String clazz;
    @CsvBindByPosition(position = 2, required = true)
    private String guildRank;
    @CsvBindByPosition(position = 3, required = true)
    private int ep;
    @CsvBindByPosition(position = 4, required = true)
    private int gp;
    @CsvBindByPosition(position = 5, required = true)
    private BigDecimal pr;

    @CsvIgnore
    private int previousEp;
    @CsvIgnore
    private int previousGp;
    @CsvIgnore
    private int previousGuildRank;
    @CsvIgnore
    private int previousClassRank;
    @CsvIgnore
    private BigDecimal previousPr;

    public MessageEmbed getMessageEmbed() {
        HashMap classInfo = (HashMap) ((HashMap) ConfigUtils.getConfig().get("icons")).get(this.getClazz().toLowerCase());
        return EmbedUtils
                .createEmbed(EPGPConstants.STANDINGS_TITLE, String.format(EPGPConstants.STANDINGS_DESCRIPTION, StringUtils
                                .capitalize(this.getPlayer().toLowerCase())), this.getFields(),
                        classInfo.get("color").toString(), String.format(DKPConstants.DKP_FOOTER, this.getModifiedDate().format(
                                Constants.DATE_TIME_FORMATTER)), null, classInfo.get("icon").toString());
    }

    public List<MessageEmbed.Field> getFields() {
        List<MessageEmbed.Field> fields = new ArrayList<>();

        int epChange = this.ep - this.previousEp;
        int gpChange = this.gp - this.previousGp;
        BigDecimal prChange = this.pr.subtract(this.previousPr).setScale(2, RoundingMode.HALF_UP);

        fields.add(new MessageEmbed.Field(EPGPConstants.EP, this.ep + " (" + EmojiUtils.getChangeEmoji(epChange) + (epChange) + ")", true));
        fields.add(new MessageEmbed.Field(EPGPConstants.GP, this.gp + " (" + EmojiUtils.getChangeEmoji(gpChange) + (gpChange) + ")", true));
        fields.add(new MessageEmbed.Field(EPGPConstants.PR, this.pr + " (" + EmojiUtils.getChangeEmoji(prChange) + prChange + ")", true));

        return fields;
    }
}
