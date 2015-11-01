package gadget.component.api;

import com.google.gson.Gson;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import gadget.component.ApiRegistry;
import gadget.component.api.data.Response;
import org.apache.commons.io.IOUtils;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.StringWriter;

/**
 * Created by Dustin on 03.10.2015.
 */
public class ApiRegistryTest {

    private Gson gson = new Gson();
    private OkHttpClient client = new OkHttpClient();

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
        Request request = new Request.Builder().url(url).build();
        com.squareup.okhttp.Response response = client.newCall(request).execute();
        StringWriter writer = new StringWriter();
        IOUtils.copy(response.body().byteStream(), writer);
        Response r = gson.fromJson(writer.toString(), Response.class);
        Object data = r.convert();

        if (data instanceof Throwable) throw new Exception((Throwable) data);
        return r;
    }

    public Response startPostRequest(String url, Object request) throws Throwable {

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, gson.toJson(request));
        Request request1 = new Request.Builder().url(url).post(body).build();
        com.squareup.okhttp.Response response = client.newCall(request1).execute();
        StringWriter writer = new StringWriter();
        IOUtils.copy(response.body().byteStream(), writer);
        Response r = gson.fromJson(writer.toString(), Response.class);
        Object data = r.convert();

        if (data instanceof Throwable) throw new Exception((Throwable) data);
        return r;
    }
}
