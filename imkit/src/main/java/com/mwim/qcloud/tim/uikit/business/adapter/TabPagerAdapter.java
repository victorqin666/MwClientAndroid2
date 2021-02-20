package com.mwim.qcloud.tim.uikit.business.adapter;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.mwim.qcloud.tim.uikit.base.BaseActivity;
import com.mwim.qcloud.tim.uikit.base.BaseFragment;

import java.util.List;


/**
 * Created by tangyx
 * Date 2018/1/4
 * email tangyx@live.com
 */

public class TabPagerAdapter extends FragmentPagerAdapter {

    private List<BaseFragment> fragments;

    public TabPagerAdapter(FragmentManager fm, List<BaseFragment> fragments) {
        super(fm);
        this.fragments = fragments;
    }

    @Override
    public BaseFragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }


    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return null;
    }
}
