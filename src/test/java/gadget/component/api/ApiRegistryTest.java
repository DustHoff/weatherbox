package gadget.component.api;

import com.google.gson.Gson;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import gadget.component.ApiRegistry;
import org.apache.commons.io.IOUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;

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

    public <T> T startGetRequest(String url, Class<T> clazz) throws Throwable {
        Request request = new Request.Builder().url(url).build();
        com.squareup.okhttp.Response response = client.newCall(request).execute();
        StringWriter writer = new StringWriter();
        IOUtils.copy(response.body().byteStream(), writer);
        java.lang.System.out.println("got: " + writer.toString());
        return gson.fromJson(writer.toString(), clazz);
    }

    public <T> T startPostRequest(String url, Object request, Class<T> clazz) throws Throwable {

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, gson.toJson(request));
        Request request1 = new Request.Builder().url(url).post(body).build();
        com.squareup.okhttp.Response response = client.newCall(request1).execute();
        StringWriter writer = new StringWriter();
        IOUtils.copy(response.body().byteStream(), writer);
        java.lang.System.out.println("got: " + writer.toString());
        return gson.fromJson(writer.toString(), clazz);
    }
}
