# MQTT Physical Adapter Java


The MqttPhysicalAdapter library provides a streamlined solution for efficiently managing physical assets through the MQTT protocol. 
It offers a range of features, including a versatile builder for effortless configuration of MQTT connections, dedicated classes for 
handling both incoming and outgoing topics, and a specialized adapter designed for seamless integration with diverse physical assets.

Key Features:

- **Builder for MQTT Configuration:** The library incorporates a flexible builder pattern, enabling users to effortlessly configure the essential parameters of the MQTT connection. This includes specifying the MQTT broker's address, port, and other relevant details to establish a reliable and customizable communication link.
- **Incoming and Outgoing Topic Handling:** MqttPhysicalAdapter facilitates the handling of incoming and outgoing topics, crucial for communication between the physical assets and the MQTT broker. The library includes dedicated classes for defining and managing topics, allowing users to efficiently subscribe to incoming data and publish outgoing messages.
- **Adapter for Physical Asset Integration:** At the core of the library is a robust adapter designed specifically for integrating with various physical assets. This adapter streamlines the process of connecting and interacting with physical devices, ensuring a smooth and standardized approach to managing asset-related data.

Prerequisites:

- **External MQTT Broker:** The MqttPhysicalAdapter library requires an external MQTT broker for optimal functionality. 
- Users must have access to a reliable MQTT broker to which the adapter can subscribe. 
- This external broker serves as the central communication hub, facilitating the exchange of messages between the adapter and the physical assets.

A complete example is provided in the `test` folder with a complete DT Creation in the `TestMain` class together with MQTT IoT demo device and 
a test MQTT consumer.

## WLDT-Core Version Compatibility

The correct mapping and compatibility between versions is reported in the following table

| **mqtt-physical-adapter** |   wldt-core 0.2.1  |   wldt-core 0.3.0  |
|:-------------------------:|:------------------:|:------------------:|
|           0.1.0           | :white_check_mark: |         :x:        |
|           0.1.1           |         :x:        | :white_check_mark: |

## Installation

To use MqttPhysicalAdapter in your Java project, you can include it as a dependency using Maven or Gradle.

### Maven

```xml
<dependency>
    <groupId>io.github.wldt</groupId>
    <artifactId>mqtt-physical-adapter</artifactId>
    <version>0.1.1</version>
</dependency>
```

### Gradle 

```groovy
implementation 'io.github.wldt:mqtt-physical-adapter:0.1.1'
```

## Class Structure & Functionalities

### MqttPhysicalAdapterConfigurationBuilder & Main Methods

The `MqttPhysicalAdapterConfigurationBuilder` is the class used to build the configuration used by the PhysicalAdater 
and described and implemented through the class `MqttPhysicalAdapterConfiguration`.

In order to create a configuration builder we can use the static method `builder()` on the `MqttPhysicalAdapterConfiguration` class: 

```java
MqttPhysicalAdapterConfiguration.builder();
```

On the builder the available methods that can be used are:

- `addPhysicalAssetPropertyAndTopic(String propertyKey, T initialValue, String topic, Function<String, T> topicFunction)`: Adds a physical asset property and its corresponding MQTT topic to the configuration.
Returns the builder for method chaining. Throws MqttPhysicalAdapterConfigurationException if the topic or function is invalid.
- `addPhysicalAssetActionAndTopic(String actionKey, String type, String contentType, String topic, Function<T, String> topicFunction)`: Adds a physical asset action and its corresponding MQTT topic to the configuration.
Returns the builder for method chaining. Throws MqttPhysicalAdapterConfigurationException if the topic or function is invalid.
- `addPhysicalAssetEventAndTopic(String eventKey, String type, String topic, Function<String, T> topicFunction)`: Adds a physical asset event and its corresponding MQTT topic to the configuration.
Returns the builder for method chaining.  Throws MqttPhysicalAdapterConfigurationException if the topic or function is invalid.
- `addIncomingTopic(DigitalTwinIncomingTopic topic, List<PhysicalAssetProperty<?>> properties, List<PhysicalAssetEvent> events)`: Adds a custom incoming topic 
along with related properties and events to the configuration.  Returns the builder for method chaining.
Throws MqttPhysicalAdapterConfigurationException if the topic or related lists are invalid.
- `addOutgoingTopic(String actionKey, String type, String contentType, DigitalTwinOutgoingTopic topic)`: Adds a custom outgoing topic to the configuration.
Returns the builder for method chaining. Throws MqttPhysicalAdapterConfigurationException if the topic or action key is invalid.
- `setConnectionTimeout(Integer connectionTimeout)`: Sets the connection timeout for the MQTT client.
Returns the builder for method chaining.  Throws MqttPhysicalAdapterConfigurationException if the provided timeout is invalid.
- `setCleanSessionFlag(boolean cleanSession)`: Sets the clean session flag for the MQTT client.
Returns the builder for method chaining.
- `setAutomaticReconnectFlag(boolean automaticReconnect)`: Sets the automatic reconnect flag for the MQTT client.
Returns the builder for method chaining.
- `setMqttClientPersistence(MqttClientPersistence persistence)`: Sets the persistence for the MQTT client.
Returns the builder for method chaining. Throws MqttPhysicalAdapterConfigurationException if the provided persistence is null.
- `build()`: Builds and returns the finalized `MqttPhysicalAdapterConfiguration` object.
Throws MqttPhysicalAdapterConfigurationException if the configuration is incomplete or invalid.

### MqttPhysicalAdapter

The MqttPhysicalAdapter class is the core component for interacting with physical assets. Instantiate it with a unique ID and the configuration.
It extends the default WLDT Library `PhysicalAdapter` implementing all the functionalities to automatically interact with an MQTT physical device 
following the specifications and details provided in the `MqttPhysicalAdapterConfiguration` and built using `MqttPhysicalAdapterConfigurationBuilder`.

An example of its creation is:

```java
MqttPhysicalAdapter mqttPhysicalAdapter = new MqttPhysicalAdapter("uniqueId", configuration);
```

### Integrated Example

This example demonstrates the integration of a `MqttPhysicalAdapter` within a Digital Twin setup, where multiple adapters 
(including a console adapter) are added to a Digital Twin, and the overall system is managed by a `DigitalTwinEngine`.

```java
// Create a Digital Twin with a default shadowing function
DigitalTwin digitalTwin = new DigitalTwin("mqtt-digital-twin", new DefaultShadowingFunction());

// Create an instance of ConsoleDigitalAdapter
ConsoleDigitalAdapter consoleDigitalAdapter = new ConsoleDigitalAdapter();

// Create an instance of MqttPhysicalAdapterConfiguration
MqttPhysicalAdapterConfiguration config = MqttPhysicalAdapterConfiguration.builder("127.0.0.1", 1883)
    .addPhysicalAssetPropertyAndTopic("intensity", 0, "sensor/intensity", Integer::parseInt)
    .addIncomingTopic(new DigitalTwinIncomingTopic("sensor/state", getSensorStateFunction()), createIncomingTopicRelatedPropertyList(), new ArrayList<>())
    .addPhysicalAssetEventAndTopic("overheating", "text/plain", "sensor/overheating", Function.identity())
    .addPhysicalAssetActionAndTopic("switch-off", "sensor.actuation", "text/plain", "sensor/actions/switch", actionBody -> "switch" + actionBody)
    .build();

// Create an instance of the MQTT Physical Adapter using the defined configuration
MqttPhysicalAdapter mqttPhysicalAdapter = new MqttPhysicalAdapter("test-mqtt-pa", config);

// Add both Digital and Physical Adapters to the Digital Twin
digitalTwin.addDigitalAdapter(consoleDigitalAdapter);
digitalTwin.addPhysicalAdapter(mqttPhysicalAdapter);

// Create the Digital Twin Engine
DigitalTwinEngine digitalTwinEngine = new DigitalTwinEngine();

// Add the Digital Twin to the Engine
digitalTwinEngine.addDigitalTwin(digitalTwin);

// Start all the Digital Twins registered on the engine
digitalTwinEngine.startAll();
```