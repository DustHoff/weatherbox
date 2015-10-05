package gadget.component;

import com.sun.net.httpserver.HttpServer;
import gadget.component.api.ApiComponent;
import org.reflections.Reflections;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Set;

/**
 * Created by Dustin on 27.09.2015.
 */
public class ApiRegistry {

    private static ApiRegistry instance;
    private HttpServer server;

    public static ApiRegistry get(){
        if(instance==null) instance = new ApiRegistry();
        return instance;
    }
    private void load(){
        Reflections reflections = new Reflections("gadget.component.api");
        Set<Class<? extends ApiComponent>> api = reflections.getSubTypesOf(ApiComponent.class);
        try {
            for (Class<? extends ApiComponent> a : api) {
                ApiComponent instance = a.newInstance();
                server.createContext(instance.getContext(),instance.getHandler());
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress(8080),0);
        server.setExecutor(null);
        load();
        server.start();
    }

    public void stop(){
        server.stop(0);
    }
}
