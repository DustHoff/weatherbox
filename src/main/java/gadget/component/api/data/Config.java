package gadget.component.api.data;

import java.io.Serializable;

/**
 * Created by Dustin on 03.10.2015.
 */
public class Config implements Serializable {
    private String city;
    private String url;
    private String key;
    private String dlcity;
    private int forecast;
    private int skyled;
    private Boolean useSky;
    private Boolean useClouds;
    private Boolean useRain;
    private long delay;
    private Boolean autoupdate;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getForecast() {
        return forecast;
    }

    public void setForecast(int forecast) {
        this.forecast = forecast;
    }

    public int getSkyled() {
        return skyled;
    }

    public void setSkyled(int skyled) {
        this.skyled = skyled;
    }

    public Boolean isUseSky() {
        return useSky;
    }

    public void setUseSky(Boolean useSky) {
        this.useSky = useSky;
    }

    public Boolean isUseClouds() {
        return useClouds;
    }

    public void setUseClouds(Boolean useClouds) {
        this.useClouds = useClouds;
    }

    public Boolean isUseRain() {
        return useRain;
    }

    public void setUseRain(Boolean useRain) {
        this.useRain = useRain;
    }

    public String getDlcity() {
        return dlcity;
    }

    public void setDlcity(String dlcity) {
        this.dlcity = dlcity;
    }

    public long getDelay() {
        return delay;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }

    public Boolean isAutoupdate() {
        return autoupdate;
    }

    public void setAutoupdate(Boolean autoupdate) {
        this.autoupdate = autoupdate;
    }
}
