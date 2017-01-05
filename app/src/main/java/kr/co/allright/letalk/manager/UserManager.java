package kr.co.allright.letalk.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.UUID;

import kr.co.allright.letalk.data.User;
import kr.co.allright.letalk.fragment.ProfileFragment;

/**
 * Created by MacPro on 2017. 1. 3..
 */

public class UserManager {
    private static UserManager mInstance = null;
    private Context mContext;

    public static final String PREFS_FILE = "user_info.xml";
    public static final String PREFS_LOGIN_ID = "login_id";
    public static final String PREFS_EMAIL = "email";
    private static final String PREFS_DEVICE_ID = "device_id";

    private volatile static UUID mUniqId;
    public static User mUser;
    private DatabaseReference mDBMyRef;

    public static UserManager getInstance(){
        return mInstance;
    }

    public UserManager(Context _context) {
        mInstance = this;

        mContext = _context;
        mUser = new User();

        makeDeviceID();
    }

    public String getDeviceId() {
        return mUniqId.toString();
    }

    public void onSignUp(String _email, String _name, String _age, String _roomTitle) {
        String loginId = _email + "-" + mUniqId.toString();

        mUser.loginId = loginId;
        mUser.email = _email;
        mUser.name = _name;
        mUser.age = Integer.parseInt(_age);
        mUser.myroomTitle = _roomTitle;

        Firebase.getInstance().onSignUp(loginId, mUniqId.toString());
    }

    public void setMyRef(DatabaseReference _dbref){
        mDBMyRef = _dbref;
        mUser.keyid = mDBMyRef.getKey();

        mDBMyRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mUser = dataSnapshot.getValue(User.class);
                ProfileFragment.getInstance().updateUI();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mDBMyRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mDBMyRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mUser = dataSnapshot.getValue(User.class);
                ProfileFragment.getInstance().updateUI();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void udpateUser(HashMap<String, Object> _map){
        mDBMyRef.updateChildren(_map);
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
