package gadget.component.api;

import gadget.component.HardwareRegistry;
import gadget.component.api.data.AmbientRequest;
import gadget.component.api.data.Response;
import gadget.component.hardware.Clouds;
import gadget.component.hardware.Rain;
import gadget.component.hardware.SkyLight;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.lang.reflect.Field;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.when;

/**
 * Created by Dustin on 03.10.2015.
 */
public class AmbientTest extends ApiRegistryTest {

    @Before
    public void mockHardwareRegistry() throws Throwable {
        HardwareRegistry registry = Mockito.mock(HardwareRegistry.class);
        Field instance = HardwareRegistry.class.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(registry, registry);
    }

    @Test
    public void simpleFailedRequest() throws Throwable {
        Response response = startGetRequest("http://localhost:8080/ambient");
        Assert.assertEquals(Boolean.class.getName(), response.getType());
        Assert.assertFalse((Boolean) response.convert());
    }

    @Test
    public void getClouds() throws Throwable {
        Clouds clouds = Mockito.mock(Clouds.class);
        when(HardwareRegistry.get().getComponent("clouds")).thenReturn(clouds);
        when(clouds.getValue()).thenReturn(Clouds.Type.NONE.name());

        Response response = startGetRequest("http://localhost:8080/ambient/clouds");
        Assert.assertEquals(String.class.getName(), response.getType());
        String value = (String) response.convert();
        Assert.assertEquals(Clouds.Type.NONE.name(), value);
    }

    @Test
    public void setClouds() throws Throwable {
        Clouds clouds = Mockito.mock(Clouds.class);
        when(HardwareRegistry.get().getComponent("clouds")).thenReturn(clouds);
        doCallRealMethod().when(clouds).setValue(anyString());
        doCallRealMethod().when(clouds).getValue();

        AmbientRequest request = new AmbientRequest();
        request.setComponent("clouds");
        request.setValue(Clouds.Type.FOG.name());

        Response response = startPostRequest("http://localhost:8080/ambient",request);
        Assert.assertEquals(Boolean.class.getName(), response.getType());
        Assert.assertTrue((Boolean) response.convert());
    }

    @Test
    public void getRain() throws Throwable {
        Rain rain = Mockito.mock(Rain.class);
        when(HardwareRegistry.get().getComponent("rain")).thenReturn(rain);
        when(rain.getValue()).thenReturn("0");

        Response response = startGetRequest("http://localhost:8080/ambient/rain");
        Assert.assertEquals(String.class.getName(), response.getType());
        String value = (String) response.convert();
        Assert.assertEquals(0, Integer.parseInt(value));
    }

    @Test
    public void setRain() throws Throwable {
        Rain rain = Mockito.mock(Rain.class);
        when(HardwareRegistry.get().getComponent("rain")).thenReturn(rain);
        doCallRealMethod().when(rain).setValue(anyString());
        doCallRealMethod().when(rain).getValue();

        AmbientRequest request = new AmbientRequest();
        request.setComponent("rain");
        request.setValue("1000");
        Response response = startPostRequest("http://localhost:8080/ambient/", request);
        Assert.assertEquals(Boolean.class.getName(), response.getType());
        Assert.assertTrue((Boolean) response.convert());

    }

    @Test
    public void getSkyLight() throws Throwable {
        SkyLight skyLight = Mockito.mock(SkyLight.class);
        when(HardwareRegistry.get().getComponent("skylight")).thenReturn(skyLight);
        when(skyLight.getValue()).thenReturn("0,0,0");

        Response response = startGetRequest("http://localhost:8080/ambient/skylight");
        Assert.assertEquals(String.class.getName(), response.getType());
        String rgb = (String) response.convert();
        Assert.assertEquals(3, rgb.split(",").length);
    }

    @Test
    public void setSkylight() throws Throwable {
        SkyLight skyLight = Mockito.mock(SkyLight.class);
        when(HardwareRegistry.get().getComponent("skylight")).thenReturn(skyLight);
        doCallRealMethod().when(skyLight).setValue(anyString());
        doCallRealMethod().when(skyLight).getType();
        AmbientRequest request = new AmbientRequest();
        request.setComponent("skylight");
        request.setValue("0,100,200");
        Response response = startPostRequest("http://localhost:8080/ambient/", request);
        Assert.assertEquals(Boolean.class.getName(), response.getType());
        Assert.assertTrue((Boolean) response.convert());
        SkyLight.Type type = skyLight.getType();
        Assert.assertEquals(0, type.getRed());
        Assert.assertEquals(100, type.getGreen());
        Assert.assertEquals(200, type.getBlue());
    }

}
