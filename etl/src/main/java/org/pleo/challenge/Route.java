package org.pleo.challenge;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class Route extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("file:{{card.events.folder}}?moveFailed=error&startingDirectoryMustExist=true&preMove=inprogress&move=done&includeExt=json").log("${body}")
            .to("kafka:cards?brokers=kafka:9092");

        from("kafka:cards?brokers=kafka:9092").log("${body}");
    }
}
