package it.unibo.disi.wldt.mqttpa.topic.incoming;

import it.unibo.disi.wldt.mqttpa.topic.MqttTopic;
import it.unimore.dipi.iot.wldt.core.event.WldtEvent;

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
