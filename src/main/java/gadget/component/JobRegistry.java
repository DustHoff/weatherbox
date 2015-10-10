package gadget.component;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.reflections.Reflections;

import java.util.Set;

/**
 * Created by Dustin on 09.10.2015.
 */
public class JobRegistry {

    private static JobRegistry instance;
    private Scheduler scheduler;
    private boolean pause;

    private JobRegistry() {
    }

    public static JobRegistry get() {
        if (instance == null) instance = new JobRegistry();
        return instance;
    }

    private void load() throws SchedulerException {
        Reflections reflections = new Reflections("gadget.component");
        Set<Class<? extends Job>> jobs = reflections.getSubTypesOf(Job.class);

        for (Class<? extends Job> job : jobs) {
            JobDetail jobDetail = JobBuilder.newJob(job).withIdentity(job.getSimpleName()).build();
            scheduler.scheduleJob(jobDetail, TriggerBuilder.newTrigger().withSchedule(CronScheduleBuilder.cronSchedule("0 * * * * ?")).build());
        }
    }

    public void start() {
        try {
            load();
            scheduler = StdSchedulerFactory.getDefaultScheduler();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    public void pause() {
        try {
            scheduler.pauseAll();
            pause = true;
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    public void resume() {
        try {
            scheduler.resumeAll();
            pause = false;
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        try {
            scheduler.shutdown();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    public boolean isPaused() {
        return pause;
    }
}
