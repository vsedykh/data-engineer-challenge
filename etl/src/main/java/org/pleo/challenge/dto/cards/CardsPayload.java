package org.pleo.challenge.dto.cards;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import lombok.Data;
import org.pleo.challenge.dto.Payload;
import org.pleo.challenge.dto.users.UsersPayload;

@Data
@Entity
@Table(name = "CARDS")
public class CardsPayload extends Payload {

    @JsonProperty("user_id")
    @Transient
    private Integer userId;

    @JsonProperty("created_by_name")
    @Column(name = "CREATED_BY_NAME")
    private String createdByName;

    @JsonProperty("updated_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
    @Column(name = "UPDATED_AT")
    private Date updatedAt;

    @JsonProperty("created_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
    @Column(name = "CREATED_AT")
    private Date createdAt;

    @JsonProperty
    private Boolean active;

    @JsonIgnore
    @ManyToOne(optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "USER_ID")
    private UsersPayload user;

    public void setUserId(Integer userId) {
        this.userId = userId;
        this.user = new UsersPayload();
        this.user.setId(userId);
    }

    public void setUser(UsersPayload user) {
        this.user = user;
        this.userId = user.getId();
    }
}
