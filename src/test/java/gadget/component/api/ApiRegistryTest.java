package gadget.component.api;

import com.google.gson.Gson;
import gadget.component.ApiRegistry;
import gadget.weatherbox.Client;
import org.junit.AfterClass;
import org.junit.BeforeClass;

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
        ApiRegistry.get().stop();
    }
}
