package gadget.component.api.data;

/**
 * Created by Dustin on 03.10.2015.
 */
public class AmbientRequest extends Request {

    private String component;
    private String value;

    public String getComponent() {
        return component;
    }

    public void setComponent(String component) {
        this.component = component;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
