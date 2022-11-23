package it.unibo.disi.wldt.mqttpa.utils;

import it.unimore.dipi.iot.wldt.adapter.digital.DigitalAdapter;
import it.unimore.dipi.iot.wldt.core.state.*;
import it.unimore.dipi.iot.wldt.exception.EventBusException;
import it.unimore.dipi.iot.wldt.exception.WldtDigitalTwinStateEventException;
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
    protected void onStateChangePropertyCreated(DigitalTwinStateProperty<?> digitalTwinStateProperty) {

    }

    @Override
    protected void onStateChangePropertyUpdated(DigitalTwinStateProperty<?> digitalTwinStateProperty) {
        logger.info("DA({}) - OnStateChangePropertyUpdate - property: {}", this.getId(), digitalTwinStateProperty);
    }

    @Override
    protected void onStateChangePropertyDeleted(DigitalTwinStateProperty<?> digitalTwinStateProperty) {

    }

    @Override
    protected void onStatePropertyUpdated(DigitalTwinStateProperty<?> digitalTwinStateProperty) {
        logger.info("DA({}) - OnStatePropertyUpdate - property:{}", this.getId(), digitalTwinStateProperty );
    }

    @Override
    protected void onStatePropertyDeleted(DigitalTwinStateProperty<?> digitalTwinStateProperty) {

    }

    @Override
    protected void onStateChangeActionEnabled(DigitalTwinStateAction digitalTwinStateAction) {

    }

    @Override
    protected void onStateChangeActionUpdated(DigitalTwinStateAction digitalTwinStateAction) {

    }

    @Override
    protected void onStateChangeActionDisabled(DigitalTwinStateAction digitalTwinStateAction) {

    }

    @Override
    protected void onStateChangeEventRegistered(DigitalTwinStateEvent digitalTwinStateEvent) {
        logger.info("DA({}) - onStateChangeEventRegistered - event: {} ", this.getId(), digitalTwinStateEvent);
    }

    @Override
    protected void onStateChangeEventRegistrationUpdated(DigitalTwinStateEvent digitalTwinStateEvent) {

    }

    @Override
    protected void onStateChangeEventUnregistered(DigitalTwinStateEvent digitalTwinStateEvent) {

    }

    @Override
    protected void onDigitalTwinStateEventNotificationReceived(DigitalTwinStateEventNotification<?> digitalTwinStateEventNotification) {
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
    public void onDigitalTwinSync(IDigitalTwinState digitalTwinState) {
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
    public void onDigitalTwinUnSync(IDigitalTwinState digitalTwinState) {
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
}
