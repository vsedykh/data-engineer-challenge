package org.pleo.challenge.route;

import static org.pleo.challenge.Constants.HEADERS;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jsonvalidator.JsonValidationException;
import org.apache.camel.component.kafka.KafkaConstants;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.pleo.challenge.dto.cards.CardsEvent;
import org.pleo.challenge.dto.cards.CardsPayload;
import org.springframework.stereotype.Component;


/**
    The route describes Cards Target Adapter logic. It reads events from cards kafka topic,
    validates them by schema and try to put to DB table. If event is not valid or during the
    message will be sent to Dead-letter queue (DQL) cards_dlq. Invalid message will not be
    retried. In case of any other error, the message will be retried 2 times, and then also
    send to DLQ
*/
@Component
public class CardsTargetAdapter extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        onException(JsonValidationException.class)
            .maximumRedeliveries(0)
            .log(LoggingLevel.ERROR,
                String.format(
                    "Event ${%s.%s} is not valid. Exception: ${exception.message}",
                    HEADERS, KafkaConstants.KEY
                )
            ).to("kafka:cards_dlq?brokers=kafka:9092")
            .handled(true);

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


        from("kafka:cards?brokers=kafka:9092").routeId("CardsTargetAdpater")
            .log(String.format("Processing event: ${%s.%s}", HEADERS, KafkaConstants.KEY))
            .to("json-validator:schema/cards-event-schema.json")
            .unmarshal().json(JsonLibrary.Jackson, CardsEvent.class)
            .setBody(exchange -> exchange.getIn().getBody(CardsEvent.class).getPayload())
            .to("jpa:" + CardsPayload.class.getName() + "?usePersist=false")
        ;
    }
}
