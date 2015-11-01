package gadget.component.job.owm;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import gadget.component.Component;
import gadget.component.job.owm.data.City;
import gadget.component.job.owm.data.CityParser;
import gadget.component.owm.generated.TimeForecast;
import gadget.component.owm.generated.Weatherdata;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.SchemaFactory;
import java.io.IOException;
import java.net.URL;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.zip.GZIPInputStream;

/**
 * Created by Dustin on 30.08.2015.
 */
public class OWM extends Component implements Job{

    private static OWM instance;
    private OkHttpClient client;
    private Weatherdata weatherData;
    private List<City> city;

    public OWM(){
        client = new OkHttpClient();
    }

    public static OWM getInstance(){
        return instance;
    }


    public void execute(JobExecutionContext context) throws JobExecutionException {
        if(instance==null){
            instance=this;
            downloadCityList();
        }
        try {
            String owmurl = getProperty("url").replace("$KEY", getProperty("key")).replace("$CITY", instance.getCity(getProperty("city")).get_id() + "");
            LOG.debug("OWM " + owmurl);
            Request request = new Request.Builder().url(owmurl).build();
            Response response = client.newCall(request).execute();

            JAXBContext jaxbContext = JAXBContext.newInstance("gadget.component.owm.generated");
            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            URL url = getClass().getClassLoader().getResource("OpenWeatherMap.xsd");
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            unmarshaller.setSchema(schemaFactory.newSchema(url));
            weatherData = (Weatherdata) unmarshaller.unmarshal(response.body().byteStream());
        } catch (Exception e) {
            throw new JobExecutionException(e);
        }
        LOG.info("finished download weather data");
    }

    public City getCity(String city) {
        for(City c : this.city){
            if(c.getName().equalsIgnoreCase(city))return c;
        }
        return null;
    }

    public TimeForecast getWeather(){
        if (weatherData==null) return null;

        Calendar calendar = GregorianCalendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY, Integer.parseInt(getProperty("forecast")));

        List<TimeForecast> forecast = weatherData.getForecast().getTime();
        for (TimeForecast time : forecast){
            if(time.getFrom().toGregorianCalendar().getTime().before(calendar.getTime()) &&
                    time.getTo().toGregorianCalendar().getTime().after(calendar.getTime())) return time;
        }
        return null;
    }

    public Weatherdata.Sun getSun(){
        if(weatherData==null)return null;
        return weatherData.getSun();
    }


    public void downloadCityList()
    {
        try {
            LOG.info("download city list");
            Request request = new Request.Builder().url(getProperty("dlcity")).build();
            LOG.debug("url " + request.urlString());
            Response response = client.newCall(request).execute();
            GZIPInputStream inputStream = new GZIPInputStream(response.body().byteStream());

            CityParser parser = new CityParser();
            city = parser.parse(inputStream);
            LOG.info("loaded Cities " + city.size());
            inputStream.close();
        } catch (IOException e) {
            LOG.error("Problem while download city list", e);
        }
    }

    public List<City> getCities() {
        return city;
    }
}
