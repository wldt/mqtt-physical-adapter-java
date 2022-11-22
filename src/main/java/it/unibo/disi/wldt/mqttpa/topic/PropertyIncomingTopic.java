package it.unibo.disi.wldt.mqttpa.topic;

import it.unimore.dipi.iot.wldt.adapter.physical.event.PhysicalAssetPropertyWldtEvent;
import it.unimore.dipi.iot.wldt.exception.EventBusException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.function.Function;

public class PropertyIncomingTopic<T> extends IncomingTopic<T>{
    public PropertyIncomingTopic(String topic, String propertyKey, Function<String, T> propertyValueProducer) {
        super(topic, topicMsgPayload -> {
            try {
                return Collections.singletonList(new PhysicalAssetPropertyWldtEvent<>(propertyKey, propertyValueProducer.apply(topicMsgPayload)));
            } catch (EventBusException e) {
                e.printStackTrace();
            }
            return new ArrayList<>();
        });
    }
}
