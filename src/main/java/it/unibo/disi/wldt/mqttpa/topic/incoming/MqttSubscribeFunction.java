package it.unibo.disi.wldt.mqttpa.topic.incoming;

import it.unimore.dipi.iot.wldt.core.event.WldtEvent;

import java.util.List;
import java.util.function.Function;

@FunctionalInterface
/**
 * Represents a function that accepts a message published on topic (of type String) and when applied, produces a WldtEvent.
 * */
public interface MqttSubscribeFunction extends Function<String, List<WldtEvent<?>>> {
}