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
import com.antonitor.gotchat.databinding.FragmentTrendigListBinding;
import com.antonitor.gotchat.model.ChatRoom;
import com.antonitor.gotchat.ui.chatroom.ChatActivity;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RoomsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RoomsFragment extends Fragment implements RoomListAdapter.OnRoomClickListener {

    private static final String LOG_TAG = "ROOMS_FRAGMENT";
    private FragmentTrendigListBinding mDataBinding;
    private RoomListAdapter recyclerViewAdapter;
    private MainViewModel viewModel;
    private Observer<List<ChatRoom>> roomsObserver = new Observer<List<ChatRoom>>() {
        @Override
        public void onChanged(List<ChatRoom> chatRooms) {
            recyclerViewAdapter.swapChatRooms(chatRooms);
        }
    };

    public RoomsFragment() {
        // Required empty public constructor
    }


    static RoomsFragment newInstance() {
        return new RoomsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mDataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_trendig_list, container, false);
        viewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);

        setUpRecyclerView();

        return mDataBinding.getRoot();
    }

    private void setUpRecyclerView() {
        recyclerViewAdapter = new RoomListAdapter(
                getActivity(),
                this,
                viewModel.getCustomUser());
        mDataBinding.trendingRecyclerview.setAdapter(recyclerViewAdapter);
        viewModel.getChatRooms().observe(getActivity(), roomsObserver);

        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        mDataBinding.trendingRecyclerview.setLayoutManager(manager);
        viewModel.getLogin().observe(getActivity(), login -> {
            if (!login) {
                Log.d(LOG_TAG, "LOGIN:FALSE ------ VM:STOPLISTENING -- VM:REMOVEOBSERVER");
                viewModel.getChatRooms().removeObserver(roomsObserver);
                viewModel.stopListeningRooms();
            }
        });
    }


    @Override
    public void onDestroyView() {
        Log.d(LOG_TAG, "FRAGMENT DESTROYED ------ VM:STOPLISTENING -- VM:REMOVEOBSERVER");
        super.onDestroyView();
        viewModel.getChatRooms().removeObserver(roomsObserver);
        viewModel.stopListeningRooms();
    }


    @Override
    public void onRoomClicked(ChatRoom room) {
        Intent chatIntent = new Intent(getActivity(), ChatActivity.class);
        chatIntent.putExtra(getString(R.string.key_chatroom), room);
        getActivity().startActivity(chatIntent);
    }
}
