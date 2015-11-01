package gadget.component.hardware;

import com.tinkerforge.*;
import gadget.component.hardware.data.CloudType;

/**
 * Created by Dustin on 03.09.2015.
 */
public class Clouds extends HardwareComponent {
    private BrickServo fan;
    private BrickletDualRelay mister;
    private BrickServo.Degree degree;
    private CloudType type;

    @Override
    public Integer[] identifierList() {
        return new Integer[]{BrickServo.DEVICE_IDENTIFIER, BrickletDualRelay.DEVICE_IDENTIFIER};
    }

    @Override
    public void initialize(IPConnection connection, String uid, int deviceIdentifier) throws TimeoutException, NotConnectedException {
        if (BrickServo.DEVICE_IDENTIFIER == deviceIdentifier) {
            fan = new BrickServo(uid, connection);
            fan.enable((short) 0);
            fan.setPeriod((short) 0, 40);
            fan.setPulseWidth((short) 0, 0, 40);
            fan.setDegree((short) 0, (short) 0, (short) 3100);
            fan.setPosition((short) 0, (short) 0);
            degree = fan.getDegree((short) 0);
        }
        if (BrickletDualRelay.DEVICE_IDENTIFIER == deviceIdentifier) {
            mister = new BrickletDualRelay(uid, connection);
            mister.setSelectedState((short) 1, false);
        }
    }

    @Override
    public State getState() {
        return (mister != null && fan != null) ? State.RUN : State.BOOT;
    }

    public void setType(CloudType type) {
        this.type = type;
        try {
            switch (type) {
                case NONE:
                    mister.setSelectedState((short) 1, false);
                    BrickServo.Degree degree = fan.getDegree((short) 0);
                    fan.setPosition((short) 0, degree.max);
                    break;
                case CLOUDY:
                    mister.setSelectedState((short) 1, true);
                    fan.setPosition((short) 0, (short) 2000);
                    break;
                case FOG:
                    mister.setSelectedState((short) 1, true);
                    fan.setPosition((short) 0, (short) 1000);
                    break;
            }
        } catch (Throwable e) {
            LOG.error("Problem while changing Cloudmode ", e);
        }
    }

    @Override
    public void setValue(String value) {
        type = CloudType.valueOf(value);
        setType(type);
    }

    @Override
    public String getValue() {
        return type.name();
    }

    public void setValue(int value) {
        if (value < 15)
            setType(CloudType.NONE);
        else if (value >= 15 && value < 75)
            setType(CloudType.CLOUDY);
        else setType(CloudType.FOG);
    }
}
