package gadget.component.job;

import gadget.component.Component;
import gadget.component.HardwareRegistry;
import gadget.component.hardware.Clouds;
import gadget.component.hardware.Rain;
import gadget.component.hardware.SkyLight;
import gadget.component.hardware.data.SkyLightType;
import gadget.component.job.owm.OWM;
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

    public WeatherUpdater(Clock clock) {
        this.clock = clock;
    }

    public WeatherUpdater() {
        clock = Clock.systemDefaultZone();
    }

    public void execute(JobExecutionContext context) throws JobExecutionException {
        LOG.info("start weather update");
        TimeForecast weather = OWM.getInstance().getWeather();
        if (weather == null) return;

        Weatherdata.Sun sun = OWM.getInstance().getSun();

        if (getProperty("use.skylight").equals("true")) updateSkyLight(sun);
        else LOG.info("Skylight update is disabled");
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
        }
        if (getProperty("use.clouds").equals("true")) updateClouds(weather);
        else LOG.info("Clouds update is disabled");
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
        }
        if (getProperty("use.rain").equals("true")) updateRain(weather);
        else LOG.info("Rain update is disabled");

        LOG.debug("finished Weatherupdate: " + weather.getTemperature().getValue() + "," +
                " Clouds:" + weather.getClouds().getAll() + "," +
                " Rain:" + weather.getPrecipitation().getValue());
    }

    private void updateRain(TimeForecast forecast) {
        TimeForecast.Precipitation rain = forecast.getPrecipitation();
        if (rain.getValue() == null) return;
        Rain component = (Rain) HardwareRegistry.get().getComponent("Rain");
        float data = rain.getValue().floatValue();
        int percent = (int) (data * 100 / 10f);
        component.setValue(percent);
    }

    private void updateClouds(TimeForecast forecast) {
        TimeForecast.Clouds data = forecast.getClouds();
        Clouds clouds = (Clouds) HardwareRegistry.get().getComponent("Clouds");
        clouds.setValue(Integer.parseInt(data.getAll()));
    }

    public void updateSkyLight(Weatherdata.Sun sun) {
        LocalTime now = LocalTime.now(clock);
        now = now.plusHours(Long.parseLong(getProperty("forecast")));

        Calendar c = sun.getRise().toGregorianCalendar();
        LocalTime sunrise = LocalTime.of(c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE));
        c = sun.getSet().toGregorianCalendar();
        LocalTime sunset = LocalTime.of(c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE));
        SkyLightType type;

        if (sunrise.isBefore(now) && sunset.isAfter(now)) type = SkyLightType.DAY;
        else type = SkyLightType.NIGHT;

        /**
         sunrise start
         */
        if (delay(now, sunrise)) {
            LOG.debug("time for sunrise");
            if (delayHalf(now, sunrise)) {
                LOG.debug("the sun is rising");
                long percent = delayPercent(now, sunrise, false);
                LOG.debug("percent " + percent);
                type = SkyLightType.NIGHT.fade(SkyLightType.RISE, (int) percent);
            } else {
                LOG.debug("the day is coming");
                long percent = delayPercent(now, sunrise, true);
                LOG.debug("percent " + percent);
                type = SkyLightType.RISE.fade(SkyLightType.DAY, (int) percent);
            }
        }
        /**
         sunrise end
         */
        /**
         sunset start
         */
        if (delay(now, sunset)) {
            LOG.debug("time for sunset");
            if (delayHalf(now, sunset)) {
                LOG.debug("the sun starts to set");
                long percent = delayPercent(now, sunset, false);
                LOG.debug("percent " + percent);
                type = SkyLightType.DAY.fade(SkyLightType.RISE, (int) percent);
            } else {
                LOG.debug("the night is coming");
                long percent = delayPercent(now, sunset, true);
                LOG.debug("percent " + percent);
                type = SkyLightType.RISE.fade(SkyLightType.NIGHT, (int) percent);
            }
        }
        /**
         sunset end
         */
        ((SkyLight) HardwareRegistry.get().getComponent("SkyLight")).setSkyLightType(type);
    }

    public long datediff(LocalTime one, LocalTime two) {
        Duration between = Duration.between(two, one);
        return between.getSeconds() / 60;
    }

    public long datediffPositive(LocalTime one, LocalTime two) {
        long diff = datediff(one, two);
        return (diff > 0) ? diff : -diff;
    }

    public boolean delay(LocalTime one, LocalTime two) {
        long delay = Long.parseLong(getProperty("delay"));
        return datediff(one, two) < delay && datediff(one, two) >= 0;
    }

    public boolean delayHalf(LocalTime one, LocalTime two) {
        long delay = Long.parseLong(getProperty("delay")) / 2;
        return datediff(one, two) < delay && datediff(one, two) >= 0;
    }

    public int delayPercent(LocalTime one, LocalTime two, boolean offset) {
        long delay = Long.parseLong(getProperty("delay")) / 2;
        int percent = (int) (((datediffPositive(one, two) - (offset ? delay : 0)) * 100) / delay);
        return percent;
    }
}
