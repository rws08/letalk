package kr.co.allright.letalk.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import java.util.HashMap;

import kr.co.allright.letalk.R;
import kr.co.allright.letalk.data.User;
import kr.co.allright.letalk.manager.UserManager;

import static kr.co.allright.letalk.data.User.SEX_MAN;
import static kr.co.allright.letalk.data.User.SEX_WOMAN;

/**
 * Created by MacPro on 2017. 1. 4..
 */

public class ProfileFragment extends Fragment {
    private static ProfileFragment mInstance = null;

    private EditText mEtName;
    private EditText mEtAge;
    private RadioGroup mRgroupSex;
    private Button mBtnAccept;

    public static ProfileFragment getInstance(){
        if (mInstance == null){
            new ProfileFragment();
        }
        return mInstance;
    }

    public ProfileFragment() {
        mInstance = this;
    }

    public void updateUI(){
        User user = UserManager.getInstance().mUser;

        if (mEtName != null) {
            mEtName.setText(user.name);
            mEtAge.setText("" + user.age);
            if(user.sex.equals(SEX_MAN)){
                mRgroupSex.check(R.id.rbtn_man);
            }else{
                mRgroupSex.check(R.id.rbtn_woman);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        mEtName = (EditText) view.findViewById(R.id.et_name);
        mEtAge = (EditText) view.findViewById(R.id.et_age);
        mRgroupSex = (RadioGroup) view.findViewById(R.id.rbtngroup);
        mBtnAccept = (Button) view.findViewById(R.id.btn_accept);

        setUI();

        updateUI();
        return view;
    }

    private void setUI(){
        mBtnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String sex = mRgroupSex.getCheckedRadioButtonId() == R.id.rbtn_man ? SEX_MAN:SEX_WOMAN;

                HashMap<String, Object> map = new HashMap();
                map.put("name", mEtName.getText().toString());
                map.put("sex", sex);
                map.put("age", Integer.parseInt(mEtAge.getText().toString()));

                UserManager.getInstance().udpateUser(map);
            }
        });
    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);

        if (menuVisible) {
            updateUI();
        }
    }
}
