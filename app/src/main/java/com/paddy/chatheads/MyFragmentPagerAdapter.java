package com.paddy.chatheads;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.paddy.chatheads.fragments.Home;
import com.paddy.chatheads.fragments.Settings;
import com.paddy.chatheads.fragments.Utilities;

public class MyFragmentPagerAdapter extends FragmentPagerAdapter {
    private Context mContext;

    public MyFragmentPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    // This determines the fragment for each tab
    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new Home();
        } else if (position == 1) {
            return new Utilities();
        } else {
            return new Settings();
        }
    }

    // This determines the number of tabs
    @Override
    public int getCount() {
        return 3;
    }

    // This determines the title for each tab
    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        switch (position) {
            case 0:
                return "Home";
            case 1:
                return "util";
            case 2:
                return "settings";
            default:
                return null;
        }
    }
}
