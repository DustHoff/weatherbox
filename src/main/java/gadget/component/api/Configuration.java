package gadget.component.api;

import gadget.component.api.data.Config;

/**
 * Created by Dustin on 03.10.2015.
 */
public class Configuration extends ApiComponent<Config> {
    @Override
    public Object handleRequest(Config request, String url) throws Exception {
        if (request != null) {
            Config data = request;
            setProperties("city", data.getCity());
            setProperties("url", data.getUrl());
            setProperties("key", data.getKey());
            setProperties("dlcity", data.getDlcity());
            setProperties("forecast", data.getForecast() + "");
            setProperties("skyled", data.getSkyled() + "");
            setProperties("use.clouds", data.isUseClouds() + "");
            setProperties("use.skylight", data.isUseSky() + "");
            setProperties("use.rain", data.isUseRain() + "");
            setProperties("delay", data.getDelay() + "");
            setProperties("autoupdate",data.isAutoupdate()+"");
        }
        Config response = new Config();
        response.setCity(getProperty("city"));
        response.setUrl(getProperty("url"));
        response.setKey(getProperty("key"));
        response.setDlcity(getProperty("dlcity"));
        response.setForecast(Integer.parseInt(getProperty("forecast")));
        response.setSkyled(Integer.parseInt(getProperty("skyled")));
        response.setUseSky(Boolean.parseBoolean(getProperty("use.skylight")));
        response.setUseClouds(Boolean.parseBoolean(getProperty("use.clouds")));
        response.setUseRain(Boolean.parseBoolean(getProperty("use.rain")));
        response.setDelay(Long.parseLong(getProperty("delay")));
        response.setAutoupdate(Boolean.parseBoolean(getProperty("autoupdate")));
        return response;
    }
}
