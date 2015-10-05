package gadget.component.owm;

import com.google.gson.Gson;
import gadget.component.Component;
import gadget.component.owm.data.City;
import gadget.component.owm.generated.TimeForecast;
import gadget.component.owm.generated.Weatherdata;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.SchemaFactory;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.zip.GZIPInputStream;

/**
 * Created by Dustin on 30.08.2015.
 */
public class OWM extends Component implements Job{

    private static OWM instance;
    private CloseableHttpClient client;
    private Weatherdata weatherData;
    private City[] city;

    public OWM(){
        client = HttpClients.createDefault();
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
            String owmurl = getProperty("url").replace("$KEY",getProperty("key")).replace("$CITY",getCity(getProperty("city")).get_id()+"");
            CloseableHttpResponse response = client.execute(new HttpGet(owmurl));

            JAXBContext jaxbContext = JAXBContext.newInstance("gadget.component.owm.generated");
            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            URL url = getClass().getClassLoader().getResource("OpenWeatherMap.xsd");
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            unmarshaller.setSchema(schemaFactory.newSchema(url));
            weatherData = (Weatherdata) unmarshaller.unmarshal(response.getEntity().getContent());
            response.close();
        } catch (Exception e) {
            throw new JobExecutionException(e);
        }
        System.out.println("Download OWM data finished");
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
        StringWriter writer = new StringWriter();
        try {
            CloseableHttpResponse response = client.execute(new HttpGet(getProperty("dlcity")));
            GZIPInputStream inputStream = new GZIPInputStream(response.getEntity().getContent());
            IOUtils.copy(inputStream, writer);
            response.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Gson gson = new Gson();
        //System.out.println(writer.toString().replace("\r","").replace("\n","").replace("}}{","}},{"));
        city = gson.fromJson("["+writer.toString().replace("\r","").replace("\n","").replace("}}{","}},{")+"]", City[].class);
    }

    public City[] getCities() {
        if(city==null) downloadCityList();
        return city;
    }
}
