package it.wldt.adapter.mqtt.physical.topic.incoming;

import it.wldt.adapter.physical.event.PhysicalAssetEventWldtEvent;
import it.wldt.exception.EventBusException;

import java.util.Collections;
import java.util.function.Function;

/**
 * Represents an MQTT topic for incoming events in the context of a Digital Twin.
 * This class extends DigitalTwinIncomingTopic and provides specialized functionality
 * for handling events with a specific key and payload type.
 *
 * @param <T> The type of the payload associated with the event.
 * @author Marco Picone, Ph.D. - picone.m@gmail.com, Marta Spadoni University of Bologna
 */
public class EventIncomingTopic<T> extends DigitalTwinIncomingTopic {

    /**
     * Constructs an EventIncomingTopic with the specified MQTT topic, event key, and a function
     * to produce the event body from the MQTT message payload.
     *
     * @param topic              The MQTT topic associated with incoming events.
     * @param eventKey           The key associated with the incoming event.
     * @param eventBodyProducer  A function to produce the event body from the MQTT message payload.
     */
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
