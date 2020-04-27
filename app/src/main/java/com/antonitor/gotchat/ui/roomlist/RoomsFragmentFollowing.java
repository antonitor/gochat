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
import com.antonitor.gotchat.databinding.FragmentFollowingListBinding;
import com.antonitor.gotchat.sync.FBRDatabaseData;


public class RoomsFragmentFollowing extends Fragment implements MainRecyclerViewAdapter.OnUnfollowClickListener {

    private ViewModel viewModel;
    private FragmentFollowingListBinding mDataBinding;
    private MainRecyclerViewAdapter recyclerViewAdapter;

    public RoomsFragmentFollowing() {
        // Required empty public constructor
    }

    public static RoomsFragmentFollowing newInstance() {
        RoomsFragmentFollowing fragment = new RoomsFragmentFollowing();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mDataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_following_list, container, false);
        viewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);

        setUpRecyclerView();

        return mDataBinding.getRoot();
    }

    private void setUpRecyclerView(){
        MainRecyclerViewAdapter recyclerViewAdapter = new MainRecyclerViewAdapter(
                FBRDatabaseData.getInstance().getFollowedChatRoomListOptions());
        mDataBinding.followingRecyclerview.setAdapter(recyclerViewAdapter);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        //manager.setReverseLayout(true);
        //manager.setStackFromEnd(true);
        mDataBinding.followingRecyclerview.setLayoutManager(manager);
        recyclerViewAdapter.startListening();
    }


    @Override
    public void onUnfollowClicked() {

    }
}
