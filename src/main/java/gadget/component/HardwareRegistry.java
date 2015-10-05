package gadget.component;

import com.tinkerforge.AlreadyConnectedException;
import com.tinkerforge.IPConnection;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;
import gadget.component.hardware.HardwareComponent;
import org.reflections.Reflections;

import java.io.IOException;
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
    private IPConnection connection;

    private HardwareRegistry() {
        components = new ArrayList<HardwareComponent>();
    }

    public static HardwareRegistry get() {
        if (instance == null) {
            instance = new HardwareRegistry();
        }
        return instance;
    }

    private void loadHardware() {

        Reflections reflections = new Reflections("gadget.component.hardware");
        Set<Class<? extends HardwareComponent>> hardware = reflections.getSubTypesOf(HardwareComponent.class);
        try {
            for (Class<? extends HardwareComponent> comp : hardware) {
                HardwareComponent instance = comp.newInstance();
                components.add(instance);
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void start() throws AlreadyConnectedException, IOException, NotConnectedException {
        loadHardware();
        connection = new IPConnection();
        connection.addEnumerateListener(this);
        connection.connect("weatherbox", 4223);
        connection.enumerate();
    }

    public void enumerate(String uid, String connectedUid, char position,
                          short[] hardwareVersion, short[] firmwareVersion,
                          int deviceIdentifier, short enumerationType) {
        for (HardwareComponent component : components) {
            if (new ArrayList<Integer>(Arrays.<Integer>asList(component.identifierList())).contains(deviceIdentifier)) {
                try {
                    component.initialize(connection, uid, deviceIdentifier);
                    System.out.println(component.getName() + " changed to " + component.getState().name());
                } catch (TimeoutException e) {
                    e.printStackTrace();
                } catch (NotConnectedException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public HardwareComponent getComponent(String name) {
        for (HardwareComponent component : components) {
            if (component.getName().equalsIgnoreCase(name)){
                if(component.getState().equals(HardwareComponent.State.RUN)) return component;
                else System.out.println("found component("+name+") but was not fully initialized");
            }
        }
        return null;
    }

    public void stop() throws NotConnectedException {
        connection.disconnect();
    }
}
