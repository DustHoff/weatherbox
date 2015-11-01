package gadget.component;

import com.sun.net.httpserver.HttpServer;
import gadget.component.api.ApiComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.reflections.Reflections;

import java.net.InetSocketAddress;
import java.util.Set;

/**
 * Created by Dustin on 27.09.2015.
 */
public class ApiRegistry {

    private static ApiRegistry instance;
    private final Logger LOG;
    private HttpServer server;

    private ApiRegistry() {
        LOG = LogManager.getLogger(getClass().getSimpleName());
    }

    public static ApiRegistry get() {
        if (instance == null) instance = new ApiRegistry();
        return instance;
    }

    private void load() {
        LOG.info("loading Api components");
        Reflections reflections = new Reflections("gadget.component.api");
        Set<Class<? extends ApiComponent>> api = reflections.getSubTypesOf(ApiComponent.class);
        for (Class<? extends ApiComponent> a : api) {
            try {
                LOG.debug("loading api " + a.getName());
                ApiComponent instance = a.newInstance();
                LOG.debug("create context " + instance.getContext());
                server.createContext(instance.getContext(), instance.getHandler());
            } catch (Throwable e) {
                LOG.error("Problem while loading ", e);
            }
        }
    }

    public void start() {
        LOG.info("starting");
        try {
            server = HttpServer.create(new InetSocketAddress(8080), 0);
            server.setExecutor(null);
            load();
            server.start();
        } catch (Throwable e) {
            LOG.error("Problem while starting", e);
        }
    }

    public void stop() {
        LOG.info("stopping");
        server.stop(0);
    }
}
