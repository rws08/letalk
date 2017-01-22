package kr.co.allright.letalk.views;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import kr.co.allright.letalk.MainActivity;
import kr.co.allright.letalk.R;
import kr.co.allright.letalk.Supporter;
import kr.co.allright.letalk.data.Chat;
import kr.co.allright.letalk.data.Message;
import kr.co.allright.letalk.data.User;
import kr.co.allright.letalk.manager.ChatManager;

import static kr.co.allright.letalk.data.User.SEX_MAN;

/**
 * Created by MacPro on 2017. 1. 4..
 */

public class SelectRoomDialog extends Dialog {
    private User mUser;

    private TextView mTvUserName;
    private TextView mTvMessage;
    private Button mBtnStart;

    public SelectRoomDialog(Context context) {
        super(context);
        setCanceledOnTouchOutside(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_selectroom);

        mTvUserName = (TextView) findViewById(R.id.tv_username);
        mTvMessage = (TextView) findViewById(R.id.tv_message);
        mBtnStart = (Button) findViewById(R.id.btn_start);

        setUI();
    }

    public void setUser(User _user){
        mUser = _user;

        if (mUser.sex.equals(SEX_MAN)){
            mTvUserName.setTextColor(Supporter.getColor(getContext(), R.color.man_text));
        }else {
            mTvUserName.setTextColor(Supporter.getColor(getContext(), R.color.woman_text));
        }
        mTvUserName.setText(mUser.name + "(" + mUser.age + ")");
    }

    @Override
    public void show() {
        super.show();
    }

    private void setUI(){
        mBtnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onStartChat();
            }
        });
    }

    private void onStartChat(){
        ChatManager.getInstance().makeNewChat(mUser, new ChatManager.ChatManagerListener() {
            @Override
            public void onChatData(Chat _chat) {
                MainActivity.getInstance().closeSelectRoom();
            }

            @Override
            public void onMessageData(Message _message) {

            }
        });
    }
}
