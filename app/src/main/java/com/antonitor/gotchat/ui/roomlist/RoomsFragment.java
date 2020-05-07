package com.antonitor.gotchat.ui.roomlist;

import android.content.Intent;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.antonitor.gotchat.R;
import com.antonitor.gotchat.databinding.FragmentTrendigListBinding;
import com.antonitor.gotchat.model.ChatRoom;
import com.antonitor.gotchat.sync.FirebaseDatabaseRepository;
import com.antonitor.gotchat.ui.chatroom.ChatActivity;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RoomsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RoomsFragment extends Fragment implements RoomListAdapter.OnRoomClickListener{

    private FragmentTrendigListBinding mDataBinding;
    private RoomListAdapter recyclerViewAdapter;
    private MainViewModel viewModel;

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

    private void setUpRecyclerView(){
        recyclerViewAdapter = new RoomListAdapter(
                FirebaseDatabaseRepository.getInstance().getTrendingChatRoomListOptions(),
                this,
                viewModel.getCustomUser());
        mDataBinding.trendingRecyclerview.setAdapter(recyclerViewAdapter);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        mDataBinding.trendingRecyclerview.setLayoutManager(manager);
        recyclerViewAdapter.startListening();
        viewModel.getLogin().observe(getActivity(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean login) {
                if (!login) recyclerViewAdapter.stopListening();
            }
        });
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