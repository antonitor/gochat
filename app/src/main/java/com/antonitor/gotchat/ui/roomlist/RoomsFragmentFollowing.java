package com.antonitor.gotchat.ui.roomlist;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.antonitor.gotchat.R;
import com.antonitor.gotchat.databinding.FragmentFollowingListBinding;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RoomsFragmentFollowing#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RoomsFragmentFollowing extends Fragment {

    private ViewModel viewModel;
    private FragmentFollowingListBinding mDataBinding;


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
        return mDataBinding.getRoot();
    }
}
