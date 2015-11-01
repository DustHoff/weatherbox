package gadget.weatherbox;

import gadget.component.ApiRegistry;
import gadget.component.HardwareRegistry;
import gadget.component.JobRegistry;

import java.security.Permission;

/**
 * Created by Dustin on 30.08.2015.
 */
public class WeatherBox extends SecurityManager {

    public static void main(String[] args) throws Exception {
        System.setSecurityManager(new WeatherBox());
        HardwareRegistry.get().start();
        ApiRegistry.get().start();
        JobRegistry.get().start();
    }

    @Override
    public void checkExit(int status) {
            HardwareRegistry.get().stop();
            ApiRegistry.get().stop();
            JobRegistry.get().stop();
    }

    @Override
    public void checkPermission(Permission perm) {

    }

    @Override
    public void checkPermission(Permission perm, Object context) {

    }
}
