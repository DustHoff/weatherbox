package gadget.component.api;

import com.sun.org.apache.xpath.internal.operations.Bool;
import gadget.component.api.data.Request;
import gadget.component.api.data.Response;
import gadget.component.api.data.WeatherRequest;
import gadget.component.api.data.WeatherResponse;
import gadget.component.owm.OWM;

/**
 * Created by Dustin on 03.10.2015.
 */
public class Weather extends ApiComponent<WeatherRequest> {
    @Override
    public Response handleRequest(Request request) throws Exception {
        if (request instanceof WeatherRequest) {
            WeatherRequest data = (WeatherRequest) request;
            setProperties("city", data.getCity());
            setProperties("url", data.getUrl());
            setProperties("key", data.getKey());
            setProperties("dlcity", data.getDlcity());
            setProperties("forecast", data.getForecast() + "");
            setProperties("skyled", data.getSkyled() + "");
            setProperties("use.clouds", data.isUseClouds() + "");
            setProperties("use.skylight", data.isUseSky() + "");
            setProperties("use.rain", data.isUseRain() + "");
        }
        WeatherResponse response = new WeatherResponse();
        response.setCity(getProperty("city"));
        response.setUrl(getProperty("url"));
        response.setKey(getProperty("key"));
        response.setDlcity(getProperty("dlcity"));
        response.setForecast(Integer.parseInt(getProperty("forecast")));
        response.setSkyled(Integer.parseInt(getProperty("skyled")));
        response.setUseSky(Boolean.parseBoolean(getProperty("use.skylight")));
        response.setUseClouds(Boolean.parseBoolean(getProperty("use.clouds")));
        response.setUseRain(Boolean.parseBoolean(getProperty("use.rain")));
        if(OWM.getInstance()!=null)response.setCities(OWM.getInstance().getCities());
        return new Response(response);
    }

    @Override
    public String getContext() {
        return "/weather";
    }
}
