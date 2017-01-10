package kr.co.allright.letalk.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Objects;

import kr.co.allright.letalk.MainActivity;
import kr.co.allright.letalk.R;
import kr.co.allright.letalk.Supporter;
import kr.co.allright.letalk.data.Room;
import kr.co.allright.letalk.data.User;
import kr.co.allright.letalk.etc.Utils;
import kr.co.allright.letalk.manager.GPSTracker;
import kr.co.allright.letalk.manager.GeoManager;
import kr.co.allright.letalk.manager.RoomManager;
import kr.co.allright.letalk.manager.UserManager;

import static kr.co.allright.letalk.data.User.SEX_MAN;

/**
 * Created by MacPro on 2017. 1. 4..
 */

public class AllRoomsFragment extends Fragment {
    private static AllRoomsFragment mInstance = null;

    private Button mBtnAll;
    private Button mBtn100;
    private RecyclerView mRecRooms;
    private RecyclerView.Adapter mAdapterRecRooms;
    private RecyclerView.LayoutManager mManagerRecRooms;

    private ValueEventListener mValueELRooms;

    private ArrayList<Room> mArrayRoom;

    public static AllRoomsFragment getInstance(){
        if (mInstance == null){
            new AllRoomsFragment();
        }
        return mInstance;
    }

    public AllRoomsFragment() {
        mInstance = this;

        mArrayRoom = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_allrooms, container, false);

        mBtnAll = (Button) view.findViewById(R.id.btn_all);
        mBtn100 = (Button) view.findViewById(R.id.btn_100);
        mRecRooms = (RecyclerView) view.findViewById(R.id.rec_rooms);

        mManagerRecRooms = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mAdapterRecRooms = new RoomsAdpater(mArrayRoom, getContext());

        setUI();

        return view;
    }

    private void setUI(){
        mRecRooms.setLayoutManager(mManagerRecRooms);
        mRecRooms.setAdapter(mAdapterRecRooms);

        mBtnAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.getInstance().showLoading();
                onAllRooms();
            }
        });

        mBtn100.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.getInstance().showLoading();
                GeoManager.getInstance().searchRange(100);
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

    private void updateListView(){
        MainActivity.getInstance().closeLoading();
        mAdapterRecRooms.notifyDataSetChanged();
    }

    private void createData(){
        mValueELRooms = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mArrayRoom.clear();
                Iterable<DataSnapshot> iterable = dataSnapshot.getChildren();
                Iterator<DataSnapshot> iter = iterable.iterator();
                while(iter.hasNext()) {
                    DataSnapshot dataSnap = iter.next();
                    Room room = dataSnap.getValue(Room.class);

                    if (room.visible) {
                        mArrayRoom.add(room);
                    }
                }

                Collections.sort(mArrayRoom, mComparator);
                updateListView();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
    }

    private final static Comparator<Room> mComparator = new Comparator<Room>() {
        @Override
        public int compare(Room o1, Room o2) {
            long comp = o2.createtime - o1.createtime;
            return (int) comp;
        }
    };

    private void onResumeData(){
        if (mValueELRooms == null){
            createData();
        }

        DatabaseReference roomsRef = RoomManager.getInstance().getRoomsRef();
        if (roomsRef != null){
//        roomsRef.addValueEventListener(mValueELRooms);
            MainActivity.getInstance().showLoading();
            onAllRooms();
        }
    }

    private void onPauseData(){
        if (mValueELRooms == null) return;

        DatabaseReference roomsRef = RoomManager.getInstance().getRoomsRef();
//        roomsRef.removeEventListener(mValueELRooms);
    }

    private void onAllRooms(){
        UserManager.getInstance().actionUser();

        DatabaseReference roomsRef = RoomManager.getInstance().getRoomsRef();
        Query query = roomsRef.limitToLast(100);

        query.addListenerForSingleValueEvent(mValueELRooms);
    }

    public void onSearchRooms(){
        mArrayRoom.clear();
        ArrayList<String> arrRoomids = GeoManager.getInstance().mArrRoomids;
        for (int i = 0; i < arrRoomids.size(); i++){
            String roomid = arrRoomids.get(i);
            DatabaseReference roomRef = RoomManager.getInstance().getRoomRefWithId(roomid);
            roomRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Room room = dataSnapshot.getValue(Room.class);
                    mArrayRoom.add(room);
                    onEndSearchRooms();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private void onEndSearchRooms(){
        ArrayList<String> arrRoomids = GeoManager.getInstance().mArrRoomids;
        if (arrRoomids.size() == mArrayRoom.size()){
            for (Room room:mArrayRoom) {
                if (room.visible == false){
                    mArrayRoom.remove(room);
                }
            }
            Collections.sort(mArrayRoom, mComparator);
            updateListView();
        }
    }

    class RoomsAdpater extends RecyclerView.Adapter<RoomsAdpater.ViewHolder>{
        private Context mContext;
        private ArrayList<Room> mArrayList;

        public RoomsAdpater(ArrayList<Room> _ArrayList, Context _Context) {
            mArrayList = _ArrayList;
            mContext = _Context;
        }

        @Override
        public int getItemCount() {
            return mArrayList.size();
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            final Room room = mArrayList.get(position);

            holder.mTvMessage.setText(room.title);

            Iterator<String> iter = room.userIds.keySet().iterator();
            while(iter.hasNext()) {
                String key = iter.next();
                boolean value = room.userIds.get(key);
                Log.d("fureun", "key : " + key + ", value : " + value);
                if (value){
                    UserManager.getInstance().getUserRefWithId(key).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            User user = dataSnapshot.getValue(User.class);
                            String strTime = Utils.getDurationTime(room.createtime, UserManager.getInstance().mLastActionTime);

                            Location myloc = GPSTracker.getInstance().getLocation();
                            Location userloc = new Location("");
                            userloc.setLatitude(user.lat);
                            userloc.setLongitude(user.lon);
                            float distanceInMeters = myloc.distanceTo(userloc);
                            String sign = "m";
                            if (distanceInMeters >= 1000){
                                sign = "km";
                                distanceInMeters /= 1000;
                            }

                            if (user.sex.equals(SEX_MAN)){
                                holder.mTvName.setTextColor(Supporter.getColor(getContext(), R.color.man_text));
                            }else {
                                holder.mTvName.setTextColor(Supporter.getColor(getContext(), R.color.woman_text));
                            }
                            holder.mTvName.setText(user.name + "(" + user.age + ")");

                            holder.mTvTime.setText(strTime);

                            holder.mTvRange.setText(String.format("%.01f %s", distanceInMeters, sign));
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_room, parent, false);
            ViewHolder holder = new ViewHolder(v);
            return holder;
        }

        class ViewHolder extends RecyclerView.ViewHolder{
            private ImageView mImgProfile;
            private TextView mTvName;
            private TextView mTvTime;
            private TextView mTvRange;
            private TextView mTvMessage;

            public ViewHolder(View itemView) {
                super(itemView);
                mImgProfile = (ImageView) itemView.findViewById(R.id.img_profile);
                mTvName = (TextView) itemView.findViewById(R.id.tv_name);
                mTvTime = (TextView) itemView.findViewById(R.id.tv_time);
                mTvRange = (TextView) itemView.findViewById(R.id.tv_range);
                mTvMessage = (TextView) itemView.findViewById(R.id.tv_message);
            }
        }
    }
}
