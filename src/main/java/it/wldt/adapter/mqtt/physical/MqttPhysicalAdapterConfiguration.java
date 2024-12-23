package it.wldt.adapter.mqtt.physical;

import com.sun.org.apache.bcel.internal.generic.PUSH;
import it.wldt.adapter.mqtt.physical.model.MqttPhysicalAdapterFileConfiguration;
import it.wldt.adapter.physical.PhysicalAssetAction;
import it.wldt.adapter.physical.PhysicalAssetDescription;
import it.wldt.adapter.physical.PhysicalAssetEvent;
import it.wldt.adapter.physical.PhysicalAssetProperty;
import it.wldt.adapter.mqtt.physical.exception.MqttPhysicalAdapterConfigurationException;
import it.wldt.adapter.mqtt.physical.topic.incoming.DigitalTwinIncomingTopic;
import it.wldt.adapter.mqtt.physical.topic.outgoing.DigitalTwinOutgoingTopic;
import org.eclipse.paho.client.mqttv3.MqttClientPersistence;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.eclipse.paho.mqttv5.client.MqttConnectionOptions;

import java.io.File;
import java.util.*;

/**
 * Configuration class for the MqttPhysicalAdapter, providing settings for connecting to an MQTT broker,
 * defining incoming and outgoing topics, and specifying the physical asset description.
 *
 * To create an instance of this configuration, use the provided builder {@link MqttPhysicalAdapterConfigurationBuilder}.
 *
 * @see MqttPhysicalAdapter
 * @see MqttPhysicalAdapterConfigurationBuilder
 *
 * @author Marco Picone, Ph.D. - picone.m@gmail.com, Marta Spadoni University of Bologna
 */
public class MqttPhysicalAdapterConfiguration {

    /** The address of the MQTT broker. */
    private final String brokerAddress;

    /** The port of the MQTT broker. */
    private final Integer brokerPort;

    /** The optional username for connecting to the MQTT broker. */
    private String username;

    /** The optional password for connecting to the MQTT broker. */
    private String password;

    /** The optional access token for connecting to the MQTT broker. */
    private String accessToken;

    /** The client ID used when connecting to the MQTT broker. */
    private final String clientId;

    /** The Base topic applied before every topic. */
    private String baseTopic;

    /** Flag indicating whether to use a clean session when connecting to the MQTT broker. */
    private boolean cleanSessionFlag = false;

    /** Flag indicating whether to use the mqtt 5.0 when connecting to MQTT broker. */
    private boolean mqttV5Flag = false;

    /** The connection timeout (in seconds) when connecting to the MQTT broker. */
    private Integer connectionTimeout = 10;

    /** The DT bound timeout (in seconds), connection lost time before going to un-bound state */
    private Integer boundTimeout = 10;

    /** The persistence mechanism used by the MQTT client. */
    private MqttClientPersistence persistence = new MemoryPersistence();

    /** The persistence mechanism used by the MQTT 5.0 client. */
    private org.eclipse.paho.mqttv5.client.MqttClientPersistence persistenceV5 = new org.eclipse.paho.mqttv5.client.persist.MemoryPersistence();

    /** Flag indicating whether automatic reconnection to the MQTT broker is enabled. */
    private boolean automaticReconnectFlag = true;

    /** The description of the physical asset, including actions, properties, and events. */
    private PhysicalAssetDescription physicalAssetDescription;

    /** List of incoming topics to which the PhysicalAdapter must subscribe. */
    private final List<DigitalTwinIncomingTopic> incomingTopics = new ArrayList<>();

    /** Map of outgoing topics on which the PhysicalAdapter must publish, indexed by action key. */
    private final Map<String, DigitalTwinOutgoingTopic> outgoingTopics = new HashMap<>();

    /**
     * Constructs a new MqttPhysicalAdapterConfiguration with the specified broker address, broker port,
     * and client ID.
     *
     * @param brokerAddress The address of the MQTT broker.
     * @param brokerPort    The port of the MQTT broker.
     * @param clientId      The client ID used for connecting to the MQTT broker.
     */
    protected MqttPhysicalAdapterConfiguration(String brokerAddress, Integer brokerPort, String clientId) {
        this.brokerAddress = brokerAddress;
        this.brokerPort = brokerPort;
        this.clientId = clientId;
    }

    /**
     * Constructs a new MqttPhysicalAdapterConfiguration with the specified broker address, broker port,
     * and default client ID.
     *
     * @param brokerAddress The address of the MQTT broker.
     * @param brokerPort    The port of the MQTT broker.
     */
    protected MqttPhysicalAdapterConfiguration(String brokerAddress, Integer brokerPort){
        this(brokerAddress, brokerPort, "wldt.mqtt.client."+new Random(System.currentTimeMillis()).nextInt());
    }

    /**
     * Constructs a new MqttPhysicalAdapterConfiguration with the specified broker address, broker port,
     * client ID, username and password.
     *
     * @param brokerAddress The address of the MQTT broker
     * @param brokerPort    The port of the MQTT broker
     * @param clientId      The client ID used for connecting to the MQTT broker
     * @param username      The username used for connecting to the MQTT broker
     * @param password      The password used for connecting to the MQTT broker
     */
    protected MqttPhysicalAdapterConfiguration(String brokerAddress, Integer brokerPort, String clientId, String username, String password) {
        this.brokerAddress = brokerAddress;
        this.brokerPort = brokerPort;
        this.clientId = clientId;
        this.username = username;
        this.password = password;
    }

    /**
     * Constructs a new MqttPhysicalAdapterConfiguration with the specified broker address, broker port,
     * client ID and access token.
     *
     * @param brokerAddress The address of the MQTT broker
     * @param brokerPort    The port of the MQTT broker
     * @param clientId      The client ID used for connecting to the MQTT broker
     * @param accessToken   The access token used for connecting to the MQTT broker
     */
    protected MqttPhysicalAdapterConfiguration(String brokerAddress, Integer brokerPort, String clientId, String accessToken) {
        this.brokerAddress = brokerAddress;
        this.brokerPort = brokerPort;
        this.clientId = clientId;
        this.accessToken = accessToken;
    }

    /**
     * Creates a new MqttPhysicalAdapterConfigurationBuilder for building MqttPhysicalAdapterConfiguration instances.
     *
     * @param brokerAddress The address of the MQTT broker.
     * @param brokerPort    The port of the MQTT broker.
     * @param clientId      The client ID used for connecting to the MQTT broker.
     * @return The MqttPhysicalAdapterConfigurationBuilder instance.
     * @throws MqttPhysicalAdapterConfigurationException If there is an issue creating the builder.
     * @see MqttPhysicalAdapterConfigurationBuilder
     */
    public static MqttPhysicalAdapterConfigurationBuilder builder(String brokerAddress, Integer brokerPort, String clientId) throws MqttPhysicalAdapterConfigurationException {
        return new MqttPhysicalAdapterConfigurationBuilder(brokerAddress, brokerPort, clientId);
    }

    /**
     * Creates a new MqttPhysicalAdapterConfigurationBuilder for building MqttPhysicalAdapterConfiguration instances.
     *
     * @param brokerAddress The address of the MQTT broker.
     * @param brokerPort    The port of the MQTT broker.
     * @param clientId      The client ID used for connecting to the MQTT broker.
     * @param username      The username used for connecting to the MQTT broker
     * @param password      The password used for connecting to the MQTT broker
     * @return The MqttPhysicalAdapterConfigurationBuilder instance.
     * @throws MqttPhysicalAdapterConfigurationException If there is an issue creating the builder.
     * @see MqttPhysicalAdapterConfigurationBuilder
     */
    public static MqttPhysicalAdapterConfigurationBuilder builder(String brokerAddress, Integer brokerPort, String clientId, String username, String password) throws MqttPhysicalAdapterConfigurationException {
        return new MqttPhysicalAdapterConfigurationBuilder(brokerAddress, brokerPort, clientId, username, password);
    }

    /**
     * Creates a new MqttPhysicalAdapterConfigurationBuilder for building MqttPhysicalAdapterConfiguration instances.
     *
     * @param brokerAddress The address of the MQTT broker.
     * @param brokerPort    The port of the MQTT broker.
     * @param clientId      The client ID used for connecting to the MQTT broker.
     * @param accessToken   The access Token used for connecting to the MQTT broker
     * @return The MqttPhysicalAdapterConfigurationBuilder instance.
     * @throws MqttPhysicalAdapterConfigurationException If there is an issue creating the builder.
     * @see MqttPhysicalAdapterConfigurationBuilder
     */
    public static MqttPhysicalAdapterConfigurationBuilder builder(String brokerAddress, Integer brokerPort, String clientId, String accessToken) throws MqttPhysicalAdapterConfigurationException {
        return new MqttPhysicalAdapterConfigurationBuilder(brokerAddress, brokerPort, clientId, accessToken);
    }

    /**
     * Creates a new MqttPhysicalAdapterConfigurationBuilder for building MqttPhysicalAdapterConfiguration instances
     * with the specified broker address and port, using a default client ID.
     *
     * @param brokerAddress The address of the MQTT broker.
     * @param brokerPort    The port of the MQTT broker.
     * @return The MqttPhysicalAdapterConfigurationBuilder instance.
     * @throws MqttPhysicalAdapterConfigurationException If there is an issue creating the builder.
     * @see MqttPhysicalAdapterConfigurationBuilder
     */
    public static MqttPhysicalAdapterConfigurationBuilder builder(String brokerAddress, Integer brokerPort) throws MqttPhysicalAdapterConfigurationException {
        return new MqttPhysicalAdapterConfigurationBuilder(brokerAddress, brokerPort);
    }

    /**
     * Creates a new MqttPhysicalAdapterConfigurationBuilder for building MqttPhysicalAdapterConfiguration instances
     * with the json File from where read all the config.
     *
     * @param jsonFile      The json File from where read all the config.
     * @return The MqttPhysicalAdapterConfigurationBuilder instance.
     * @throws MqttPhysicalAdapterConfigurationException If there is an issue creating the builder.
     * @see MqttPhysicalAdapterConfigurationBuilder
     */
    public static MqttPhysicalAdapterConfigurationBuilder builder(File jsonFile) throws MqttPhysicalAdapterConfigurationException {
        return new MqttPhysicalAdapterConfigurationBuilder(jsonFile);
    }

    /**
     * Gets the address of the MQTT broker.
     *
     * @return The address of the MQTT broker.
     */
    public String getBrokerAddress() {
        return brokerAddress;
    }

    /**
     * Gets the port of the MQTT broker.
     *
     * @return The port of the MQTT broker.
     */
    public Integer getBrokerPort() {
        return brokerPort;
    }

    /**
     * Gets the username used for connecting to the MQTT broker.
     *
     * @return The username for connecting to the MQTT broker.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Gets the password used for connecting to the MQTT broker.
     *
     * @return The password for connecting to the MQTT broker.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Gets the access token used for connecting to the MQTT broker.
     *
     * @return The access token for connecting to the MQTT broker.
     */
    public String getAccessToken() {
        return accessToken;
    }

    /**
     * Gets the client ID used for connecting to the MQTT broker.
     *
     * @return The client ID for connecting to the MQTT broker.
     */
    public String getClientId() {
        return clientId;
    }

    /**
     * Gets the MQTT broker connection string.
     *
     * @return The MQTT broker connection string.
     */
    public String getBrokerConnectionString(){
        return String.format("tcp://%s:%d", brokerAddress, brokerPort);
    }

    /**
     * Gets the MQTT 5.0 Flag.
     *
     * @return The MQTT 5.0 Flag.
     */
    public boolean isMqttV5Flag() {
        return mqttV5Flag;
    }

    /**
     * Gets the persistence mechanism used by the MQTT client.
     *
     * @return The MQTT client persistence mechanism.
     */
    public MqttClientPersistence getPersistence() {
        return persistence;
    }

    /**
     * Gets the persistence mechanism used by the MQTT 5.0 client.
     *
     * @return The MQTT 5.0 client persistence mechanism.
     */
    public org.eclipse.paho.mqttv5.client.MqttClientPersistence getPersistenceV5() {
        return persistenceV5;
    }

    /**
     * Gets the base topic.
     *
     * @return The Base topic.
     */
    public String getBaseTopic() {
        return baseTopic;
    }

    /**
     * Sets the DT bound timeout
     *
     * @return The time before DT state goes to un-bound
     */
    public Integer getBoundTimeout() {
        return boundTimeout;
    }

    /**
     * Gets the connection options for connecting to the MQTT broker.
     *
     * @return The MQTT connection options.
     */
    public MqttConnectOptions getConnectOptions(){
        MqttConnectOptions options = new MqttConnectOptions();
        options.setAutomaticReconnect(automaticReconnectFlag);
        options.setCleanSession(cleanSessionFlag);
        options.setConnectionTimeout(connectionTimeout);
        if(username != null && !username.isEmpty() && password != null && !password.isEmpty()){
            options.setUserName(username);
            options.setPassword(password.toCharArray());
        } else if (accessToken != null && !accessToken.isEmpty()) {
            options.setUserName(accessToken);
        }
        return options;
    }

    /**
     * Gets the connection options for connecting to the MQTT broker.
     *
     * @return The MQTT connection options.
     */
    public MqttConnectionOptions getConnectOptionsV5(){
        MqttConnectionOptions options = new MqttConnectionOptions();
        options.setAutomaticReconnect(automaticReconnectFlag);
        options.setCleanStart(cleanSessionFlag);
        options.setConnectionTimeout(connectionTimeout);
        if(username != null && !username.isEmpty() && password != null && !password.isEmpty()){
            options.setUserName(username);
            options.setPassword(password.getBytes());
        } else if (accessToken != null && !accessToken.isEmpty()) {
            options.setUserName(accessToken);
        }
        return options;
    }


    /**
     * Gets the list of incoming topics to which the PhysicalAdapter must subscribe.
     *
     * @return The list of incoming topics.
     */
    public List<DigitalTwinIncomingTopic> getIncomingTopics() {
        return incomingTopics;
    }

    /**
     * Gets the map of outgoing topics on which the PhysicalAdapter must publish, indexed by action key.
     *
     * @return The map of outgoing topics.
     */
    public Map<String, DigitalTwinOutgoingTopic> getOutgoingTopics() {
        return outgoingTopics;
    }

    /**
     * Gets the outgoing topic for a specific action key, if present.
     *
     * @param key The action key.
     * @return An {@link Optional} containing the outgoing topic, or an empty {@link Optional} if not present.
     */
    public Optional<DigitalTwinOutgoingTopic> getOutgoingTopicByActionKey(String key){
        return outgoingTopics.containsKey(key) ? Optional.of(outgoingTopics.get(key)) : Optional.empty();
    }

    /**
     * Gets the description of the physical asset, including actions, properties, and events.
     *
     * @return The physical asset description.
     */
    public PhysicalAssetDescription getPhysicalAssetDescription() {
        return physicalAssetDescription;
    }

    /**
     * Adds an incoming topic to the configuration.
     *
     * @param topic The incoming topic to add.
     */
    protected void addIncomingTopic(DigitalTwinIncomingTopic topic){
        this.incomingTopics.add(topic);
    }

    /**
     * Adds an outgoing topic to the configuration.
     *
     * @param actionKey The action key associated with the outgoing topic.
     * @param topic     The outgoing topic to add.
     */
    protected void addOutgoingTopic(String actionKey, DigitalTwinOutgoingTopic topic){
        this.outgoingTopics.put(actionKey,topic);
    }

    /**
     * Sets the description of the physical asset, including actions, properties, and events.
     *
     * @param actions    The list of physical asset actions.
     * @param properties The list of physical asset properties.
     * @param events     The list of physical asset events.
     */
    protected void setPhysicalAssetDescription(List<PhysicalAssetAction> actions,
                                            List<PhysicalAssetProperty<?>> properties,
                                            List<PhysicalAssetEvent> events){
        this.physicalAssetDescription = new PhysicalAssetDescription(actions, properties, events);
    }

    /**
     * Sets the connection timeout value.
     *
     * @param connectionTimeout The connection timeout value.
     */
    protected void setConnectionTimeout(Integer connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    /**
     * Sets the clean session flag.
     *
     * @param cleanSession The clean session flag.
     */
    protected void setCleanSessionFlag(boolean cleanSession) {
        this.cleanSessionFlag = cleanSession;
    }

    /**
     * Sets the mqtt version flag.
     *
     * @param mqttV5Flag The mqtt version flag.
     */
    protected void setMqttV5Flag(boolean mqttV5Flag) {
        this.mqttV5Flag = mqttV5Flag;
    }

    /**
     * Sets the automatic reconnect flag.
     *
     * @param automaticReconnect The automatic reconnect flag.
     */
    protected void setAutomaticReconnectFlag(boolean automaticReconnect){
        this.automaticReconnectFlag = automaticReconnect;
    }

    /**
     * Sets the MQTT client persistence mechanism.
     *
     * @param persistence The MQTT client persistence mechanism.
     */
    protected void setMqttClientPersistence(MqttClientPersistence persistence) {
        this.persistence = persistence;
    }

    /**
     * Sets the Base topic.
     *
     * @param baseTopic The MQTT client persistence mechanism.
     */
    protected void setBaseTopic(String baseTopic) {
        this.baseTopic = baseTopic;
    }

    /**
     * Sets the DT bound timeout
     *
     * @param boundTimeout The time before DT state goes to un-bound
     */
    public void setBoundTimeout(Integer boundTimeout) {
        this.boundTimeout = boundTimeout;
    }
}

