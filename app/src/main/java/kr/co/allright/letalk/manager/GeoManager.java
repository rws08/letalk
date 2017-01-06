package kr.co.allright.letalk.manager;

import android.content.Context;

import com.firebase.geofire.GeoFire;
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
}
