package it.unibo.disi.wldt.mqttpa;

import it.unimore.dipi.iot.wldt.adapter.physical.ConfigurablePhysicalAdapter;
import it.unimore.dipi.iot.wldt.adapter.physical.event.PhysicalAssetActionWldtEvent;

public class MqttPhysicalAdapter extends ConfigurablePhysicalAdapter<MqttPhysicalAdapterConfiguration> {

    public MqttPhysicalAdapter(String id, MqttPhysicalAdapterConfiguration configuration) {
        super(id, configuration);
    }

    @Override
    public void onIncomingPhysicalAction(PhysicalAssetActionWldtEvent<?> physicalActionEvent) {

    }

    @Override
    public void onAdapterStart() {

    }

    @Override
    public void onAdapterStop() {

    }
}
