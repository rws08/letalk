package kr.co.allright.letalk.manager;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static android.content.ContentValues.TAG;

/**
 * Created by MacPro on 2017. 1. 3..
 */

public class Firebase {
    private static Firebase mInstance = null;
    private Context mContext;
    private FirebaseAnalytics mFirebaseAnalytics;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    public static Firebase getInstance(){
        return mInstance;
    }

    public Firebase(Context _context) {
        mInstance = this;

        mContext = _context;
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(mContext);
        mAuth = FirebaseAuth.getInstance();
        createAuth();
    }

    public void onLogin(String _email, String _password){
        mAuth.createUserWithEmailAndPassword(_email, _password).addOnCompleteListener((Activity) mContext, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                if (!task.isSuccessful()) {

                }
            }
        });
    }

    public void onStart(){
        mAuth.addAuthStateListener(mAuthListener);
    }

    public void onStop(){
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void createAuth(){
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
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
