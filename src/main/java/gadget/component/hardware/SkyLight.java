package gadget.component.hardware;

import com.tinkerforge.BrickletLEDStrip;
import com.tinkerforge.IPConnection;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;
import gadget.component.hardware.data.SkyLightType;

/**
 * Created by Dustin on 03.09.2015.
 */
public class SkyLight extends HardwareComponent implements BrickletLEDStrip.FrameRenderedListener {
    private BrickletLEDStrip device;
    private boolean lightning = false;
    private SkyLightType skyLightType;
    private int lednum;

    @Override
    public Integer[] identifierList() {
        return new Integer[]{BrickletLEDStrip.DEVICE_IDENTIFIER};
    }

    @Override
    public void initialize(IPConnection connection, String uid, int deviceIdentifier) throws TimeoutException, NotConnectedException {
        lednum = Integer.parseInt(getProperty("skyled"));
        device = new BrickletLEDStrip(uid, connection);
        device.addFrameRenderedListener(this);
        device.setFrameDuration(20);
        setRGB(new short[lednum], new short[lednum], new short[lednum]);
    }

    @Override
    public State getState() {
        return (device != null) ? State.RUN : State.BOOT;
    }

    @Override
    public String getValue() {
        if (skyLightType == null) return "0,0,0";
        return skyLightType.getRed() + "," + skyLightType.getGreen() + "," + skyLightType.getBlue();
    }

    @Override
    public void setValue(String value) {
        String[] rgb = value.split(",");
        skyLightType = SkyLightType.FADED;
        skyLightType.modify(Short.parseShort(rgb[0]), Short.parseShort(rgb[1]), Short.parseShort(rgb[2]));
    }

    private short[] getColorArray(short num) {
        short[] data = new short[lednum];
        for (int x = 0; x < lednum; x++)
            data[x] = num;
        return data;
    }

    private void setRGB(int index, short count, short[] red, short[] green, short[] blue) {
        try {
            device.setRGBValues(index, count, red, green, blue);
        } catch (Throwable e) {
            LOG.error("Problem while changing sky color", e);
        }
    }

    private void setRGB(short[] red, short[] green, short[] blue) {
        for (int i = 0; i < (lednum / 10); i++) {
            setRGB(i * 10, (short) 10, red, green, blue);
        }
    }

    public void frameRendered(int i) {
        if (skyLightType == null) return;
        setRGB(getColorArray(skyLightType.getBlue()), getColorArray(skyLightType.getGreen()), getColorArray(skyLightType.getRed()));
        double rand = Math.random();
        if (lightning && rand > 0.48d && rand < 0.52d) {
            int light = (int) ((lednum / 2) * 0.4d);
            int offset = (int) ((lednum - light) * Math.random());
            setRGB(offset, (short) light, getColorArray((short) 255), getColorArray((short) 255), getColorArray((short) 255));
        }

    }

    public void setLightning(boolean lightning) {
        this.lightning = lightning;
    }

    public SkyLightType getSkyLightType() {
        return skyLightType;
    }

    public void setSkyLightType(SkyLightType SkyLightType) {
        this.skyLightType = SkyLightType;
    }
}
