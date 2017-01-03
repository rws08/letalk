package kr.co.allright.letalk.manager;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

/**
 * Created by MacPro on 2017. 1. 3..
 */

public class UserManager {
    private static UserManager mInstance = null;

    private Context mContext;
    private static final String PREFS_FILE = "user_info.xml";
    private static final String PREFS_DEVICE_ID = "device_id";
    private volatile static UUID mUniqId;
    private static String mEmail;

    public static UserManager getInstance(){
        return mInstance;
    }

    public UserManager(Context _context) {
        mInstance = this;

        mContext = _context;

        makeDeviceID();
    }

    public String getDeviceId() {
        return mUniqId.toString();
    }

    public void login() {
        getUserEmail();

        Firebase.getInstance().onLogin(mEmail, mUniqId.toString());
    }

    private void getUserEmail() {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        AccountManager accountManager = AccountManager.get(mContext);
        Account[] accounts = accountManager.getAccountsByType("com.google");
        Account account = null;

        if (accounts.length > 0) {
            account = accounts[0];
        }

        if (account == null) {
            mEmail = null;
        } else {
            mEmail = account.name;
        }
    }

    private void makeDeviceID(){
        if (mUniqId == null){
            synchronized (UserManager.class){
                if (mUniqId == null){
                    final SharedPreferences preferences = mContext.getSharedPreferences(PREFS_FILE, 0);
                    final String id = preferences.getString(PREFS_DEVICE_ID, null);
                    if (id != null){
                        mUniqId = UUID.fromString(id);
                    }else{
                        final String androidId = Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);
                        try {
                            if (!"9774d56d682e549c".equals(androidId)) {
                                mUniqId = UUID.nameUUIDFromBytes(androidId.getBytes("utf8"));
                            }else{
                                final TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
                                final String deviceId = tm.getDeviceId();
                                mUniqId = deviceId != null ? UUID.nameUUIDFromBytes(deviceId.getBytes("utf8")) : UUID.randomUUID();
                            }
                        } catch (UnsupportedEncodingException e){
                            throw new RuntimeException(e);
                        }

                        preferences.edit().putString(PREFS_DEVICE_ID, mUniqId.toString()).commit();
                    }
                }
            }
        }
    }
}
