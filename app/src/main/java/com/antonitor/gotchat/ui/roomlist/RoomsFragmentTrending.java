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
import com.antonitor.gotchat.databinding.FragmentTrendigListBinding;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RoomsFragmentTrending#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RoomsFragmentTrending extends Fragment {

    private ViewModel viewModel;
    private FragmentTrendigListBinding mDataBinding;

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
        return mDataBinding.getRoot();
    }
}
