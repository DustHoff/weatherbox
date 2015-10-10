package gadget.component.api.data;

/**
 * Created by Dustin on 09.10.2015.
 */
public class SysInfoResponse {
    private long uptime;
    private boolean mode;
    private double load;
    private String temperature;
    private String humidity;
    private String clouds;
    private String precipitation;

    public long getUptime() {
        return uptime;
    }

    public void setUptime(long uptime) {
        this.uptime = uptime;
    }

    public boolean isMode() {
        return mode;
    }

    public void setMode(boolean mode) {
        this.mode = mode;
    }

    public void setLoad(double load) {
        this.load = load;
    }

    public double getLoad() {
        return load;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setClouds(String clouds) {
        this.clouds = clouds;
    }

    public String getClouds() {
        return clouds;
    }

    public void setPrecipitation(String precipitation) {
        this.precipitation = precipitation;
    }

    public void setPrecipitation(String precipitation) {
        this.precipitation = precipitation;
    }

    public String getPrecipitation() {
        return precipitation;
    }
}
