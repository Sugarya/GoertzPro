package com.sugary.goertzpro.widget.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.HorizontalScrollView;
import android.widget.RelativeLayout;

/**
 * Created by Ethan on 2017/10/11.
 * 选项条，用于筛选列表信息
 */

public class TabBarLayout extends HorizontalScrollView {

    private static final String TAG = "TabBarLayout";

    public TabBarLayout(Context context) {
        super(context);
        init();
    }

    public TabBarLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init(){
        RelativeLayout containerLayout = new RelativeLayout(getContext());

    }


    private void unitTab(){

    }

}
