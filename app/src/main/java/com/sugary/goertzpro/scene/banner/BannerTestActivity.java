package com.sugary.goertzpro.scene.banner;

import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.sugary.goertzpro.R;
import com.sugary.goertzpro.widget.banner.LoopViewPager;
import com.sugary.goertzpro.widget.banner.PagerIndicator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class BannerTestActivity extends AppCompatActivity {

    private static final String TAG = "BannerTestActivity";

    @BindView(R.id.scroll_body)
    NestedScrollView mNestedScrollView;

    @BindView(R.id.tv_banner_bg)
    View mViewBg;

    @BindView(R.id.pager_looper)
    LoopViewPager mLoopViewPager;

    @BindView(R.id.indicator_pager)
    PagerIndicator mPagerIndicator;


    private int mIndex = 0;
    private int mScrollYTotal;
    private int mScrollYAtMost;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banner_test);
        ButterKnife.bind(this);

        List<String> urlList = createData();
        final int size = urlList.size();
        MyPagerAdapter pagerAdapter = new MyPagerAdapter(urlList);
        mLoopViewPager.setAdapter(pagerAdapter);
        mPagerIndicator.setViewPager(mLoopViewPager);

        Observable.interval(3, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        if(mIndex >= size){
                            mIndex = 0;
                        }
                        mLoopViewPager.setCurrentItem(mIndex);
                        mIndex ++;
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                    }
                });


        mLoopViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mIndex = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mScrollYAtMost = dip2px(75);
        Log.d(TAG, "mScrollYAtMost = " + mScrollYAtMost);
        mNestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                mScrollYTotal += scrollY;
                float delta = mScrollYAtMost - scrollY;
                Log.d(TAG, "onScrollChange: delta = " + delta);
                if(delta >= 0){
                    mViewBg.setAlpha(delta / mScrollYAtMost);
                }else{
                    mViewBg.setAlpha(0);
                }
            }
        });
    }

    private List<String>  createData(){
        List<String> urlList = new ArrayList<>();
        urlList.add("http://img30.360buyimg.com/da/jfs/t8152/240/1322073024/95957/6cc8b2ff/59bf9a25N92c97068.jpg");
        urlList.add("http://img30.360buyimg.com/da/jfs/t8266/119/1504843661/265394/2ba3c240/59bb3139Nc8e13f1a.jpg");
        urlList.add("http://img30.360buyimg.com/da/jfs/t8329/47/1591956625/29917/50cf7c2c/59bc8bafN48bcdb92.jpg");
        urlList.add("http://img30.360buyimg.com/da/jfs/t8827/327/844036523/55967/538151e3/59afa5eaN5b25753f.jpg");
        return urlList;
    }


    public int dip2px(float dipValue) {
        float scale = getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }



}
