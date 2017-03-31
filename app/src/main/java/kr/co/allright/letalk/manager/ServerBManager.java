package kr.co.allright.letalk.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;
import android.util.LruCache;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by MacPro on 2016. 11. 9..
 */

public class ServerBManager {
    private static ServerBManager mInstance = null;
    private Context mContext;

    public static final int ID_API_LOGIN = 1;

    private static final String URL_API_LOGIN = "/api/member/login.json?";

    public static final String PARAM_AND = "&";

    public static final String IN_MEMBER_ID = "MEMBER_ID=";

    public static final String OUT_RESULT_CODE = "RESULT_CODE";

    public static final String RESPONSE_CODE_SUCCESS = "200";
    public static final String RESPONSE_CODE_ALREADY_CONFIRMED = "601";
    public static final String RESPONSE_CODE_ALREADY_COMPLETE = "602";

    private static final String SET_COOKIE_KEY = "Set-Cookie";
    private static final String COOKIE_KEY = "Cookie";
    private static final String SESSION_COOKIE = "JSESSIONID";

    private SharedPreferences mPreferences;
    private String mSessionId = "";
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private HashMap<String, Object> m_mapResponse;

    public static ServerBManager getInstance(){
        return mInstance;
    }

    public ServerBManager(Context context) {
        mInstance = this;

        mContext = context;

        mPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        mRequestQueue = getRequestQueue();

        mImageLoader = new ImageLoader(mRequestQueue,
                new ImageLoader.ImageCache() {
                    private final LruCache<String, Bitmap>
                            cache = new LruCache<String, Bitmap>(20);

                    @Override
                    public Bitmap getBitmap(String url) {
                        return cache.get(url);
                    }

                    @Override
                    public void putBitmap(String url, Bitmap bitmap) {
                        cache.put(url, bitmap);
                    }
                });

        m_mapResponse = new HashMap<String, Object>();
    }

    private RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    public ImageLoader getImageLoader() {
        return mImageLoader;
    }

    public static String getUrl(int api){
        String ret = "https://fcm.googleapis.com/fcm/send";

        switch (api){
            case ID_API_LOGIN:{
                ret += URL_API_LOGIN;
            }break;
        }
        return ret;
    }

    public String parseResponse(int api, String response){
        return "";
    }

    public static void responseError(VolleyError error){
    }

    public HashMap<String, Object> getMapResponse(){
        return m_mapResponse;
    }

    /**
     * Checks the response headers for session cookie and saves it
     * if it finds it.
     * @param headers Response Headers.
     */
    public final void checkSessionCookie(Map<String, String> headers) {
        if (headers.containsKey(SET_COOKIE_KEY)
                && headers.get(SET_COOKIE_KEY).startsWith(SESSION_COOKIE)) {
            String cookie = headers.get(SET_COOKIE_KEY);
            if (cookie.length() > 0) {
                String[] splitCookie = cookie.split(";");
                String[] splitSessionId = splitCookie[0].split("=");
                cookie = splitSessionId[1];
                mSessionId = cookie;
//                SharedPreferences.Editor prefEditor = mPreferences.edit();
//                prefEditor.putString(SESSION_COOKIE, cookie);
//                prefEditor.commit();
            }
        }
    }

    /**
     * Adds session cookie to headers if exists.
     * @param headers
     */
    public final void addSessionCookie(Map<String, String> headers) {
        String sessionId = mSessionId;//mPreferences.getString(SESSION_COOKIE, "");
        if (sessionId.length() > 0) {
            StringBuilder builder = new StringBuilder();
            builder.append(SESSION_COOKIE);
            builder.append("=");
            builder.append(sessionId);
            if (headers.containsKey(COOKIE_KEY)) {
                builder.append("; ");
                builder.append(headers.get(COOKIE_KEY));
            }
            headers.put(COOKIE_KEY, builder.toString());
        }
    }
}
