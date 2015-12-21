package gadget.component.api;

import gadget.component.JobRegistry;
import gadget.component.api.data.Weather;
import gadget.component.job.owm.OWM;
import gadget.component.owm.generated.TimeForecast;

/**
 * Created by Dustin on 09.10.2015.
 */
public class System extends ApiComponent<Boolean> {
    @Override
    public Object handleRequest(Boolean request, String url) throws Exception {
        if (request != null) {
            if (request) JobRegistry.get().resume();
            else JobRegistry.get().pause();
            setProperties("mode", request.booleanValue() + "");
        }

        Weather response = new Weather();
        response.setAutoUpdate(!JobRegistry.get().isPaused());
        if (!JobRegistry.get().isPaused() && OWM.getInstance() != null) {
            TimeForecast weather = OWM.getInstance().getWeather();
            TimeForecast.Temperature temp = weather.getTemperature();
            response.setTemperature(temp.getMin() + " - " + temp.getMax() + " " + temp.getUnit());
            response.setHumidity(weather.getHumidity().getValue() + " " + weather.getHumidity().getUnit());
            response.setClouds(weather.getClouds().getValue() + " " + weather.getClouds().getUnit());
            response.setPrecipitation(weather.getPrecipitation().getValue() + " " + weather.getPrecipitation().getUnit());
        }
        return response;
    }

    @Override
    public String getContext() {
        return "/system";
    }
}
