package gadget.component.api;

import gadget.component.HardwareRegistry;
import gadget.component.api.data.ComponentInfo;
import gadget.component.hardware.HardwareComponent;

/**
 * Created by Dustin on 28.09.2015.
 */
public class Ambient extends ApiComponent<ComponentInfo> {

    @Override
    public Object handleRequest(ComponentInfo request, String url) throws Exception {
        if (request instanceof ComponentInfo) {
            HardwareComponent component = HardwareRegistry.get().getComponent(request.getComponent());
            component.setValue(request.getValue());
            return true;
        } else if (!url.isEmpty()) {
            if (url.startsWith("/")) url = url.substring(1);
            HardwareComponent component = HardwareRegistry.get().getComponent(url);
            if (component == null) throw new Exception("Unknown Hardware " + url);
            return component.getValue();
        }
        return false;
    }

    @Override
    public String getContext() {
        return "/ambient";
    }
}
