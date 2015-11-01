package gadget.component;

import com.tinkerforge.IPConnection;
import com.tinkerforge.NotConnectedException;
import gadget.component.hardware.HardwareComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.reflections.Reflections;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Created by Dustin on 01.09.2015.
 */
public class HardwareRegistry implements IPConnection.EnumerateListener {

    private static HardwareRegistry instance;
    private final List<HardwareComponent> components;
    private final Logger LOG;
    private IPConnection connection;

    private HardwareRegistry() {
        LOG = LogManager.getLogger(getClass().getSimpleName());
        components = new ArrayList<HardwareComponent>();
    }

    public static HardwareRegistry get() {
        if (instance == null) {
            instance = new HardwareRegistry();
        }
        return instance;
    }

    private void loadHardware() {
        LOG.info("loading Hardware");
        Reflections reflections = new Reflections("gadget.component.hardware");
        Set<Class<? extends HardwareComponent>> hardware = reflections.getSubTypesOf(HardwareComponent.class);

        for (Class<? extends HardwareComponent> comp : hardware) {
            LOG.debug("loading Hardware " + comp.getName());
            try {
                HardwareComponent instance = comp.newInstance();
                components.add(instance);
            } catch (Throwable e) {
                LOG.error("Problem while loading " + comp.getName(), e);
            }
        }
    }

    public void start() {
        LOG.info("starting");
        loadHardware();
        try {
            LOG.info("connecting");
            connection = new IPConnection();
            connection.addEnumerateListener(this);
            connection.connect("weatherbox", 4223);
            connection.enumerate();
        } catch (Throwable e) {
            LOG.error("Problem while starting", e);
        }
    }

    public void enumerate(String uid, String connectedUid, char position,
                          short[] hardwareVersion, short[] firmwareVersion,
                          int deviceIdentifier, short enumerationType) {
        LOG.debug("Found new hardware");
        LOG.debug("uid = [" + uid + "], connectedUid = [" + connectedUid + "], position = [" + position + "], hardwareVersion = [" + hardwareVersion + "], firmwareVersion = [" + firmwareVersion + "], deviceIdentifier = [" + deviceIdentifier + "], enumerationType = [" + enumerationType + "]");
        for (HardwareComponent component : components) {
            if (new ArrayList<Integer>(Arrays.asList(component.identifierList())).contains(deviceIdentifier)) {
                try {
                    component.initialize(connection, uid, deviceIdentifier);
                    LOG.info(component.getName() + " changed to " + component.getState().name());
                } catch (Throwable e) {
                    LOG.error("Problem while initializing " + component.getName(), e);
                }
            }
        }

    }

    public HardwareComponent getComponent(String name) {
        for (HardwareComponent component : components) {
            if (component.getName().equalsIgnoreCase(name)) {
                if (!component.getState().equals(HardwareComponent.State.RUN))
                    LOG.warn("found component(" + name + ") but was not fully initialized");
                return component;
            }
        }
        LOG.error("Could not find component by name " + name);
        return null;
    }

    public void stop() {
        LOG.info("stopping");
        try {
            connection.disconnect();
        } catch (NotConnectedException e) {
            LOG.error("Problem while stopping");
        }
    }
}
