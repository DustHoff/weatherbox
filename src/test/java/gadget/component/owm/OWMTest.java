package gadget.component.owm;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import gadget.component.job.owm.OWM;
import gadget.component.job.owm.data.City;
import gadget.component.owm.generated.TimeForecast;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

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
        Request request = new Request.Builder().url("http://api.openweathermap.org/data/2.5/forecast?mode=xml&APPID=828ce75931f9fef098daf3930139f6cd&id=2856883").build();
        Response response = new OkHttpClient().newCall(request).execute();
        Assert.assertEquals("text/xml; charset=utf-8", response.header("content-type"));
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
