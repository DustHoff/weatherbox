package gadget.weatherbox.job;

import gadget.component.Component;
import gadget.component.HardwareRegistry;
import gadget.component.hardware.Clouds;
import gadget.component.hardware.Rain;
import gadget.component.hardware.SkyLight;
import gadget.component.hardware.data.SkyLightType;
import gadget.component.owm.OWM;
import gadget.component.owm.generated.TimeForecast;
import gadget.component.owm.generated.Weatherdata;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalTime;
import java.util.Calendar;

/**
 * Created by Dustin on 16.09.2015.
 */
public class WeatherUpdater extends Component implements Job {
    private final Clock clock;

    public WeatherUpdater(Clock clock){
        this.clock = clock;
    }

    public WeatherUpdater(){
        clock = Clock.systemDefaultZone();
    }

    public void execute(JobExecutionContext context) throws JobExecutionException {
        TimeForecast weather = OWM.getInstance().getWeather();
        if (weather == null) return;

        Weatherdata.Sun sun = OWM.getInstance().getSun();

        if (getProperty("use.skylight").equals("true")) updateSkyLight(weather, sun);
        if (getProperty("use.clouds").equals("true")) updateClouds(weather);
        if (getProperty("use.rain").equals("true")) updateRain(weather);

        System.out.println("finished Weatherupdate: " + weather.getTemperature().getValue() + "," +
                " Clouds:" + weather.getClouds().getAll() + "," +
                " Rain:" + weather.getPrecipitation().getValue());
    }

    private void updateRain(TimeForecast forecast) {
        TimeForecast.Precipitation rain = forecast.getPrecipitation();
        if (rain.getValue() == null) return;
        Rain component = (Rain) HardwareRegistry.get().getComponent("Rain");
        float data = rain.getValue().floatValue();
        int percent = (int) (data *100/10f);
        component.setValue(percent);
    }

    private void updateClouds(TimeForecast forecast) {
        TimeForecast.Clouds data = forecast.getClouds();
        Clouds clouds = (Clouds) HardwareRegistry.get().getComponent("Clouds");
        clouds.setValue(Integer.parseInt(data.getAll()));
    }

    public void updateSkyLight(TimeForecast weather, Weatherdata.Sun sun) {
        LocalTime now = LocalTime.now(clock);
        now = now.plusHours(Long.parseLong(getProperty("forecast")));

        Calendar c = weather.getFrom().toGregorianCalendar();
        LocalTime from = LocalTime.of(c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE));
        c = weather.getTo().toGregorianCalendar();
        LocalTime to = LocalTime.of(c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE));
        c = sun.getRise().toGregorianCalendar();
        LocalTime sunrise = LocalTime.of(c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE));
        c = sun.getSet().toGregorianCalendar();
        LocalTime sunset = LocalTime.of(c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE));
        SkyLightType type = null;

        if (sunrise.isBefore(now) && sunset.isAfter(now)) type = SkyLightType.DAY;
        else type = SkyLightType.NIGHT;

        /**
         sunrise start
         */
        if (datediff(now, sunrise) < 90L && datediff(now, sunrise) > -90L) {
            System.out.println("sunrise");
            System.out.println("sunrise = " + sunrise + ",now = " + now + ",diff = " + datediff(now, sunrise));
            if (datediff(now, sunrise) < 90L && datediff(now, sunrise) > 0L) {
                long percent = ((datediff(now, sunrise) * 100L) / 90L);
                percent = 100 - percent;
                System.out.println("percent = " + percent);
                type = SkyLightType.NIGHT.fade(SkyLightType.RISE, (int) percent);
            }
            if (datediff(now, sunrise) > -90L && datediff(now, sunrise) <= 0L) {
                long percent = ((datediff(now, sunrise) * 100L) / 90L);
                percent = percent * -1;
                System.out.println("percent = " + percent);
                type = SkyLightType.RISE.fade(SkyLightType.DAY, (int) percent);
            }
        }
        /**
         sunrise end
         */
        /**
         sunset start
         */
        if (datediff(now, sunset) < 90L && datediff(now, sunset) > -90L) {
            System.out.println("sunset");
            System.out.println("sunset = " + sunset + ", now = " + now + ", diff = " + datediff(now, sunset));
            if (datediff(now, sunset) < 90L && datediff(now, sunset) > 0L) {
                long percent = ((datediff(now, sunset) * 100L) / 90L);
                percent = 100 - percent;
                System.out.println("percent = " + percent);
                type = SkyLightType.DAY.fade(SkyLightType.RISE, (int) percent);
            }

            System.out.println("sunset = " + sunset + ", now = " + now + ", diff = " + datediff(now, sunset));
            if (datediff(now, sunset) > -90L && datediff(now, sunset) <= 0L) {
                long percent = ((datediff(now, sunset) * 100L) / 90L);
                percent = percent * -1;
                System.out.println("percent = " + percent);
                type = SkyLightType.RISE.fade(SkyLightType.NIGHT, (int) percent);
            }
        }
        /**
         sunset end
         */
        ((SkyLight) HardwareRegistry.get().getComponent("SkyLight")).setSkyLightType(type);
        System.out.println(type);
    }

    public long datediff(LocalTime one, LocalTime two) {
        Duration between = Duration.between(one, two);
        return between.getSeconds() / 60;
    }
}
