package kr.co.allright.letalk.manager;

import android.content.Context;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import kr.co.allright.letalk.data.Chat;
import kr.co.allright.letalk.data.User;

/**
 * Created by MacPro on 2017. 1. 4..
 */

public class ChatManager {
    private static ChatManager mInstance = null;
    private Context mContext;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mDBChatsRef;

    public static ChatManager getInstance(){
        return mInstance;
    }

    public ChatManager(Context _context) {
        mInstance = this;

        mContext = _context;

        mDatabase = FirebaseDatabase.getInstance();
        mDBChatsRef = mDatabase.getReference("chats");
    }

    public DatabaseReference getRoomRefWithId(String _keyid){
        DatabaseReference roomRef = mDBChatsRef.child(_keyid);
        return roomRef;
    }

    public void makeNewChat(User _otherUser){
        DatabaseReference chatsRef = mDBChatsRef.push();
        String keyid = chatsRef.getKey();
        User user = UserManager.getInstance().mUser;
        User otherUser = _otherUser;

        // 내정보 업데이트
        Map<String, Boolean> chatsMap = user.myChatIds;
        chatsMap.put(keyid, true);

        HashMap<String, Object> map = new HashMap();
        map.put("myChatIds", chatsMap);
        UserManager.getInstance().udpateUser(map);

        // 상대 정보 업데이트
        Map<String, Boolean> otherChatsMap = otherUser.myChatIds;
        otherChatsMap.put(keyid, true);

        HashMap<String, Object> othermap = new HashMap();
        othermap.put("myChatIds", otherChatsMap);
        UserManager.getInstance().udpateUser(otherUser, othermap);

        Chat chat = new Chat(keyid);
        chat.visible = true;
        chat.userIds.put(user.keyid, true);
        chat.userIds.put(otherUser.keyid, true);

        chatsRef.setValue(chat);
        chatsRef.child("createtime").setValue(ServerValue.TIMESTAMP);
    }

    public void removeChat(Chat _chat){
        DatabaseReference chatsRef = mDBChatsRef.child(_chat.keyid);
        final String keyid = _chat.keyid;

        Iterator<String> iter = _chat.userIds.keySet().iterator();
        while(iter.hasNext()) {
            String userId = iter.next();
            UserManager.getInstance().getUserData(userId, new UserManager.UserManagerListener(){
                @Override
                public void onUserData(User _user) {
                    User otherUser = _user;
                    // 상대 정보 업데이트
                    Map<String, Boolean> otherChatsMap = otherUser.myChatIds;
                    otherChatsMap.put(keyid, false);

                    HashMap<String, Object> othermap = new HashMap();
                    othermap.put("myChatIds", otherChatsMap);
                    UserManager.getInstance().udpateUser(otherUser, othermap);
                }
            });
        }

        _chat.visible = false;

        chatsRef.setValue(_chat);
        chatsRef.child("createtime").setValue(ServerValue.TIMESTAMP);
    }

    public DatabaseReference getChatsRef(){
        return mDBChatsRef;
    }

    public DatabaseReference getChatRefWithId(String _keyid){
        DatabaseReference chatRef = mDBChatsRef.child(_keyid);
        return chatRef;
    }

    public void onResume(){
    }

    public void onPause(){
    }
}
