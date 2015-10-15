package com.parse.starter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by xieminjie on 15/09/2015.
 */
public class ViewPagerAdapter extends FragmentStatePagerAdapter {
    int ICONS[];
    int NumbOfTabs;
    public ViewPagerAdapter(FragmentManager fm,int ICONS[], int mNumbOfTabsumb) {
        super(fm);
        this.ICONS = ICONS;
        this.NumbOfTabs = mNumbOfTabsumb;
    }

    @Override
    public Fragment getItem(int position) {

        if(position == 0)
        {
            main_tab mtab = new main_tab();
            return mtab;
        }
        else if(position ==1)
        {
            discovery_tab dtab = new discovery_tab();
            return dtab;
        }else if(position ==2){
            photo_tab ptab = new photo_tab();
            return ptab;
        }else if(position ==3){
            activity_tab atab = new activity_tab();
            return atab;
        }else{
            profile_tab ptab = new profile_tab();
            return ptab;
        }
    }


    @Override
    public CharSequence getPageTitle(int position) {
        return null;
    }

    public int getDrawableId(int position){
        return ICONS[position];
    }
    @Override
    public int getCount() {
        return ICONS.length;
    }


}
