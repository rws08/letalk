package kr.co.allright.letalk.views;

import android.app.Dialog;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;

import kr.co.allright.letalk.MainActivity;
import kr.co.allright.letalk.R;
import kr.co.allright.letalk.Supporter;
import kr.co.allright.letalk.data.Chat;
import kr.co.allright.letalk.data.User;
import kr.co.allright.letalk.etc.Utils;
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

    private RecyclerView mRecMessages;
    private RecyclerView.Adapter mAdapterRecMessages;
    private RecyclerView.LayoutManager mManagerRecMessages;

    private ImageButton mBtnPlus;
    private EditText mEtMessage;
    private Button mBtnSend;

    public ChatDialog(Context context) {
        super(context);
        setCanceledOnTouchOutside(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_chat);
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        mBtnClose = (ImageButton) findViewById(R.id.btn_close);
        mTvName = (TextView) findViewById(R.id.tv_name);
        mTvRange = (TextView) findViewById(R.id.tv_range);

        mRecMessages = (RecyclerView) findViewById(R.id.rec_messages);

        mBtnPlus = (ImageButton) findViewById(R.id.btn_add);
        mEtMessage = (EditText) findViewById(R.id.et_message);
        mBtnSend = (Button) findViewById(R.id.btn_send);

        setUI();
    }

    @Override
    public void show() {
        super.show();

        mEtMessage.setText("");
    }

    public void setChat(Chat _chat){
        mChat = _chat;

        updateOtherUser();

        setMessage();
    }

    private void setMessage(){

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

        mBtnSend.setEnabled(false);
        mBtnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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
    }

    class MessageAdpater extends RecyclerView.Adapter<ChatDialog.MessageAdpater.ViewHolder>{
        private Context mContext;
        private ArrayList<Chat> mArrayList;

        public MessageAdpater(ArrayList<Chat> _ArrayList, Context _Context) {
            mArrayList = _ArrayList;
            mContext = _Context;
        }

        @Override
        public int getItemCount() {
            return mArrayList.size();
        }

        @Override
        public void onBindViewHolder(final ChatDialog.MessageAdpater.ViewHolder holder, int position) {
            final Chat chat = mArrayList.get(position);

            Iterator<String> iter = chat.userIds.keySet().iterator();
            while(iter.hasNext()) {
                String key = iter.next();
                boolean value = chat.userIds.get(key);
                Log.d("fureun", "key : " + key + ", value : " + value);
                if (value){
                    UserManager.getInstance().getUserRefWithId(key).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            User user = dataSnapshot.getValue(User.class);
                            holder.setUI(user, chat);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        }

        @Override
        public ChatDialog.MessageAdpater.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_message, parent, false);
            ChatDialog.MessageAdpater.ViewHolder holder = new ChatDialog.MessageAdpater.ViewHolder(v);
            return holder;
        }

        class ViewHolder extends RecyclerView.ViewHolder{
            private User mUser;
            private Chat mChat;

            private TextView mTvMessage;
            private TextView mTvTime;

            public ViewHolder(View itemView) {
                super(itemView);
                mTvMessage = (TextView) itemView.findViewById(R.id.tv_message);
                mTvTime = (TextView) itemView.findViewById(R.id.tv_time);
            }

            public void setUI(User _user, Chat _chat){
                mUser = _user;
                mChat = _chat;

                String strTime = Utils.getDurationTime(mChat.createtime, UserManager.getInstance().mLastActionTime);

                mTvMessage.setText(mChat.lastMessage);

                mTvTime.setText(strTime);
            }
        }
    }
}
