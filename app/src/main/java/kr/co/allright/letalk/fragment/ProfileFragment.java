package kr.co.allright.letalk.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.util.HashMap;

import kr.co.allright.letalk.R;
import kr.co.allright.letalk.data.User;
import kr.co.allright.letalk.manager.UserManager;

/**
 * Created by MacPro on 2017. 1. 4..
 */

public class ProfileFragment extends Fragment {
    private static ProfileFragment mInstance = null;

    private EditText mEtName;
    private EditText mEtAge;
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
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        mEtName = (EditText) view.findViewById(R.id.et_name);
        mEtAge = (EditText) view.findViewById(R.id.et_age);
        mBtnAccept = (Button) view.findViewById(R.id.btn_accept);

        setUI();

        updateUI();
        return view;
    }

    private void setUI(){
        mBtnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, Object> map = new HashMap();
                map.put("name", mEtName.getText().toString());
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
