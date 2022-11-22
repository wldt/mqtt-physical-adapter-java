package it.unibo.disi.wldt.mqttpa.topic;

import it.unimore.dipi.iot.wldt.core.event.WldtEvent;

import java.util.List;

public class IncomingTopic<T> extends MqttTopic{

    private final MqttSubscribeFunction<T> mqttSubscribeFunction;

    public IncomingTopic(String topic, MqttSubscribeFunction<T> mqttSubscribeFunction) {
        super(topic);
        this.mqttSubscribeFunction = mqttSubscribeFunction;
    }

    public List<WldtEvent<T>> applySubscribeFunction(String topicMessagePayload){
        return mqttSubscribeFunction.apply(topicMessagePayload);
    }
}
