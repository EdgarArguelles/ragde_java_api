package ragde.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@ToString(of = "id")
@EqualsAndHashCode(of = "id")
@MappedSuperclass
public abstract class Model {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "bigint")
    @Getter
    @Setter
    protected String id;

    @Column(name = "created_at")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    @Getter
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    @Getter
    private LocalDateTime updatedAt;

    @PrePersist
    public void createdAt() {
        this.createdAt = this.updatedAt = LocalDateTime.now(ZoneOffset.UTC);
    }

    @PreUpdate
    public void updatedAt() {
        this.updatedAt = LocalDateTime.now(ZoneOffset.UTC);
    }
}