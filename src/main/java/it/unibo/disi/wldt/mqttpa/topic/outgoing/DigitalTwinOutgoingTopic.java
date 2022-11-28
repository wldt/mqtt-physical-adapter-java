package it.unibo.disi.wldt.mqttpa.topic.outgoing;


import it.unibo.disi.wldt.mqttpa.topic.MqttTopic;
import it.unimore.dipi.iot.wldt.adapter.physical.event.PhysicalAssetActionWldtEvent;

public class DigitalTwinOutgoingTopic<T> extends MqttTopic {
    private final MqttPublishFunction<T> publishFunction;

    public DigitalTwinOutgoingTopic(String topic, MqttPublishFunction<T> publishFunction) {
        super(topic);
        this.publishFunction = publishFunction;
    }

    public String applyPublishFunction(PhysicalAssetActionWldtEvent<T> actionWldtEvent){
        return this.publishFunction.apply(actionWldtEvent);
    }

    public MqttPublishFunction<T> getPublishFunction() {
        return publishFunction;
    }
}
