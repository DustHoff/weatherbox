package gadget.component.api;

import gadget.component.job.owm.data.City;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * Created by Dustin on 23.12.2015.
 */
public class CitySearchTest extends ApiRegistryTest {

    @Test
    public void simpleSearch() throws Throwable {
        List<City> result = client.searchCity(null);
        Assert.assertNotNull(result);
        Assert.assertEquals(10,result.size());
    }
    @Test
    public void searchOsnabrueck() throws Throwable {
        List<City> result = client.searchCity("Osnab");
        Assert.assertNotNull(result);

    }

    @Test
    public void searchBerlin() throws Throwable {
        List<City> result = client.searchCity("Berli");
        Assert.assertNotNull(result);
    }
}
