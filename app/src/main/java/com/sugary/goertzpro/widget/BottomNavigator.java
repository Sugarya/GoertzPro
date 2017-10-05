package com.sugary.goertzpro.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sugary.goertzpro.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ethan on 2017/10/4.
 * 主页底部导航器
 */

public class BottomNavigator extends LinearLayout {


    public static final int UN_SELECT_SIZE = 12;
    public static final int SELECT_SIZE = 13;
    private String mFirstTitle;
    private String mSecondTitle;
    private String mThirdTitle;
    private String mFourthTitle;
    private String mFifthTitle;

    private Drawable mFirstIcon;
    private Drawable mSecondIcon;
    private Drawable mThirdIcon;
    private Drawable mFourIcon;
    private Drawable mFifthIcon;


    private List<BottomUnit> mBottomUnitList = new ArrayList<>();
    private TextView mLastTvIcon;
    private CheckedTextView mLastTvTitle;
    private OnNavigationItemClickListener mOnNavigationItemClickListener;

    public BottomNavigator(Context context) {
        super(context);
    }

    public BottomNavigator(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.BottomNavigator);
        mFirstTitle = typedArray.getString(R.styleable.BottomNavigator_firstTitle);
        mSecondTitle = typedArray.getString(R.styleable.BottomNavigator_secondTitle);
        mThirdTitle = typedArray.getString(R.styleable.BottomNavigator_thirdTitle);
        mFourthTitle = typedArray.getString(R.styleable.BottomNavigator_fourthTitle);
        mFifthTitle = typedArray.getString(R.styleable.BottomNavigator_fifthTitle);

        mFirstIcon = typedArray.getDrawable(R.styleable.BottomNavigator_firstIcon);
        mSecondIcon = typedArray.getDrawable(R.styleable.BottomNavigator_secondIcon);
        mThirdIcon = typedArray.getDrawable(R.styleable.BottomNavigator_thirdIcon);
        mFourIcon = typedArray.getDrawable(R.styleable.BottomNavigator_fourthIcon);
        mFifthIcon = typedArray.getDrawable(R.styleable.BottomNavigator_fifthIcon);

        typedArray.recycle();
        init();
    }


    private void configure(String firstTitle, String secondTitle, String thirdTitle, String fourthTitle, String fifthTitle,
                           Drawable firstIcon, Drawable secondIcon, Drawable thirdIcon, Drawable fourIcon, Drawable fifthIcon) {
        mFirstTitle = firstTitle;
        mSecondTitle = secondTitle;
        mThirdTitle = thirdTitle;
        mFourthTitle = fourthTitle;
        mFifthTitle = fifthTitle;

        mFirstIcon = firstIcon;
        mSecondIcon = secondIcon;
        mThirdIcon = thirdIcon;
        mFourIcon = fourIcon;
        mFifthIcon = fifthIcon;

        init();
    }


    private void init() {
        int childCount = getChildCount();
        if (childCount > 0) {
            return;
        }

        setOrientation(HORIZONTAL);
        initData();

        int size = mBottomUnitList.size();
        for (int i = 0; i < size; i++) {
            BottomUnit unit = mBottomUnitList.get(i);
            Drawable icon = unit.getIcon();
            String title = unit.getTitle();

            LinearLayout containerLayout = new LinearLayout(getContext());
            LayoutParams containerLayoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
            containerLayoutParams.weight = 1;
            containerLayout.setLayoutParams(containerLayoutParams);
            containerLayout.setOrientation(VERTICAL);
            containerLayout.setGravity(Gravity.CENTER);
            containerLayout.setBackgroundResource(R.drawable.bg_item_bill_structure);

            final TextView tvIcon = new TextView(getContext());
            LayoutParams iconLayoutParam = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            tvIcon.setLayoutParams(iconLayoutParam);
            if (Build.VERSION.SDK_INT >= 16) {
                tvIcon.setBackground(icon);
            } else {
                tvIcon.setBackgroundDrawable(icon);
            }

            final CheckedTextView tvTitle = new CheckedTextView(getContext());
            LayoutParams titleLayoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            tvTitle.setLayoutParams(titleLayoutParams);
            tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, UN_SELECT_SIZE);
            tvTitle.setTextColor(getResources().getColorStateList(R.color.bottom_navigator_txt));
            tvTitle.setText(title);

            if (i == 0) {
                tvIcon.setEnabled(true);
                tvTitle.setChecked(true);
                mLastTvIcon = tvIcon;
                mLastTvTitle = tvTitle;
            } else {
                tvIcon.setEnabled(false);
                tvTitle.setChecked(false);
            }

            containerLayout.addView(tvIcon);
            containerLayout.addView(tvTitle);
            addView(containerLayout);

            final int index = i;
            containerLayout.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (tvIcon == mLastTvIcon || tvTitle == mLastTvTitle) {
                        return;
                    }

                    mLastTvIcon.setEnabled(false);
                    mLastTvTitle.setChecked(false);
                    mLastTvTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, UN_SELECT_SIZE);
                    tvIcon.setEnabled(true);
                    tvTitle.setChecked(true);
                    tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, SELECT_SIZE);

                    if (mOnNavigationItemClickListener != null) {
                        mOnNavigationItemClickListener.onClick(index, tvIcon, tvTitle);
                    }

                    mLastTvIcon = tvIcon;
                    mLastTvTitle = tvTitle;
                }
            });
        }
    }

    private void initData() {
        if (!TextUtils.isEmpty(mFirstTitle) && mFirstIcon != null) {
            mBottomUnitList.add(new BottomUnit(mFirstTitle, mFirstIcon));
        }
        if (!TextUtils.isEmpty(mSecondTitle) && mSecondIcon != null) {
            mBottomUnitList.add(new BottomUnit(mSecondTitle, mSecondIcon));
        }
        if (!TextUtils.isEmpty(mThirdTitle) && mThirdIcon != null) {
            mBottomUnitList.add(new BottomUnit(mThirdTitle, mThirdIcon));
        }
        if (!TextUtils.isEmpty(mFourthTitle) && mFourIcon != null) {
            mBottomUnitList.add(new BottomUnit(mFourthTitle, mFourIcon));
        }
        if (!TextUtils.isEmpty(mFifthTitle) && mFifthIcon != null) {
            mBottomUnitList.add(new BottomUnit(mFifthTitle, mFifthIcon));
        }
    }


    class BottomUnit {
        private String title;
        private Drawable icon;

        BottomUnit(String title, Drawable icon) {
            this.title = title;
            this.icon = icon;
        }

        public String getTitle() {
            return title;
        }

        public Drawable getIcon() {
            return icon;
        }
    }

    private int dip2px(float dipValue) {
        float scale = getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public interface OnNavigationItemClickListener {
        void onClick(int position, TextView tvIcon, TextView tvTitle);
    }

    public void setOnNavigationItemClickListener(OnNavigationItemClickListener onNavigationItemClickListener) {
        mOnNavigationItemClickListener = onNavigationItemClickListener;
    }
}
