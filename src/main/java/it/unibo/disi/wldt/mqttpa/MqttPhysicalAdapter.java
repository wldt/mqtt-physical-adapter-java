package it.unibo.disi.wldt.mqttpa;

import it.unibo.disi.wldt.mqttpa.topic.DigitalTwinIncomingTopic;
import it.unibo.disi.wldt.mqttpa.topic.EventIncomingTopic;
import it.unibo.disi.wldt.mqttpa.topic.PropertyIncomingTopic;
import it.unimore.dipi.iot.wldt.adapter.physical.ConfigurablePhysicalAdapter;
import it.unimore.dipi.iot.wldt.adapter.physical.PhysicalAssetDescription;
import it.unimore.dipi.iot.wldt.adapter.physical.event.PhysicalAssetActionWldtEvent;
import it.unimore.dipi.iot.wldt.adapter.physical.event.PhysicalAssetEventWldtEvent;
import it.unimore.dipi.iot.wldt.adapter.physical.event.PhysicalAssetPropertyWldtEvent;
import it.unimore.dipi.iot.wldt.core.event.WldtEvent;
import it.unimore.dipi.iot.wldt.exception.EventBusException;
import it.unimore.dipi.iot.wldt.exception.PhysicalAdapterException;
import org.eclipse.paho.client.mqttv3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.function.Consumer;

public class MqttPhysicalAdapter extends ConfigurablePhysicalAdapter<MqttPhysicalAdapterConfiguration> {

    private static final Logger logger = LoggerFactory.getLogger(MqttPhysicalAdapter.class);

    private final IMqttClient mqttClient;
    public MqttPhysicalAdapter(String id, MqttPhysicalAdapterConfiguration configuration) throws MqttException {
        super(id, configuration);
        this.mqttClient = new MqttClient(getConfiguration().getBrokerConnectionString(),
                getConfiguration().getClientId(),
                getConfiguration().getPersistence());
    }

    @Override
    public void onIncomingPhysicalAction(PhysicalAssetActionWldtEvent<?> physicalActionEvent) {

    }

    @Override
    public void onAdapterStart() {
        try {
            connectToMqttBroker();
            getConfiguration().getIncomingTopics().forEach(this::subscribeClientToDigitalTwinIncomingTopic);
            logger.info("MQTT Physical Adapter - MQTT client suscribed to incoming topic");
            notifyPhysicalAdapterBound(new PhysicalAssetDescription(getConfiguration().getPhysicalAssetActions(),
                    getConfiguration().getPhysicalAssetProperties(),
                    getConfiguration().getPhysicalAssetEvents()));
        } catch (PhysicalAdapterException | EventBusException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAdapterStop() {

    }

    private void subscribeClientToDigitalTwinIncomingTopic(DigitalTwinIncomingTopic topic) {
        try {
            mqttClient.subscribe(topic.getTopic(), (t, msg) ->{
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

    private void connectToMqttBroker(){
        try {
            mqttClient.connect(getConfiguration().getConnectOptions());
            logger.info("MQTT Physical Adapter - MQTT client connected to broker");
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
