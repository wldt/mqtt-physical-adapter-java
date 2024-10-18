package it.wldt.adapter.mqtt.physical;

import com.google.gson.Gson;
import it.wldt.adapter.mqtt.physical.topic.MqttQosLevel;
import it.wldt.adapter.mqtt.physical.topic.incoming.DigitalTwinIncomingTopic;
import it.wldt.adapter.mqtt.physical.topic.incoming.MqttSubscribeFunction;
import it.wldt.adapter.mqtt.physical.utils.ConsoleDigitalAdapter;
import it.wldt.adapter.mqtt.physical.utils.DefaultShadowingFunction;
import it.wldt.adapter.mqtt.physical.utils.MessageDescriptor;
import it.wldt.adapter.physical.PhysicalAssetProperty;
import it.wldt.adapter.physical.event.PhysicalAssetPropertyWldtEvent;
import it.wldt.core.engine.DigitalTwin;
import it.wldt.core.engine.DigitalTwinEngine;
import it.wldt.core.event.WldtEvent;
import it.wldt.exception.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class TestMain {
    public static void main(String[] args) {

        try{

            DigitalTwin digitalTwin = new DigitalTwin("mqtt-digital-twin", new DefaultShadowingFunction());

            // Create an Instance of ConsoleDigital Adapter
            ConsoleDigitalAdapter consoleDigitalAdapter = new ConsoleDigitalAdapter();

            // Create an instance of MqttPhysical Adapter Configuration
            MqttPhysicalAdapterConfiguration config = MqttPhysicalAdapterConfiguration.builder("127.0.0.1", 1883)
                    .addPhysicalAssetPropertyAndTopic("intensity", 0, "sensor/intensity", Integer::parseInt)
                    .addIncomingTopic(new DigitalTwinIncomingTopic("sensor/state", getSensorStateFunction()), createIncomingTopicRelatedPropertyList(), new ArrayList<>())
                    .addPhysicalAssetEventAndTopic("overheating", "text/plain", "sensor/overheating", Function.identity())
                    .addPhysicalAssetActionAndTopic("switch-off", "sensor.actuation", "text/plain", "sensor/actions/switch", actionBody -> "switch" + actionBody)
                    .build();


            /* Example with QoS and Retained Parameters
            .addPhysicalAssetActionAndTopic("switch-off",
                    "sensor.actuation",
                    "text/plain",
                    "sensor/actions/switch",
                    MqttQosLevel.MQTT_QOS_2,
                    true,
                    actionBody -> "switch" + actionBody)
            */

            //                .addPhysicalAssetPropertyAndTopic("intensity", 0, "sensor/intensity", Integer::parseInt)
            //                .addIncomingTopic(new DigitalTwinIncomingTopic("sensor/state", getSensorStateFunction()))
            //                .addPhysicalAssetProperty("temperature", 0)
            //                .addPhysicalAssetProperty("humidity", 0)
            //                .addPhysicalAssetEventAndTopic("overheating", "text/plain", "sensor/overheating", Function.identity())
            //                .addPhysicalAssetActionAndTopic("switch-off", "sensor.actuation", "text/plain", "sensor/overheating", actionBody -> "switch" + actionBody)
            //      .addPhysicalAssetAction("switch-off", "sensor.actuation", "text/plain");
            //      .addOutgoingTopic("switch-off", new ActionOutgoingTopic<String>("sensor/switch", actionEventBody -> "switch-"+actionEventBody));

            // Create an Instance of the MQTT Physical Adapter using the defined configuration
            MqttPhysicalAdapter mqttPhysicalAdapter = new MqttPhysicalAdapter("test-mqtt-pa", config);

            //Add both Digital and Physical Adapters to the DT
            digitalTwin.addDigitalAdapter(consoleDigitalAdapter);
            digitalTwin.addPhysicalAdapter(mqttPhysicalAdapter);

            // Create the Digital Twin Engine
            DigitalTwinEngine digitalTwinEngine = new DigitalTwinEngine();

            // Add the Digital Twin to the Engine
            digitalTwinEngine.addDigitalTwin(digitalTwin);

            // Start all the DTs registered on the engine
            digitalTwinEngine.startAll();

            Thread.sleep(2000);

            consoleDigitalAdapter.invokeAction("switch-off", "off");

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private static List<PhysicalAssetProperty<?>> createIncomingTopicRelatedPropertyList(){
        List<PhysicalAssetProperty<?>> properties = new ArrayList<>();
        properties.add(new PhysicalAssetProperty<>("temperature", 0));
        properties.add(new PhysicalAssetProperty<>("humidity", 0));
        return properties;
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

