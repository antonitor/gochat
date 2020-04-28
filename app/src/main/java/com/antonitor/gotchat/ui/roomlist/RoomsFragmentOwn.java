package com.antonitor.gotchat.ui.roomlist;

import android.content.Intent;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.antonitor.gotchat.R;
import com.antonitor.gotchat.databinding.FragmentOwnRoomsBinding;
import com.antonitor.gotchat.model.ChatRoom;
import com.antonitor.gotchat.sync.FirebaseDatabaseRepository;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RoomsFragmentOwn#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RoomsFragmentOwn extends Fragment implements RecyclerViewAdapterOwn.OnCloseClickListener{

    private ViewModel viewModel;
    private FragmentOwnRoomsBinding mDataBinding;
    private RecyclerViewAdapterOwn recyclerViewAdapter;

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
            getActivity().startActivity(newChatRoomIntent);
        });
    }

    private void setUpRecyclerView(){
        recyclerViewAdapter = new RecyclerViewAdapterOwn(
                FirebaseDatabaseRepository.getInstance().getOwnChatRoomListOptions(), this);
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
    public void onCloseClicked(ChatRoom room) {
        FirebaseDatabaseRepository.getInstance().removeChatRoom(room.getId());
    }
}
