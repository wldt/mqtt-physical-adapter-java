package it.unibo.disi.wldt.mqttpa.utils;

import java.util.Random;

/**
 * @author Marco Picone, Ph.D. - picone.m@gmail.com
 * @project mqtt-playground
 * @created 14/10/2020 - 09:19
 */
public class EngineSensor {

    private Random rnd;
    
    private double temperatureValue;
    private double humidityValue;

    public EngineSensor() {
        this.rnd = new Random(System.currentTimeMillis());
        this.temperatureValue = 0.0; 
        this.humidityValue = 0.0;
    }
 
    private void generateEngineTemperature() {
    	temperatureValue =  80 + rnd.nextDouble() * 20.0;     
    }

	private void generateEngineHumidity() {
		humidityValue =  50 + rnd.nextDouble() * 20.0;
	}

	public double getTemperatureValue() {
		generateEngineTemperature();
		return temperatureValue;
	}

	public double getHumidityValue() {
		generateEngineHumidity();
		return humidityValue;
	}

	public void setTemperatureValue(double temperatureValue) {
		this.temperatureValue = temperatureValue;
	}

	@Override
	public String toString() {
		return "EngineTemperatureSensor [temperatureValue=" + temperatureValue + "]";
	}
    
}