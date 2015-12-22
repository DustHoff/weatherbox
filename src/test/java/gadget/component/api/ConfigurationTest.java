package gadget.component.api;

import gadget.component.api.data.Config;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Dustin on 03.10.2015.
 */
public class ConfigurationTest extends ApiRegistryTest {

    @Test
    public void getCity() throws Throwable {
        Config response = client.getConfig();
        Assert.assertNotNull(response);
        Assert.assertTrue(response instanceof Config);
        Assert.assertEquals("Osnabruck", response.getCity());
    }

    @Test
    public void setCity() throws Throwable {
        Config config = client.getConfig();
        config.setCity("Berlin");
        config = client.setConfig(config);
        Assert.assertEquals("Berlin", config.getCity());
    }
}
