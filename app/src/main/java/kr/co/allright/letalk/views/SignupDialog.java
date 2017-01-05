package kr.co.allright.letalk.views;

import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
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

public class SignupDialog extends Dialog {
    private EditText mEtEmail;
    private EditText mEtName;
    private EditText mEtAge;
    private RadioGroup mRgroupSex;
    private EditText mEtRoomTitle;
    private Button mBtnSignup;

    public SignupDialog(Context context) {
        super(context);
        setCanceledOnTouchOutside(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_signup);

        mEtEmail = (EditText) findViewById(R.id.et_email);
        mEtName = (EditText) findViewById(R.id.et_name);
        mEtAge = (EditText) findViewById(R.id.et_age);
        mRgroupSex = (RadioGroup) findViewById(R.id.rbtngroup);
        mEtRoomTitle = (EditText) findViewById(R.id.et_myroom_title);
        mBtnSignup = (Button) findViewById(R.id.btn_signup);

        setUI();
    }

    @Override
    public void show() {
        super.show();

        mEtEmail.setText("");
        mEtName.setText("");
        mEtAge.setText("");
        mEtRoomTitle.setText("");

        checkBtnSignup();
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

        mEtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (isValidEmail(mEtEmail.getText().toString())){
                    mEtEmail.setBackgroundTintList(ColorStateList.valueOf(Supporter.getColor(getContext(), android.R.color.holo_green_dark)));
                }else{
                    mEtEmail.setBackgroundTintList(null);
                }

                checkBtnSignup();
            }
        });
        mEtName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0){
                    mEtName.setBackgroundTintList(ColorStateList.valueOf(Supporter.getColor(getContext(), android.R.color.holo_green_dark)));
                }else{
                    mEtName.setBackgroundTintList(null);
                }

                checkBtnSignup();
            }
        });
        mEtAge.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0){
                    mEtAge.setBackgroundTintList(ColorStateList.valueOf(Supporter.getColor(getContext(), android.R.color.holo_green_dark)));
                }else{
                    mEtAge.setBackgroundTintList(null);
                }

                checkBtnSignup();
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

                checkBtnSignup();
            }
        });
    }

    private void checkBtnSignup(){
        boolean enable = true;

        if (enable == true && !isValidEmail(mEtEmail.getText().toString())){
            enable = false;
        }
        if (enable == true && mEtName.getText().length() == 0){
            enable = false;
        }
        if (enable == true && mEtAge.getText().length() == 0){
            enable = false;
        }
        if (enable == true && mEtRoomTitle.getText().length() == 0){
            enable = false;
        }

        mBtnSignup.setEnabled(enable);
    }

    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }
}
