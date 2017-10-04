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
 * Banner适配器
 */

public class BannerPagerAdapter extends PagerAdapter {

    private List<String> mDataList;


    public BannerPagerAdapter(List<String> dataList) {
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
    public Object instantiateItem(final ViewGroup container, int position) {
        RoundedImageView imageView = new RoundedImageView(container.getContext());
        imageView.setCornerRadius(8);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        ImageLoader.display(container.getContext(), mDataList.get(position), imageView);
        container.addView(imageView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        return imageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
