package kr.co.allright.letalk.fragment;

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
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;

import kr.co.allright.letalk.MainActivity;
import kr.co.allright.letalk.R;
import kr.co.allright.letalk.Supporter;
import kr.co.allright.letalk.data.Chat;
import kr.co.allright.letalk.data.User;
import kr.co.allright.letalk.etc.Utils;
import kr.co.allright.letalk.manager.ChatManager;
import kr.co.allright.letalk.manager.GPSTracker;
import kr.co.allright.letalk.manager.UserManager;

import static kr.co.allright.letalk.data.User.SEX_MAN;

/**
 * Created by MacPro on 2017. 1. 4..
 */

public class AllChatsFragment extends Fragment {
    private static AllChatsFragment mInstance = null;

    private RecyclerView mRecChats;
    private RecyclerView.Adapter mAdapterRecChats;
    private RecyclerView.LayoutManager mManagerRecChats;

    private ValueEventListener mValueELChats;

    private ArrayList<Chat> mArrayChat;

    public static AllChatsFragment getInstance(){
        if (mInstance == null){
            new AllChatsFragment();
        }
        return mInstance;
    }

    public AllChatsFragment() {
        mInstance = this;

        mArrayChat = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_allchats, container, false);

        mRecChats = (RecyclerView) view.findViewById(R.id.rec_chats);

        mManagerRecChats = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        mAdapterRecChats = new ChatsAdpater(mArrayChat, getContext());

        setUI();

        return view;
    }

    private void setUI(){
        mRecChats.setLayoutManager(mManagerRecChats);
        mRecChats.setAdapter(mAdapterRecChats);
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
        mAdapterRecChats.notifyDataSetChanged();
    }

    private void createData(){
        mValueELChats = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
    }

    private final static Comparator<Chat> mComparator = new Comparator<Chat>() {
        @Override
        public int compare(Chat o1, Chat o2) {
            long comp = o2.createtime - o1.createtime;
            return (int) comp;
        }
    };

    private void onResumeData(){
        if (mValueELChats == null){
            createData();
        }

        DatabaseReference chatsRef = ChatManager.getInstance().getChatsRef();
        if (chatsRef != null){
            MainActivity.getInstance().showLoading();
            onAllRooms();
        }
    }

    private void onPauseData(){
        if (mValueELChats == null) return;

        DatabaseReference chatsRef = ChatManager.getInstance().getChatsRef();
    }

    private void onAllRooms(){
        UserManager.getInstance().actionUser();

        mArrayChat.clear();
        Map<String, Boolean> chatIds = UserManager.getInstance().mUser.myChatIds;

        if (chatIds.size() == 0){
            onEndAllRooms();
            return;
        }

        Iterator<String> iter = chatIds.keySet().iterator();
        while(iter.hasNext()) {
            String chatId = iter.next();
            DatabaseReference chatRef = ChatManager.getInstance().getChatRefWithId(chatId);
            chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Chat chat = dataSnapshot.getValue(Chat.class);
                    mArrayChat.add(chat);
                    onEndAllRooms();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private void onEndAllRooms(){
        Map<String, Boolean> chatIds = UserManager.getInstance().mUser.myChatIds;
        if (chatIds.size() == mArrayChat.size()){
            Iterator<Chat> iter = mArrayChat.iterator();
            while (iter.hasNext()){
                Chat chat = iter.next();
                if (chat.visible == false){
                    iter.remove();
                }
            }
            Collections.sort(mArrayChat, mComparator);
            updateListView();
        }
    }

    class ChatsAdpater extends RecyclerView.Adapter<ChatsAdpater.ViewHolder>{
        private Context mContext;
        private ArrayList<Chat> mArrayList;

        public ChatsAdpater(ArrayList<Chat> _ArrayList, Context _Context) {
            mArrayList = _ArrayList;
            mContext = _Context;
        }

        @Override
        public int getItemCount() {
            return mArrayList.size();
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            final Chat chat = mArrayList.get(position);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //// TODO: 2017. 1. 15. 채팅방 들어가기
//                    MainActivity.getInstance().showLoading();
//                    ChatManager.getInstance().removeChat(chat);
//                    onAllRooms();

                    MainActivity.getInstance().showChat(chat);
                }
            });

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
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_room, parent, false);
            ViewHolder holder = new ViewHolder(v);
            return holder;
        }

        class ViewHolder extends RecyclerView.ViewHolder{
            private User mUser;
            private Chat mChat;
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

            public void setUI(User _user, Chat _chat){
                mUser = _user;
                mChat = _chat;

                String strTime = Utils.getDurationTime(mChat.createtime, UserManager.getInstance().mLastActionTime);

                Location myloc = GPSTracker.getInstance().getLocation();
                Location userloc = new Location("");
                userloc.setLatitude(mUser.lat);
                userloc.setLongitude(mUser.lon);
                float distanceInMeters = myloc.distanceTo(userloc);
                String sign = "m";
                if (distanceInMeters >= 1000){
                    sign = "km";
                    distanceInMeters /= 1000;
                }else {
                    sign = "km";
                    distanceInMeters = 1;
                }

                if (mUser.sex.equals(SEX_MAN)){
                    mTvName.setTextColor(Supporter.getColor(getContext(), R.color.man_text));
                }else {
                    mTvName.setTextColor(Supporter.getColor(getContext(), R.color.woman_text));
                }
                mTvName.setText(mUser.name + "(" + mUser.age + ")");

                mTvTime.setText(strTime);

                if (distanceInMeters < 13177) {
                    mTvRange.setText(String.format("%.01f %s", distanceInMeters, sign));
                }else if(distanceInMeters <= 1){
                    mTvRange.setText(String.format("1 %s", sign));
                }else{
                    mTvRange.setText(String.format("?? %s", sign));
                }

                mTvMessage.setText(mChat.lastMessage);
            }
        }
    }
}
