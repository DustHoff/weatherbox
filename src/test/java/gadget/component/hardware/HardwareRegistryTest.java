package gadget.component.hardware;

import gadget.component.HardwareRegistry;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by Dustin on 13.09.2015.
 */
public class HardwareRegistryTest {

    protected static HardwareRegistry registry;

    @BeforeClass
    public static void startRegistry() throws Throwable {
        registry = Mockito.mock(HardwareRegistry.class);
        Field instance = HardwareRegistry.class.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(registry, registry);
    }

    @Test
    public void checkMock() {
        Assert.assertEquals(registry, HardwareRegistry.get());
    }

    public GregorianCalendar getDate(String datestring) throws ParseException {
        DateFormat format = new SimpleDateFormat("dd.MM.yyyy kk:mm");
        Calendar c = GregorianCalendar.getInstance();
        c.setTime(format.parse(datestring));
        return (GregorianCalendar) c;
    }
}
