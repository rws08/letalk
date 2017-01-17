package kr.co.allright.letalk.views;

import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import kr.co.allright.letalk.R;
import kr.co.allright.letalk.Supporter;
import kr.co.allright.letalk.manager.UserManager;

import static kr.co.allright.letalk.data.User.SEX_MAN;
import static kr.co.allright.letalk.data.User.SEX_WOMAN;

/**
 * Created by MacPro on 2017. 1. 4..
 */

public class ChatDialog extends Dialog {
    private EditText mEtEmail;
    private EditText mEtName;
    private EditText mEtAge;
    private RadioGroup mRgroupSex;
    private EditText mEtRoomTitle;
    private Button mBtnSignup;

    public ChatDialog(Context context) {
        super(context);
        setCanceledOnTouchOutside(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_signup);

        mEtEmail = (EditText) findViewById(R.id.et_email);

        setUI();
    }

    @Override
    public void show() {
        super.show();

        mEtEmail.setText("");
        mEtName.setText("");
        mEtAge.setText("");
        mEtRoomTitle.setText("");

    }

    private void setUI(){
        mBtnSignup.setEnabled(false);
        mBtnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = mEtEmail.getText().toString();
                String name = mEtName.getText().toString();
                String age = mEtAge.getText().toString();
                String sex = mRgroupSex.getCheckedRadioButtonId() == R.id.rbtn_man ? SEX_MAN:SEX_WOMAN;
                String roomtitle = mEtRoomTitle.getText().toString();

                UserManager.getInstance().onSignUp(email, name, age, sex, roomtitle);
            }
        });

        mEtRoomTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0){
                    mEtRoomTitle.setBackgroundTintList(ColorStateList.valueOf(Supporter.getColor(getContext(), android.R.color.holo_green_dark)));
                }else{
                    mEtRoomTitle.setBackgroundTintList(null);
                }

            }
        });
    }
}
