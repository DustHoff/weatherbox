package gadget.component;

import gadget.component.job.owm.OWM;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;
import org.reflections.Reflections;

import java.util.Set;

/**
 * Created by Dustin on 09.10.2015.
 */
public class JobRegistry extends Component {

    private static JobRegistry instance;
    private final Logger LOG;
    private Scheduler scheduler;
    private boolean pause;

    private JobRegistry() {
        LOG = LogManager.getLogger(getClass().getSimpleName());
    }

    public static JobRegistry get() {
        if (instance == null) instance = new JobRegistry();
        return instance;
    }

    private void load() throws SchedulerException {
        Reflections reflections = new Reflections("gadget.component");
        Set<Class<? extends Job>> jobs = reflections.getSubTypesOf(Job.class);

        for (Class<? extends Job> job : jobs) {
            LOG.debug("create Job: " + job.getName());
            JobDetail jobDetail = JobBuilder.newJob(job).withIdentity(job.getSimpleName()).build();
            scheduler.scheduleJob(jobDetail, TriggerBuilder.newTrigger().withSchedule(CronScheduleBuilder.cronSchedule("0 * * * * ?")).build());
        }
    }

    public void start() {
        try {
            LOG.info("starting");
            scheduler = StdSchedulerFactory.getDefaultScheduler();
            load();
            scheduler.start();
            if (getProperty("mode") != null)
                if (getProperty("mode").equalsIgnoreCase("true")) pause();
        } catch (SchedulerException e) {
            LOG.error("Problem while starting", e);
        }
    }

    public void pause() {
        try {
            for (String group : scheduler.getJobGroupNames()) {
                for (JobKey key : scheduler.getJobKeys(GroupMatcher.<JobKey>groupEquals(group))) {
                    if (!key.getName().equalsIgnoreCase(OWM.class.getSimpleName())) scheduler.pauseJob(key);
                }
            }
            pause = true;
        } catch (SchedulerException e) {
            LOG.error("Problem while pausing", e);
        }
    }

    public void resume() {
        try {
            scheduler.resumeAll();
            pause = false;
        } catch (SchedulerException e) {
            LOG.error("Problem while resuming", e);
        }
    }

    public void stop() {
        try {
            LOG.info("stopping");
            scheduler.shutdown();
        } catch (SchedulerException e) {
            LOG.error("Problem while stopping", e);
        }
    }

    public boolean isPaused() {
        return pause;
    }
}
