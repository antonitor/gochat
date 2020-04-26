package com.antonitor.gotchat.ui.roomlist;

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
import com.antonitor.gotchat.sync.FirebaseGoChatData;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RoomsFragmentOwn#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RoomsFragmentOwn extends Fragment implements MainRecyclerViewAdapter.RoomOnClickListener {

    private ViewModel viewModel;
    private FragmentOwnRoomsBinding mDataBinding;
    private MainRecyclerViewAdapter recyclerViewAdapter;

    public RoomsFragmentOwn() {}

    public static RoomsFragmentOwn newInstance() {
        RoomsFragmentOwn fragment = new RoomsFragmentOwn();
        return fragment;
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
        mDataBinding.addFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseGoChatData.getInstance().newChatRoom("123", "WEEP", "topic", null);
            }
        });
    }

    private void setUpRecyclerView(){
        recyclerViewAdapter = new MainRecyclerViewAdapter(
                FirebaseGoChatData.getInstance().getOwnChatRoomListOptions(),
                this);
        mDataBinding.ownRecyclerview.setAdapter(recyclerViewAdapter);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        //manager.setReverseLayout(true);
        //manager.setStackFromEnd(true);
        mDataBinding.ownRecyclerview.setLayoutManager(manager);
        recyclerViewAdapter.startListening();
    }

    @Override
    public void onRoomClicked() {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (recyclerViewAdapter != null)
            recyclerViewAdapter.stopListening();
    }


}
