package gadget.component.hardware;

import com.tinkerforge.BrickletLEDStrip;
import com.tinkerforge.IPConnection;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

/**
 * Created by Dustin on 03.09.2015.
 */
public class SkyLight extends HardwareComponent implements BrickletLEDStrip.FrameRenderedListener {
    private BrickletLEDStrip device;
    private boolean lightning = false;
    private Type type;
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
        return type.red + "," + type.green + "," + type.blue;
    }

    @Override
    public void setValue(String value) {
        String[] rgb = value.split(",");
        type=Type.FADED;
        type.modify(Short.parseShort(rgb[0]),Short.parseShort(rgb[1]),Short.parseShort(rgb[2]));
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
        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (NotConnectedException e) {
            e.printStackTrace();
        }
    }

    private void setRGB(short[] red, short[] green, short[] blue) {
        for (int i = 0; i < (lednum / 10); i++) {
            setRGB(i * 10, (short) 10, red, green, blue);
        }
    }

    public void frameRendered(int i) {
        if (type == null) return;
        setRGB(getColorArray(type.getBlue()), getColorArray(type.getGreen()), getColorArray(type.getRed()));
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

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public enum Type {
        RISE((short) 253, (short) 40, (short) 1),
        DAY((short) 102, (short) 134, (short) 170),
        NIGHT((short) 0, (short) 0, (short) 20),
        THUNDER((short) 0, (short) 24, (short) 72),
        FADED((short) 0, (short) 0, (short) 0);

        private short red;
        private short green;
        private short blue;

        Type(short red, short green, short blue) {
            this.red = red;
            this.green = green;
            this.blue = blue;
        }

        public Type fade(Type fadeTo, int percentage) {
            short red = 0, green = 0, blue = 0;
            if (percentage >= 100) return fadeTo;
            if (percentage == 0) return this;
            int rest = 100 - percentage;
            red += (short) ((this.red * rest) + (fadeTo.red * percentage)) / 100;
            green += (short) ((this.green * rest) + (fadeTo.green * percentage)) / 100;
            blue += (short) ((this.blue * rest) + (fadeTo.blue * percentage)) / 100;
            Type faded = Type.FADED;
            faded.modify(red, green, blue);
            return faded;
        }

        private void modify(short red, short green, short blue) {
            if (!this.equals(FADED)) return;
            this.red = red;
            this.green = green;
            this.blue = blue;
        }


        @Override
        public String toString() {
            return name() + " RGB(" + red + "," + green + "," + blue + ")";
        }

        public short getBlue() {
            return blue;
        }

        public short getGreen() {
            return green;
        }

        public short getRed() {
            return red;
        }
    }
}
