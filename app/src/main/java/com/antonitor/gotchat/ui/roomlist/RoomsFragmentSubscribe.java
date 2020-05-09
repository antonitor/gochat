package com.antonitor.gotchat.ui.roomlist;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;

import com.antonitor.gotchat.R;
import com.antonitor.gotchat.databinding.FragmentFollowingListBinding;
import com.antonitor.gotchat.model.ChatRoom;
import com.antonitor.gotchat.sync.FirebaseAuthRepository;
import com.antonitor.gotchat.ui.chatroom.ChatActivity;

import java.util.List;
import java.util.Objects;

public class RoomsFragmentSubscribe extends Fragment implements  RoomListAdapter.OnRoomClickListener {

    private static final String LOG_TAG = "SUBS_ROOMS_FRAGMENT";
    private FragmentFollowingListBinding mDataBinding;
    private RoomListAdapter recyclerViewAdapter;
    private MainViewModel viewModel;

    public RoomsFragmentSubscribe() {
        // Required empty public constructor
    }

    static RoomsFragmentSubscribe newInstance() {
        return new RoomsFragmentSubscribe();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mDataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_following_list, container, false);
        viewModel = new ViewModelProvider(Objects.requireNonNull(getActivity())).get(MainViewModel.class);

        setUpRecyclerView();

        return mDataBinding.getRoot();
    }

    private void setUpRecyclerView(){
        recyclerViewAdapter = new RoomListAdapter(
                getActivity(),
                RoomListAdapter.roomTypes.SUBSCRIBED,
                this,
                //TODO: Get Custom User From ViewModel for MVVM pattern
                FirebaseAuthRepository.getInstance().getCustomUser());
        mDataBinding.followingRecyclerview.setAdapter(recyclerViewAdapter);
        viewModel.getSubscribedChatRooms().observe(getActivity(), new Observer<List<ChatRoom>>() {
            @Override
            public void onChanged(List<ChatRoom> chatRooms) {
                recyclerViewAdapter.swapChatRooms(chatRooms);
            }
        });

        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        mDataBinding.followingRecyclerview.setLayoutManager(manager);
        //TODO: Create loginObserver member
        viewModel.getLogin().observe(getActivity(), login -> {
            if (!login) {
                viewModel.stopListeningSubscribedRooms();
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
