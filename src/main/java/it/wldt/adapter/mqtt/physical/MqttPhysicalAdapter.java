package it.wldt.adapter.mqtt.physical;

import it.wldt.adapter.mqtt.physical.topic.incoming.DigitalTwinIncomingTopic;
import it.wldt.adapter.mqtt.physical.topic.outgoing.DigitalTwinOutgoingTopic;
import it.wldt.adapter.physical.ConfigurablePhysicalAdapter;
import it.wldt.adapter.physical.PhysicalAssetDescription;
import it.wldt.adapter.physical.event.PhysicalAssetActionWldtEvent;
import it.wldt.adapter.physical.event.PhysicalAssetEventWldtEvent;
import it.wldt.adapter.physical.event.PhysicalAssetPropertyWldtEvent;
import it.wldt.core.event.WldtEvent;
import it.wldt.exception.EventBusException;
import it.wldt.exception.PhysicalAdapterException;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.mqttv5.client.*;
import org.eclipse.paho.mqttv5.client.IMqttToken;
import org.eclipse.paho.mqttv5.client.MqttCallback;
import org.eclipse.paho.mqttv5.common.packet.MqttProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

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
    private IMqttClient mqttClient = null;

    /** The MQTT client used for communication with the broker. */
    private org.eclipse.paho.mqttv5.client.IMqttClient mqttClientV5 = null;

    /** The Scheduler Executor Service that runs the boundTimeoutScheduler */
    private final ScheduledExecutorService schedulerExecutorServiceBoundTimeout = Executors.newScheduledThreadPool(1);

    /** The Scheduler that after the boundTimeout set the DT state to un-bound */
    private ScheduledFuture<?> boundTimeoutScheduler = null;

    /** Indicates if the DT state is unbound due to lost connection to MQTT broker */
    private Boolean boundLostByConnectionTimeout = false;

    /**
     * Constructs an instance of MqttPhysicalAdapter.
     *
     * @param id            The identifier for the adapter.
     * @param configuration The configuration for the MQTT physical adapter.
     * @throws MqttException If there is an issue creating the MQTT client.
     */
    public MqttPhysicalAdapter(String id, MqttPhysicalAdapterConfiguration configuration) throws MqttException {
        super(id, configuration);
        if(getConfiguration().isMqttV5Flag()) {
            try {
                this.mqttClientV5 = new org.eclipse.paho.mqttv5.client.MqttClient(getConfiguration().getBrokerConnectionString(),
                        getConfiguration().getClientId(),
                        getConfiguration().getPersistenceV5());
            } catch (org.eclipse.paho.mqttv5.common.MqttException e) {
                throw new RuntimeException(e);
            }
        } else {
            this.mqttClient = new MqttClient(getConfiguration().getBrokerConnectionString(),
                    getConfiguration().getClientId(),
                    getConfiguration().getPersistence());
        }
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
        if(getConfiguration().isMqttV5Flag()) {
            try {
                mqttClientV5.disconnect();
            } catch (org.eclipse.paho.mqttv5.common.MqttException e) {
                e.printStackTrace();
            }
        } else {
            try {
                mqttClient.disconnect();
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Publishes a message on the specified MQTT topic.
     *
     * @param topic   The MQTT topic to publish on.
     * @param payload The message payload.
     */
    private void publishOnTopic(DigitalTwinOutgoingTopic topic, String payload){
        if(getConfiguration().isMqttV5Flag()) {
            try {
                org.eclipse.paho.mqttv5.common.MqttMessage msg = new org.eclipse.paho.mqttv5.common.MqttMessage(payload.getBytes());
                msg.setQos(topic.getQos());
                msg.setRetained(topic.isRetained());
                mqttClientV5.publish(getConfiguration().getBaseTopic() + topic.getTopic(), msg);
                logger.info("Physical Adapter - MQTT client published message: {} on topic: {}", payload, topic.getTopic());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                MqttMessage msg = new MqttMessage(payload.getBytes());
                msg.setQos(topic.getQos());
                msg.setRetained(topic.isRetained());
                mqttClient.publish(getConfiguration().getBaseTopic() + topic.getTopic(), msg);
                logger.info("Physical Adapter - MQTT client published message: {} on topic: {}", payload, topic.getTopic());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * Subscribes the MQTT client to the specified DigitalTwinIncomingTopic.
     *
     * @param topic The DigitalTwinIncomingTopic to subscribe to.
     */
    private void subscribeClientToDigitalTwinIncomingTopic(DigitalTwinIncomingTopic topic) {

        if(getConfiguration().isMqttV5Flag()) {
            try {
                mqttClientV5.subscribe(getConfiguration().getBaseTopic() + topic.getTopic(), topic.getQos(), (t, msg) ->{
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
            } catch (org.eclipse.paho.mqttv5.common.MqttException ex) {
                ex.printStackTrace();
            }

        } else {
            try {
                mqttClient.subscribe(getConfiguration().getBaseTopic() + topic.getTopic(), topic.getQos(), (t, msg) ->{
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
    }

    /**
     * Connects the MQTT client to the MQTT broker using the specified connection options.
     */
    private void connectToMqttBroker(){
        try {

            if(getConfiguration().isMqttV5Flag()) {
                mqttClientV5.setCallback(new MqttCallback() {
                    @Override
                    public void disconnected(MqttDisconnectResponse mqttDisconnectResponse) {
                        logger.error("MQTT Physical Adapter - MQTT client connection lost to broker");

                        boundTimeoutScheduler = schedulerExecutorServiceBoundTimeout.schedule(() -> {
                            notifyPhysicalAdapterUnBound("MQTT Physical Adapter - MQTT client connection lost to broker for more than " + getConfiguration().getBoundTimeout() + " seconds");
                            boundLostByConnectionTimeout = true;
                        }, (long) getConfiguration().getBoundTimeout(), TimeUnit.SECONDS);
                    }

                    @Override
                    public void mqttErrorOccurred(org.eclipse.paho.mqttv5.common.MqttException e) {

                    }

                    @Override
                    public void messageArrived(String s, org.eclipse.paho.mqttv5.common.MqttMessage mqttMessage) throws Exception {

                    }

                    @Override
                    public void deliveryComplete(IMqttToken iMqttToken) {

                    }

                    @Override
                    public void connectComplete(boolean b, String s) {
                        logger.info("MQTT Physical Adapter - MQTT client connected to broker - clientId: {}", getConfiguration().getClientId());

                        if(boundLostByConnectionTimeout) {
                            try {
                                notifyPhysicalAdapterBound(new PhysicalAssetDescription());
                                boundLostByConnectionTimeout = false;
                            } catch (PhysicalAdapterException | EventBusException e) {
                                e.printStackTrace();
                            }
                        }

                        if(boundTimeoutScheduler != null) {
                            boundTimeoutScheduler.cancel(true);
                            boundTimeoutScheduler = null;
                        }
                    }

                    @Override
                    public void authPacketArrived(int i, MqttProperties mqttProperties) {

                    }
                });

                mqttClientV5.connect(getConfiguration().getConnectOptionsV5());

            } else {
                mqttClient.setCallback(new MqttCallbackExtended() {
                    @Override
                    public void connectComplete(boolean b, String s) {
                        logger.info("MQTT Physical Adapter - MQTT client connected to broker - clientId: {}", getConfiguration().getClientId());

                        if(boundLostByConnectionTimeout) {
                            try {
                                notifyPhysicalAdapterBound(new PhysicalAssetDescription());
                                boundLostByConnectionTimeout = false;
                            } catch (PhysicalAdapterException | EventBusException e) {
                                e.printStackTrace();
                            }
                        }

                        if(boundTimeoutScheduler != null) {
                            boundTimeoutScheduler.cancel(true);
                            boundTimeoutScheduler = null;
                        }
                    }

                    @Override
                    public void connectionLost(Throwable throwable) {
                        logger.error("MQTT Physical Adapter - MQTT client connection lost to broker");

                        boundTimeoutScheduler = schedulerExecutorServiceBoundTimeout.schedule(() -> {
                            notifyPhysicalAdapterUnBound("MQTT Physical Adapter - MQTT client connection lost to broker for more than " + getConfiguration().getBoundTimeout() + " seconds");
                            boundLostByConnectionTimeout = true;
                        }, (long) getConfiguration().getBoundTimeout(), TimeUnit.SECONDS);

                    }

                    @Override
                    public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {

                    }

                    @Override
                    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

                    }
                });

                mqttClient.connect(getConfiguration().getConnectOptions());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
