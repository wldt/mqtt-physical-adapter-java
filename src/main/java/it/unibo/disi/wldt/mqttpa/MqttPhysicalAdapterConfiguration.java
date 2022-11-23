package it.unibo.disi.wldt.mqttpa;

import it.unibo.disi.wldt.mqttpa.topic.DigitalTwinIncomingTopic;
import it.unimore.dipi.iot.wldt.adapter.physical.PhysicalAssetAction;
import it.unimore.dipi.iot.wldt.adapter.physical.PhysicalAssetEvent;
import it.unimore.dipi.iot.wldt.adapter.physical.PhysicalAssetProperty;
import org.eclipse.paho.client.mqttv3.MqttClientPersistence;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.*;

public class MqttPhysicalAdapterConfiguration {
    private final String brokerAddress;
    private final Integer brokerPort;
    private String username;
    private String password;
    private String clientId;
    private boolean cleanSessionFlag = true;
    private Integer connectionTimeout = 10;
    private MqttClientPersistence persistence = new MemoryPersistence();
    private boolean automaticReconnectFlag = true;

    private final List<PhysicalAssetProperty<?>> physicalAssetProperties = new ArrayList<>();
    private final List<PhysicalAssetEvent> physicalAssetEvents = new ArrayList<>();
    private final List<PhysicalAssetAction> physicalAssetActions = new ArrayList<>();

    //INCOMING TOPICS: Topics to which the PhysicalAdapter must subscribe
    private final List<DigitalTwinIncomingTopic> incomingTopics = new ArrayList<>();
    //OUTGOING TOPICS: Topics on which the PhysicalAdapter must publish
    //TODO: change type in Map<ActionKey, OutgoingTopic>
    private final List<String> outgoingTopics = new ArrayList<>();

    public MqttPhysicalAdapterConfiguration(String brokerAddress, Integer brokerPort, String clientId) {
        this.brokerAddress = brokerAddress;
        this.brokerPort = brokerPort;
        this.clientId = clientId;
    }

    public MqttPhysicalAdapterConfiguration(String brokerAddress, Integer brokerPort){
        this(brokerAddress, brokerPort, "wldt.mqtt.client."+new Random(System.currentTimeMillis()).nextInt());
    }

    public String getBrokerAddress() {
        return brokerAddress;
    }

    public Integer getBrokerPort() {
        return brokerPort;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getClientId() {
        return clientId;
    }

    public String getBrokerConnectionString(){
        return String.format("tcp://%s:%d", brokerAddress, brokerPort);
    }

    public MqttClientPersistence getPersistence() {
        return persistence;
    }

    public List<DigitalTwinIncomingTopic> getIncomingTopics() {
        return incomingTopics;
    }

    public List<PhysicalAssetProperty<?>> getPhysicalAssetProperties() {
        return physicalAssetProperties;
    }

    public List<PhysicalAssetEvent> getPhysicalAssetEvents() {
        return physicalAssetEvents;
    }

    public List<PhysicalAssetAction> getPhysicalAssetActions() {
        return physicalAssetActions;
    }

    public void addIncomingTopic(DigitalTwinIncomingTopic topic){
        this.incomingTopics.add(topic);
    }

//    public void addIncomingTopics(Collection<DigitalTwinIncomingTopic> topics){
//        this.incomingTopics.addAll(topics);
//    }

    public <T> void addPhysicalAssetProperty(String key, T initValue){
        this.physicalAssetProperties.add(new PhysicalAssetProperty<>(key, initValue));
    }

    public void addPhysicalAssetAction(String key, String type, String contentType){
        this.physicalAssetActions.add(new PhysicalAssetAction(key, type, contentType));
    }

    public void addPhysicalAssetEvent(String key, String type){
        this.physicalAssetEvents.add(new PhysicalAssetEvent(key, type));
    }

    public void setMqttClientId(String clientId) {
        this.clientId = clientId;
    }

    public void setConnectionTimeout(Integer connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public void setCleanSessionFlag(boolean cleanSession) {
        this.cleanSessionFlag = cleanSession;
    }

    public void setAutomaticReconnectFlag(boolean automaticReconnect){
        this.automaticReconnectFlag = automaticReconnect;
    }

    public void setMqttClientPersistence(MqttClientPersistence persistence) {
        this.persistence = persistence;
    }

    public MqttConnectOptions getConnectOptions(){
        MqttConnectOptions options = new MqttConnectOptions();
        options.setAutomaticReconnect(automaticReconnectFlag);
        options.setCleanSession(cleanSessionFlag);
        options.setConnectionTimeout(connectionTimeout);
        return options;
    }
}

