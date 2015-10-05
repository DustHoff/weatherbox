package gadget.component.hardware;

import com.tinkerforge.*;

/**
 * Created by Dustin on 03.09.2015.
 */
public class Rain extends HardwareComponent {
    private BrickServo device;
    private BrickServo.Degree degree;
    private int position;

    @Override
    public Integer[] identifierList() {
        return new Integer[]{BrickServo.DEVICE_IDENTIFIER};
    }

    @Override
    public void initialize(IPConnection connection, String uid, int deviceIdentifier) throws TimeoutException, NotConnectedException {
        device = new BrickServo(uid, connection);
        device.enable((short) 1);
        device.setPeriod((short)1,40);
        device.setPulseWidth((short)1,0,40);
        device.setDegree((short)1,(short)0,(short)3000);
        device.setVelocity((short)1,5000);
        degree = device.getDegree((short) 1);
        setValue(0);
    }

    @Override
    public State getState() {
        return (device != null) ? State.RUN : State.BOOT;
    }

    @Override
    public String getValue() {
        return position+"";
    }

    @Override
    public void setValue(String value) {
        setValue(Integer.parseInt(value));
    }

    public void setValue(int value) {
        position = value;
        try {
            device.setPosition(degree.servoNum, (short) value);
        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (NotConnectedException e) {
            e.printStackTrace();
        }
    }

}
