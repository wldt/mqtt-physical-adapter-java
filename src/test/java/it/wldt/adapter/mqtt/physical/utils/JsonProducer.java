package it.wldt.adapter.mqtt.physical.utils;

import com.google.gson.Gson;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Random;
import java.util.UUID;

/**
 * Simple MQTT Producer using the library Eclipse Paho
 * and generating JSON structured messages
 *
 * @author Marco Picone, Ph.D. - picone.m@gmail.com
 * @project mqtt-playground
 * @created 14/10/2020 - 09:19
 */
public class JsonProducer {

    private final static Logger logger = LoggerFactory.getLogger(JsonProducer.class);

    //BROKER URL
    private static String BROKER_URI = "tcp://127.0.0.1:1883";

    //Message Limit generated and sent by the producer
    private static final int MESSAGE_COUNT = 100;

    private static final long SLEEP_TIME_MS = 5000;

    //Topic used to publish generated demo data to properties
    private static final String JSON_TOPIC_PROPERTY = "sensor/json";
    private static final String DOUBLE_TOPIC_PROPERTY = "sensor/double";
    private static final String INTEGER_TOPIC_PROPERTY = "sensor/integer";
    private static final String STRING_TOPIC_PROPERTY = "sensor/string";
    private static final String BOOLEAN_TOPIC_PROPERTY = "sensor/boolean";
    private static final String BYTE_TOPIC_PROPERTY = "sensor/bytes";

    //Topic used to publish generated demo data to events
    private static final String JSON_TOPIC_EVENT = "sensor/event/json";
    private static final String DOUBLE_TOPIC_EVENT = "sensor/event/double";
    private static final String INTEGER_TOPIC_EVENT = "sensor/event/integer";
    private static final String STRING_TOPIC_EVENT = "sensor/event/string";
    private static final String BOOLEAN_TOPIC_EVENT = "sensor/event/boolean";
    private static final String BYTE_TOPIC_EVENT = "sensor/event/bytes";

    // Set this to true if you want to test events instead of properties
    private static final Boolean PUBLISH_ON_EVENTS = true;




    public static void main(String[] args) {

        logger.info("JsonProducer started ...");

        try{

            //Generate a random MQTT client ID using the UUID class
            String mqttClientId = UUID.randomUUID().toString();

            //Represents a persistent data store, used to store outbound and inbound messages while they
            //are in flight, enabling delivery to the QoS specified. In that case use a memory persistence.
            //When the application stops all the temporary data will be deleted.
            MqttClientPersistence persistence = new MemoryPersistence();

            //The persistence is not passed to the constructor the default file persistence is used.
            //In case of a file-based storage the same MQTT client UUID should be used
            IMqttClient client = new MqttClient(BROKER_URI,mqttClientId, persistence);

            //Define MQTT Connection Options such as reconnection, persistent/clean session and connection timeout
            //Authentication option can be added -> See AuthProducer example
            MqttConnectOptions options = new MqttConnectOptions();
            options.setAutomaticReconnect(true);
            options.setCleanSession(true);
            options.setConnectionTimeout(10);
            client.connect(options);

            //Connect to the target broker
            logger.info("Connected ! Client Id: {}", mqttClientId);

            //Create an instance of an Engine Temperature Sensor
            EngineSensor engineTemperatureSensor = new EngineSensor();

            Random random = new Random();
            String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
            int length = 10;
            StringBuilder randomString = new StringBuilder();
            byte[] randomBytes = new byte[10];

            //Start to publish MESSAGE_COUNT messages
            for(int i = 0; i < MESSAGE_COUNT; i++) {

                //Get updated temperature value and build the associated Json Message
                //through the internal method buildJsonMessage
            	double sensorValue = engineTemperatureSensor.getTemperatureValue();
            	String payloadString = buildJsonMessage(sensorValue, engineTemperatureSensor.getHumidityValue());

                for (int j = 0; j < length; j++) {
                    int index = random.nextInt(chars.length());
                    randomString.append(chars.charAt(index));
                }
                random.nextBytes(randomBytes);

            	//Internal Method to publish MQTT data using the created MQTT Client
            	if(payloadString != null) {
                    if(PUBLISH_ON_EVENTS) {
                        publishData(client, JSON_TOPIC_EVENT, payloadString);
                        publishData(client, INTEGER_TOPIC_EVENT, String.valueOf(random.nextInt(100)));
                        publishData(client, DOUBLE_TOPIC_EVENT, String.valueOf(random.nextDouble(100.0)));
                        publishData(client, BOOLEAN_TOPIC_EVENT, String.valueOf(random.nextBoolean()));
                        publishData(client, STRING_TOPIC_EVENT, randomString.toString());
                        publishData(client, BYTE_TOPIC_EVENT, Arrays.toString(randomBytes));
                    } else {
                        publishData(client, JSON_TOPIC_PROPERTY, payloadString);
                        publishData(client, INTEGER_TOPIC_PROPERTY, String.valueOf(random.nextInt(100)));
                        publishData(client, DOUBLE_TOPIC_PROPERTY, String.valueOf(random.nextDouble(100.0)));
                        publishData(client, BOOLEAN_TOPIC_PROPERTY, String.valueOf(random.nextBoolean()));
                        publishData(client, STRING_TOPIC_PROPERTY, randomString.toString());
                        publishData(client, BYTE_TOPIC_PROPERTY, Arrays.toString(randomBytes));
                    }
                }
            	else
            		logger.error("Skipping message send due to NULL Payload !");

            	Thread.sleep(SLEEP_TIME_MS);
            }

            //Disconnect from the broker and close connection
            client.disconnect();
            client.close();

            logger.info("Disconnected !");

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public static String buildJsonMessage(double temperatureSensorValue, double humiditySensorValue) {

    	try {

    		Gson gson = new Gson();

        	MessageDescriptor messageDescriptor = new MessageDescriptor(System.currentTimeMillis()
                    , "ENGINE_SENSOR",
                    temperatureSensorValue, humiditySensorValue);

        	return gson.toJson(messageDescriptor);

    	}catch(Exception e) {
    		logger.error("Error creating json payload ! Message: {}", e.getLocalizedMessage());
    		return null;
    	}
    }

    /**
     * Send a target String Payload to the specified MQTT topic
     *
     * @param mqttClient
     * @param topic
     * @param msgString
     * @throws MqttException
     */
    public static void publishData(IMqttClient mqttClient, String topic, String msgString) throws MqttException {

        logger.debug("Publishing to Topic: {} Data: {}", topic, msgString);

        if (mqttClient.isConnected() && msgString != null && topic != null) {
        	
            MqttMessage msg = new MqttMessage(msgString.getBytes());
            msg.setQos(0);
            msg.setRetained(false);
            mqttClient.publish(topic,msg);
            logger.debug("Data Correctly Published !");
        }
        else{
            logger.error("Error: Topic or Msg = Null or MQTT Client is not Connected !");
        }

    }

}
