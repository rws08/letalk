package kr.co.allright.letalk.manager;

import android.content.Context;

import com.google.firebase.database.DatabaseReference;

/**
 * Created by MacPro on 2017. 1. 4..
 */

public class RoomManager {
    private static RoomManager mInstance = null;
    private Context mContext;

    private DatabaseReference mDBRoomsRef;

    public static RoomManager getInstance(){
        return mInstance;
    }

    public RoomManager(Context _context) {
        mInstance = this;

        mContext = _context;
    }
}
