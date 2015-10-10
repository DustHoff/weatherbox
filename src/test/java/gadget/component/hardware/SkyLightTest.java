package gadget.component.hardware;

import com.sun.org.apache.xerces.internal.jaxp.datatype.XMLGregorianCalendarImpl;
import gadget.component.hardware.data.SkyLightType;
import gadget.component.job.WeatherUpdater;
import gadget.component.owm.generated.TimeForecast;
import gadget.component.owm.generated.Weatherdata;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.time.Clock;
import java.time.ZoneId;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.when;

/**
 * Created by Dustin on 13.09.2015.
 */

public class SkyLightTest extends HardwareRegistryTest {

    @Before
    public void setup() {
        SkyLight skyLight = Mockito.mock(SkyLight.class);
        doCallRealMethod().when(skyLight).setSkyLightType(any(SkyLightType.class));
        doCallRealMethod().when(skyLight).getSkyLightType();
        when(registry.getComponent("SkyLight")).thenReturn(skyLight);
    }

    @Test
    public void day() throws Throwable {
        Clock clock = Clock.fixed(getDate("01.01.2015 12:00").toInstant(), ZoneId.systemDefault());
        WeatherUpdater updater = new WeatherUpdater(clock);
        Weatherdata.Sun sun = new Weatherdata.Sun();
        sun.setRise(new XMLGregorianCalendarImpl(getDate("01.01.2015 07:30")));
        sun.setSet(new XMLGregorianCalendarImpl(getDate("01.01.2015 17:30")));
        TimeForecast forecast = new TimeForecast();
        forecast.setFrom(new XMLGregorianCalendarImpl(getDate("01.01.2015 10:00")));
        forecast.setTo(new XMLGregorianCalendarImpl(getDate("01.01.2015 13:00")));

        updater.updateSkyLight(forecast, sun);

        Assert.assertEquals(SkyLightType.DAY, ((SkyLight) registry.getComponent("SkyLight")).getSkyLightType());
    }

    @Test
    public void sunset_before() throws Throwable {
        Clock clock = Clock.fixed(getDate("01.01.2015 17:30").toInstant(), ZoneId.systemDefault());
        WeatherUpdater updater = new WeatherUpdater(clock);

        Weatherdata.Sun sun = new Weatherdata.Sun();
        sun.setRise(new XMLGregorianCalendarImpl(getDate("01.01.2015 07:30")));
        sun.setSet(new XMLGregorianCalendarImpl(getDate("01.01.2015 18:00")));
        TimeForecast forecast = new TimeForecast();
        forecast.setFrom(new XMLGregorianCalendarImpl(getDate("01.01.2015 16:00")));
        forecast.setTo(new XMLGregorianCalendarImpl(getDate("01.01.2015 19:00")));

        updater.updateSkyLight(forecast, sun);

        SkyLightType type = SkyLightType.DAY.fade(SkyLightType.RISE, 77);
        SkyLightType result = ((SkyLight) registry.getComponent("SkyLight")).getSkyLightType();

        Assert.assertEquals("ENUM", type, result);
        Assert.assertEquals("Red", type.getRed(), result.getRed());
        Assert.assertEquals("Green", type.getGreen(), result.getGreen());
        Assert.assertEquals("Blue", type.getBlue(), result.getBlue());
    }

    @Test
    public void sunset() throws Throwable {
        Clock clock = Clock.fixed(getDate("01.01.2015 18:00").toInstant(), ZoneId.systemDefault());
        WeatherUpdater updater = new WeatherUpdater(clock);

        Weatherdata.Sun sun = new Weatherdata.Sun();
        sun.setRise(new XMLGregorianCalendarImpl(getDate("01.01.2015 07:30")));
        sun.setSet(new XMLGregorianCalendarImpl(getDate("01.01.2015 18:00")));
        TimeForecast forecast = new TimeForecast();
        forecast.setFrom(new XMLGregorianCalendarImpl(getDate("01.01.2015 16:00")));
        forecast.setTo(new XMLGregorianCalendarImpl(getDate("01.01.2015 19:00")));

        updater.updateSkyLight(forecast, sun);

        SkyLightType type = SkyLightType.RISE;
        SkyLightType result = ((SkyLight) registry.getComponent("SkyLight")).getSkyLightType();

        Assert.assertEquals("ENUM", type, result);
        Assert.assertEquals("Red", type.getRed(), result.getRed());
        Assert.assertEquals("Green", type.getGreen(), result.getGreen());
        Assert.assertEquals("Blue", type.getBlue(), result.getBlue());
    }

    @Test
    public void sunset_after() throws Throwable {
        Clock clock = Clock.fixed(getDate("01.01.2015 18:30").toInstant(), ZoneId.systemDefault());
        WeatherUpdater updater = new WeatherUpdater(clock);

        Weatherdata.Sun sun = new Weatherdata.Sun();
        sun.setRise(new XMLGregorianCalendarImpl(getDate("01.01.2015 07:30")));
        sun.setSet(new XMLGregorianCalendarImpl(getDate("01.01.2015 18:00")));
        TimeForecast forecast = new TimeForecast();
        forecast.setFrom(new XMLGregorianCalendarImpl(getDate("01.01.2015 16:00")));
        forecast.setTo(new XMLGregorianCalendarImpl(getDate("01.01.2015 19:00")));

        updater.updateSkyLight(forecast, sun);

        SkyLightType type = SkyLightType.RISE.fade(SkyLightType.NIGHT, 33);
        SkyLightType result = ((SkyLight) registry.getComponent("SkyLight")).getSkyLightType();

        Assert.assertEquals("ENUM", type, result);
        Assert.assertEquals("Red", type.getRed(), result.getRed());
        Assert.assertEquals("Green", type.getGreen(), result.getGreen());
        Assert.assertEquals("Blue", type.getBlue(), result.getBlue());

        clock = Clock.fixed(getDate("01.01.2015 19:20").toInstant(), ZoneId.systemDefault());
        updater = new WeatherUpdater(clock);
        updater.updateSkyLight(forecast, sun);

        type = SkyLightType.RISE.fade(SkyLightType.NIGHT, 88);
        result = ((SkyLight) registry.getComponent("SkyLight")).getSkyLightType();

        Assert.assertEquals("ENUM", type, result);
        Assert.assertEquals("Red", type.getRed(), result.getRed());
        Assert.assertEquals("Green", type.getGreen(), result.getGreen());
        Assert.assertEquals("Blue", type.getBlue(), result.getBlue());
    }


    @Test
    public void sunrise_before() throws Throwable {
        Clock clock = Clock.fixed(getDate("01.01.2015 07:00").toInstant(), ZoneId.systemDefault());
        WeatherUpdater updater = new WeatherUpdater(clock);

        Weatherdata.Sun sun = new Weatherdata.Sun();
        sun.setRise(new XMLGregorianCalendarImpl(getDate("01.01.2015 07:30")));
        sun.setSet(new XMLGregorianCalendarImpl(getDate("01.01.2015 18:00")));
        TimeForecast forecast = new TimeForecast();
        forecast.setFrom(new XMLGregorianCalendarImpl(getDate("01.01.2015 06:00")));
        forecast.setTo(new XMLGregorianCalendarImpl(getDate("01.01.2015 09:00")));

        updater.updateSkyLight(forecast, sun);

        SkyLightType type = SkyLightType.DAY.fade(SkyLightType.RISE, 77);
        SkyLightType result = ((SkyLight) registry.getComponent("SkyLight")).getSkyLightType();

        Assert.assertEquals("ENUM", type, result);
        Assert.assertEquals("Red", type.getRed(), result.getRed());
        Assert.assertEquals("Green", type.getGreen(), result.getGreen());
        Assert.assertEquals("Blue", type.getBlue(), result.getBlue());
    }

    @Test
    public void sunrise() throws Throwable {
        Clock clock = Clock.fixed(getDate("01.01.2015 07:30").toInstant(), ZoneId.systemDefault());
        WeatherUpdater updater = new WeatherUpdater(clock);

        Weatherdata.Sun sun = new Weatherdata.Sun();
        sun.setRise(new XMLGregorianCalendarImpl(getDate("01.01.2015 07:30")));
        sun.setSet(new XMLGregorianCalendarImpl(getDate("01.01.2015 18:00")));
        TimeForecast forecast = new TimeForecast();
        forecast.setFrom(new XMLGregorianCalendarImpl(getDate("01.01.2015 06:00")));
        forecast.setTo(new XMLGregorianCalendarImpl(getDate("01.01.2015 09:00")));

        updater.updateSkyLight(forecast, sun);

        SkyLightType type = SkyLightType.RISE;
        SkyLightType result = ((SkyLight) registry.getComponent("SkyLight")).getSkyLightType();

        Assert.assertEquals("ENUM", type, result);
        Assert.assertEquals("Red", type.getRed(), result.getRed());
        Assert.assertEquals("Green", type.getGreen(), result.getGreen());
        Assert.assertEquals("Blue", type.getBlue(), result.getBlue());
    }

    @Test
    public void sunrise_after() throws Throwable {
        Clock clock = Clock.fixed(getDate("01.01.2015 08:00").toInstant(), ZoneId.systemDefault());
        WeatherUpdater updater = new WeatherUpdater(clock);

        Weatherdata.Sun sun = new Weatherdata.Sun();
        sun.setRise(new XMLGregorianCalendarImpl(getDate("01.01.2015 07:30")));
        sun.setSet(new XMLGregorianCalendarImpl(getDate("01.01.2015 18:00")));
        TimeForecast forecast = new TimeForecast();
        forecast.setFrom(new XMLGregorianCalendarImpl(getDate("01.01.2015 06:00")));
        forecast.setTo(new XMLGregorianCalendarImpl(getDate("01.01.2015 09:00")));

        updater.updateSkyLight(forecast, sun);

        SkyLightType type = SkyLightType.RISE.fade(SkyLightType.DAY, 77);
        SkyLightType result = ((SkyLight) registry.getComponent("SkyLight")).getSkyLightType();

        Assert.assertEquals("ENUM", type, result);
        Assert.assertEquals("Red", type.getRed(), result.getRed());
        Assert.assertEquals("Green", type.getGreen(), result.getGreen());
        Assert.assertEquals("Blue", type.getBlue(), result.getBlue());
    }


    @Test
    public void night() throws Throwable {
        Clock clock = Clock.fixed(getDate("01.01.2015 20:00").toInstant(), ZoneId.systemDefault());
        WeatherUpdater updater = new WeatherUpdater(clock);

        Weatherdata.Sun sun = new Weatherdata.Sun();
        sun.setRise(new XMLGregorianCalendarImpl(getDate("01.01.2015 07:30")));
        sun.setSet(new XMLGregorianCalendarImpl(getDate("01.01.2015 17:30")));
        TimeForecast forecast = new TimeForecast();
        forecast.setFrom(new XMLGregorianCalendarImpl(getDate("01.01.2015 19:00")));
        forecast.setTo(new XMLGregorianCalendarImpl(getDate("01.01.2015 21:00")));

        updater.updateSkyLight(forecast, sun);

        Assert.assertEquals(SkyLightType.NIGHT, ((SkyLight) registry.getComponent("SkyLight")).getSkyLightType());
    }
}
