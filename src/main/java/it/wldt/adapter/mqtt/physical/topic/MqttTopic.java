package it.wldt.adapter.mqtt.physical.topic;

/**
 * Represents an MQTT topic with an associated Quality of Service (QoS) level.
 *
 * @author Marco Picone, Ph.D. - picone.m@gmail.com, Marta Spadoni University of Bologna
 */
public class MqttTopic{

    /** The MQTT topic string. */
    private final String topic;

    /** The QoS level associated with the MQTT topic. Default is QoS 0. */
    private MqttQosLevel qosLevel = MqttQosLevel.MQTT_QOS_0;

    /** The retained flag. Default is false. */
    private boolean isRetained = false;

    /**
     * Constructs an instance of MqttTopic with the specified topic.
     *
     * @param topic The MQTT topic string.
     */
    public MqttTopic(String topic) {
        this.topic = topic;
    }

    /**
     * Constructs an instance of MqttTopic with the specified topic and QoS level.
     *
     * @param topic    The MQTT topic string.
     * @param qosLevel The Quality of Service (QoS) level associated with the topic.
     */
    public MqttTopic(String topic, MqttQosLevel qosLevel) {
        this.topic = topic;
        this.qosLevel = qosLevel;
    }

    /**
     * Constructs an instance of MqttTopic with the specified topic and QoS level.
     *
     * @param topic    The MQTT topic string.
     * @param isRetained The retained flag.
     * @param qosLevel The Quality of Service (QoS) level associated with the topic.
     */
    public MqttTopic(String topic, MqttQosLevel qosLevel, boolean isRetained) {
        this.topic = topic;
        this.qosLevel = qosLevel;
        this.isRetained = isRetained;
    }

    /**
     * Gets the MQTT topic string.
     *
     * @return The MQTT topic.
     */
    public String getTopic() {
        return topic;
    }

    /**
     * Gets the Quality of Service (QoS) level associated with the MQTT topic.
     *
     * @return The QoS level.
     */
    public Integer getQos() {
        return qosLevel.getQosValue();
    }

    /**
     * Sets the Quality of Service (QoS) level for the MQTT topic.
     *
     * @param qosLevel The new QoS level to be set.
     */
    public void setQosLevel(MqttQosLevel qosLevel) {
        this.qosLevel = qosLevel;
    }

    /**
     * Gets the retained flag for the message.
     *
     * @return The retained flag.
     */
    public boolean isRetained() {
        return isRetained;
    }

    /**
     * Sets the retained flag for the message.
     *
     * @param retained The retained flag to set.
     */
    public void setRetained(boolean retained) {
        isRetained = retained;
    }
}
