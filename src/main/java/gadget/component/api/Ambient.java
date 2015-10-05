package gadget.component.api;

import gadget.component.HardwareRegistry;
import gadget.component.api.data.AmbientRequest;
import gadget.component.api.data.Request;
import gadget.component.api.data.Response;
import gadget.component.hardware.HardwareComponent;

/**
 * Created by Dustin on 28.09.2015.
 */
public class Ambient extends ApiComponent<AmbientRequest> {

    @Override
    public Response handleRequest(Request request) throws Exception {
        if (request instanceof AmbientRequest) {
            HardwareComponent component = HardwareRegistry.get().getComponent(((AmbientRequest) request).getComponent());
            component.setValue(((AmbientRequest) request).getValue());
            return new Response(Boolean.TRUE);
        } else if (!request.getRequestUrl().isEmpty()) {
            HardwareComponent component = HardwareRegistry.get().getComponent(request.getRequestUrl().replace("/", ""));
            if(component==null)throw new Exception("Unknown Hardware");
            return new Response(component.getValue());
        }
        return new Response(Boolean.FALSE);
    }

    @Override
    public String getContext() {
        return "/ambient";
    }
}
