package gadget.weatherbox;

import com.google.gson.Gson;
import com.squareup.okhttp.*;
import gadget.component.api.data.ComponentInfo;
import gadget.component.api.data.Config;
import gadget.component.api.data.Weather;
import gadget.component.hardware.data.CloudType;
import gadget.component.hardware.data.SkyLightType;
import gadget.component.job.owm.data.City;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Created by Dustin on 22.12.2015.
 */
public class Client {

    private final Gson gson = new Gson();
    private final OkHttpClient client = new OkHttpClient();
    private final String host;

    public Client(String host) {
        this.host = host;
    }

    private <T> T request(Request request, Class<T> clazz) {
        T data = null;
        try {
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) throw new IOException(request.urlString() + " returned " + response.code());
            InputStream stream = response.body().byteStream();
            data = gson.fromJson(new InputStreamReader(stream), clazz);
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    private <T> T postRequest(String url, Class<T> clazz, Object data) {
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, gson.toJson(data));
        Request request = new Request.Builder().url(host + url).post(body).build();
        return request(request, clazz);
    }

    private <T> T getRequest(String url, Class<T> clazz) {
        Request request = new Request.Builder().url(host + url).build();
        return request(request, clazz);
    }

    public Weather getWeather() {
        return getRequest("/WeatherInfo", Weather.class);
    }

    public Config getConfig() {
        return getRequest("/Configuration", Config.class);
    }

    public Config setConfig(Config config) {
        return postRequest("/Configuration", Config.class, config);
    }

    public void enableAutoUpdate() {
        Config config = getConfig();
        config.setAutoupdate(true);
        setConfig(config);
    }

    public void disableAutoUpdate() {
        Config config = getConfig();
        config.setAutoupdate(false);
        setConfig(config);
    }

    public CloudType getClouds() {
        String clouds = getRequest("/Ambient/Clouds", String.class);
        return CloudType.valueOf(clouds);
    }

    public Boolean setClouds(CloudType clouds) {
        ComponentInfo info = new ComponentInfo();
        info.setValue(clouds.name());
        info.setComponent("Clouds");
        return postRequest("/Ambient", Boolean.class, info);
    }

    public SkyLightType getSkyLight() {
        String[] rgb = getRequest("/Ambient/SkyLight", String.class).split(",");
        SkyLightType type = SkyLightType.FADED;
        type.modify(Short.parseShort(rgb[0]), Short.parseShort(rgb[1]), Short.parseShort(rgb[2]));
        return type;
    }

    public Boolean setSkyLight(SkyLightType type) {
        ComponentInfo info = new ComponentInfo();
        info.setComponent("SkyLight");
        info.setValue(type.getRed() + "," + type.getGreen() + "," + type.getBlue());
        return postRequest("/Ambient", Boolean.class, info);
    }

    public int getRain(){
        return getRequest("/Ambient/Rain",Integer.class);
    }

    public Boolean setRain(int rain){
        ComponentInfo info = new ComponentInfo();
        info.setComponent("Rain");
        info.setValue(rain+"");
        return postRequest("/Ambient", Boolean.class, info);
    }

    @SuppressWarnings("unchecked")
    public List<City> searchCity(String name){
        return postRequest("/CitySearch",List.class,name);
    }
}
