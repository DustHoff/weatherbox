package gadget.component.api;

import gadget.component.api.data.Config;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Dustin on 03.10.2015.
 */
public class ConfigurationTest extends ApiRegistryTest {

    @Test
    public void getCity() throws Throwable{
        Config response = startGetRequest("http://localhost:8080/config", Config.class);
        Assert.assertNotNull(response);
        Assert.assertTrue(response instanceof Config);
        Assert.assertEquals("Osnabruck", response.getCity());
    }

    @Test
    public void setCity() throws Throwable{
        Config config = startGetRequest("http://localhost:8080/config", Config.class);
        config.setCity("Berlin");
        config = startPostRequest("http://localhost:8080/config", config, Config.class);
        Assert.assertEquals("Berlin", config.getCity());
    }
}
