package gadget.component.api;

import com.google.gson.Gson;
import gadget.component.ApiRegistry;
import gadget.component.api.data.Response;
import gadget.component.owm.OWM;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.Assert;

import java.io.IOException;
import java.io.StringWriter;

/**
 * Created by Dustin on 03.10.2015.
 */
public class ApiRegistryTest {

    private Gson gson = new Gson();
    private CloseableHttpClient client = HttpClients.createDefault();

    @BeforeClass
    public static void start() throws Throwable {
        ApiRegistry.get().start();
    }

    @AfterClass
    public static void stop() throws Throwable {
        ApiRegistry.get().stop();
    }

    @Test
    public void simpleAmbient() throws Throwable {
        Response response = startGetRequest("http://localhost:8080/ambient");
        Assert.assertNotNull(response);
        Assert.assertEquals(200, response.getCode());
    }

    @Test
    public void simpleWeather() throws Throwable {
        Response response = startGetRequest("http://localhost:8080/weather");
        Assert.assertNotNull(response);
        Assert.assertEquals(200, response.getCode());
    }

    public Response startGetRequest(String url) throws Throwable {
        CloseableHttpResponse response = null;
        try {
            response = client.execute(new HttpGet(url));
            StringWriter writer = new StringWriter();
            IOUtils.copy(response.getEntity().getContent(), writer);
            Response r = gson.fromJson(writer.toString(), Response.class);
            Object data = r.convert();

            if (data instanceof Throwable) throw new Exception((Throwable) data);
            return r;
        } finally {
            response.close();
        }
    }

    public Response startPostRequest(String url, Object request) throws Throwable {
        CloseableHttpResponse response = null;
        try {
            HttpPost post = new HttpPost(url);
            post.setEntity(new StringEntity(gson.toJson(request)));
            response = client.execute(post);
            StringWriter writer = new StringWriter();
            IOUtils.copy(response.getEntity().getContent(), writer);
            Response r = gson.fromJson(writer.toString(), Response.class);
            Object data = r.convert();

            if (data instanceof Throwable) throw new Exception((Throwable) data);
            return r;
        } finally {
            response.close();
        }
    }
}
