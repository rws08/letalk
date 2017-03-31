package kr.co.allright.letalk.message;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import kr.co.allright.letalk.manager.UserManager;

import static android.content.ContentValues.TAG;

/**
 * Created by MacPro on 2017. 1. 3..
 */

public class FbInstanceIDService extends FirebaseInstanceIdService {
    public FbInstanceIDService() {
        super();
    }

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);
        UserManager.getInstance().updateUserTokenId(refreshedToken);
    }
}
