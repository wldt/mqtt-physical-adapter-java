package it.unibo.disi.wldt.mqttpa.topic.outgoing;

import java.util.function.Function;

public class ActionOutgoingTopic<T> extends DigitalTwinOutgoingTopic<T> {
    public ActionOutgoingTopic(String topic, Function<T, String> actionBodyConsumer) {
        super(topic, actionWldtEvent -> actionBodyConsumer.apply(actionWldtEvent.getBody()));
    }
}
