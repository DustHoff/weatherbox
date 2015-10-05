package gadget.weatherbox;

import com.tinkerforge.NotConnectedException;
import gadget.component.ApiRegistry;
import gadget.component.HardwareRegistry;
import gadget.component.owm.OWM;
import gadget.weatherbox.job.WeatherUpdater;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.security.Permission;

/**
 * Created by Dustin on 30.08.2015.
 */
public class WeatherBox extends SecurityManager {

    private static Scheduler scheduler;

    public static void main(String[] args) throws Exception {
        System.setSecurityManager(new WeatherBox());
        HardwareRegistry.get().start();
        ApiRegistry.get().start();
        scheduler = StdSchedulerFactory.getDefaultScheduler();


        JobDetail owmDetail = JobBuilder.newJob(OWM.class).withIdentity("OWM").build();
        JobDetail weatherUpdater = JobBuilder.newJob(WeatherUpdater.class).withIdentity("Updater").build();

        Trigger triggerOWM = TriggerBuilder.newTrigger().
                withSchedule(CronScheduleBuilder.cronSchedule("0 * * * * ?")).
                forJob(owmDetail).build();

        Trigger triggerUpdater = TriggerBuilder.newTrigger().
                withSchedule(CronScheduleBuilder.cronSchedule("15 * * * * ?")).
                forJob(weatherUpdater).build();

        scheduler.scheduleJob(owmDetail, triggerOWM);
        scheduler.scheduleJob(weatherUpdater, triggerUpdater);

        // and start it off
        scheduler.start();
    }

    @Override
    public void checkExit(int status) {
        try {
            HardwareRegistry.get().stop();
            ApiRegistry.get().stop();
            scheduler.shutdown();
        } catch (NotConnectedException e) {
            e.printStackTrace();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void checkPermission(Permission perm) {

    }

    @Override
    public void checkPermission(Permission perm, Object context) {

    }
}
