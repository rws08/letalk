package kr.co.allright.letalk;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import kr.co.allright.letalk.manager.Firebase;
import kr.co.allright.letalk.manager.UserManager;
import kr.co.allright.letalk.views.SignupDialog;

import static kr.co.allright.letalk.manager.UserManager.PREFS_FILE;
import static kr.co.allright.letalk.manager.UserManager.PREFS_LOGIN_ID;

public class MainActivity extends AppCompatActivity {
    private static MainActivity mInstance = null;
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;

    public SignupDialog mSignupDl;

    private Firebase mFirebase;
    private UserManager mUserManager;

    public static MainActivity getInstance(){
        return mInstance;
    }

    MainActivity(){
        super();
        mInstance = this;
    }

    public void showSignup(){
        mSignupDl = new SignupDialog(this);
        mSignupDl.show();
    }

    public void closeSignup(){
        if (mSignupDl != null){
            mSignupDl.hide();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createManager();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        checkNewApp();
    }

    private void createManager(){
        mFirebase = new Firebase(this);
        mUserManager = new UserManager(this);
    }

    private void checkNewApp(){
        final SharedPreferences preferences = this.getSharedPreferences(PREFS_FILE, 0);
        final String loginId = preferences.getString(PREFS_LOGIN_ID, null);

        if (loginId == null) {
            showSignup();
        }else{
            mFirebase.onLogin(null);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mFirebase.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mFirebase.onStop();
    }

    /**
     * Show the contacts in the ListView.
     */
    private void showContacts() {
        if (checkSelfPermission(android.Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.GET_ACCOUNTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
        } else {
            // 권한 동의 완료
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                showContacts();
            } else {
                Toast.makeText(this, "사용권한이 없어 실행할 수 없습니다", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
