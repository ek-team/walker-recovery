package com.pharos.walker.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhanglun on 2021/1/28
 * Describe:
 */
public class TitleFragmentAdapter extends FragmentPagerAdapter {
    private List<Fragment> mFragmentList;
    private String[] titles;
    public TitleFragmentAdapter(FragmentManager fm, ArrayList<Fragment> fragmentList) {
        super(fm);
        this.mFragmentList = fragmentList;
    }
    public TitleFragmentAdapter(FragmentManager fm, List<Fragment> fragmentList, String[] titles) {
        super(fm);
        this.mFragmentList = fragmentList;
        this.titles = titles;
    }
    public TitleFragmentAdapter(FragmentManager fm, List<Fragment> fragmentList, String[] titles,int behavior) {
        super(fm,behavior);
        this.mFragmentList = fragmentList;
        this.titles = titles;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment;
        if (position < mFragmentList.size()) {
            fragment = mFragmentList.get(position);
        } else {
            fragment = mFragmentList.get(0);
        }
        return fragment;
    }

//    @Override
//    public int getItemPosition(@NonNull Object object) {
//        return POSITION_NONE;
//    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }
    @Override
    public CharSequence getPageTitle(int position) {
        if (titles != null && titles.length > 0)
            return titles[position];
        return null;
    }
}
