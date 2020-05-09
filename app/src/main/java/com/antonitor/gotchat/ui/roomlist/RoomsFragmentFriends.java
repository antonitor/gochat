package com.antonitor.gotchat.ui.roomlist;

import android.content.Intent;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.antonitor.gotchat.R;
import com.antonitor.gotchat.databinding.FragmentOwnRoomsBinding;
import com.antonitor.gotchat.model.ChatRoom;
import com.antonitor.gotchat.sync.FirebaseAuthRepository;
import com.antonitor.gotchat.ui.chatroom.ChatActivity;
import com.antonitor.gotchat.ui.newroom.AddNewRoomActivity;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RoomsFragmentFriends#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RoomsFragmentFriends extends Fragment implements RoomListAdapter.OnRoomClickListener {

    private static final String LOG_TAG = "FRIEND_ROOMS_FRAG";
    private FragmentOwnRoomsBinding mDataBinding;
    private RoomListAdapter recyclerViewAdapter;
    private MainViewModel viewModel;

    public RoomsFragmentFriends() {}

    static RoomsFragmentFriends newInstance() {
        return new RoomsFragmentFriends();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mDataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_own_rooms, container, false);
        viewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);
        setUpAddFab();

        setUpRecyclerView();

        return mDataBinding.getRoot();
    }

    private void setUpAddFab(){
        mDataBinding.addFab.setOnClickListener(view -> {
            Intent newChatRoomIntent = new Intent(getActivity(), AddNewRoomActivity.class);
            //TODO: Get Custom User From ViewModel for MVVM pattern
            newChatRoomIntent.putExtra(getString(R.string.extra_userowner), FirebaseAuthRepository.getInstance().getCustomUser());
            getActivity().startActivity(newChatRoomIntent);
        });
    }

    private void setUpRecyclerView(){
        recyclerViewAdapter = new RoomListAdapter(
                getActivity(),
                RoomListAdapter.roomTypes.FRIENDS,
                this,
                //TODO: Get Custom User From ViewModel for MVVM pattern
                FirebaseAuthRepository.getInstance().getCustomUser());
        mDataBinding.ownRecyclerview.setAdapter(recyclerViewAdapter);
        viewModel.getFriendChatRooms().observe(getActivity(), new Observer<List<ChatRoom>>() {
            @Override
            public void onChanged(List<ChatRoom> chatRooms) {
                recyclerViewAdapter.swapChatRooms(chatRooms);
            }
        });

        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        mDataBinding.ownRecyclerview.setLayoutManager(manager);
        viewModel.getLogin().observe(getActivity(), login -> {
            if (!login) {
                Log.d(LOG_TAG, "LOGIN:FALSE ------ VM:STOPLISTENING -- VM:REMOVEOBSERVER");
                viewModel.stopListeningFriendRooms();
            }
        });
    }


    @Override
    public void onRoomClicked(ChatRoom room) {
        Intent chatIntent = new Intent(getActivity(), ChatActivity.class);
        chatIntent.putExtra(getString(R.string.key_chatroom), room);
        getActivity().startActivity(chatIntent);
    }
}
