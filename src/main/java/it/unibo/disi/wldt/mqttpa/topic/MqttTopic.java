package it.unibo.disi.wldt.mqttpa.topic;

public class MqttTopic {

    private String topic;
    private MqttQosLevel qosLevel = MqttQosLevel.MQTT_QOS_0;

    public MqttTopic(String topic) {
        this.topic = topic;
    }

    public MqttTopic(String topic, MqttQosLevel qosLevel) {
        this.topic = topic;
        this.qosLevel = qosLevel;
    }
}
