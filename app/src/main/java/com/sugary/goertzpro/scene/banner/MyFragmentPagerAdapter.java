package com.sugary.goertzpro.scene.banner;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by Ethan on 2017/9/21.
 */

public class MyFragmentPagerAdapter extends FragmentPagerAdapter {

    private List<ImageFragment> mDataList;
    private List<String> mUrList;

    public MyFragmentPagerAdapter(FragmentManager fm, List<ImageFragment> dataList) {
        super(fm);
        mDataList = dataList;
    }

    public MyFragmentPagerAdapter(FragmentManager fm, List<ImageFragment> dataList, List<String> urList) {
        super(fm);
        mDataList = dataList;
        mUrList = urList;
    }

    @Override
    public Fragment getItem(int position) {
        ImageFragment fragment = mDataList.get(position);
        fragment.displayImg(mUrList.get(position));
        return fragment;
    }

    @Override
    public int getCount() {
        return mDataList != null ? mDataList.size() : 0;
    }
}
