package com.sugary.goertzpro.scene.banner;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.sugary.goertzpro.utils.ImageLoader;

import java.util.List;

/**
 * Created by Ethan on 2017/9/20.
 */

public class MyPagerAdapter extends PagerAdapter {

    private List<String> mDataList;


    public MyPagerAdapter(List<String> dataList) {
        mDataList = dataList;
    }

    @Override
    public int getCount() {
        return mDataList != null ? mDataList.size() : 0;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        RoundedImageView imgView = new RoundedImageView(container.getContext());
        imgView.setCornerRadius(8);
        imgView.setScaleType(ImageView.ScaleType.FIT_XY);
        ImageLoader.display(container.getContext(), mDataList.get(position), imgView);
        container.addView(imgView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        return imgView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
//        super.destroyItem(container,position,object);
        container.removeView((View) object);
    }
}
