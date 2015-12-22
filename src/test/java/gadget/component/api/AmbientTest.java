package gadget.component.api;

import gadget.component.HardwareRegistry;
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
    public void getClouds() throws Throwable {
        Clouds clouds = Mockito.mock(Clouds.class);
        when(HardwareRegistry.get().getComponent(Clouds.class.getSimpleName())).thenReturn(clouds);
        when(clouds.getValue()).thenReturn(CloudType.NONE.name());

        HardwareComponent h = HardwareRegistry.get().getComponent(Clouds.class.getSimpleName());
        Assert.assertNotNull(h);
        CloudType info = client.getClouds();
        Assert.assertEquals(CloudType.NONE, info);
    }

    @Test
    public void setClouds() throws Throwable {
        Clouds clouds = Mockito.mock(Clouds.class);
        when(HardwareRegistry.get().getComponent(Clouds.class.getSimpleName())).thenReturn(clouds);
        doCallRealMethod().when(clouds).setValue(anyString());
        doCallRealMethod().when(clouds).getValue();

        Assert.assertTrue(client.setClouds(CloudType.FOG));
    }

    @Test
    public void getRain() throws Throwable {
        Rain rain = Mockito.mock(Rain.class);
        when(HardwareRegistry.get().getComponent(Rain.class.getSimpleName())).thenReturn(rain);
        when(rain.getValue()).thenReturn("0");

        Assert.assertEquals(0, client.getRain());
    }

    @Test
    public void setRain() throws Throwable {
        Rain rain = Mockito.mock(Rain.class);
        when(HardwareRegistry.get().getComponent(Rain.class.getSimpleName())).thenReturn(rain);
        doCallRealMethod().when(rain).setValue(anyString());
        doCallRealMethod().when(rain).getValue();

        Assert.assertTrue(client.setRain(1000));

    }

    @Test
    public void getSkyLight() throws Throwable {
        SkyLight skyLight = Mockito.mock(SkyLight.class);
        when(HardwareRegistry.get().getComponent(SkyLight.class.getSimpleName())).thenReturn(skyLight);
        when(skyLight.getValue()).thenReturn("0,0,0");

        SkyLightType info = client.getSkyLight();
        Assert.assertEquals(0, info.getRed());
        Assert.assertEquals(0, info.getGreen());
        Assert.assertEquals(0, info.getBlue());
    }

    @Test
    public void setSkylight() throws Throwable {
        SkyLight skyLight = Mockito.mock(SkyLight.class);
        when(HardwareRegistry.get().getComponent(SkyLight.class.getSimpleName())).thenReturn(skyLight);
        doCallRealMethod().when(skyLight).setValue(anyString());
        doCallRealMethod().when(skyLight).getSkyLightType();
        SkyLightType type = SkyLightType.FADED;
        type.modify((short)0,(short)100,(short)200);
        Assert.assertTrue(client.setSkyLight(type));
        type = skyLight.getSkyLightType();
        Assert.assertEquals(0, type.getRed());
        Assert.assertEquals(100, type.getGreen());
        Assert.assertEquals(200, type.getBlue());
    }

}
