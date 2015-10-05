package gadget.component.api.data;

/**
 * Created by Dustin on 03.10.2015.
 */
public class Request {

    private String method;
    private String requestUrl;

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
    }
}
