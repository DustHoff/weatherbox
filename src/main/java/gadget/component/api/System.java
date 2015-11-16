package gadget.component.api;

import gadget.component.JobRegistry;
import gadget.component.api.data.Request;
import gadget.component.api.data.Response;
import gadget.component.api.data.SysInfoRequest;
import gadget.component.api.data.SysInfoResponse;
import gadget.component.job.owm.OWM;
import gadget.component.owm.generated.TimeForecast;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;

/**
 * Created by Dustin on 09.10.2015.
 */
public class System extends ApiComponent<SysInfoRequest> {
    @Override
    public Response handleRequest(Request request) throws Exception {
        RuntimeMXBean rb = ManagementFactory.getRuntimeMXBean();
        OperatingSystemMXBean os = ManagementFactory.getOperatingSystemMXBean();
        TimeForecast weather = OWM.getInstance().getWeather();

        if (request instanceof SysInfoRequest) {
            SysInfoRequest sys = (SysInfoRequest) request;
            if (sys.isMode()) JobRegistry.get().resume();
            else JobRegistry.get().pause();
            setProperties("mode", sys.isMode() + "");
        }

        SysInfoResponse response = new SysInfoResponse();
        response.setMode(!JobRegistry.get().isPaused());
        response.setUptime(rb.getUptime());
        response.setLoad(os.getSystemLoadAverage());
        if (weather != null) {
            TimeForecast.Temperature temp = weather.getTemperature();
            response.setTemperature(temp.getMin() + " - " + temp.getMax() + " " + temp.getUnit());
            response.setHumidity(weather.getHumidity().getValue() + " " + weather.getHumidity().getUnit());
            response.setClouds(weather.getClouds().getValue() + " " + weather.getClouds().getUnit());
            response.setPrecipitation(weather.getPrecipitation().getValue() + " " + weather.getPrecipitation().getUnit());
        }
        return new Response(response);
    }

    @Override
    public String getContext() {
        return "/system";
    }
}
