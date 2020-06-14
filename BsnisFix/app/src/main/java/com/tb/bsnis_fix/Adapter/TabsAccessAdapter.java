package com.tb.bsnis_fix.Adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.tb.bsnis_fix.Fragment.ChatFragment;
import com.tb.bsnis_fix.Fragment.GroupFragment;

public class TabsAccessAdapter extends FragmentPagerAdapter {


    public TabsAccessAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                ChatFragment chatFragment = new ChatFragment();
                return chatFragment;
            case 1:
                GroupFragment groupFragment = new GroupFragment();
                return groupFragment;

            default:
                return null;


        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {

        switch (position) {
            case 0:
                return "Chats";
            case 1:
                return "Groups";

            default:
                return null;
        }

    }
}
