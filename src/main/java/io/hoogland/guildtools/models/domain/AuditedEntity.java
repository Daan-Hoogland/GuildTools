package io.hoogland.guildtools.models.domain;

import com.opencsv.bean.CsvIgnore;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@Data
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class AuditedEntity {
    @CsvIgnore
    @Column(name = "created_date", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdDate;

    @CsvIgnore
    @Column(name = "modified_date")
    @UpdateTimestamp
    private LocalDateTime modifiedDate;
}
