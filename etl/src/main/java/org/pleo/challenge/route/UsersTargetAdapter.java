package org.pleo.challenge.route;

import static org.pleo.challenge.Constants.HEADERS;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jsonvalidator.JsonValidationException;
import org.apache.camel.component.kafka.KafkaConstants;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.pleo.challenge.dto.cards.CardsEvent;
import org.pleo.challenge.dto.cards.CardsPayload;
import org.pleo.challenge.dto.users.UsersEvent;
import org.pleo.challenge.dto.users.UsersPayload;
import org.springframework.stereotype.Component;


/**
    The route describes Cards Target Adapter logic. It reads events from cards kafka topic,
    validates them by schema and try to put to DB table. If event is not valid or during the
    message will be sent to Dead-letter queue (DQL) cards_dlq. Invalid message will not be
    retried. In case of any other error, the message will be retried 2 times, and then also
    send to DLQ
*/
@Component
public class UsersTargetAdapter extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        onException(Exception.class)
            .maximumRedeliveries(2)
            .logExhausted(true).logStackTrace(true).retryAttemptedLogLevel(LoggingLevel.WARN)
            .log(LoggingLevel.ERROR,
                String.format(
                    "Event ${%s.%s} processing is failed. Exception: ${exception.message}",
                    HEADERS, KafkaConstants.KEY
                )
            ).to("kafka:cards_dlq?brokers=kafka:9092")
            .handled(true);


        from("kafka:users?brokers=kafka:9092&autoOffsetReset=earliest").routeId("UsersTargetAdapter")
            .log(String.format("Processing event: ${%s.%s}", HEADERS, KafkaConstants.KEY))
            .unmarshal().json(JsonLibrary.Jackson, UsersEvent.class)
            .setBody(exchange -> exchange.getIn().getBody(UsersEvent.class).getPayload())
            .to("jpa:" + UsersPayload.class.getName() + "?usePersist=false")
        ;
    }
}
