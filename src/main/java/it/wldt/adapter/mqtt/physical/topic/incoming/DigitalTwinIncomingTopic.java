package it.wldt.adapter.mqtt.physical.topic.incoming;

import it.wldt.adapter.mqtt.physical.topic.MqttTopic;
import it.wldt.core.event.WldtEvent;

import java.util.List;

public class DigitalTwinIncomingTopic extends MqttTopic {

    private final MqttSubscribeFunction mqttSubscribeFunction;

    public DigitalTwinIncomingTopic(String topic, MqttSubscribeFunction mqttSubscribeFunction) {
        super(topic);
        this.mqttSubscribeFunction = mqttSubscribeFunction;
    }

    public List<WldtEvent<?>> applySubscribeFunction(String topicMessagePayload){
        return mqttSubscribeFunction.apply(topicMessagePayload);
    }

    public MqttSubscribeFunction getSubscribeFunction() {
        return mqttSubscribeFunction;
    }
}
