package it.wldt.adapter.mqtt.physical;

import it.wldt.adapter.mqtt.physical.exception.MqttPhysicalAdapterConfigurationException;
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

import java.util.ArrayList;
import java.util.List;
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
}
