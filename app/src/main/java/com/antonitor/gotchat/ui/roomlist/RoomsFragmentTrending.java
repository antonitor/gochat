package com.antonitor.gotchat.ui.roomlist;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.antonitor.gotchat.R;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RoomsFragmentTrending#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RoomsFragmentTrending extends Fragment {

    ViewModel viewModel;

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
        View view = inflater.inflate(R.layout.fragment_trendig_list, container, false);
        viewModel = new ViewModelProvider(Objects.requireNonNull(getActivity())).get(MainViewModel.class);


        return view;
    }
}
