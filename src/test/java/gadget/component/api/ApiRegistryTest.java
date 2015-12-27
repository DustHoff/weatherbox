package gadget.component.api;

import com.google.gson.Gson;
import gadget.component.ApiRegistry;
import gadget.component.HardwareRegistry;
import gadget.component.job.owm.OWM;
import gadget.weatherbox.Client;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.mockito.Mockito;

import java.lang.reflect.Field;

/**
 * Created by Dustin on 03.10.2015.
 */
public class ApiRegistryTest {

    protected Client client = new Client("http://localhost:8080");
    private Gson gson = new Gson();

    @BeforeClass
    public static void start() throws Throwable {
        ApiRegistry.get().start();
    }

    @AfterClass
    public static void stop() throws Throwable {
        Thread.sleep(5000);
        ApiRegistry.get().stop();
    }

    @Before
    public void mockHardwareRegistry() throws Throwable {
        HardwareRegistry registry = Mockito.mock(HardwareRegistry.class);
        Field instance = HardwareRegistry.class.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(registry, registry);
        OWM owm = new OWM();
        owm.execute(null);

    }
}
