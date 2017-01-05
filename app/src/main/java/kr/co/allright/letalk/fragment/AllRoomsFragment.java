package kr.co.allright.letalk.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import kr.co.allright.letalk.R;

/**
 * Created by MacPro on 2017. 1. 4..
 */

public class AllRoomsFragment extends Fragment {
    private static AllRoomsFragment mInstance = null;

    private Button mBtnAll;

    public static AllRoomsFragment getInstance(){
        if (mInstance == null){
            new AllRoomsFragment();
        }
        return mInstance;
    }

    public AllRoomsFragment() {
        mInstance = this;
    }

    public void updateUI(){
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_allrooms, container, false);

        mBtnAll = (Button) view.findViewById(R.id.btn_all);

        setUI();

        updateUI();
        return view;
    }

    private void setUI(){

    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);

        if (menuVisible) {
            updateUI();
        }
    }
}
