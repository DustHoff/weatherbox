package gadget.component.hardware;

import com.sun.org.apache.xerces.internal.jaxp.datatype.XMLGregorianCalendarImpl;
import gadget.component.hardware.data.SkyLightType;
import gadget.component.job.WeatherUpdater;
import gadget.component.owm.generated.Weatherdata;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.text.ParseException;
import java.time.Clock;
import java.time.LocalTime;
import java.time.ZoneId;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.when;

/**
 * Created by Dustin on 13.09.2015.
 */

public class SkyLightTest extends HardwareRegistryTest {
    Logger LOG = LogManager.getRootLogger();

    private void runTest(String date, SkyLightType expected) throws Throwable {
        Clock clock = getClock(date);
        WeatherUpdater updater = new WeatherUpdater(clock);

        Weatherdata.Sun sun = getSun();
        updater.updateSkyLight(sun);
        assertEquals(expected, ((SkyLight) registry.getComponent("SkyLight")).getSkyLightType());
    }

    private void assertEquals(SkyLightType reqired, SkyLightType result) {
        Assert.assertEquals("ENUM", reqired, result);
        Assert.assertEquals("Red", reqired.getRed(), result.getRed());
        Assert.assertEquals("Green", reqired.getGreen(), result.getGreen());
        Assert.assertEquals("Blue", reqired.getBlue(), result.getBlue());
    }

    private Weatherdata.Sun getSun() throws ParseException {
        Weatherdata.Sun sun = new Weatherdata.Sun();
        sun.setRise(new XMLGregorianCalendarImpl(getDate("01.01.2015 07:30")));
        sun.setSet(new XMLGregorianCalendarImpl(getDate("01.01.2015 17:30")));
        return sun;
    }

    private Clock getClock(String date) throws ParseException {
        return Clock.fixed(getDate(date).toInstant(), ZoneId.systemDefault());
    }

    @Before
    public void setup() {
        SkyLight skyLight = Mockito.mock(SkyLight.class);
        doCallRealMethod().when(skyLight).setSkyLightType(any(SkyLightType.class));
        doCallRealMethod().when(skyLight).getSkyLightType();
        when(registry.getComponent("SkyLight")).thenReturn(skyLight);
    }

    @Test
    public void day() throws Throwable {
        LOG.info("day");
        runTest("01.01.2015 12:00", SkyLightType.DAY);
    }


    @Test
    public void night() throws Throwable {
        LOG.info("night");
        runTest("01.01.2015 21:00", SkyLightType.NIGHT);
    }

    @Test
    public void sunrise() throws Throwable {
        LOG.info("sunrise");
        runTest("01.01.2015 07:30", SkyLightType.NIGHT);
        LOG.info("sunrise: rise");
        runTest("01.01.2015 08:00", SkyLightType.NIGHT.fade(SkyLightType.RISE, 33));
        runTest("01.01.2015 09:30", SkyLightType.RISE.fade(SkyLightType.DAY, 33));
        LOG.info("sunrise: day");
        runTest("01.01.2015 10:30", SkyLightType.DAY);
    }

    @Test
    public void sunset() throws Throwable {
        LOG.info("sunset");
        runTest("01.01.2015 17:30", SkyLightType.DAY);
        LOG.info("sunset: rise");
        runTest("01.01.2015 18:00", SkyLightType.DAY.fade(SkyLightType.RISE, 33));
        runTest("01.01.2015 18:00", SkyLightType.RISE.fade(SkyLightType.NIGHT, 33));
        LOG.info("sunset: night");
        runTest("01.01.2015 22:30", SkyLightType.NIGHT);
    }

    @Test
    public void dateDiffTest() throws Throwable {
        WeatherUpdater updater = new WeatherUpdater();
        long result = updater.datediffPositive(LocalTime.now(getClock("01.01.2015 10:00")), LocalTime.now(getClock("01.01.2015 12:00")));
        Assert.assertEquals(120L, result);
    }

    @Test
    public void dateDiffTest2() throws Throwable {
        WeatherUpdater updater = new WeatherUpdater();
        long result = updater.datediffPositive(LocalTime.now(getClock("01.01.2015 12:00")), LocalTime.now(getClock("01.01.2015 10:00")));
        Assert.assertEquals(120L, result);
    }

    @Test
    public void delay() throws Throwable {
        WeatherUpdater updater = new WeatherUpdater();
        long result = updater.delayPercent(LocalTime.now(getClock("01.01.2015 11:00")), LocalTime.now(getClock("01.01.2015 10:00")), false);
        Assert.assertEquals(66, result);
        result = updater.delayPercent(LocalTime.now(getClock("01.01.2015 10:30")), LocalTime.now(getClock("01.01.2015 10:00")), false);
        Assert.assertEquals(33, result);
    }

    @Test
    public void delayHalf() throws Throwable {
        WeatherUpdater updater = new WeatherUpdater();
        boolean result = updater.delayHalf(LocalTime.now(getClock("01.01.2015 10:00")), LocalTime.now(getClock("01.01.2015 10:00")));
        Assert.assertTrue(result);

    }
}


