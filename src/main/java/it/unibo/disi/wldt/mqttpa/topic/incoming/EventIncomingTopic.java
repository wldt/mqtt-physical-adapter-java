package it.unibo.disi.wldt.mqttpa.topic.incoming;

import it.unimore.dipi.iot.wldt.adapter.physical.event.PhysicalAssetEventWldtEvent;
import it.unimore.dipi.iot.wldt.exception.EventBusException;

import java.util.Collections;
import java.util.function.Function;

public class EventIncomingTopic<T> extends DigitalTwinIncomingTopic {
    public EventIncomingTopic(String topic, String eventKey, Function<String, T>eventBodyProducer) {
        super(topic, topicMsgPayload -> {
            try {
                return Collections.singletonList(new PhysicalAssetEventWldtEvent<>(eventKey, eventBodyProducer.apply(topicMsgPayload)));
            } catch (EventBusException e) {
                e.printStackTrace();
            }
            return null;
        } );
    }
}
