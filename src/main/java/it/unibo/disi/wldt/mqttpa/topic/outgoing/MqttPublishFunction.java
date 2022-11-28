package it.unibo.disi.wldt.mqttpa.topic.outgoing;

import it.unimore.dipi.iot.wldt.adapter.physical.event.PhysicalAssetActionWldtEvent;

import java.util.function.Function;

public interface MqttPublishFunction<T> extends Function<PhysicalAssetActionWldtEvent<T>, String> {
}
