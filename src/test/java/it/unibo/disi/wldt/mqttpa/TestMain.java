package it.unibo.disi.wldt.mqttpa;

import com.google.gson.Gson;
import it.unibo.disi.wldt.mqttpa.exception.MqttPhysicalAdapterConfigurationException;
import it.unibo.disi.wldt.mqttpa.topic.incoming.DigitalTwinIncomingTopic;
import it.unibo.disi.wldt.mqttpa.topic.incoming.MqttSubscribeFunction;
import it.unibo.disi.wldt.mqttpa.utils.ConsoleDigitalAdapter;
import it.unibo.disi.wldt.mqttpa.utils.DefaultShadowingFunction;
import it.unibo.disi.wldt.mqttpa.utils.MessageDescriptor;
import it.unimore.dipi.iot.wldt.adapter.physical.event.PhysicalAssetPropertyWldtEvent;
import it.unimore.dipi.iot.wldt.core.engine.WldtConfiguration;
import it.unimore.dipi.iot.wldt.core.engine.WldtEngine;
import it.unimore.dipi.iot.wldt.core.event.WldtEvent;
import it.unimore.dipi.iot.wldt.exception.EventBusException;
import it.unimore.dipi.iot.wldt.exception.ModelException;
import it.unimore.dipi.iot.wldt.exception.WldtConfigurationException;
import it.unimore.dipi.iot.wldt.exception.WldtRuntimeException;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class TestMain {
    public static void main(String[] args) throws WldtConfigurationException, ModelException, WldtRuntimeException, EventBusException, MqttException, InterruptedException, MqttPhysicalAdapterConfigurationException {
        WldtEngine dt = new WldtEngine(new DefaultShadowingFunction(),buildWldtConfiguration());
        ConsoleDigitalAdapter dtAdapter = new ConsoleDigitalAdapter();
        dt.addDigitalAdapter(dtAdapter);

        MqttPhysicalAdapterConfiguration config = MqttPhysicalAdapterConfiguration.builder("127.0.0.1", 1883)
                .addPhysicalAssetPropertyAndTopic("intensity", 0, "sensor/intensity", Integer::parseInt)
                .addIncomingTopic(new DigitalTwinIncomingTopic("sensor/state", getSensorStateFunction()))
                .addPhysicalAssetProperty("temperature", 0)
                .addPhysicalAssetProperty("humidity", 0)
                .addPhysicalAssetEventAndTopic("overheating", "text/plain", "sensor/overheating", Function.identity())
                .addPhysicalAssetActionAndTopic("switch-off", "sensor.actuation", "text/plain", "sensor/actions/switch", actionBody -> "switch" + actionBody)
                .build();

//                .addPhysicalAssetPropertyAndTopic("intensity", 0, "sensor/intensity", Integer::parseInt)
//                .addIncomingTopic(new DigitalTwinIncomingTopic("sensor/state", getSensorStateFunction()))
//                .addPhysicalAssetProperty("temperature", 0)
//                .addPhysicalAssetProperty("humidity", 0)
//                .addPhysicalAssetEventAndTopic("overheating", "text/plain", "sensor/overheating", Function.identity())
//                .addPhysicalAssetActionAndTopic("switch-off", "sensor.actuation", "text/plain", "sensor/overheating", actionBody -> "switch" + actionBody)
//      .addPhysicalAssetAction("switch-off", "sensor.actuation", "text/plain");
//      .addOutgoingTopic("switch-off", new ActionOutgoingTopic<String>("sensor/switch", actionEventBody -> "switch-"+actionEventBody));

        dt.addPhysicalAdapter(new MqttPhysicalAdapter("test-mqtt-pa", config));

        dt.startLifeCycle();

        Thread.sleep(2000);

        dtAdapter.invokeAction("switch-off", "off");

    }

    private static WldtConfiguration buildWldtConfiguration() {

        //Manual creation of the WldtConfiguration
        WldtConfiguration wldtConfiguration = new WldtConfiguration();
        wldtConfiguration.setDeviceNameSpace("it.unimore.dipi.things");
        wldtConfiguration.setWldtBaseIdentifier("wldt");
        wldtConfiguration.setWldtStartupTimeSeconds(10);
        return wldtConfiguration;
    }

    private static MqttSubscribeFunction getSensorStateFunction(){
        return msgPayload -> {
            MessageDescriptor md = new Gson().fromJson(msgPayload, MessageDescriptor.class);
            List<WldtEvent<?>> events = new ArrayList<>();
            try {
                events.add(new PhysicalAssetPropertyWldtEvent<>("temperature", md.getTemperatureValue()));
                events.add(new PhysicalAssetPropertyWldtEvent<>("humidity", md.getHumidityValue()));
            } catch (EventBusException e) {
                e.printStackTrace();
            }
            return events;
        };
    }
}

