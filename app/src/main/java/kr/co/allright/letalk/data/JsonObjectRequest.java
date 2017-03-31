package kr.co.allright.letalk.data;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by MacPro on 2016. 11. 15..
 */

public class JsonObjectRequest extends com.android.volley.toolbox.JsonObjectRequest {
    public JsonObjectRequest(int method, String url, JSONObject jsonRequest, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(method, url, jsonRequest, listener, errorListener);
    }

    public JsonObjectRequest(String url, JSONObject jsonRequest, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(url, jsonRequest, listener, errorListener);
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String,String> headers = new HashMap<>();

        String credentials = "key=AAAAgH489Lg:APA91bE9Jn4NNmZoIlgekfv6ZLp_7WQmGs8xDWaa3m5QJCZjcc4qpjYpz9sFAP7IcdDE45dUKeZMoCOUxeFDcqIN0IKY4P7S3GSsYh1Rsf3AXe3WNSqgXgtgEVxft89GzTZzxcd06y3zydCpfCnyXGznj3ZzbEpLUw";

        headers.put("Content-Type", "application/json");
        headers.put("Authorization", credentials);

        return headers;
    }
}
