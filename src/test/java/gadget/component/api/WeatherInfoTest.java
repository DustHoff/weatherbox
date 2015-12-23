package gadget.component.api;

import gadget.component.api.data.Weather;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Dustin on 23.12.2015.
 */
public class WeatherInfoTest extends ApiRegistryTest {

    @Test
    public void getWeather() throws Throwable {
        Weather weather = client.getWeather();
        Assert.assertNotNull(weather);
        Assert.assertNotNull(weather.getClouds());
        Assert.assertNotNull(weather.getHumidity());
        Assert.assertNotNull(weather.getPrecipitation());
        Assert.assertNotNull(weather.getTemperature());
    }
}
