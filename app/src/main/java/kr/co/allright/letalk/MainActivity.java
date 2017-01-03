package kr.co.allright.letalk;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import kr.co.allright.letalk.manager.Firebase;
import kr.co.allright.letalk.manager.UserManager;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;

    private Firebase mFirebase;
    private UserManager mUserManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createManager();

        showContacts();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private void createManager(){
        mFirebase = new Firebase(this);
        mUserManager = new UserManager(this);
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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Show the contacts in the ListView.
     */
    private void showContacts() {
        if (checkSelfPermission(android.Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.GET_ACCOUNTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
        } else {
            mUserManager.login();
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
