package kr.co.allright.letalk.fragment;

import android.content.Context;
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
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;

import kr.co.allright.letalk.R;
import kr.co.allright.letalk.Supporter;
import kr.co.allright.letalk.data.Room;
import kr.co.allright.letalk.data.User;
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

                updateListView();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
    }

    private void onResumeData(){
        if (mValueELRooms == null){
            createData();
        }

        DatabaseReference roomsRef = RoomManager.getInstance().getRoomsRef();
//        roomsRef.addValueEventListener(mValueELRooms);

        onAllRooms();
    }

    private void onPauseData(){
        if (mValueELRooms == null) return;

        DatabaseReference roomsRef = RoomManager.getInstance().getRoomsRef();
//        roomsRef.removeEventListener(mValueELRooms);
    }

    private void onAllRooms(){
        DatabaseReference roomsRef = RoomManager.getInstance().getRoomsRef();
        Query query = roomsRef.limitToFirst(100);

        query.addListenerForSingleValueEvent(mValueELRooms);
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
            Room room = mArrayList.get(position);

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

                            if (user.sex.equals(SEX_MAN)){
                                holder.mTvName.setTextColor(Supporter.getColor(getContext(), R.color.man_text));
                            }else {
                                holder.mTvName.setTextColor(Supporter.getColor(getContext(), R.color.woman_text));
                            }
                            holder.mTvName.setText(user.name + "(" + user.age + ")");
//                            holder.mTvRange
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
            private TextView mTvRange;
            private TextView mTvMessage;

            public ViewHolder(View itemView) {
                super(itemView);
                mImgProfile = (ImageView) itemView.findViewById(R.id.img_profile);
                mTvName = (TextView) itemView.findViewById(R.id.tv_name);
                mTvRange = (TextView) itemView.findViewById(R.id.tv_range);
                mTvMessage = (TextView) itemView.findViewById(R.id.tv_message);
            }
        }
    }
}
