package gadget.component.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import gadget.component.Component;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by Dustin on 27.09.2015.
 */
public abstract class ApiComponent<T> extends Component {

    private final Type type;
    private final Gson gson;

    public ApiComponent() {
        type = ((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0];

        gson = new GsonBuilder().setPrettyPrinting().create();
    }

    public abstract Object handleRequest(T request, String requestMethod) throws Exception;

    public final String getContext(){
        return "/"+getClass().getSimpleName();
    }

    public final HttpHandler getHandler() {
        return new HttpHandler() {
            public void handle(HttpExchange httpExchange) throws IOException {
                long start = System.currentTimeMillis();
                LOG.info("receive request");
                Object response;
                try {
                    T request;
                    if (httpExchange.getRequestMethod().equalsIgnoreCase("post")) {
                        StringWriter writer = new StringWriter();
                        IOUtils.copy(httpExchange.getRequestBody(), writer);
                        LOG.debug("Postdata: " + writer);
                        request = gson.fromJson(writer.toString(), type);
                    } else {
                        request = null;
                    }

                    LOG.debug("URL: " + httpExchange.getRequestURI().getPath());
                    LOG.debug("Method: " + httpExchange.getRequestMethod());
                    LOG.info("processing request");
                    response = handleRequest(request, httpExchange.getRequestURI().getPath().substring(getContext().length()));
                } catch (Exception e) {
                    response = null;
                    LOG.error("Problem while processing API-Request", e);
                }
                if (response != null) {
                    LOG.info("prepare response");
                    StringWriter writer = new StringWriter();
                    gson.toJson(response, writer);
                    LOG.debug("Response: " + writer.toString());
                    httpExchange.sendResponseHeaders(200, writer.toString().length());
                    httpExchange.getResponseBody().write(writer.toString().getBytes());
                } else {
                    httpExchange.sendResponseHeaders(500, 0);

                }
                httpExchange.close();
                LOG.debug("Took "+(System.currentTimeMillis()-start)+"ms");
            }
        };
    }
}
