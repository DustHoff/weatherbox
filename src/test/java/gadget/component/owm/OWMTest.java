package gadget.component.owm;

import gadget.component.owm.data.City;
import gadget.component.owm.generated.TimeForecast;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

/**
 * Created by Dustin on 30.08.2015.
 */
public class OWMTest {

    private OWM owm;

    @Before
    public void setup(){
        owm = new OWM();
    }

    @Test
    public void loadXMLFromOWM()throws Throwable {
        CloseableHttpResponse response = HttpClients.createDefault().execute(new HttpGet("http://api.openweathermap.org/data/2.5/forecast?mode=xml&APPID=828ce75931f9fef098daf3930139f6cd&id=2856883"));
        Assert.assertEquals("text/xml; charset=utf-8", response.getEntity().getContentType().getValue());
        //System.out.println(new Scanner(response.getEntity().getContent()).useDelimiter("\r").next());
        response.close();
    }

    @Test
    public void executeOWMJob() throws Throwable{
        owm.execute(null);
        TimeForecast weather = owm.getWeather();
        Assert.assertNotNull(weather);
    }

    @Test
    public void loadCityList() throws Throwable {
        owm.downloadCityList();
        City os = owm.getCity("Osnabruck");
        Assert.assertNotNull(os);
        Assert.assertEquals("Osnabruck",os.getName());
    }


}
