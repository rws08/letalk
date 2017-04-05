package kr.co.allright.letalk.manager;

import android.content.Context;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import kr.co.allright.letalk.data.JsonObjectRequest;
import kr.co.allright.letalk.data.User;

/**
 * Created by MacPro on 2017. 1. 4..
 */

public class PushManager {
    private static final String PUSH_URL = "https://fcm.googleapis.com/fcm/send";

    public static final String PUSH_ACTION_NEW_MESSAGE = "NEW_MESSAGE";
    public static final String PUSH_ACTION_NEW_CHAT = "NEW_CHAT";
    public static final String PUSH_ACTION_REMOVE_CHAT = "REMOVE_CHAT";

    private static PushManager mInstance = null;
    private Context mContext;

    public static PushManager getInstance(){
        return mInstance;
    }

    public PushManager(Context _context) {
        mInstance = this;

        mContext = _context;
    }

    public static void requestNewMessagePush(User _user){
        JSONObject jsonObject = new JSONObject();
        JSONObject jsonData = new JSONObject();

        try {
            jsonData.put("title", "New message");
            jsonData.put("body", "A new message has arrived.");
            jsonData.put("action", PUSH_ACTION_NEW_MESSAGE);

            jsonObject.put("to", _user.tokenId);
            jsonObject.put("data", jsonData);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = null;
        request = new JsonObjectRequest(PUSH_URL, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        ServerBManager.getInstance().addToRequestQueue(request);
    }

    public static void requestNewChatPush(User _user){
        JSONObject jsonObject = new JSONObject();
        JSONObject jsonData = new JSONObject();

        try {
            jsonData.put("title", "Invite to you");
            jsonData.put("body", "Someone wants to talk to you.");
            jsonData.put("action", PUSH_ACTION_NEW_CHAT);

            jsonObject.put("to", _user.tokenId);
            jsonObject.put("data", jsonData);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = null;
        request = new JsonObjectRequest(PUSH_URL, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        ServerBManager.getInstance().addToRequestQueue(request);
    }

    public static void requestRemoveChatPush(User _user){
        JSONObject jsonObject = new JSONObject();
        JSONObject jsonData = new JSONObject();

        try {
            jsonData.put("title", "End talk");
            jsonData.put("body", "Some talk is over.");
            jsonData.put("action", PUSH_ACTION_REMOVE_CHAT);

            jsonObject.put("to", _user.tokenId);
            jsonObject.put("data", jsonData);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = null;
        request = new JsonObjectRequest(PUSH_URL, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        ServerBManager.getInstance().addToRequestQueue(request);
    }
}
