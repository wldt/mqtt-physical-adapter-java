package it.wldt.adapter.mqtt.physical.topic;

/**
 * Enum representing the Quality of Service (QoS) levels for MQTT communication.
 * It defines three levels: QoS 0, QoS 1, and QoS 2.
 *
 * @author Marco Picone, Ph.D. - picone.m@gmail.com, Marta Spadoni University of Bologna
 */
public enum MqttQosLevel {

    /** QoS 0: At most once delivery. */
    MQTT_QOS_0(0),

    /** QoS 1: At least once delivery. */
    MQTT_QOS_1(1),

    /** QoS 2: Exactly once delivery. */
    MQTT_QOS_2(2);

    /** The qosValue **/
    private final int qosValue;

    /**
     * Constructs an instance of MqttQosLevel with the specified QoS value.
     *
     * @param qosValue The QoS value associated with the enum constant.
     */
    MqttQosLevel(int qosValue) {
        this.qosValue = qosValue;
    }

    /**
     * Gets the integer value representing the QoS level.
     *
     * @return The QoS value.
     */
    public int getQosValue() {
        return qosValue;
    }
}
