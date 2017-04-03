package kr.co.allright.letalk.views;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;

import kr.co.allright.letalk.MainActivity;
import kr.co.allright.letalk.R;
import kr.co.allright.letalk.Supporter;
import kr.co.allright.letalk.data.Chat;
import kr.co.allright.letalk.data.Message;
import kr.co.allright.letalk.data.User;
import kr.co.allright.letalk.etc.Utils;
import kr.co.allright.letalk.fragment.AllChatsFragment;
import kr.co.allright.letalk.manager.ChatManager;
import kr.co.allright.letalk.manager.GPSTracker;
import kr.co.allright.letalk.manager.UserManager;

import static kr.co.allright.letalk.data.User.SEX_MAN;

/**
 * Created by MacPro on 2017. 1. 4..
 */

public class ChatDialog extends Dialog {
    private Chat mChat;

    private ImageButton mBtnClose;
    private TextView mTvName;
    private TextView mTvRange;
    private ImageButton mBtnDelete;

    private RecyclerView mRecMessages;
    private RecyclerView.Adapter mAdapterRecMessages;
    private RecyclerView.LayoutManager mManagerRecMessages;

    private ImageButton mBtnPlus;
    private EditText mEtMessage;
    private Button mBtnSend;

    private DatabaseReference mDBMessagesRef;
    private ValueEventListener mMessagesValueListener;
    private ChildEventListener mMessagesEventListener;

    private ArrayList<Message> mArrayMessage;

    public ChatDialog(Context context) {
        super(context);
        setCanceledOnTouchOutside(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_chat);
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        mArrayMessage = new ArrayList<>();

        mBtnClose = (ImageButton) findViewById(R.id.btn_close);
        mTvName = (TextView) findViewById(R.id.tv_name);
        mTvRange = (TextView) findViewById(R.id.tv_range);
        mBtnDelete = (ImageButton) findViewById(R.id.btn_delete);

        mRecMessages = (RecyclerView) findViewById(R.id.rec_messages);
        mManagerRecMessages = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        mAdapterRecMessages = new MessageAdpater(mArrayMessage, getContext());

        mBtnPlus = (ImageButton) findViewById(R.id.btn_add);
        mEtMessage = (EditText) findViewById(R.id.et_message);
        mBtnSend = (Button) findViewById(R.id.btn_send);

        setUI();
    }

    @Override
    public void show() {
        super.show();

        UserManager.getInstance().actionUser();
        mEtMessage.setText("");
    }

    @Override
    public void dismiss() {
        UserManager.getInstance().actionUser();
        mDBMessagesRef.removeEventListener(mMessagesEventListener);
        mDBMessagesRef.removeEventListener(mMessagesValueListener);
        super.dismiss();
    }

    public void setChat(Chat _chat){
        mChat = _chat;
        mArrayMessage.clear();

        updateOtherUser();

        setMessage();
    }

    private void setMessage(){
        mMessagesValueListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> iter = dataSnapshot.getChildren().iterator();
                while(iter.hasNext()) {
                    DataSnapshot messageSnap = iter.next();
                    Message message = messageSnap.getValue(Message.class);
                    mArrayMessage.add(message);
                }

                mAdapterRecMessages.notifyDataSetChanged();
                mRecMessages.scrollToPosition(mArrayMessage.size() - 1);

                mDBMessagesRef.addChildEventListener(mMessagesEventListener);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        mMessagesEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                boolean find = false;
                for (int i = mArrayMessage.size() - 1; i >= 0; i--){
                    Message message = mArrayMessage.get(i);
                    if (message.keyid.equals(dataSnapshot.getKey())){
                        find = true;
                        break;
                    }
                }

                if (find == false){
                    Message message = dataSnapshot.getValue(Message.class);
                    mArrayMessage.add(message);
                    UserManager.getInstance().actionUser();
                    mAdapterRecMessages.notifyDataSetChanged();
                    mRecMessages.scrollToPosition(mArrayMessage.size() - 1);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                for (int i = mArrayMessage.size() - 1; i >= 0; i--){
                    Message message = mArrayMessage.get(i);
                    if (message.keyid.equals(dataSnapshot.getKey())){
                        Message newmessage = dataSnapshot.getValue(Message.class);
                        message.updateData(newmessage);
                        mAdapterRecMessages.notifyItemChanged(i);
                        break;
                    }
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        mDBMessagesRef = ChatManager.getInstance().getMessagesRef(mChat);
        mDBMessagesRef.addListenerForSingleValueEvent(mMessagesValueListener);
    }

    private void updateOtherUser(){
        // 상대 정보 업데이트
        Iterator<String> iter = mChat.userIds.keySet().iterator();
        while(iter.hasNext()) {
            String userId = iter.next();
            if (!userId.equals(UserManager.getInstance().mUser.keyid)) {
                UserManager.getInstance().getUserData(userId, new UserManager.UserManagerListener() {
                    @Override
                    public void onUserData(User _user) {
                        User otherUser = _user;

                        Location myloc = GPSTracker.getInstance().getLocation();
                        Location userloc = new Location("");
                        userloc.setLatitude(otherUser.lat);
                        userloc.setLongitude(otherUser.lon);
                        float distanceInMeters = myloc.distanceTo(userloc);
                        String sign = "m";
                        if (distanceInMeters >= 1000){
                            sign = "km";
                            distanceInMeters /= 1000;
                        }else {
                            sign = "km";
                            distanceInMeters = 1;
                        }

                        if (otherUser.sex.equals(SEX_MAN)){
                            mTvName.setTextColor(Supporter.getColor(getContext(), R.color.man_text));
                        }else {
                            mTvName.setTextColor(Supporter.getColor(getContext(), R.color.woman_text));
                        }
                        mTvName.setText(otherUser.name + "(" + otherUser.age + ")");

                        if (distanceInMeters < 13177) {
                            mTvRange.setText(String.format("%.01f %s", distanceInMeters, sign));
                        }else if(distanceInMeters <= 1){
                            mTvRange.setText(String.format("1 %s", sign));
                        }else{
                            mTvRange.setText(String.format("?? %s", sign));
                        }
                    }
                });
            }
        }
    }

    private void setUI(){
        mBtnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.getInstance().closeChat();
            }
        });

        mBtnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
                alertDialog.setMessage("방을 삭제하시겠습니까?");
                alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int which) {
                        ChatManager.getInstance().removeChat(mChat);
                        AllChatsFragment.getInstance().onResumeData();
                        MainActivity.getInstance().closeChat();
                    }
                });
                alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                alertDialog.show();
            }
        });

        mBtnSend.setEnabled(false);
        mBtnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Message message = new Message();
                message.contenttype = Message.CONTENTTYPE_TEXT;
                message.contents = mEtMessage.getText().toString();
                UserManager.getInstance().actionUser();
                ChatManager.getInstance().makeNewMessage(mChat, message, new ChatManager.ChatManagerListener() {
                    @Override
                    public void onChatData(Chat _chat) {

                    }

                    @Override
                    public void onMessageData(Message _message) {
                        mEtMessage.setText("");
                    }
                });
            }
        });

        mEtMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0){
                    mBtnSend.setEnabled(true);
                }else{
                    mBtnSend.setEnabled(false);
                }
            }
        });

        mRecMessages.setLayoutManager(mManagerRecMessages);
        mRecMessages.setAdapter(mAdapterRecMessages);
    }

    class MessageAdpater extends RecyclerView.Adapter<ChatDialog.MessageAdpater.ViewHolder>{
        private static final int TYPE_SELF = 10;
        private static final int TYPE_OTHER = 20;
        private Context mContext;
        private ArrayList<Message> mArrayList;

        public MessageAdpater(ArrayList<Message> _ArrayList, Context _Context) {
            mArrayList = _ArrayList;
            mContext = _Context;
        }

        @Override
        public int getItemCount() {
            return mArrayList.size();
        }

        @Override
        public int getItemViewType(int position) {
            final Message message = mArrayList.get(position);
            if (message.userid.equals(UserManager.mUser.keyid)){
                return TYPE_SELF;
            }else{
                return TYPE_OTHER;
            }
        }

        @Override
        public void onBindViewHolder(final ChatDialog.MessageAdpater.ViewHolder holder, int position) {
            final Message message = mArrayList.get(position);

            String key = message.userid;
            UserManager.getInstance().getUserRefWithId(key).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    holder.setUI(user, message);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        @Override
        public ChatDialog.MessageAdpater.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == TYPE_SELF) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_message, parent, false);
                ChatDialog.MessageAdpater.ViewHolder holder = new ChatDialog.MessageAdpater.ViewHolder(v);
                return holder;
            }else{
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_message_other, parent, false);
                ChatDialog.MessageAdpater.ViewHolder holder = new ChatDialog.MessageAdpater.ViewHolder(v);
                return holder;
            }
        }

        class ViewHolder extends RecyclerView.ViewHolder{
            private User mUser;
            private Message mMessage;

            private CardView mLayMain;
            private TextView mTvMessage;
            private TextView mTvTime;

            public ViewHolder(View itemView) {
                super(itemView);
                mLayMain = (CardView) itemView.findViewById(R.id.cv_main);
                mTvMessage = (TextView) itemView.findViewById(R.id.tv_message);
                mTvTime = (TextView) itemView.findViewById(R.id.tv_time);
            }

            public void setUI(User _user, Message _message){
                mUser = _user;
                mMessage = _message;

                String strTime = Utils.getDurationTime(mMessage.createtime, UserManager.getInstance().mLastActionTime);

                mTvMessage.setText(mMessage.contents);

                mTvTime.setText(strTime);
            }
        }
    }
}
