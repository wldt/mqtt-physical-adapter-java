package it.wldt.adapter.mqtt.physical.utils;

import it.wldt.adapter.digital.DigitalAdapter;
import it.wldt.core.state.*;
import it.wldt.exception.EventBusException;
import it.wldt.exception.WldtDigitalTwinStateEventException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ConsoleDigitalAdapter extends DigitalAdapter<String> {

    private static final Logger logger = LoggerFactory.getLogger(ConsoleDigitalAdapter.class);

    public ConsoleDigitalAdapter() {
        super("console-DA", "default");
    }

    @Override
    protected void onStateUpdate(DigitalTwinState newDigitalTwinState, DigitalTwinState previousDigitalTwinState, ArrayList<DigitalTwinStateChange> digitalTwinStateChangeList) {

        // In newDigitalTwinState we have the new DT State
        System.out.println("New DT State is: " + newDigitalTwinState);

        // The previous DT State is available through the variable previousDigitalTwinState
        System.out.println("Previous DT State is: " + previousDigitalTwinState);

        // We can also check each DT's state change potentially differentiating the behaviour for each change
        if (digitalTwinStateChangeList != null && !digitalTwinStateChangeList.isEmpty()) {

            // Iterate through each state change in the list
            for (DigitalTwinStateChange stateChange : digitalTwinStateChangeList) {

                // Get information from the state change
                DigitalTwinStateChange.Operation operation = stateChange.getOperation();
                DigitalTwinStateChange.ResourceType resourceType = stateChange.getResourceType();
                DigitalTwinStateResource resource = stateChange.getResource();

                // Perform different actions based on the type of operation
                switch (operation) {
                    case OPERATION_UPDATE:
                        // Handle an update operation
                        System.out.println("Update operation on " + resourceType + ": " + resource);
                        break;
                    case OPERATION_UPDATE_VALUE:
                        // Handle an update value operation
                        System.out.println("Update value operation on " + resourceType + ": " + resource);
                        break;
                    case OPERATION_ADD:
                        // Handle an add operation
                        System.out.println("Add operation on " + resourceType + ": " + resource);
                        break;
                    case OPERATION_REMOVE:
                        // Handle a remove operation
                        System.out.println("Remove operation on " + resourceType + ": " + resource);
                        break;
                    default:
                        // Handle unknown operation (optional)
                        System.out.println("Unknown operation on " + resourceType + ": " + resource);
                        break;
                }

                // Specific log example for Relationships Instance Variation
                if(resourceType.equals(DigitalTwinStateChange.ResourceType.RELATIONSHIP_INSTANCE))
                    System.out.println("New Relationship Instance operation:" + operation + " Resource:" + resource);
            }
        } else {
            // No state changes
            System.out.println("No state changes detected.");
        }
    }

    @Override
    protected void onEventNotificationReceived(DigitalTwinStateEventNotification<?> digitalTwinStateEventNotification) {
        logger.info("DA({}) - onEventNotificationReceived - event: {} ", this.getId(), digitalTwinStateEventNotification.getBody());
    }

    @Override
    public void onAdapterStart() {
        logger.debug("DA({}) - onAdapterStart", this.getId());
    }

    @Override
    public void onAdapterStop() {

    }

    @Override
    public void onDigitalTwinSync(DigitalTwinState digitalTwinState) {
        logger.debug("DA({}) - onDTSync - state: {}", this.getId(), digitalTwinState);
        try {

            List<String> eventsKeys = digitalTwinState.getEventList()
                    .orElse(new ArrayList<>()).stream().map(DigitalTwinStateEvent::getKey)
                    .collect(Collectors.toList());

            this.observeDigitalTwinEventsNotifications(eventsKeys);

        } catch (EventBusException | WldtDigitalTwinStateEventException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDigitalTwinUnSync(DigitalTwinState digitalTwinState) {
        logger.info("DA({}) - onDTUnSync - state: {}", this.getId(), digitalTwinState);
    }

    @Override
    public void onDigitalTwinCreate() {

    }

    @Override
    public void onDigitalTwinStart() {

    }

    @Override
    public void onDigitalTwinStop() {

    }

    @Override
    public void onDigitalTwinDestroy() {

    }

    public <T> void invokeAction(String actionKey, T body){
        try {
            publishDigitalActionWldtEvent(actionKey, body);
        } catch (EventBusException e) {
            e.printStackTrace();
        }
    }
}
