package kr.co.allright.letalk.manager;

import android.content.Context;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by MacPro on 2017. 1. 4..
 */

public class RoomManager {
    private static RoomManager mInstance = null;
    private Context mContext;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mDBRoomsRef;

    public static RoomManager getInstance(){
        return mInstance;
    }

    public RoomManager(Context _context) {
        mInstance = this;

        mContext = _context;

        mDatabase = FirebaseDatabase.getInstance();
        mDBRoomsRef = mDatabase.getReference("rooms");
    }

    public void makeNewMyRoom(){

    }
}
