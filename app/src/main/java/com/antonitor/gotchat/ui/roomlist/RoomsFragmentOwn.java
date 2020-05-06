package com.antonitor.gotchat.ui.roomlist;

import android.content.Intent;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.antonitor.gotchat.R;
import com.antonitor.gotchat.databinding.FragmentOwnRoomsBinding;
import com.antonitor.gotchat.model.ChatRoom;
import com.antonitor.gotchat.sync.FirebaseDatabaseRepository;
import com.antonitor.gotchat.ui.chatroom.ChatActivity;
import com.antonitor.gotchat.ui.newroom.AddNewRoomActivity;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RoomsFragmentOwn#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RoomsFragmentOwn extends Fragment implements RoomListAdapterOwn.OnRoomClickListener {

    private FragmentOwnRoomsBinding mDataBinding;
    private RoomListAdapterOwn recyclerViewAdapter;
    private MainViewModel viewModel;

    public RoomsFragmentOwn() {}

    static RoomsFragmentOwn newInstance() {
        return new RoomsFragmentOwn();
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
            newChatRoomIntent.putExtra(getString(R.string.extra_userowner), viewModel.getCustomUser());
            getActivity().startActivity(newChatRoomIntent);
        });
    }

    private void setUpRecyclerView(){
        recyclerViewAdapter = new RoomListAdapterOwn(
                FirebaseDatabaseRepository.getInstance().getOwnChatRoomListOptions(),
                this,
                viewModel.getCustomUser());
        mDataBinding.ownRecyclerview.setAdapter(recyclerViewAdapter);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        mDataBinding.ownRecyclerview.setLayoutManager(manager);
        recyclerViewAdapter.startListening();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (recyclerViewAdapter != null)
            recyclerViewAdapter.stopListening();
    }

    @Override
    public void onRoomClicked(ChatRoom room) {
        Intent chatIntent = new Intent(getActivity(), ChatActivity.class);
        chatIntent.putExtra(getString(R.string.key_chatroom), room);
        getActivity().startActivity(chatIntent);
    }
}
