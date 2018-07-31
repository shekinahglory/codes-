package com.shekglory.friends;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

class SectionPagerAdapter extends FragmentPagerAdapter {


    public  SectionPagerAdapter(FragmentManager fm){

        super(fm);
    }


    @Override
    public Fragment getItem(int position) {
        switch(position){
            case 0:
                ChatsFragment chatsFragment = new ChatsFragment();
                return  chatsFragment;

            case 1:
                RequestFragment requestFragment = new RequestFragment();
                return requestFragment;

            case 2:
                FriendsFragment friendsFragment = new FriendsFragment();
                return friendsFragment;


            default:
                return null;
        }

    }

    @Override
    public int getCount() {
        return 3;
    }


    public CharSequence getPageTitle(int position){
        switch (position){
            case 0:
                return "CHATS";
            case 1:
                return "FIND";

            case 2:
                return "FRIENDS";

            default:
                return null;


        }
    }


}
