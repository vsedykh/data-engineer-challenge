package org.pleo.challenge.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.lang3.StringUtils;

import static org.pleo.challenge.Constants.EVENT_ID;

public class GetEventId implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {
        exchange.setProperty(
            EVENT_ID,
            StringUtils.removeEnd(
                exchange.getIn().getHeader(Exchange.FILE_NAME_CONSUMED, String.class),
                ".json"
            ));
    }
}
