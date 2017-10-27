package com.sugary.goertzpro.widget.sortbar;

import android.content.Context;
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

public class SortBarUnit extends LinearLayout {


    private int mTriangleLeftMargin;
    private int mTxtSize;
    private int mTxtColor;
    private int mTxtSelectedColor;

    private TextView mTvTitle;
    private SortView mSortView;

    private boolean mHasSortView = false;
    private boolean mIsUnitSelected = false;
    private boolean mIsFirsToggleUpward = false;


    public SortBarUnit(Context context) {
        super(context);
        init();
    }

    public SortBarUnit(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setGravity(Gravity.CENTER_VERTICAL);

        mTvTitle = new TextView(getContext());
        mTvTitle.setGravity(Gravity.CENTER_VERTICAL);

        mSortView = new SortView(getContext());

        addView(mTvTitle);
        addView(mSortView);
    }

    private int dip2px(float dipValue) {
        float scale = getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    private void toggleTitle() {
        mIsUnitSelected = !mIsUnitSelected;
        setupTitleColor(mIsUnitSelected);
    }

    private void setupTitleColor(boolean isUnitSelected) {
        if (isUnitSelected) {
            mTvTitle.setTextColor(mTxtSelectedColor);
        } else {
            mTvTitle.setTextColor(mTxtColor);
        }
    }

    private void setupSortViewVisibility(boolean hasSortView) {
        if (hasSortView) {
            mSortView.setVisibility(VISIBLE);
        } else {
            mSortView.setVisibility(GONE);
        }
    }

    //****************************************对外提供方法

    public void bindUnitData(ItemSortable itemSortable) {
        if (itemSortable == null) {
            return;
        }

        mHasSortView = itemSortable.hasSortView();
        String title = itemSortable.getTitle();
        mTvTitle.setText(title);

        boolean hasIndicator = itemSortable.hasSortView();
        if (!hasIndicator) {
            mSortView.setVisibility(GONE);
            return;
        }
        mSortView.checkInitialTriangle();
    }

    public IndicatorStatusEnum toggleTriangle(){
        toggleTitle();
        if(mHasSortView){
            if(mIsFirsToggleUpward){
                mSortView.toggleUpward();
            }else {
                mSortView.toggleUnder();
            }
        }
        return mSortView.getStatusEnum();
    }

    public IndicatorStatusEnum toggleAlternative(){
        if(mHasSortView){
            mSortView.toggle();
        }
        return mSortView.getStatusEnum();
    }

    public void restore() {
        toggleTitle();
        mSortView.checkInitialTriangle();
        setupSortViewVisibility(mHasSortView);
    }

    public void setUnitUpwardSelected(boolean unitSelected) {
        mIsUnitSelected = unitSelected;
        setupTitleColor(unitSelected);
        setupSortViewVisibility(mHasSortView);
        mSortView.checkTriangle();
    }

    public void setTitleSize(int txtSize) {
        mTxtSize = txtSize;
        mTvTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, txtSize);

        LinearLayout.LayoutParams indicatorLayoutParams = new LinearLayout.LayoutParams(txtSize / 3, txtSize / 2);
        indicatorLayoutParams.gravity = Gravity.CENTER_VERTICAL;
        mSortView.setLayoutParams(indicatorLayoutParams);
    }

    public void setTitleColor(int txtColor) {
        mTxtColor = txtColor;
        mTvTitle.setTextColor(txtColor);
    }

    public void setTitleSelectedColor(int txtSelectedColor) {
        mTxtSelectedColor = txtSelectedColor;
    }

    public void setTitle(String title) {
        if (title == null) {
            return;
        }
        mTvTitle.setText(title);
    }

    public void setTriangleLeftMargin(int triangleLeftMargin) {
        mTriangleLeftMargin = triangleLeftMargin;
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mSortView.getLayoutParams();
        lp.leftMargin = triangleLeftMargin;
        mSortView.setLayoutParams(lp);
    }

    /**
     * 设置点击，第一次箭头的朝向
     * @param firsToggleUpward
     */
    public void setFirsToggleUpward(boolean firsToggleUpward) {
        mIsFirsToggleUpward = firsToggleUpward;
    }

    public void setTriangleColor(int triangleColor){
        mSortView.setTriangleColor(triangleColor);
    }

    public void setTriangleSelectedColor(int triangleSelectedColor){
        mSortView.setTriangleSelectedColor(triangleSelectedColor);
    }

}
