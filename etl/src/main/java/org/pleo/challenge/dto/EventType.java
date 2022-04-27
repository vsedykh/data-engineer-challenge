package org.pleo.challenge.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum EventType {

    @JsonProperty("card")
    CARD,

    @JsonProperty("user")
    USER
}
