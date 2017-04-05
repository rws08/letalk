package kr.co.allright.letalk.manager;

import android.content.Context;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;

import kr.co.allright.letalk.MainActivity;
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

        if (MainActivity.SERVER_TYPE == MainActivity.SERVER_TYPE_DEV){
            mDBRoomsRef = mDatabase.getReference("DEV").child("rooms");
        }else{
            mDBRoomsRef = mDatabase.getReference("rooms");
        }
    }

    public DatabaseReference getRoomRefWithId(String _keyid){
        DatabaseReference roomRef = mDBRoomsRef.child(_keyid);
        return roomRef;
    }

    public void makeNewMyRoom(){
        DatabaseReference myRoomRef = mDBRoomsRef.push();
        String keyid = myRoomRef.getKey();
        User user = UserManager.getInstance().mUser;

        HashMap<String, Object> map = new HashMap();
        map.put("myroomId", keyid);
        UserManager.getInstance().udpateUser(map);

        Room room = new Room(keyid, user.myroomTitle);
        room.visible = true;
        room.userIds.put(user.keyid, true);

        myRoomRef.setValue(room);
        myRoomRef.child("createtime").setValue(ServerValue.TIMESTAMP);
    }

    public DatabaseReference getRoomsRef(){
        return mDBRoomsRef;
    }

    public DatabaseReference getMyRoomRef(){
        User user = UserManager.getInstance().mUser;
        DatabaseReference myRoomRef = null;
        if (user.myroomId != null){
            myRoomRef = getRoomRefWithId(user.myroomId);
        }
        return myRoomRef;
    }

    public void udpateMyRoom(HashMap<String, Object> _map){
        getMyRoomRef().updateChildren(_map);
        getMyRoomRef().child("createtime").setValue(ServerValue.TIMESTAMP);
    }

    public void onResume(){
    }

    public void onPause(){
    }
}
