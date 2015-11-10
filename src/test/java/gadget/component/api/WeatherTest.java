package gadget.component.api;

import gadget.component.api.data.Response;
import gadget.component.api.data.WeatherRequest;
import gadget.component.api.data.WeatherResponse;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Dustin on 03.10.2015.
 */
public class WeatherTest extends ApiRegistryTest{

    @Test
    public void getCity() throws Throwable{
        Response response = startGetRequest("http://localhost:8080/weather");
        Object data  = response.convert();
        Assert.assertNotNull(data);
        Assert.assertTrue(data instanceof WeatherResponse);
        Assert.assertEquals("Osnabruck",((WeatherResponse) data).getCity());
    }

    @Test
    public void setCity() throws Throwable{
        WeatherRequest request = new WeatherRequest();
        WeatherResponse response = (WeatherResponse) startGetRequest("http://localhost:8080/weather").convert();
        request.setCity("Berlin");
        request.setKey(response.getKey());
        request.setDlcity(response.getDlcity());
        request.setUrl(response.getUrl());
        request.setForecast(response.getForecast());
        request.setSkyled(response.getSkyled());
        request.setUseClouds(response.isUseClouds());
        request.setUseSky(response.isUseSky());
        request.setUseRain(response.isUseRain());
        request.setDelay(response.getDelay());
        Response r = startPostRequest("http://localhost:8080/weather",request);
        if(r.convert() instanceof Throwable){
            ((Throwable) r.convert()).printStackTrace();
        }
        Assert.assertEquals(WeatherResponse.class.getName(),r.getType());
        response = (WeatherResponse) r.convert();

        Assert.assertEquals("Berlin",response.getCity());
    }
}
