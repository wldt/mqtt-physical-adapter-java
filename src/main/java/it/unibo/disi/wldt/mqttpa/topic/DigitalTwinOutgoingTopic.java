package it.unibo.disi.wldt.mqttpa.topic;


import it.unimore.dipi.iot.wldt.adapter.physical.event.PhysicalAssetActionWldtEvent;

public class DigitalTwinOutgoingTopic extends MqttTopic {
    private final MqttPublishFunction publishFunction;

    public DigitalTwinOutgoingTopic(String topic, MqttPublishFunction publishFunction) {
        super(topic);
        this.publishFunction = publishFunction;
    }

    public String applyPublishFunction(PhysicalAssetActionWldtEvent<?> actionWldtEvent){
        return this.publishFunction.apply(actionWldtEvent);
    }
}
