package kr.co.allright.letalk.manager;

import android.content.Context;
import android.util.Log;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by MacPro on 2017. 1. 6..
 */

public class GeoManager {
    private static GeoManager mInstance = null;
    private Context mContext;

    private FirebaseDatabase mDatabase;
    private GeoFire geoFire;

    public static GeoManager getInstance(){
        return mInstance;
    }

    public GeoManager(Context _context) {
        mInstance = this;

        mContext = _context;

        mDatabase = FirebaseDatabase.getInstance();
        geoFire = new GeoFire(mDatabase.getReference("geofile"));
    }

    public GeoFire getGeoFire(){
        return geoFire;
    }

    public void searchRange(double _km){
        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(GPSTracker.getInstance().getLatitude(), GPSTracker.getInstance().getLongitude()), _km);
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                Log.d("search", String.format("Key %s entered the search area at [%f,%f]", key, location.latitude, location.longitude));
            }

            @Override
            public void onKeyExited(String key) {
                Log.d("search", String.format("Key %s is no longer in the search area", key));
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
                Log.d("search", String.format("Key %s moved within the search area to [%f,%f]", key, location.latitude, location.longitude));
            }

            @Override
            public void onGeoQueryReady() {
                Log.d("search", "All initial data has been loaded and events have been fired!");
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {
                Log.d("search", "There was an error with this query: " + error);
            }
        });
    }
}
