package it.wldt.adapter.mqtt.physical.topic.incoming;

import it.wldt.adapter.mqtt.physical.topic.MqttTopic;
import it.wldt.core.event.WldtEvent;

import java.util.List;

/**
 * Represents an MQTT topic for incoming messages in the context of a Digital Twin.
 * This class extends the generic MqttTopic class and includes a function to handle
 * the subscription process and transform the MQTT payload into a list of WldtEvent objects.
 *
 * @author Marco Picone, Ph.D. - picone.m@gmail.com, Marta Spadoni University of Bologna
 */
public class DigitalTwinIncomingTopic extends MqttTopic {

    /**
     * The function responsible for handling the subscription process and transforming
     * the MQTT payload into a list of WldtEvent objects.
     */
    private final MqttSubscribeFunction mqttSubscribeFunction;

    /**
     * Constructs a DigitalTwinIncomingTopic with the specified topic and MQTT subscription function.
     *
     * @param topic                 The MQTT topic associated with incoming messages.
     * @param mqttSubscribeFunction The function to apply for handling the subscription process.
     */
    public DigitalTwinIncomingTopic(String topic, MqttSubscribeFunction mqttSubscribeFunction) {
        super(topic);
        this.mqttSubscribeFunction = mqttSubscribeFunction;
    }

    /**
     * Applies the subscription function to the provided MQTT message payload.
     * Transforms the payload into a list of WldtEvent objects.
     *
     * @param topicMessagePayload The MQTT message payload to be processed.
     * @return A list of WldtEvent objects resulting from the subscription function.
     */
    public List<WldtEvent<?>> applySubscribeFunction(String topicMessagePayload){
        return mqttSubscribeFunction.apply(topicMessagePayload);
    }

    /**
     * Retrieves the MQTT subscription function associated with this DigitalTwinIncomingTopic.
     *
     * @return The MQTT subscription function.
     */
    public MqttSubscribeFunction getSubscribeFunction() {
        return mqttSubscribeFunction;
    }
}
