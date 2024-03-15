package it.wldt.adapter.mqtt.physical.topic.incoming;

import it.wldt.adapter.physical.event.PhysicalAssetPropertyWldtEvent;
import it.wldt.exception.EventBusException;

import java.util.Collections;
import java.util.function.Function;

/**
 * Represents an MQTT topic for incoming property updates in the context of a Digital Twin.
 * This class extends DigitalTwinIncomingTopic and provides specialized functionality
 * for handling property updates with a specific key and payload type.
 *
 * @param <T> The type of the payload associated with the property.
 * @author Marco Picone, Ph.D. - picone.m@gmail.com, Marta Spadoni University of Bologna
 */
public class PropertyIncomingTopic<T> extends DigitalTwinIncomingTopic {

    /**
     * Constructs a PropertyIncomingTopic with the specified MQTT topic, property key,
     * and a function to convert the MQTT message payload into the property value.
     *
     * @param topic              The MQTT topic associated with incoming property updates.
     * @param propertyKey       The key of the property.
     * @param propertyValueProducer  A function to convert the MQTT message payload into the property value.
     */
    public PropertyIncomingTopic(String topic, String propertyKey, Function<String, T> propertyValueProducer) {
        super(topic, topicMsgPayload -> {
            try {
                return Collections.singletonList(new PhysicalAssetPropertyWldtEvent<>(propertyKey, propertyValueProducer.apply(topicMsgPayload)));
            } catch (EventBusException e) {
                e.printStackTrace();
            }
            return null;
        });
    }
}
