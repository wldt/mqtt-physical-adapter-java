package it.wldt.adapter.mqtt.physical.topic.outgoing;

import java.util.function.Function;

/**
 * Represents an MQTT topic for outgoing actions in the context of a Digital Twin.
 * This class extends DigitalTwinOutgoingTopic and provides specialized functionality
 * for handling outgoing actions with a specific payload type.
 *
 * @param <T> The type of the payload associated with the action.
 * @author Marco Picone, Ph.D. - picone.m@gmail.com, Marta Spadoni University of Bologna
 */
public class ActionOutgoingTopic<T> extends DigitalTwinOutgoingTopic {

    /**
     * Constructs an ActionOutgoingTopic with the specified MQTT topic and a function
     * to convert the action payload into the MQTT message body.
     *
     * @param topic              The MQTT topic associated with outgoing actions.
     * @param actionBodyConsumer A function to convert the action payload into the MQTT message body.
     */
    public ActionOutgoingTopic(String topic, Function<T, String> actionBodyConsumer) {
        super(topic, actionWldtEvent -> actionBodyConsumer.apply((T) actionWldtEvent.getBody()));
    }
}
