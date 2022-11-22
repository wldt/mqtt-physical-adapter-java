package it.unibo.disi.wldt.mqttpa;

import it.unibo.disi.wldt.mqttpa.topic.IncomingTopic;
import it.unimore.dipi.iot.wldt.adapter.physical.PhysicalAssetAction;
import it.unimore.dipi.iot.wldt.adapter.physical.PhysicalAssetEvent;
import it.unimore.dipi.iot.wldt.adapter.physical.PhysicalAssetProperty;

import java.util.*;

public class MqttPhysicalAdapterConfiguration {
    private final String brokerAddress;
    private final String brokerPort;
    private String username;
    private String password;
    private final String clientId;
    private boolean cleanSessionFlag = true;
    private final List<PhysicalAssetProperty<?>> physicalAssetProperties = new ArrayList<>();
    private final List<PhysicalAssetEvent> physicalAssetEvents = new ArrayList<>();
    private final List<PhysicalAssetAction> physicalAssetActions = new ArrayList<>();
    //INCOMING TOPICS: Topics to which the PhysicalAdapter must subscribe
    private final List<IncomingTopic<?>> incomingTopics = new ArrayList<>();
    //OUTGOING TOPICS: Topics on which the PhysicalAdapter must publish
    //TODO: change type in Map<ActionKey, OutgoingTopic>
    private final List<String> outgoingTopics = new ArrayList<>();

    public MqttPhysicalAdapterConfiguration(String brokerAddress, String brokerPort, String clientId) {
        this.brokerAddress = brokerAddress;
        this.brokerPort = brokerPort;
        this.clientId = clientId;
    }

    public MqttPhysicalAdapterConfiguration(String brokerAddress, String brokerPort){
        this(brokerAddress, brokerPort, "wldt.mqtt.client."+new Random().nextInt());
    }

    public String getBrokerAddress() {
        return brokerAddress;
    }

    public String getBrokerPort() {
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

    public boolean getCleanSessionFlag() {
        return cleanSessionFlag;
    }

    public List<IncomingTopic<?>> getIncomingTopics() {
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

    public void addIncomingTopic(IncomingTopic<?> topic){
        this.incomingTopics.add(topic);
    }

    public void addIncomingTopics(Collection<IncomingTopic<?>> topics){
        this.incomingTopics.addAll(topics);
    }

    public <T> void addPhysicalAssetProperty(String key, T initValue){
        this.physicalAssetProperties.add(new PhysicalAssetProperty<>(key, initValue));
    }

    public void addPhysicalAssetAction(String key, String type, String contentType){
        this.physicalAssetActions.add(new PhysicalAssetAction(key, type, contentType));
    }

    public void addPhysicalAssetEvent(String key, String type){
        this.physicalAssetEvents.add(new PhysicalAssetEvent(key, type));
    }
}

