package gadget.component.hardware.data;

/**
 * Created by Dustin on 08.10.2015.
 */
public enum SkyLightType {
    RISE((short) 253, (short) 40, (short) 1),
    DAY((short) 102, (short) 134, (short) 170),
    NIGHT((short) 0, (short) 0, (short) 20),
    THUNDER((short) 0, (short) 24, (short) 72),
    FADED((short) 0, (short) 0, (short) 0);

    private short red;
    private short green;
    private short blue;

    SkyLightType(short red, short green, short blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    public SkyLightType fade(SkyLightType fadeTo, int percentage) {
        short red = 0, green = 0, blue = 0;
        if (percentage >= 100) return fadeTo;
        if (percentage == 0) return this;
        int rest = 100 - percentage;
        red += (short) ((this.red * rest) + (fadeTo.red * percentage)) / 100;
        green += (short) ((this.green * rest) + (fadeTo.green * percentage)) / 100;
        blue += (short) ((this.blue * rest) + (fadeTo.blue * percentage)) / 100;
        SkyLightType faded = SkyLightType.FADED;
        faded.modify(red, green, blue);
        return faded;
    }

    public void modify(short red, short green, short blue) {
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
