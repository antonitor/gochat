package com.antonitor.gotchat.ui.roomlist;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.antonitor.gotchat.R;
import com.antonitor.gotchat.databinding.FragmentFollowingListBinding;
import com.antonitor.gotchat.model.ChatRoom;
import com.antonitor.gotchat.sync.FirebaseDatabaseRepository;
import com.antonitor.gotchat.ui.chatroom.ChatActivity;

import java.util.Objects;


public class RoomsFragmentFollowing extends Fragment implements RoomListAdapter.OnLongClickListener, RoomListAdapter.OnRoomClickListener {

    private ViewModel viewModel;
    private FragmentFollowingListBinding mDataBinding;
    private RoomListAdapter recyclerViewAdapter;

    public RoomsFragmentFollowing() {
        // Required empty public constructor
    }

    static RoomsFragmentFollowing newInstance() {
        return new RoomsFragmentFollowing();
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
                FirebaseDatabaseRepository.getInstance().getFollowedChatRoomListOptions(),  this, this);
        mDataBinding.followingRecyclerview.setAdapter(recyclerViewAdapter);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        //manager.setReverseLayout(true);
        //manager.setStackFromEnd(true);
        mDataBinding.followingRecyclerview.setLayoutManager(manager);
        recyclerViewAdapter.startListening();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (recyclerViewAdapter != null)
            recyclerViewAdapter.stopListening();
    }

    @Override
    public void onLongClick(ChatRoom room) {




        // FirebaseDatabaseRepository.getInstance().unFollowChat(room);
    }

    @Override
    public void onRoomClicked(ChatRoom room) {
        Intent chatIntent = new Intent(getActivity(), ChatActivity.class);
        chatIntent.putExtra(getString(R.string.key_chatroom), (Parcelable) room);
        getActivity().startActivity(chatIntent);
    }
}
