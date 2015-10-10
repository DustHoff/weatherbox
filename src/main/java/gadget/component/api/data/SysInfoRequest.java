package gadget.component.api.data;

/**
 * Created by Dustin on 09.10.2015.
 */
public class SysInfoRequest extends Request {

    private boolean mode;

    public boolean isMode() {
        return mode;
    }

    public void setMode(boolean mode) {
        this.mode = mode;
    }
}
