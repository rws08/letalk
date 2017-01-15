package kr.co.allright.letalk;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import kr.co.allright.letalk.data.User;
import kr.co.allright.letalk.fragment.AllChatsFragment;
import kr.co.allright.letalk.fragment.AllRoomsFragment;
import kr.co.allright.letalk.fragment.ProfileFragment;
import kr.co.allright.letalk.manager.ChatManager;
import kr.co.allright.letalk.manager.Firebase;
import kr.co.allright.letalk.manager.GPSTracker;
import kr.co.allright.letalk.manager.GeoManager;
import kr.co.allright.letalk.manager.RoomManager;
import kr.co.allright.letalk.manager.UserManager;
import kr.co.allright.letalk.views.SelectRoomDialog;
import kr.co.allright.letalk.views.SignupDialog;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static kr.co.allright.letalk.manager.UserManager.PREFS_FILE;
import static kr.co.allright.letalk.manager.UserManager.PREFS_LOGIN_ID;

public class MainActivity extends AppCompatActivity {
    private static MainActivity mInstance = null;
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 200;

    public SignupDialog mSignupDl;
    public SelectRoomDialog mSelectRoomDl;
    public ProgressDialog mDialog;

    private Firebase mFirebase;
    private GPSTracker mGps;
    private GeoManager mGeoManager;
    private UserManager mUserManager;
    private RoomManager mRoomManager;
    private ChatManager mChatManager;

    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private PagerAdapter mAdapter;

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

    public void showLoading(){
        if (mDialog != null){
            return;
        }
        mDialog = ProgressDialog.show(this, "", "잠시만 기다리세요.", true);
    }

    public void closeLoading(){
        if (mDialog == null){
            return;
        }
        mDialog.dismiss();
        mDialog = null;
    }

    public void showSelectRoom(User _user){
        if (_user == null) return;

        mSelectRoomDl = new SelectRoomDialog(this);
        mSelectRoomDl.show();
        mSelectRoomDl.setUser(_user);
    }

    public void closeSelectRoom(){
        if (mSelectRoomDl != null){
            mSelectRoomDl.hide();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createManager();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mTabLayout = (TabLayout) findViewById(R.id.tabs);
        mViewPager = (ViewPager) findViewById(R.id.pager);

        setUI();

        checkNewApp();
    }

    private void createManager(){
        mGps = new GPSTracker(this);
        mFirebase = new Firebase(this);
        mGeoManager = new GeoManager(this);
        mUserManager = new UserManager(this);
        mRoomManager = new RoomManager(this);
        mChatManager = new ChatManager(this);
    }

    private void setUI(){
        mTabLayout.setupWithViewPager(mViewPager);

        mAdapter = new PagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                ((NestedScrollView) findViewById(R.id.nestedScrollView)).scrollTo(0, 0);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void checkNewApp(){
        final SharedPreferences preferences = this.getSharedPreferences(PREFS_FILE, 0);
        final String loginId = preferences.getString(PREFS_LOGIN_ID, null);

        if (loginId == null) {
            if (!mGps.canGetLocation()){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                }else{
                    showSignup();
                }
            }else {
                showSignup();
            }
        }else{
            if (!mGps.canGetLocation()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mUserManager.onResume();
        mRoomManager.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mUserManager.onPause();
        mRoomManager.onPause();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class PagerAdapter extends FragmentStatePagerAdapter {
        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            switch (position) {
                case 0:
                    fragment = AllRoomsFragment.getInstance();
                    break;
                case 1:
                    fragment = AllChatsFragment.getInstance();
                    break;
                case 2:{
                    fragment = ProfileFragment.getInstance();
                }break;
            }
            return fragment;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "전체 방";
                case 1:
                    return "참여 방";
                case 2:
                    return "기타";
                default:
                    return super.getPageTitle(position);
            }
        }

        @Override
        public int getCount() {
            return 3;
        }
    }

    /**
     * Show the contacts in the ListView.
     */
    private void showContacts() {
//        if (checkSelfPermission(android.Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
//            requestPermissions(new String[]{android.Manifest.permission.GET_ACCOUNTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
//        } else {
//            // 권한 동의 완료
//        }
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
        }else if(requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mGps.getLocation();
            } else {
//                Toast.makeText(this, "사용권한이 없어 실행할 수 없습니다", Toast.LENGTH_SHORT).show();
            }

            final SharedPreferences preferences = this.getSharedPreferences(PREFS_FILE, 0);
            final String loginId = preferences.getString(PREFS_LOGIN_ID, null);

            if (loginId == null) {
                showSignup();
            }
        }
    }
}
