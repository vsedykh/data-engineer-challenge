package org.pleo.challenge.route;

import static org.pleo.challenge.Constants.EVENT_ID;
import static org.pleo.challenge.Constants.EX_PROP;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.kafka.KafkaConstants;
import org.pleo.challenge.processor.GetEventId;
import org.springframework.stereotype.Component;

/**
    Route describes logic of Cards Source adapter. It is responsible for reading events from
    cards folder and publish to kafka topic. In case of error there will 3 redelivery attempts,
    then failed file will be moved to 'error' subfolder. If a file processed successfully it will
    be moved to 'done' subfolder.
 */
@Component
public class CardsSourceAdapter extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        //exception processing logic
        onException(Exception.class).maximumRedeliveries(3)
            .retryAttemptedLogLevel(LoggingLevel.WARN)
            .log(
                LoggingLevel.ERROR,
                String.format("Processing of event ${%s.%s} failed", EX_PROP,  EVENT_ID))
            .logExhausted(true);

        //main flow logic
        from("file:{{events.folder.cards}}?"
                + "moveFailed=error&startingDirectoryMustExist=true&preMove=inprogress&move=done"
                + "&includeExt=json").routeId("CardsSourceAdapter")
            .process(new GetEventId())
            .log(String.format("Processing event: ${%s.%s}", EX_PROP, EVENT_ID))
            .setHeader(KafkaConstants.KEY, exchangeProperty(EVENT_ID))
            .to("kafka:cards?brokers=kafka:9092");
    }
}
