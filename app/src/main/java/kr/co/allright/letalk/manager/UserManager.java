package kr.co.allright.letalk.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.firebase.geofire.GeoLocation;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import kr.co.allright.letalk.MainActivity;
import kr.co.allright.letalk.data.User;

import static android.content.ContentValues.TAG;

/**
 * Created by MacPro on 2017. 1. 3..
 */

public class UserManager {
    public interface UserManagerListener {
        void onUserData(User _user);
    }

    private static UserManager mInstance = null;
    private Context mContext;
    private ArrayList<UserManagerListener> mArrListeners;

    public static final String PREFS_FILE = "user_info.xml";
    public static final String PREFS_LOGIN_ID = "login_id";
    public static final String PREFS_EMAIL = "email";
    private static final String PREFS_DEVICE_ID = "device_id";

    private static final String USER_KEY_LASTACTIONTIME = "lastActionTime";
    public static final String USER_KEY_MYROOMID = "myroomId";
    public static final String USER_KEY_USERS = "users";
    private static final String USER_KEY_MYCHATIDS = "myChatIds";

    private volatile static UUID mUniqId;

    public static User mUser;
    public long mLastActionTime;

    private DatabaseReference mDBMyRef;
    private ValueEventListener mValueListener;
    private ChildEventListener mEventListener;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mDBUsersRef;

    public static UserManager getInstance(){
        return mInstance;
    }

    public UserManager(Context _context) {
        mInstance = this;

        mContext = _context;
        mArrListeners = new ArrayList<>();
        mUser = new User();

        mDatabase = FirebaseDatabase.getInstance();
        mDBUsersRef = mDatabase.getReference(USER_KEY_USERS);

        makeDeviceID();
    }

    public User getUserData(String _userid, @NotNull final UserManagerListener _listener){
        mArrListeners.add(_listener);
        DatabaseReference userRef = getUserRefWithId(_userid);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                _listener.onUserData(user);
                mArrListeners.remove(_listener);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return null;
    }

    public String getDeviceId() {
        return mUniqId.toString();
    }

    public boolean isLoigin(){
        if (mUser.loginId != null && mUser.loginId.length() > 0){
            return true;
        }
        return false;
    }

    public void onSignUp(String _email, String _name, String _age, String _sex, String _roomTitle) {
        String loginId = _email + "-" + mUniqId.toString();

        mUser.loginId = loginId;
        mUser.email = _email;
        mUser.name = _name;
        mUser.age = Integer.parseInt(_age);
        mUser.sex = _sex;
        mUser.myroomTitle = _roomTitle;

        Firebase.getInstance().onSignUp(loginId, mUniqId.toString());
    }

    public DatabaseReference getUsersRef(){
        return mDBUsersRef;
    }

    public DatabaseReference getUserRefWithId(String _keyid){
        return mDBUsersRef.child(_keyid);
    }

    public DatabaseReference getMyRef(){
        return mDBMyRef;
    }

    public void setMyRef(DatabaseReference _dbref){
        mDBMyRef = _dbref;
        mUser.keyid = mDBMyRef.getKey();

        mValueListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mUser = dataSnapshot.getValue(User.class);

                if (mUser.loginId == null){
                    // 유저정보 지워짐
                    Log.d(TAG, "UserManager:setMyRef");
                    //TODO: 로그인 실패 -> 로그인 화면으로 전환 -> 확인 필요
                    MainActivity.getInstance().showSignup();
                    MainActivity.getInstance().closeLoading();
                }else{
                    MainActivity.getInstance().onUpdateUI();
                    MainActivity.getInstance().closeLoading();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        mEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.getKey().equals(USER_KEY_LASTACTIONTIME)){
                    mLastActionTime = (long) dataSnapshot.getValue();
                }else if (dataSnapshot.getKey().equals(USER_KEY_MYCHATIDS)){
                    HashMap hashMap = (HashMap) dataSnapshot.getValue();
                    mUser.myChatIds.clear();
                    mUser.myChatIds.putAll(hashMap);
                }
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
        };

        mDBMyRef.addListenerForSingleValueEvent(mValueListener);
        mDBMyRef.addChildEventListener(mEventListener);
    }

    public void actionUser(){
        if (mDBMyRef == null){
            return;
        }
        mDBMyRef.child(USER_KEY_LASTACTIONTIME).setValue(ServerValue.TIMESTAMP);
    }

    public void udpateUser(HashMap<String, Object> _map){
        mDBMyRef.updateChildren(_map);
    }

    public void udpateUser(User _otherUser, HashMap<String, Object> _map){
        mDBUsersRef.child(_otherUser.keyid).updateChildren(_map);
    }

    public void updateUserLocation(){
        HashMap<String, Object> map = new HashMap();
        map.put("lat", GPSTracker.getInstance().getLatitude());
        map.put("lon", GPSTracker.getInstance().getLongitude());

        udpateUser(map);

        GeoManager.getInstance().getGeoFire().setLocation(mUser.keyid, new GeoLocation(GPSTracker.getInstance().getLatitude(), GPSTracker.getInstance().getLongitude()));
    }

    public void updateUserLoginTime(){
        mDBMyRef.child("logintime").setValue(ServerValue.TIMESTAMP);
    }

    public void updateUserTokenId(String _tokenID){
        if (_tokenID == null || mDBMyRef == null){
            return;
        }
        mDBMyRef.child("tokenId").setValue(_tokenID);
    }

    public void onResume(){
        if (mValueListener != null){
            mDBMyRef.addListenerForSingleValueEvent(mValueListener);
            mDBMyRef.addChildEventListener(mEventListener);
        }
    }

    public void onPause(){
        if (mValueListener != null) {
            mDBMyRef.removeEventListener(mValueListener);
            mDBMyRef.removeEventListener(mEventListener);
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
