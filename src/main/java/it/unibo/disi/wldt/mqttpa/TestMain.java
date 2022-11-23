package it.unibo.disi.wldt.mqttpa;

import com.google.gson.Gson;
import it.unibo.disi.wldt.mqttpa.topic.*;
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
    public static void main(String[] args) throws WldtConfigurationException, ModelException, WldtRuntimeException, EventBusException, MqttException, InterruptedException {
        WldtEngine dt = new WldtEngine(new DefaultShadowingFunction(),buildWldtConfiguration());
        ConsoleDigitalAdapter dtAdapter = new ConsoleDigitalAdapter();
        dt.addDigitalAdapter(dtAdapter);

        MqttPhysicalAdapterConfiguration config = new MqttPhysicalAdapterConfiguration("127.0.0.1", 1883);
        config.addIncomingTopic(new PropertyIncomingTopic<>("lamp/intensity", "intensity", Integer::parseInt));
        config.addIncomingTopic(new DigitalTwinIncomingTopic("sensor/state", msgPayload -> {
            MessageDescriptor md = new Gson().fromJson(msgPayload, MessageDescriptor.class);
            List<WldtEvent<?>> events = new ArrayList<>();
            try {
                events.add(new PhysicalAssetPropertyWldtEvent<>("temperature", md.getTemperatureValue()));
                events.add(new PhysicalAssetPropertyWldtEvent<>("humidity", md.getHumidityValue()));
            } catch (EventBusException e) {
                e.printStackTrace();
            }
            return events;
        }));
        config.addIncomingTopic(new EventIncomingTopic<>("sensor/overheating", "overheating", Function.identity()));

        config.addPhysicalAssetProperty("intensity", 0);
        config.addPhysicalAssetProperty("temperature", 0);
        config.addPhysicalAssetProperty("humidity", 0);

        config.addPhysicalAssetEvent("overheating", "text/plain");

        config.addPhysicalAssetAction("switch-off", "sensor.actuation", "text/plain");

        config.addOutgoingTopic("switch-off", new DigitalTwinOutgoingTopic("sensor/switch", actionWldtEvent -> "switch-off"));

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
}

