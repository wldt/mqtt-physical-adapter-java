package it.unibo.disi.wldt.mqttpa.topic;

import it.unimore.dipi.iot.wldt.adapter.physical.event.PhysicalAssetActionWldtEvent;

import java.util.function.Function;

public interface MqttPublishFunction extends Function<PhysicalAssetActionWldtEvent<?>, String> {
}
