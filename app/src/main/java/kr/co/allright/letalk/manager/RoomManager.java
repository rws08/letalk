package kr.co.allright.letalk.manager;

import android.content.Context;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;

import kr.co.allright.letalk.data.Room;
import kr.co.allright.letalk.data.User;

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
        DatabaseReference myRoomRef = mDBRoomsRef.push();
        String keyid = myRoomRef.getKey();
        User user = UserManager.getInstance().mUser;

        HashMap<String, Object> map = new HashMap();
        map.put("myroomId", user.myroomId);
        UserManager.getInstance().udpateUser(map);

        Room room = new Room(keyid, user.myroomTitle);
        room.userIds.put(user.keyid, true);

        myRoomRef.setValue(room);
        myRoomRef.child("createtime").setValue(ServerValue.TIMESTAMP);
    }

    public void deleteMyRoom(){
        User user = UserManager.getInstance().mUser;
        if (user.myroomId.length() > 0){
            DatabaseReference myRoomRef = mDBRoomsRef.child(user.myroomId);
        }
    }

    public void onResume(){
//        mDBMyRef.onDisconnect()
    }

    public void onPause(){
//        if (mAuthListener != null) {
//            mAuth.removeAuthStateListener(mAuthListener);
//        }
    }
}
