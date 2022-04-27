package org.pleo.challenge.dto.users;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.Data;
import org.pleo.challenge.dto.Payload;
import org.pleo.challenge.dto.cards.CardsPayload;

@Data
@Entity
@Table(name = "USERS")
public class UsersPayload extends Payload {

    @JsonProperty
    @Column(name = "NAME")
    private String name;

    @JsonProperty
    @Column(name = "ADDRESS")
    private String address;

    @JsonProperty
    @Column(name = "JOB")
    private String job;

    @JsonProperty
    @Column(name = "SCORE")
    private BigDecimal score;

    @JsonIgnore
    @OneToMany(mappedBy="user", fetch = FetchType.LAZY)
    private Set<CardsPayload> cards;
}
