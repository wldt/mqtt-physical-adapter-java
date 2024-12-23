package it.wldt.adapter.mqtt.physical;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import it.wldt.adapter.mqtt.physical.exception.MqttPhysicalAdapterConfigurationException;
import it.wldt.adapter.mqtt.physical.model.MqttPhysicalAdapterFileConfiguration;
import it.wldt.adapter.mqtt.physical.topic.MqttQosLevel;
import it.wldt.adapter.mqtt.physical.topic.MqttTopic;
import it.wldt.adapter.mqtt.physical.topic.incoming.DigitalTwinIncomingTopic;
import it.wldt.adapter.mqtt.physical.topic.incoming.EventIncomingTopic;
import it.wldt.adapter.mqtt.physical.topic.incoming.PropertyIncomingTopic;
import it.wldt.adapter.mqtt.physical.topic.outgoing.ActionOutgoingTopic;
import it.wldt.adapter.mqtt.physical.topic.outgoing.DigitalTwinOutgoingTopic;
import it.wldt.adapter.physical.PhysicalAssetAction;
import it.wldt.adapter.physical.PhysicalAssetEvent;
import it.wldt.adapter.physical.PhysicalAssetProperty;
import org.eclipse.paho.client.mqttv3.MqttClientPersistence;

import java.io.File;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Builder class for creating instances of MqttPhysicalAdapterConfiguration.
 *
 * @author Marco Picone, Ph.D. - picone.m@gmail.com, Marta Spadoni University of Bologna
 */
public class MqttPhysicalAdapterConfigurationBuilder {

    /** Builder target configuration **/
    private final MqttPhysicalAdapterConfiguration configuration;

    /** List of target properties to be mapped into the configuration **/
    private final List<PhysicalAssetProperty<?>> properties = new ArrayList<>();

    /** List of target events to be mapped into the configuration **/
    private final List<PhysicalAssetEvent> events = new ArrayList<>();

    /** List of target actions to be mapped into the configuration **/
    private final List<PhysicalAssetAction> actions = new ArrayList<>();

    /** Static keys of File configuration map **/
    private static final String WLDT_TYPE_MAP = "type";
    private static final String FUNCTION_TYPE_MAP = "function_type";
    private static final String PROPERTY_MAP = "property";
    private static final String PROPERTY_KEY_MAP = "property_key";
    private static final String PROPERTY_INITIAL_VALUE_MAP = "initial_value";
    private static final String PROPERTY_TOPIC_MAP = "topic";
    private static final String EVENT_MAP = "event";
    private static final String EVENT_KEY_MAP = "event_key";
    private static final String EVENT_TYPE_MAP = "event_type";
    private static final String EVENT_INITIAL_VALUE_MAP = "initial_value";
    private static final String EVENT_TOPIC_MAP = "topic";
    private static final String ACTION_MAP = "action";
    private static final String ACTION_KEY_MAP = "action_key";
    private static final String ACTION_TYPE_MAP = "action_type";
    private static final String ACTION_CONTENT_TYPE_MAP = "content_type";
    private static final String ACTION_INITIAL_VALUE_MAP = "initial_value";
    private static final String ACTION_TOPIC_MAP = "topic";
    private static final String NUMERIC_TYPE_MAP = "number";
    private static final String STRING_TYPE_MAP = "string";
    private static final String BOOLEAN_TYPE_MAP = "boolean";
    private static final String BYTES_TYPE_MAP = "bytes";
    private static final String JSON_TYPE_MAP = "json";


    /**
     * Constructs a builder with the required parameters for creating MqttPhysicalAdapterConfiguration.
     *
     * @param brokerAddress The address of the MQTT broker.
     * @param brokerPort    The port of the MQTT broker.
     * @param clientId      The client ID for connecting to the MQTT broker.
     * @throws MqttPhysicalAdapterConfigurationException If the provided parameters are invalid.
     */
    public MqttPhysicalAdapterConfigurationBuilder(String brokerAddress, int brokerPort, String clientId) throws MqttPhysicalAdapterConfigurationException {
        if(!isValid(brokerAddress) || !isValid(brokerPort) || !isValid(clientId))
            throw new MqttPhysicalAdapterConfigurationException("Broker Address or Client Id cannot be empty strings or null and Broker Port must be a positive number");
        configuration = new MqttPhysicalAdapterConfiguration(brokerAddress, brokerPort, clientId);
    }

    /**
     * Constructs a builder with the required parameters for creating MqttPhysicalAdapterConfiguration.
     *
     * @param brokerAddress The address of the MQTT broker.
     * @param brokerPort    The port of the MQTT broker.
     * @throws MqttPhysicalAdapterConfigurationException If the provided parameters are invalid.
     */
    public MqttPhysicalAdapterConfigurationBuilder(String brokerAddress, int brokerPort) throws MqttPhysicalAdapterConfigurationException {
        if(!isValid(brokerAddress) || !isValid(brokerPort))
            throw new MqttPhysicalAdapterConfigurationException("Broker Address cannot be empty strings or null and Broker Port must be a positive number");
        configuration = new MqttPhysicalAdapterConfiguration(brokerAddress, brokerPort);
    }

    /**
     * Constructs a builder with the required parameters for creating MqttPhysicalAdapterConfiguration.
     *
     * @param brokerAddress The address of the MQTT broker.
     * @param brokerPort    The port of the MQTT broker.
     * @param clientId      The client ID for connecting to the MQTT broker.
     * @param username      The username used for connecting to the MQTT broker
     * @param password      The password used for connecting to the MQTT broker
     * @throws MqttPhysicalAdapterConfigurationException If the provided parameters are invalid.
     */
    public MqttPhysicalAdapterConfigurationBuilder(String brokerAddress, int brokerPort, String clientId, String username, String password) throws MqttPhysicalAdapterConfigurationException {
        if(!isValid(brokerAddress) || !isValid(brokerPort) || !isValid(clientId) || !isValid(username) || !isValid(password))
            throw new MqttPhysicalAdapterConfigurationException("Broker Address, Client Id, Username or Password cannot be empty strings or null and Broker Port must be a positive number");
        configuration = new MqttPhysicalAdapterConfiguration(brokerAddress, brokerPort, clientId, username, password);
    }

    /**
     * Constructs a builder with the required parameters for creating MqttPhysicalAdapterConfiguration.
     *
     * @param brokerAddress The address of the MQTT broker.
     * @param brokerPort    The port of the MQTT broker.
     * @param clientId      The client ID for connecting to the MQTT broker.
     * @param accessToken   The access token used for connecting to the MQTT broker
     * @throws MqttPhysicalAdapterConfigurationException If the provided parameters are invalid.
     */
    public MqttPhysicalAdapterConfigurationBuilder(String brokerAddress, int brokerPort, String clientId, String accessToken) throws MqttPhysicalAdapterConfigurationException {
        if(!isValid(brokerAddress) || !isValid(brokerPort) || !isValid(clientId) || !isValid(accessToken))
            throw new MqttPhysicalAdapterConfigurationException("Broker Address, Client Id or Access Token cannot be empty strings or null and Broker Port must be a positive number");
        configuration = new MqttPhysicalAdapterConfiguration(brokerAddress, brokerPort, clientId, accessToken);
    }

    /**
     * Constructs a builder with the required parameters for creating MqttPhysicalAdapterConfiguration.
     *
     * @param jsonFile      The json file with all the configuration parameters.
     * @throws MqttPhysicalAdapterConfigurationException If the provided parameters are invalid.
     */
    public MqttPhysicalAdapterConfigurationBuilder(File jsonFile) throws MqttPhysicalAdapterConfigurationException {
        if(!isValid(jsonFile))
            throw new MqttPhysicalAdapterConfigurationException("Configuration file must exists, must be a file and it must be read.");
        MqttPhysicalAdapterFileConfiguration fileConfig = getMqttFileConfiguration(jsonFile);
        if(!isValid(fileConfig.getMqttClientId())) { fileConfig.setMqttClientId("wldt.mqtt.client." + new Random(System.currentTimeMillis()).nextInt()); }
        if(fileConfig.getAccessToken() != null && !fileConfig.getAccessToken().isEmpty()) {
            configuration = new MqttPhysicalAdapterConfiguration(fileConfig.getMqttBroker(), fileConfig.getMqttPort(), fileConfig.getMqttClientId(), fileConfig.getAccessToken());
        } else {
            configuration = new MqttPhysicalAdapterConfiguration(fileConfig.getMqttBroker(), fileConfig.getMqttPort(), fileConfig.getMqttClientId(), fileConfig.getMqttUsername(), fileConfig.getMqttPassword());
        }
        configuration.setBaseTopic(fileConfig.getMqttBaseTopic());
        try {
            for (HashMap<String, Object> topicMap : fileConfig.getMqttTopicList()) {

                if(topicMap.get(WLDT_TYPE_MAP).equals(PROPERTY_MAP)) {
                    if(topicMap.get(FUNCTION_TYPE_MAP).equals(NUMERIC_TYPE_MAP)) {
                        if(topicMap.get(PROPERTY_INITIAL_VALUE_MAP) instanceof Integer) {
                            addPhysicalAssetPropertyAndTopic((String) topicMap.get(PROPERTY_KEY_MAP), topicMap.get(PROPERTY_INITIAL_VALUE_MAP), (String) topicMap.get(PROPERTY_TOPIC_MAP), Integer::parseInt);
                        } else if (topicMap.get(PROPERTY_INITIAL_VALUE_MAP) instanceof Double) {
                            addPhysicalAssetPropertyAndTopic((String) topicMap.get(PROPERTY_KEY_MAP), topicMap.get(PROPERTY_INITIAL_VALUE_MAP), (String) topicMap.get(PROPERTY_TOPIC_MAP), Double::parseDouble);
                        }
                    } else if(topicMap.get(FUNCTION_TYPE_MAP).equals(STRING_TYPE_MAP)) {
                        addPhysicalAssetPropertyAndTopic((String) topicMap.get(PROPERTY_KEY_MAP), topicMap.get(PROPERTY_INITIAL_VALUE_MAP), (String) topicMap.get(PROPERTY_TOPIC_MAP), String::valueOf);
                    } else if(topicMap.get(FUNCTION_TYPE_MAP).equals(BOOLEAN_TYPE_MAP)) {
                        addPhysicalAssetPropertyAndTopic((String) topicMap.get(PROPERTY_KEY_MAP), topicMap.get(PROPERTY_INITIAL_VALUE_MAP), (String) topicMap.get(PROPERTY_TOPIC_MAP), Boolean::parseBoolean);
                    } else if(topicMap.get(FUNCTION_TYPE_MAP).equals(BYTES_TYPE_MAP)) {
                        addPhysicalAssetPropertyAndTopic((String) topicMap.get(PROPERTY_KEY_MAP), MqttPhysicalAdapterConfigurationBuilder.parseBytesFromString(String.valueOf(topicMap.get(PROPERTY_INITIAL_VALUE_MAP))), (String) topicMap.get(PROPERTY_TOPIC_MAP), MqttPhysicalAdapterConfigurationBuilder::parseBytesFromString);
                    } else if(topicMap.get(FUNCTION_TYPE_MAP).equals(JSON_TYPE_MAP)) {
                        ObjectMapper mapper = new ObjectMapper();
                        addPhysicalAssetPropertyAndTopic((String) topicMap.get(PROPERTY_KEY_MAP), topicMap.get(PROPERTY_INITIAL_VALUE_MAP), (String) topicMap.get(PROPERTY_TOPIC_MAP), value -> {
                            try {
                                return mapper.readTree(value);
                            } catch (JsonProcessingException e) {
                                throw new RuntimeException(e);
                            }
                        });
                    } else {
                        throw new MqttPhysicalAdapterConfigurationException("Wrong function type passed in file configuration. Property function can be number, string, boolean, bytes or json");
                    }
                } else if(topicMap.get(WLDT_TYPE_MAP).equals(EVENT_MAP)) {
                    if(topicMap.get(FUNCTION_TYPE_MAP).equals(NUMERIC_TYPE_MAP)) {
                        if(topicMap.get(EVENT_INITIAL_VALUE_MAP) instanceof Integer) {
                            addPhysicalAssetEventAndTopic((String) topicMap.get(EVENT_KEY_MAP), (String) topicMap.get(EVENT_TYPE_MAP), (String) topicMap.get(EVENT_TOPIC_MAP), Integer::valueOf);
                        } else if (topicMap.get(EVENT_INITIAL_VALUE_MAP) instanceof Double) {
                            addPhysicalAssetEventAndTopic((String) topicMap.get(EVENT_KEY_MAP), (String) topicMap.get(EVENT_TYPE_MAP), (String) topicMap.get(EVENT_TOPIC_MAP), Double::valueOf);
                        }
                    } else if(topicMap.get(FUNCTION_TYPE_MAP).equals(STRING_TYPE_MAP)) {
                        addPhysicalAssetEventAndTopic((String) topicMap.get(EVENT_KEY_MAP), (String) topicMap.get(EVENT_TYPE_MAP), (String) topicMap.get(EVENT_TOPIC_MAP), String::valueOf);
                    } else if(topicMap.get(FUNCTION_TYPE_MAP).equals(BOOLEAN_TYPE_MAP)) {
                        addPhysicalAssetEventAndTopic((String) topicMap.get(EVENT_KEY_MAP), (String) topicMap.get(EVENT_TYPE_MAP), (String) topicMap.get(EVENT_TOPIC_MAP), Boolean::parseBoolean);
                    } else if(topicMap.get(FUNCTION_TYPE_MAP).equals(BYTES_TYPE_MAP)) {
                        addPhysicalAssetEventAndTopic((String) topicMap.get(EVENT_KEY_MAP), (String) topicMap.get(EVENT_TYPE_MAP), (String) topicMap.get(EVENT_TOPIC_MAP), MqttPhysicalAdapterConfigurationBuilder::parseBytesFromString);
                    } else if(topicMap.get(FUNCTION_TYPE_MAP).equals(JSON_TYPE_MAP)) {
                        ObjectMapper mapper = new ObjectMapper();
                        addPhysicalAssetEventAndTopic((String) topicMap.get(EVENT_KEY_MAP), (String) topicMap.get(EVENT_TYPE_MAP), (String) topicMap.get(EVENT_TOPIC_MAP), value -> {
                            try {
                                return mapper.readTree(value);
                            } catch (JsonProcessingException e) {
                                throw new RuntimeException(e);
                            }
                        });
                    } else {
                        throw new MqttPhysicalAdapterConfigurationException("Wrong function type passed in file configuration. Event function can be number, string, boolean, bytes or json");
                    }
                } else if(topicMap.get(WLDT_TYPE_MAP).equals(ACTION_MAP)) {
                    if(topicMap.get(FUNCTION_TYPE_MAP).equals(NUMERIC_TYPE_MAP)) {
                        if(topicMap.get(ACTION_INITIAL_VALUE_MAP) instanceof Integer) {
                            addPhysicalAssetActionAndTopic((String) topicMap.get(ACTION_KEY_MAP), (String) topicMap.get(ACTION_TYPE_MAP), (String) topicMap.get(ACTION_CONTENT_TYPE_MAP), (String) topicMap.get(ACTION_TOPIC_MAP), value -> Integer.toString((int) value));
                        } else if (topicMap.get(ACTION_INITIAL_VALUE_MAP) instanceof Double) {
                            addPhysicalAssetActionAndTopic((String) topicMap.get(ACTION_KEY_MAP), (String) topicMap.get(ACTION_TYPE_MAP), (String) topicMap.get(ACTION_CONTENT_TYPE_MAP), (String) topicMap.get(ACTION_TOPIC_MAP), value -> Double.toString((double) value));
                        }
                    } else if(topicMap.get(FUNCTION_TYPE_MAP).equals(STRING_TYPE_MAP)) {
                        addPhysicalAssetActionAndTopic((String) topicMap.get(ACTION_KEY_MAP), (String) topicMap.get(ACTION_TYPE_MAP), (String) topicMap.get(ACTION_CONTENT_TYPE_MAP), (String) topicMap.get(ACTION_TOPIC_MAP), String::toString);
                    } else if(topicMap.get(FUNCTION_TYPE_MAP).equals(BOOLEAN_TYPE_MAP)) {
                        addPhysicalAssetActionAndTopic((String) topicMap.get(ACTION_KEY_MAP), (String) topicMap.get(ACTION_TYPE_MAP), (String) topicMap.get(ACTION_CONTENT_TYPE_MAP), (String) topicMap.get(ACTION_TOPIC_MAP), value -> Boolean.toString((boolean) value));
                    } else if(topicMap.get(FUNCTION_TYPE_MAP).equals(BYTES_TYPE_MAP)) {
                        addPhysicalAssetActionAndTopic((String) topicMap.get(ACTION_KEY_MAP), (String) topicMap.get(ACTION_TYPE_MAP), (String) topicMap.get(ACTION_CONTENT_TYPE_MAP), (String) topicMap.get(ACTION_TOPIC_MAP),  MqttPhysicalAdapterConfigurationBuilder::fromStringToBytes);
                    } else if(topicMap.get(FUNCTION_TYPE_MAP).equals(JSON_TYPE_MAP)) {
                        addPhysicalAssetActionAndTopic((String) topicMap.get(ACTION_KEY_MAP), (String) topicMap.get(ACTION_TYPE_MAP), (String) topicMap.get(ACTION_CONTENT_TYPE_MAP), (String) topicMap.get(ACTION_TOPIC_MAP),  ObjectNode::toString);
                    } else {
                        throw new MqttPhysicalAdapterConfigurationException("Wrong function type passed in file configuration. Action function can be number, string, boolean, bytes or json");
                    }
                }
            }
        } catch (Exception e) {
            throw new MqttPhysicalAdapterConfigurationException("Error occurred during topic list read in configuration file.");
        }
    }

    /**
     * Transform a String Byte Array in a byte[]
     *
     * @param intArrayString Is the Bytes Array in String
     * @return a byte[] representing the initial Byte Array in String
     */
    private static byte[] parseBytesFromString(String intArrayString) {

        if (intArrayString.trim().equals("[]")) {
            return new byte[0]; // Return an empty byte array
        }

        try {
            // Remove square brackets and split the string
            String cleanedString = intArrayString.replaceAll("[\\[\\]]", ""); // Remove square brackets
            String[] intStrings = cleanedString.split(","); // Split by comma

            // Convert to byte array
            byte[] byteArray = new byte[intStrings.length];
            for (int i = 0; i < intStrings.length; i++) {
                byteArray[i] = (byte) Integer.parseInt(intStrings[i].trim());
            }

            return byteArray;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Input string contains invalid integers.", e);
        }
    }

    /**
     * Transform a Byte Array in a String
     *
     * @param byteArray Is the Bytes Array
     * @return a String representing the initial Byte Array
     */
    private static String fromStringToBytes(byte[] byteArray) {
        if (byteArray.length == 0) {
            return "[]"; // Return an empty array representation
        }

        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < byteArray.length; i++) {
            sb.append(byteArray[i] & 0xFF); // Ensure the byte is treated as unsigned
            if (i < byteArray.length - 1) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    /**
     * Adds a physical asset property and its corresponding MQTT topic to the configuration.
     *
     * @param <T>           The type of the property.
     * @param propertyKey   The key of the property.
     * @param initialValue  The initial value of the property.
     * @param topic         The MQTT topic associated with the property.
     * @param topicFunction A function to parse the MQTT topic payload into the property type.
     * @return The updated MqttPhysicalAdapterConfigurationBuilder.
     * @throws MqttPhysicalAdapterConfigurationException If there is a configuration error.
     */
    public <T> MqttPhysicalAdapterConfigurationBuilder addPhysicalAssetPropertyAndTopic(String propertyKey, T initialValue, String topic, Function<String, T> topicFunction) throws MqttPhysicalAdapterConfigurationException {
        checkTopicAndFunction(topic, topicFunction, this.configuration.getIncomingTopics().stream().map(MqttTopic::getTopic).collect(Collectors.toList()));
        configuration.addIncomingTopic(new PropertyIncomingTopic<>(topic, propertyKey, topicFunction));
        return addPhysicalAssetProperty(propertyKey, initialValue);
    }

    /**
     * Adds a physical asset action and its corresponding MQTT topic to the configuration.
     *
     * @param <T>            The type of the action payload.
     * @param actionKey      The key of the action.
     * @param type           The type of the action.
     * @param contentType    The content type of the action.
     * @param topic          The MQTT topic associated with the action.
     * @param topicFunction  A function to convert the action payload into the MQTT topic payload.
     * @return The updated MqttPhysicalAdapterConfigurationBuilder.
     * @throws MqttPhysicalAdapterConfigurationException If there is a configuration error.
     */
    public <T> MqttPhysicalAdapterConfigurationBuilder addPhysicalAssetActionAndTopic(String actionKey, String type, String contentType,
                                                   String topic, Function<T, String> topicFunction) throws MqttPhysicalAdapterConfigurationException {
        checkTopicAndFunction(topic, topicFunction, this.configuration.getOutgoingTopics().values().stream().map(MqttTopic::getTopic).collect(Collectors.toList()));
        configuration.addOutgoingTopic(actionKey, new ActionOutgoingTopic<>(topic, topicFunction));
        return addPhysicalAssetAction(actionKey, type, contentType);
    }

    /**
     * Adds a physical asset action and its corresponding MQTT topic to the configuration.
     *
     * @param <T>            The type of the action payload.
     * @param actionKey      The key of the action.
     * @param type           The type of the action.
     * @param contentType    The content type of the action.
     * @param topic          The MQTT topic associated with the action.
     * @param qosLevel       The Quality of Service (QoS) level for message delivery.
     * @param isRetained     The retained flag.
     * @param topicFunction  A function to convert the action payload into the MQTT topic payload.
     * @return The updated MqttPhysicalAdapterConfigurationBuilder.
     * @throws MqttPhysicalAdapterConfigurationException If there is a configuration error.
     */
    public <T> MqttPhysicalAdapterConfigurationBuilder addPhysicalAssetActionAndTopic(String actionKey,
                                                                                      String type,
                                                                                      String contentType,
                                                                                      String topic,
                                                                                      MqttQosLevel qosLevel,
                                                                                      boolean isRetained,
                                                                                      Function<T, String> topicFunction) throws MqttPhysicalAdapterConfigurationException {
        checkTopicAndFunction(topic, topicFunction, this.configuration.getOutgoingTopics().values().stream().map(MqttTopic::getTopic).collect(Collectors.toList()));
        configuration.addOutgoingTopic(actionKey, new ActionOutgoingTopic<>(topic, qosLevel, isRetained, topicFunction));
        return addPhysicalAssetAction(actionKey, type, contentType);
    }

    /**
     * Adds a physical asset event and its corresponding MQTT topic to the configuration.
     *
     * @param <T>            The type of the event payload.
     * @param eventKey       The key of the event.
     * @param type           The type of the event.
     * @param topic          The MQTT topic associated with the event.
     * @param topicFunction  A function to parse the MQTT topic payload into the event payload type.
     * @return The updated MqttPhysicalAdapterConfigurationBuilder.
     * @throws MqttPhysicalAdapterConfigurationException If there is a configuration error.
     */
    public <T> MqttPhysicalAdapterConfigurationBuilder addPhysicalAssetEventAndTopic(String eventKey, String type, String topic, Function<String, T> topicFunction) throws MqttPhysicalAdapterConfigurationException {
        checkTopicAndFunction(topic, topicFunction, this.configuration.getIncomingTopics().stream().map(MqttTopic::getTopic).collect(Collectors.toList()));
        configuration.addIncomingTopic(new EventIncomingTopic<>(topic, eventKey, topicFunction));
        return addPhysicalAssetEvent(eventKey, type);

    }

    /**
     * Adds a DigitalTwinIncomingTopic to the configuration along with its related properties and events.
     *
     * @param topic      The DigitalTwinIncomingTopic to be added.
     * @param properties The list of related physical asset properties.
     * @param events     The list of related physical asset events.
     * @return The updated MqttPhysicalAdapterConfigurationBuilder.
     * @throws MqttPhysicalAdapterConfigurationException If there is a configuration error.
     */
    public MqttPhysicalAdapterConfigurationBuilder addIncomingTopic(DigitalTwinIncomingTopic topic, List<PhysicalAssetProperty<?>> properties, List<PhysicalAssetEvent> events) throws MqttPhysicalAdapterConfigurationException {
        if(topic == null) throw new MqttPhysicalAdapterConfigurationException("DigitalTwinIncomingTopic cannot be null");
        if(!isValid(properties) && !isValid(events)) throw new MqttPhysicalAdapterConfigurationException("Property and event list cannot be null or empty. For each DigitalTwinIncomingTopic, related properties and events must be specified");
        checkTopicAndFunction(topic.getTopic(), topic.getSubscribeFunction(), this.configuration.getIncomingTopics().stream().map(MqttTopic::getTopic).collect(Collectors.toList()));
        this.properties.addAll(properties);
        this.events.addAll(events);
        configuration.addIncomingTopic(topic);
        return this;
    }

    /**
     * Adds a DigitalTwinOutgoingTopic to the configuration.
     *
     * @param actionKey    The key of the associated action.
     * @param type         The type of the associated action.
     * @param contentType  The content type of the associated action.
     * @param topic        The DigitalTwinOutgoingTopic to be added.
     * @return The updated MqttPhysicalAdapterConfigurationBuilder.
     * @throws MqttPhysicalAdapterConfigurationException If there is a configuration error.
     */
    public MqttPhysicalAdapterConfigurationBuilder addOutgoingTopic(String actionKey,  String type, String contentType, DigitalTwinOutgoingTopic topic) throws MqttPhysicalAdapterConfigurationException {
        if(topic == null || isValid(actionKey)) throw new MqttPhysicalAdapterConfigurationException("DigitalTwinOutgoingTopic cannot be null | Action key cannot be empty string or null");
        checkTopicAndFunction(topic.getTopic(), topic.getPublishFunction(), this.configuration.getOutgoingTopics().values().stream().map(MqttTopic::getTopic).collect(Collectors.toList()));
        configuration.addOutgoingTopic(actionKey, topic);
        return addPhysicalAssetAction(actionKey, type, contentType);
    }

    /**
     * Read a json File and store data inside a MqttPhysicalAdapterFileConfiguration class which is returned.
     *
     * @param jsonFile is the File that must be read to get configuration.
     * @return an MqttPhysicalAdapterFileConfiguration that contains all the config info inside the File.
     * @throws MqttPhysicalAdapterConfigurationException If there is a configuration error.
     */
    private MqttPhysicalAdapterFileConfiguration getMqttFileConfiguration(File jsonFile) throws MqttPhysicalAdapterConfigurationException {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(jsonFile, MqttPhysicalAdapterFileConfiguration.class);
        } catch (Exception e) {
            throw new MqttPhysicalAdapterConfigurationException("Error occurred when reading mqtt physical adapter configuration file.");
        }
    }

    /**
     * Adds a physical asset property to the configuration.
     *
     * @param <T>      The type of the property.
     * @param key      The key of the property.
     * @param initValue The initial value of the property.
     * @return The updated MqttPhysicalAdapterConfigurationBuilder.
     */
    private <T> MqttPhysicalAdapterConfigurationBuilder addPhysicalAssetProperty(String key, T initValue){
        this.properties.add(new PhysicalAssetProperty<>(key, initValue));
        return this;
    }

    /**
     * Adds a physical asset action to the configuration.
     *
     * @param key         The key of the action.
     * @param type        The type of the action.
     * @param contentType The content type of the action.
     * @return The updated MqttPhysicalAdapterConfigurationBuilder.
     */
    private MqttPhysicalAdapterConfigurationBuilder addPhysicalAssetAction(String key, String type, String contentType){
        this.actions.add(new PhysicalAssetAction(key, type, contentType));
        return this;
    }

    /**
     * Adds a physical asset event to the configuration.
     *
     * @param key  The key of the event.
     * @param type The type of the event.
     * @return The updated MqttPhysicalAdapterConfigurationBuilder.
     */
    private MqttPhysicalAdapterConfigurationBuilder addPhysicalAssetEvent(String key, String type){
       this.events.add(new PhysicalAssetEvent(key, type));
        return this;
    }

    /**
     * Sets the connection timeout for the MQTT Physical Adapter configuration.
     *
     * @param connectionTimeout The connection timeout value in seconds.
     * @return The updated MqttPhysicalAdapterConfigurationBuilder.
     * @throws MqttPhysicalAdapterConfigurationException If the connection timeout is not a positive number.
     */
    public MqttPhysicalAdapterConfigurationBuilder setConnectionTimeout(Integer connectionTimeout) throws MqttPhysicalAdapterConfigurationException {
        if(!isValid(connectionTimeout)) throw new MqttPhysicalAdapterConfigurationException("Connection Timeout must be a positive number");
        this.configuration.setConnectionTimeout(connectionTimeout);
        return this;
    }

    /**
     * Sets the clean session flag for the MQTT Physical Adapter configuration.
     *
     * @param cleanSession The clean session flag.
     * @return The updated MqttPhysicalAdapterConfigurationBuilder.
     */
    public MqttPhysicalAdapterConfigurationBuilder setCleanSessionFlag(boolean cleanSession) {
        this.configuration.setCleanSessionFlag(cleanSession);
        return this;
    }

    /**
     * Sets the automatic reconnect flag for the MQTT Physical Adapter configuration.
     *
     * @param automaticReconnect The automatic reconnect flag.
     * @return The updated MqttPhysicalAdapterConfigurationBuilder.
     */
    public MqttPhysicalAdapterConfigurationBuilder setAutomaticReconnectFlag(boolean automaticReconnect){
        this.configuration.setAutomaticReconnectFlag(automaticReconnect);
        return this;
    }

    /**
     * Sets the MQTT client persistence for the MQTT Physical Adapter configuration.
     *
     * @param persistence The MqttClientPersistence object.
     * @return The updated MqttPhysicalAdapterConfigurationBuilder.
     * @throws MqttPhysicalAdapterConfigurationException If the persistence is null.
     */
    public MqttPhysicalAdapterConfigurationBuilder setMqttClientPersistence(MqttClientPersistence persistence) throws MqttPhysicalAdapterConfigurationException {
        if(persistence == null) throw new MqttPhysicalAdapterConfigurationException("MqttClientPersistence cannot be null");
        this.configuration.setMqttClientPersistence(persistence);
        return this;
    }

    /**
     * Builds and returns the final MQTT Physical Adapter configuration.
     *
     * @return The configured MqttPhysicalAdapterConfiguration.
     * @throws MqttPhysicalAdapterConfigurationException If the configuration is invalid.
     */
    public MqttPhysicalAdapterConfiguration build() throws MqttPhysicalAdapterConfigurationException {
        if(properties.isEmpty() && actions.isEmpty() && events.isEmpty())
            throw new MqttPhysicalAdapterConfigurationException("Physical Adapter must have at least one property or event or action");
        if(this.configuration.getIncomingTopics().isEmpty() && this.configuration.getOutgoingTopics().isEmpty())
            throw new MqttPhysicalAdapterConfigurationException("MQTT Physical Adapter must define at least one DigitalTwinIncomingTopic or DigitalTwinOutgoingTopic");
        this.configuration.setPhysicalAssetDescription(actions, properties, events);
        return this.configuration;
    }

    /**
     * Checks if the given MQTT topic and function are valid.
     *
     * @param <I>          The input type of the function.
     * @param <O>          The output type of the function.
     * @param topic        The MQTT topic.
     * @param topicFunction The function associated with the topic.
     * @param topicList    The list of existing topics.
     * @throws MqttPhysicalAdapterConfigurationException If the topic or function is invalid, or if the topic is already defined.
     */
    private <I, O> void checkTopicAndFunction(String topic, Function<I, O> topicFunction, List<String> topicList) throws MqttPhysicalAdapterConfigurationException {
        if(!isValid(topic) || topicFunction == null)
            throw new MqttPhysicalAdapterConfigurationException("topic cannot be empty or null | topic function cannot be null");
        if(topicList.contains(topic))
            throw new MqttPhysicalAdapterConfigurationException("topic already defined");
    }


    /**
     * Checks if the given list is valid (not null and not empty).
     *
     * @param <T>  The type of the list elements.
     * @param list The list to be checked.
     * @return true if the list is valid, false otherwise.
     */
    private <T> boolean isValid(List<T> list){
        return list != null && !list.isEmpty();
    }

    /**
     * Checks if the given string parameter is valid (not null and not empty).
     *
     * @param param The string parameter to be checked.
     * @return true if the string parameter is valid, false otherwise.
     */
    private boolean isValid(String param){
        return param != null && !param.isEmpty();
    }

    /**
     * Checks if the given integer parameter is valid (greater than 0).
     *
     * @param param The integer parameter to be checked.
     * @return true if the integer parameter is valid, false otherwise.
     */
    private boolean isValid(int param){
        return param > 0;
    }

    /**
     * Checks if the given file parameter is valid (exists, is a file, can be read).
     *
     * @param param The file parameter to be checked.
     * @return true if the file exists, is a file and can be read.
     */
    private boolean isValid(File param){
        return param.exists() && param.isFile() && param.canRead();
    }
}
