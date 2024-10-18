package it.wldt.adapter.mqtt.physical;

import it.wldt.adapter.mqtt.physical.topic.incoming.DigitalTwinIncomingTopic;
import it.wldt.adapter.mqtt.physical.topic.outgoing.DigitalTwinOutgoingTopic;
import it.wldt.adapter.physical.ConfigurablePhysicalAdapter;
import it.wldt.adapter.physical.event.PhysicalAssetActionWldtEvent;
import it.wldt.adapter.physical.event.PhysicalAssetEventWldtEvent;
import it.wldt.adapter.physical.event.PhysicalAssetPropertyWldtEvent;
import it.wldt.core.event.WldtEvent;
import it.wldt.exception.EventBusException;
import it.wldt.exception.PhysicalAdapterException;
import org.eclipse.paho.client.mqttv3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Implementation of a physical adapter for managing physical assets using the MQTT protocol.
 *
 * This class extends ConfigurablePhysicalAdapter and provides MQTT-specific functionality
 * for handling incoming and outgoing events, actions, and properties related to physical assets.
 *
 * Requires an external MQTT broker for subscription and publication, and it based on the MQTT Paho Java library.
 *
 * @author Marco Picone, Ph.D. - picone.m@gmail.com, Marta Spadoni University of Bologna
 */
public class MqttPhysicalAdapter extends ConfigurablePhysicalAdapter<MqttPhysicalAdapterConfiguration> {

    private static final Logger logger = LoggerFactory.getLogger(MqttPhysicalAdapter.class);

    /** The MQTT client used for communication with the broker. */
    private final IMqttClient mqttClient;

    /**
     * Constructs an instance of MqttPhysicalAdapter.
     *
     * @param id            The identifier for the adapter.
     * @param configuration The configuration for the MQTT physical adapter.
     * @throws MqttException If there is an issue creating the MQTT client.
     */
    public MqttPhysicalAdapter(String id, MqttPhysicalAdapterConfiguration configuration) throws MqttException {
        super(id, configuration);
        this.mqttClient = new MqttClient(getConfiguration().getBrokerConnectionString(),
                getConfiguration().getClientId(),
                getConfiguration().getPersistence());
    }

    /**
     * Handles incoming physical actions and publishes them to the appropriate MQTT topic.
     *
     * @param physicalActionEvent The incoming physical action event.
     */
    @Override
    public void onIncomingPhysicalAction(PhysicalAssetActionWldtEvent<?> physicalActionEvent) {
        logger.info("MQTT Physical Adapter received action event: {}", physicalActionEvent);
        getConfiguration()
                .getOutgoingTopicByActionKey(physicalActionEvent.getActionKey())
                .ifPresent(t -> publishOnTopic(t, t.applyPublishFunction(physicalActionEvent)));
    }

    /**
     * Initializes and connects the MQTT client to the broker upon starting the adapter.
     */
    @Override
    public void onAdapterStart() {
        try {
            connectToMqttBroker();
            getConfiguration().getIncomingTopics().forEach(this::subscribeClientToDigitalTwinIncomingTopic);
            logger.info("MQTT Physical Adapter - MQTT client subscribed to incoming topics");
            notifyPhysicalAdapterBound(getConfiguration().getPhysicalAssetDescription());
        } catch (PhysicalAdapterException | EventBusException e) {
            e.printStackTrace();
        }
    }

    /**
     * Disconnects the MQTT client from the broker upon stopping the adapter.
     */
    @Override
    public void onAdapterStop() {
        try {
            mqttClient.disconnect();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    /**
     * Publishes a message on the specified MQTT topic.
     *
     * @param topic   The MQTT topic to publish on.
     * @param payload The message payload.
     */
    private void publishOnTopic(DigitalTwinOutgoingTopic topic, String payload){
        try {
            MqttMessage msg = new MqttMessage(payload.getBytes());
            msg.setQos(topic.getQos());
            msg.setRetained(topic.isRetained());
            mqttClient.publish(topic.getTopic(), msg);
            logger.info("Physical Adapter - MQTT client published message: {} on topic: {}", payload, topic.getTopic());
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    /**
     * Subscribes the MQTT client to the specified DigitalTwinIncomingTopic.
     *
     * @param topic The DigitalTwinIncomingTopic to subscribe to.
     */
    private void subscribeClientToDigitalTwinIncomingTopic(DigitalTwinIncomingTopic topic) {
        try {
            mqttClient.subscribe(topic.getTopic(), topic.getQos(), (t, msg) ->{
                List<? extends WldtEvent<?>> wldtEvents = topic.applySubscribeFunction(new String(msg.getPayload()));
                wldtEvents.forEach(e -> {
                    try {
                        if(e instanceof PhysicalAssetEventWldtEvent){
                            publishPhysicalAssetEventWldtEvent((PhysicalAssetEventWldtEvent<?>) e);
                        }else if(e instanceof PhysicalAssetPropertyWldtEvent){
                            publishPhysicalAssetPropertyWldtEvent((PhysicalAssetPropertyWldtEvent<?>) e);
                        }
                    } catch (EventBusException ex) {
                        ex.printStackTrace();
                    }
                });
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    /**
     * Connects the MQTT client to the MQTT broker using the specified connection options.
     */
    private void connectToMqttBroker(){
        try {
            mqttClient.connect(getConfiguration().getConnectOptions());
            logger.info("MQTT Physical Adapter - MQTT client connected to broker - clientId: {}", getConfiguration().getClientId());
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
