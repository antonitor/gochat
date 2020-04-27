package com.antonitor.gotchat.ui.roomlist;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class MainPageAdapter extends FragmentPagerAdapter {

    private static final int NUM_ITEMS = 3;
    private String[] tabTitles = new String[]{"TRENDING", "FOLLOWINNG", "OWNER"};

    public MainPageAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return RoomsFragmentTrending.newInstance();
            case 1:
                return RoomsFragmentFollowing.newInstance();
            case 2:
                return RoomsFragmentOwn.newInstance();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return NUM_ITEMS;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }
}
