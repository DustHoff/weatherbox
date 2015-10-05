package gadget.component.api.data;

import com.google.gson.Gson;

/**
 * Created by Dustin on 03.10.2015.
 */
public class Response {
    private int code;
    private String type;
    private String message;

    public Response(){}

    public Response(Object data){
        code=200;
        Gson gson = new Gson();
        message = gson.toJson(data);
        type = data.getClass().getName();
    }

    public Response(Throwable t){
        this((Object)t);
        code=500;
    }

    public int getCode() {
        return code;
    }

    public String getType() {
        return type;
    }

    public Object convert() throws ClassNotFoundException {
        Gson gson = new Gson();
        return gson.fromJson(message,Class.forName(type));
    }
}
