package it.wldt.adapter.mqtt.physical.topic.outgoing;

import it.wldt.adapter.physical.event.PhysicalAssetActionWldtEvent;

import java.util.function.Function;

/**
 * Represents a function for publishing MQTT messages in the context of a Digital Twin.
 * This interface extends the Java Function interface, specifying the input type as
 * PhysicalAssetActionWldtEvent and the output type as String.
 *
 * @author Marco Picone, Ph.D. - picone.m@gmail.com, Marta Spadoni University of Bologna
 */
public interface MqttPublishFunction extends Function<PhysicalAssetActionWldtEvent<?>, String> {
}
