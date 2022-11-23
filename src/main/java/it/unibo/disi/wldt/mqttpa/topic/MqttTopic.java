package it.unibo.disi.wldt.mqttpa.topic;

import java.util.function.Function;

public class MqttTopic{

    private final String topic;
    private MqttQosLevel qosLevel = MqttQosLevel.MQTT_QOS_0;

    public MqttTopic(String topic) {
        this.topic = topic;
    }

    public MqttTopic(String topic, MqttQosLevel qosLevel) {
        this.topic = topic;
        this.qosLevel = qosLevel;
    }

    public String getTopic() {
        return topic;
    }

    public MqttQosLevel getQosLevel() {
        return qosLevel;
    }
}
