package kr.co.allright.letalk.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import kr.co.allright.letalk.R;
import kr.co.allright.letalk.data.Room;
import kr.co.allright.letalk.data.User;
import kr.co.allright.letalk.manager.RoomManager;
import kr.co.allright.letalk.manager.UserManager;

import static kr.co.allright.letalk.data.User.SEX_MAN;
import static kr.co.allright.letalk.data.User.SEX_WOMAN;
import static kr.co.allright.letalk.manager.UserManager.mUser;

/**
 * Created by MacPro on 2017. 1. 4..
 */

public class ProfileFragment extends Fragment {
    private static ProfileFragment mInstance = null;

    private EditText mEtName;
    private EditText mEtAge;
    private RadioGroup mRgroupSex;
    private EditText mEtRoomTitle;
    private Switch mSwMyroomVisble;
    private Button mBtnAccept;

    private ValueEventListener mValueELUser;
    private ChildEventListener mChildELUser;
    private ValueEventListener mValueELRoom;

    public static ProfileFragment getInstance(){
        if (mInstance == null){
            new ProfileFragment();
        }
        return mInstance;
    }

    public ProfileFragment() {
        mInstance = this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        mEtName = (EditText) view.findViewById(R.id.et_name);
        mEtAge = (EditText) view.findViewById(R.id.et_age);
        mRgroupSex = (RadioGroup) view.findViewById(R.id.rbtngroup);
        mEtRoomTitle = (EditText) view.findViewById(R.id.et_myroom_title);
        mSwMyroomVisble = (Switch) view.findViewById(R.id.sw_myroom);
        mBtnAccept = (Button) view.findViewById(R.id.btn_accept);

        setUI();

        return view;
    }

    private void setUI(){
        mBtnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String sex = mRgroupSex.getCheckedRadioButtonId() == R.id.rbtn_man ? SEX_MAN:SEX_WOMAN;

                HashMap<String, Object> mapUser = new HashMap();
                mapUser.put("name", mEtName.getText().toString());
                mapUser.put("sex", sex);
                mapUser.put("age", Integer.parseInt(mEtAge.getText().toString()));
                mapUser.put("myroomTitle", mEtRoomTitle.getText().toString());

                UserManager.getInstance().udpateUser(mapUser);

                HashMap<String, Object> mapRoom = new HashMap();
                mapRoom.put("title", mEtRoomTitle.getText().toString());
                mapRoom.put("visible", mSwMyroomVisble.isChecked());

                RoomManager.getInstance().udpateMyRoom(mapRoom);
            }
        });
    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);

        if (menuVisible) {
            onResumeData();
        }else{
            onPauseData();
        }
    }

    private void createData(){
        mValueELUser = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mUser = dataSnapshot.getValue(User.class);

                mEtName.setText(mUser.name);
                mEtAge.setText("" + mUser.age);
                if(mUser.sex.equals(SEX_MAN)){
                    mRgroupSex.check(R.id.rbtn_man);
                }else{
                    mRgroupSex.check(R.id.rbtn_woman);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        mChildELUser = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.getKey().equals("logintime")){

                }else{
                    Toast.makeText(getContext(), getString(R.string.lay_frag_applied), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getContext(), getString(R.string.lay_frag_faild), Toast.LENGTH_SHORT).show();
            }
        };

        mValueELRoom = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Room room = dataSnapshot.getValue(Room.class);
                mEtRoomTitle.setText(room.title);
                mSwMyroomVisble.setChecked(room.visible);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
    }

    private void onResumeData(){
        if (mValueELUser == null){
            createData();
        }

        DatabaseReference myRef = UserManager.getInstance().getMyRef();
        myRef.addValueEventListener(mValueELUser);
        myRef.addChildEventListener(mChildELUser);

        DatabaseReference roomRef = RoomManager.getInstance().getMyRoomRef();
        roomRef.addValueEventListener(mValueELRoom);
    }

    private void onPauseData(){
        if (mValueELUser == null) return;

        DatabaseReference myRef = UserManager.getInstance().getMyRef();
        myRef.removeEventListener(mValueELUser);
        myRef.removeEventListener(mChildELUser);

        DatabaseReference roomRef = RoomManager.getInstance().getMyRoomRef();
        roomRef.removeEventListener(mValueELRoom);
    }
}
