package org.pleo.challenge.dto;

import lombok.Data;

@Data
public abstract class Event<T extends Payload> {

    private T payload;

    private Metadata metadata;
}
