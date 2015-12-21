package gadget.component.api.data;

import java.io.Serializable;

/**
 * Created by Dustin on 03.10.2015.
 */
public class ComponentInfo implements Serializable {

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
