package com.sugary.goertzpro.widget.sortbar;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by Ethan on 2017/10/24.
 * 排序单元
 */

public class SortBarUnit extends LinearLayout{


    private static final int DEFAULT_TXT_SIZE = 15;
    private static final int DEFAULT_TXT_COLOR = Color.parseColor("#cccccc");
    private static final int DEFAULT_SELECTED_TXT_COLOR = Color.parseColor("#E4393C");

    private static final int DEFAULT_INDICATOR_LEFT_MARGIN = 8;

    private int mIndicatorLeftMargin = DEFAULT_INDICATOR_LEFT_MARGIN;
    private int mTxtSize = DEFAULT_TXT_SIZE;
    private int mTxtColor = DEFAULT_TXT_COLOR;
    private int mTxtSelectedColor = DEFAULT_SELECTED_TXT_COLOR;
    private ItemSortable mCurrentItemSortable;
    private TextView mTvTitle;
    private SortView mSortView;

    private boolean mIsUnitSelected = false;

    public SortBarUnit(Context context) {
        super(context);
        init();
    }

    public SortBarUnit(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init(){
        setGravity(Gravity.CENTER_VERTICAL);

        mTvTitle = new TextView(getContext());
        mTvTitle.setGravity(Gravity.CENTER_VERTICAL);
        mTvTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, mTxtSize);
        mTvTitle.setTextColor(mTxtColor);
        mTvTitle.setText("");

        int size = (int) mTvTitle.getTextSize();
        mSortView = new SortView(getContext());
        LinearLayout.LayoutParams indicatorLayoutParams = new LinearLayout.LayoutParams(size/3, size/2);
        indicatorLayoutParams.leftMargin = mIndicatorLeftMargin;
        indicatorLayoutParams.gravity = Gravity.CENTER_VERTICAL;
        mSortView.setLayoutParams(indicatorLayoutParams);

        addView(mTvTitle);
        addView(mSortView);
    }

    private int dip2px(float dipValue) {
        float scale = getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    //****************************************对外提供方法

    public void bindUnitData(ItemSortable itemSortable){
        if(itemSortable == null){
            return;
        }

        mCurrentItemSortable = itemSortable;
        String title = itemSortable.getTitle();
        mTvTitle.setText(title);

//        if(mIsUnitSelected){
//            mTvTitle.setTextColor(mTxtSelectedColor);
//        }else{
//            mTvTitle.setTextColor(mTxtColor);
//        }
//
//        boolean hasIndicator = itemSortable.hasSortView();
//        if(!hasIndicator){
//            mSortView.setVisibility(GONE);
//            return;
//        }
//        mSortView.checkTriangle(IndicatorStatusEnum.INITIAL);
    }

    public void toggleUpward(){
        mSortView.toggleUpward();

        if(mIsUnitSelected){
            mTvTitle.setTextColor(mTxtColor);
        }else{
            mTvTitle.setTextColor(mTxtSelectedColor);
        }
    }

    public void toggleUnder(){
        mSortView.toggleUnder();

        if(mIsUnitSelected){
            mTvTitle.setTextColor(mTxtColor);
        }else{
            mTvTitle.setTextColor(mTxtSelectedColor);
        }
    }

    public void restore(){
        setUnitUpwardSelected(false);
    }

    public void setUnitUpwardSelected(boolean unitSelected) {
        mIsUnitSelected = unitSelected;
        setupUnitShow();
    }

    private void setupUnitShow() {
        if(mIsUnitSelected){
            mTvTitle.setTextColor(mTxtSelectedColor);
            mSortView.checkUpwardTriangle();
        }else{
            mSortView.checkInitalTriagle();
            mTvTitle.setTextColor(mTxtColor);
        }

        if(mCurrentItemSortable != null){
            boolean hasSortView = mCurrentItemSortable.hasSortView();
            if(hasSortView){
                mSortView.setVisibility(VISIBLE);
            }else{
                mSortView.setVisibility(GONE);
            }
        }
    }

    public void setTitleSize(int txtSize) {
        mTxtSize = txtSize;
        mTvTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, txtSize);
    }

    public void setTitleColor(int txtColor) {
        mTxtColor = txtColor;
        mTvTitle.setTextColor(txtColor);
    }


    public void setTitle(String title){
        if(title == null){
            return;
        }
        mTvTitle.setText(title);
    }

    public void setIndicatorLeftMargin(int indicatorLeftMargin) {
        mIndicatorLeftMargin = indicatorLeftMargin;
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mSortView.getLayoutParams();
        lp.leftMargin = indicatorLeftMargin;
        mSortView.setLayoutParams(lp);
    }


}
