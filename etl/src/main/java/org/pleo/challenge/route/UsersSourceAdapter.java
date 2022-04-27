package org.pleo.challenge.route;

import static org.pleo.challenge.Constants.EVENT_ID;
import static org.pleo.challenge.Constants.EX_PROP;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jsonvalidator.JsonValidationException;
import org.apache.camel.component.kafka.KafkaConstants;
import org.pleo.challenge.processor.GetEventId;
import org.springframework.stereotype.Component;

@Component
public class UsersSourceAdapter extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        //exception processing logic
        onException(JsonValidationException.class)
            .maximumRedeliveries(0)
            .log(LoggingLevel.ERROR,
                String.format(
                    "Event ${%s.%s} is not valid. Exception: ${exception.message}",
                    EX_PROP, EVENT_ID
                )
            );

        onException(Exception.class).maximumRedeliveries(3)
            .retryAttemptedLogLevel(LoggingLevel.WARN)
            .log(
                LoggingLevel.ERROR,
                String.format("Processing of event ${%s.%s} failed", EX_PROP, EVENT_ID))
            .logExhausted(true);

        //main flow logic
        from("file:{{events.folder.users}}?"
            + "moveFailed=error&startingDirectoryMustExist=true&preMove=inprogress"
            + "&move=done&includeExt=json").routeId("UsersSourceAdapter")
            .process(new GetEventId())
            .log(String.format("Processing event: ${%s.%s}", EX_PROP, EVENT_ID))
            .to("json-validator:schema/users-event-schema.json")
            .setHeader(KafkaConstants.KEY, exchangeProperty(EVENT_ID))
            .to("kafka:users?brokers=kafka:9092");
    }

}
