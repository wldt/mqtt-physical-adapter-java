package it.unibo.disi.wldt.mqttpa.topic.outgoing;


import it.unibo.disi.wldt.mqttpa.topic.MqttTopic;
import it.unimore.dipi.iot.wldt.adapter.physical.event.PhysicalAssetActionWldtEvent;

public class DigitalTwinOutgoingTopic extends MqttTopic {
    private final MqttPublishFunction publishFunction;

    public DigitalTwinOutgoingTopic(String topic, MqttPublishFunction publishFunction) {
        super(topic);
        this.publishFunction = publishFunction;
    }

    public <T> String applyPublishFunction(PhysicalAssetActionWldtEvent<T> actionWldtEvent){
        return this.publishFunction.apply(actionWldtEvent);
    }

    public MqttPublishFunction getPublishFunction() {
        return publishFunction;
    }
}
