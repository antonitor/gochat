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
import com.antonitor.gotchat.databinding.FragmentTrendigListBinding;
import com.antonitor.gotchat.sync.FirebaseGoChatData;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RoomsFragmentTrending#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RoomsFragmentTrending extends Fragment implements MainRecyclerViewAdapter.RoomOnClickListener{

    private ViewModel viewModel;
    private FragmentTrendigListBinding mDataBinding;
    private MainRecyclerViewAdapter recyclerViewAdapter;

    public RoomsFragmentTrending() {
        // Required empty public constructor
    }


    public static RoomsFragmentTrending newInstance() {
        return new RoomsFragmentTrending();
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
        recyclerViewAdapter = new MainRecyclerViewAdapter(
                FirebaseGoChatData.getInstance().getTrendingChatRoomListOptions(),
                this);
        mDataBinding.trendingRecyclerview.setAdapter(recyclerViewAdapter);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        //manager.setReverseLayout(true);
        //manager.setStackFromEnd(true);
        mDataBinding.trendingRecyclerview.setLayoutManager(manager);
        recyclerViewAdapter.startListening();
    }

    @Override
    public void onRoomClicked() {

    }
}
