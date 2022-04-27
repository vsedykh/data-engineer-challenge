package org.pleo.challenge.dto;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import lombok.Data;

@Data
@MappedSuperclass
public abstract class Payload {

    @Id
    private Integer id;
}
