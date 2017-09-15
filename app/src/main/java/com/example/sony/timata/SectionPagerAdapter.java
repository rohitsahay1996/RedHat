package com.example.sony.timata;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by sony on 15-07-2017.
 */

class SectionPagerAdapter extends FragmentPagerAdapter {
    public SectionPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    /**
     * Return the Fragment associated with a specified position.
     *
     * @param position
     */
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:RequestsFragment requestsFragment = new RequestsFragment();
                return requestsFragment;
            case 1:ChatsFragment chatsFragment = new ChatsFragment();
                return chatsFragment;
            case 2:FriendFragment friendFragment = new FriendFragment();
                return friendFragment;
            default:
                return null;

        }


    }

    /**
     * Return the number of views available.
     */
    @Override
    public int getCount() {
        return 3;//no of tabs
    }
    public CharSequence getPageTitle(int position){
        switch (position){
            case 0:
                return "REQUEST";
            case 1:
                return "CHATS";
            case 2:
                return "FRIENDS";
            default:
                return null;

        }
    }
}
