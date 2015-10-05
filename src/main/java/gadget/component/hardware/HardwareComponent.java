package gadget.component.hardware;

import com.tinkerforge.IPConnection;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;
import gadget.component.Component;

/**
 * Created by Dustin on 01.09.2015.
 */
public abstract class HardwareComponent extends Component {

    public String getName() {
        return getClass().getSimpleName();
    }

    public abstract Integer[] identifierList();

    public abstract void initialize(IPConnection connection, String uid, int deviceIdentifier) throws TimeoutException, NotConnectedException;

    public abstract State getState();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof HardwareComponent) {
            return getName().equals(((HardwareComponent) o).getName());
        }
        if (o instanceof String)
            return getName().equalsIgnoreCase((String) o);
        return false;
    }

    @Override
    public int hashCode() {
        return getName().hashCode();
    }

    public abstract String getValue();

    public abstract void setValue(String value);

    public enum State {
        BOOT, RUN
    }
}
