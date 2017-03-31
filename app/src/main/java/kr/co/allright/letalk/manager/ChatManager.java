package kr.co.allright.letalk.manager;

import android.content.Context;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import kr.co.allright.letalk.data.Chat;
import kr.co.allright.letalk.data.JsonObjectRequest;
import kr.co.allright.letalk.data.Message;
import kr.co.allright.letalk.data.User;

/**
 * Created by MacPro on 2017. 1. 4..
 */

public class ChatManager {
    public interface ChatManagerListener {
        void onChatData(Chat _chat);
        void onMessageData(Message _message);
    }

    private static ChatManager mInstance = null;
    private Context mContext;
    private ArrayList<ChatManagerListener> mArrListeners;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mDBChatsRef;
    private DatabaseReference mDBMessagesRef;

    private ValueEventListener mMessagesValueListener;
    private ChildEventListener mMessagesEventListener;

    public static ChatManager getInstance(){
        return mInstance;
    }

    public ChatManager(Context _context) {
        mInstance = this;

        mContext = _context;
        mArrListeners = new ArrayList<>();

        mDatabase = FirebaseDatabase.getInstance();
        mDBChatsRef = mDatabase.getReference("chats");
        mDBMessagesRef = mDatabase.getReference("messages");
    }

    public DatabaseReference getMessagesRef(Chat _chat){
        DatabaseReference messagesRef = mDBMessagesRef.child(_chat.keyid);
        return messagesRef;
    }

    public void makeNewMessage(Chat _chat, Message _message, @NotNull final ChatManagerListener _listener){
        mArrListeners.add(_listener);

        DatabaseReference messagesRef = mDBMessagesRef.child(_chat.keyid).push();
        String keyid = messagesRef.getKey();
        User user = UserManager.getInstance().mUser;

        _message.keyid = keyid;
        _message.userid = user.keyid;
        _message.chatid = _chat.keyid;
        _message.unreadcount = _chat.userIds.size() - 1;

        messagesRef.setValue(_message);
        messagesRef.child("createtime").setValue(ServerValue.TIMESTAMP);

        _listener.onMessageData(_message);
        mArrListeners.remove(_listener);

        Iterator<String> iter = _chat.userIds.keySet().iterator();
        while(iter.hasNext()) {
            String userId = iter.next();
            if (!userId.equals(UserManager.getInstance().mUser.keyid)) {
                UserManager.getInstance().getUserData(userId, new UserManager.UserManagerListener() {
                    @Override
                    public void onUserData(User _user) {
                        User otherUser = _user;
                        requestNewMessagePush(otherUser);
                    }
                });
            }
        }
    }

    private static void requestNewMessagePush(User _user){
        String url = "https://fcm.googleapis.com/fcm/send";
        JSONObject jsonObject = new JSONObject();
        JSONObject jsonNoti = new JSONObject();
        JsonObjectRequest request = null;
        try {
            jsonNoti.put("title", "new Message");
            jsonNoti.put("body", "New Message");

            jsonObject.put("to", _user.tokenId);
            jsonObject.put("notification", jsonNoti);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        request = new JsonObjectRequest(url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        ServerBManager.getInstance().addToRequestQueue(request);
    }

    private static void responseStoreStatus(JSONObject jsonObj) throws JSONException {

    }

    public void makeNewChat(User _otherUser, @NotNull final ChatManagerListener _listener){
        mArrListeners.add(_listener);

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
        chat.userCount = chat.userIds.size();

        chatsRef.setValue(chat);
        chatsRef.child("createtime").setValue(ServerValue.TIMESTAMP);

        _listener.onChatData(chat);
        mArrListeners.remove(_listener);
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

        UserManager.getInstance().mUser.myChatIds.put(keyid, false);
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
