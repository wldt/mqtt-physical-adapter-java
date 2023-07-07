package it.wldt.adapter.mqtt.physical.utils;

/**
 * Basic and demo structure of a common message carrying a numeric value
 *
 * @author Marco Picone, Ph.D. - picone.m@gmail.com
 * @project mqtt-playground
 * @created 14/10/2020 - 09:19
 */
public class MessageDescriptor {
	
	private long timestamp;
	
	private String type;
	
	private double temperatureValue;
	private double humidityValue;

	public MessageDescriptor() {
	}

	public MessageDescriptor(long timestamp, String type, double tempValue, double humidityValue) {
		this.timestamp = timestamp;
		this.type = type;
		this.humidityValue = humidityValue;
		this.temperatureValue = tempValue;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public double getTemperatureValue() {
		return temperatureValue;
	}

	public void setTemperatureValue(double temperatureValue) {
		this.temperatureValue = temperatureValue;
	}

	public double getHumidityValue() {
		return humidityValue;
	}

	public void setHumidityValue(double humidityValue) {
		this.humidityValue = humidityValue;
	}

	@Override
	public String toString() {
		final StringBuffer sb = new StringBuffer("MessageDescriptor{");
		sb.append("timestamp=").append(timestamp);
		sb.append(", type='").append(type).append('\'');
		sb.append(", temperatureValue=").append(temperatureValue);
		sb.append(", humidityValue=").append(humidityValue);
		sb.append('}');
		return sb.toString();
	}
}
