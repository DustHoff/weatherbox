package gadget.component.api;

import gadget.component.HardwareRegistry;
import gadget.component.api.data.ComponentInfo;
import gadget.component.hardware.Clouds;
import gadget.component.hardware.HardwareComponent;
import gadget.component.hardware.Rain;
import gadget.component.hardware.SkyLight;
import gadget.component.hardware.data.CloudType;
import gadget.component.hardware.data.SkyLightType;
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
        Boolean b = startGetRequest("http://localhost:8080/ambient", Boolean.class);
        Assert.assertFalse(b);
    }

    @Test
    public void getClouds() throws Throwable {
        Clouds clouds = Mockito.mock(Clouds.class);
        when(HardwareRegistry.get().getComponent("clouds")).thenReturn(clouds);
        when(clouds.getValue()).thenReturn(CloudType.NONE.name());

        HardwareComponent h = HardwareRegistry.get().getComponent("clouds");
        Assert.assertNotNull(h);

        String info = startGetRequest("http://localhost:8080/ambient/clouds", String.class);
        Assert.assertEquals(CloudType.NONE.name(), info);
    }

    @Test
    public void setClouds() throws Throwable {
        Clouds clouds = Mockito.mock(Clouds.class);
        when(HardwareRegistry.get().getComponent("clouds")).thenReturn(clouds);
        doCallRealMethod().when(clouds).setValue(anyString());
        doCallRealMethod().when(clouds).getValue();

        ComponentInfo request = new ComponentInfo();
        request.setComponent("clouds");
        request.setValue(CloudType.FOG.name());

        Boolean b = startPostRequest("http://localhost:8080/ambient", request, Boolean.class);
        Assert.assertTrue(b);
    }

    @Test
    public void getRain() throws Throwable {
        Rain rain = Mockito.mock(Rain.class);
        when(HardwareRegistry.get().getComponent("rain")).thenReturn(rain);
        when(rain.getValue()).thenReturn("0");

        String info = startGetRequest("http://localhost:8080/ambient/rain", String.class);
        Assert.assertEquals(0, Integer.parseInt(info));
    }

    @Test
    public void setRain() throws Throwable {
        Rain rain = Mockito.mock(Rain.class);
        when(HardwareRegistry.get().getComponent("rain")).thenReturn(rain);
        doCallRealMethod().when(rain).setValue(anyString());
        doCallRealMethod().when(rain).getValue();

        ComponentInfo request = new ComponentInfo();
        request.setComponent("rain");
        request.setValue("1000");
        Boolean b = startPostRequest("http://localhost:8080/ambient/", request, Boolean.class);
        Assert.assertTrue(b);

    }

    @Test
    public void getSkyLight() throws Throwable {
        SkyLight skyLight = Mockito.mock(SkyLight.class);
        when(HardwareRegistry.get().getComponent("skylight")).thenReturn(skyLight);
        when(skyLight.getValue()).thenReturn("0,0,0");

        String info = startGetRequest("http://localhost:8080/ambient/skylight", String.class);
        Assert.assertEquals(3, info.split(",").length);
        Assert.assertEquals("0,0,0", info);
    }

    @Test
    public void setSkylight() throws Throwable {
        SkyLight skyLight = Mockito.mock(SkyLight.class);
        when(HardwareRegistry.get().getComponent("skylight")).thenReturn(skyLight);
        doCallRealMethod().when(skyLight).setValue(anyString());
        doCallRealMethod().when(skyLight).getSkyLightType();
        ComponentInfo request = new ComponentInfo();
        request.setComponent("skylight");
        request.setValue("0,100,200");
        Boolean b = startPostRequest("http://localhost:8080/ambient/", request, Boolean.class);
        Assert.assertTrue(b);
        SkyLightType type = skyLight.getSkyLightType();
        Assert.assertEquals(0, type.getRed());
        Assert.assertEquals(100, type.getGreen());
        Assert.assertEquals(200, type.getBlue());
    }

}
