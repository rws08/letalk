package kr.co.allright.letalk.manager;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import kr.co.allright.letalk.MainActivity;
import kr.co.allright.letalk.data.User;

import static android.content.ContentValues.TAG;
import static kr.co.allright.letalk.manager.UserManager.PREFS_FILE;
import static kr.co.allright.letalk.manager.UserManager.PREFS_LOGIN_ID;

/**
 * Created by MacPro on 2017. 1. 3..
 */

public class Firebase {
    private static Firebase mInstance = null;
    private Context mContext;
    private FirebaseAnalytics mFirebaseAnalytics;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mDBUsersRef;

    public static Firebase getInstance(){
        return mInstance;
    }

    public Firebase(Context _context) {
        mInstance = this;

        mContext = _context;
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(mContext);
        mAuth = FirebaseAuth.getInstance();

        mDatabase = FirebaseDatabase.getInstance();

        if (MainActivity.SERVER_TYPE == MainActivity.SERVER_TYPE_DEV){
            mDBUsersRef = mDatabase.getReference("DEV").child("users");
        }else{
            mDBUsersRef = mDatabase.getReference("users");
        }

        createListener();
    }

    public void onSignUp(final String _email, String _password){
        mAuth.createUserWithEmailAndPassword(_email, _password).addOnCompleteListener((Activity) mContext, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    makeNewUser(task.getResult().getUser());
                    onLogin(_email);
                }else{
                    onLogin(_email);
                }

                MainActivity.getInstance().closeSignup();
                MainActivity.getInstance().closeLoading();
            }
        });
    }

    public void onLogin(String _loginId){
        final SharedPreferences preferences = mContext.getSharedPreferences(PREFS_FILE, 0);
        final String loginId = _loginId == null ? preferences.getString(PREFS_LOGIN_ID, null) : _loginId;

        mAuth.signInWithEmailAndPassword(loginId, UserManager.getInstance().getDeviceId());

        preferences.edit().putString(PREFS_LOGIN_ID, loginId).commit();
    }

    private void makeNewUser(FirebaseUser _fbuser){
        FirebaseUser fbuser = _fbuser;
        DatabaseReference myRef = mDBUsersRef.child(fbuser.getUid());
        User user = UserManager.getInstance().mUser;
        user.keyid = myRef.getKey();

        myRef.setValue(user);

        final SharedPreferences preferences = mContext.getSharedPreferences(PREFS_FILE, 0);
        preferences.edit().putString(PREFS_LOGIN_ID, user.loginId).commit();

        RoomManager.getInstance().makeNewMyRoom();
    }

    private void setDatabse(FirebaseUser _fbuser){
        // 로그인 완료시 호출됨
        FirebaseUser fbuser = _fbuser;

        DatabaseReference myRef = mDBUsersRef.child(fbuser.getUid());
        UserManager.getInstance().setMyRef(myRef);
    }

    public void onStart(){
        mAuth.addAuthStateListener(mAuthListener);
    }

    public void onStop(){
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void createListener(){
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    MainActivity.getInstance().showLoading();
                    // User is signed in
                    setDatabse(user);

                    String refreshedToken = FirebaseInstanceId.getInstance().getToken();

                    UserManager.getInstance().updateUserLocation();
                    UserManager.getInstance().updateUserLoginTime();
                    UserManager.getInstance().updateUserTokenId(refreshedToken);
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    //TODO: 로그인 실패 -> 로그인 화면으로 전환 -> 확인 필요
                    MainActivity.getInstance().showSignup();
                }
                // ...
            }
        };
    }

    private void sendSampleLog(){
        Bundle params = new Bundle();
        params.putString("image_name", "sample");
        params.putString("full_text", "sample text");
        mFirebaseAnalytics.logEvent("share_image", params);
    }
}
