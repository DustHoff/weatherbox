package gadget.component.api;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import gadget.component.Component;
import gadget.component.api.data.Request;
import gadget.component.api.data.Response;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by Dustin on 27.09.2015.
 */
public abstract class ApiComponent<T extends Request> extends Component {

    private final Type type;
    private Gson gson = new Gson();

    public ApiComponent() {
        type = ((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0];
    }

    public abstract Response handleRequest(Request request) throws Exception;

    public abstract String getContext();

    public final HttpHandler getHandler() {
        return new HttpHandler() {
            public void handle(HttpExchange httpExchange) throws IOException {

                LOG.info("receive request");
                Response response;
                try {
                    String method = httpExchange.getRequestMethod();
                    Request request;
                    if (method.equalsIgnoreCase("post")) {
                        StringWriter writer = new StringWriter();
                        IOUtils.copy(httpExchange.getRequestBody(), writer);
                        request = gson.fromJson(writer.toString(), type);
                    } else {
                        request = new Request();
                    }
                    request.setRequestUrl(httpExchange.getRequestURI().getPath().substring(getContext().length()));
                    request.setMethod(method);

                    LOG.debug("URL: " + request.getRequestUrl());
                    LOG.debug("Method: " + request.getMethod());
                    LOG.info("processing request");
                    response = handleRequest(request);
                } catch (Exception e) {
                    response = new Response(e);
                    LOG.error("Problem while processing API-Request", e);
                }
                LOG.info("prepare response");
                String message = gson.toJson(response);
                LOG.debug("Response: " + message);
                httpExchange.sendResponseHeaders(response.getCode(), 0);
                httpExchange.getResponseBody().write(message.getBytes());
                httpExchange.close();
            }
        };
    }
}
