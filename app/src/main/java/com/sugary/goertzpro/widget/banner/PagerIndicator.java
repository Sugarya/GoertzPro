package com.sugary.goertzpro.widget.banner;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.os.Build;
import android.support.annotation.AnimatorRes;
import android.support.annotation.DrawableRes;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationSet;
import android.view.animation.Interpolator;
import android.widget.LinearLayout;

import com.sugary.goertzpro.R;

import static android.support.v4.view.ViewPager.OnPageChangeListener;


/**
 * Modify by Ethan 2017/09/20
 * 实现矩形指示器 原型指示器动画
 * created by ongakuer(https://github.com/ongakuer/CircleIndicator)
 */
public class PagerIndicator extends LinearLayout {

    private static final int TYPE_SELECTED_INDICATOR = 1;
    private static final int TYPE_NOT_SELECTED_INDICATOR = 0;

    private static final int UNIT_DISTANCE = 8;
    private static final float DEFAULT_INDICATOR_WIDTH = 2.4f * UNIT_DISTANCE;
    private static final float DEFAULT_INDICATOR_HEIGHT = UNIT_DISTANCE;
    private ViewPager mViewpager;
    private int mIndicatorWidth = -1;
    private int mIndicatorHeight = -1;
    private int mIndicatorMargin = -1;
    private int mAnimatorResId = -1;
    private int mAnimatorReverseResId = -1;
    private int mIndicatorBackgroundResId = R.drawable.rectangle_red;
    private int mIndicatorUnselectedBackgroundResId = R.drawable.white_radius;
    private ValueAnimator mAnimatorOut;
    private Animator mAnimatorIn;
    private Animator mImmediateAnimatorOut;
    private Animator mImmediateAnimatorIn;

    private int mLastPosition = -1;

    public PagerIndicator(Context context) {
        super(context);
        init(context, null);
    }

    public PagerIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public PagerIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PagerIndicator(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        handleTypedArray(context, attrs);
        checkIndicatorConfig(context);
    }

    private void handleTypedArray(Context context, AttributeSet attrs) {
        if (attrs == null) {
            return;
        }

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.PagerIndicator);
        mIndicatorWidth = typedArray.getDimensionPixelSize(R.styleable.PagerIndicator_ci_width, 0);
        mIndicatorHeight = typedArray.getDimensionPixelSize(R.styleable.PagerIndicator_ci_height, 0);
        mIndicatorMargin = typedArray.getDimensionPixelSize(R.styleable.PagerIndicator_ci_margin, 0);

        mIndicatorBackgroundResId = typedArray.getResourceId(R.styleable.PagerIndicator_ci_drawable, 0);
        mIndicatorUnselectedBackgroundResId = typedArray.getResourceId(R.styleable.PagerIndicator_ci_drawable_unselected, 0);

        mAnimatorResId = typedArray.getResourceId(R.styleable.PagerIndicator_ci_animator, 0);
        mAnimatorReverseResId = typedArray.getResourceId(R.styleable.PagerIndicator_ci_animator_reverse, 0);

        int orientation = typedArray.getInt(R.styleable.PagerIndicator_ci_orientation, -1);
        setOrientation(orientation == VERTICAL ? VERTICAL : HORIZONTAL);

        int gravity = typedArray.getInt(R.styleable.PagerIndicator_ci_gravity, -1);
        setGravity(gravity >= 0 ? gravity : Gravity.CENTER);

        typedArray.recycle();
    }

    /**
     * Create and configure Indicator in Java code.
     */
    public void configureIndicator(int indicatorWidth, int indicatorHeight, int indicatorMargin) {
        configureIndicator(indicatorWidth, indicatorHeight, indicatorMargin,
                R.animator.scale_with_alpha, R.animator.scale_with_alpha_reverse, R.drawable.white_radius, R.drawable.rectangle_red);
    }

    public void configureIndicator(int indicatorWidth, int indicatorHeight, int indicatorMargin,
                                   @AnimatorRes int animatorId,
                                   @AnimatorRes int animatorReverseId,
                                   @DrawableRes int indicatorBackgroundId,
                                   @DrawableRes int indicatorUnselectedBackgroundId) {

        mIndicatorWidth = indicatorWidth;
        mIndicatorHeight = indicatorHeight;
        mIndicatorMargin = indicatorMargin;

        mAnimatorResId = animatorId;
        mAnimatorReverseResId = animatorReverseId;
        mIndicatorBackgroundResId = indicatorBackgroundId;
        mIndicatorUnselectedBackgroundResId = indicatorUnselectedBackgroundId;

        checkIndicatorConfig(getContext());
    }

    private void checkIndicatorConfig(Context context) {
        mIndicatorWidth = (mIndicatorWidth <= 0) ? dip2px(DEFAULT_INDICATOR_WIDTH) : mIndicatorWidth;
        mIndicatorHeight = (mIndicatorHeight <= 0) ? dip2px(DEFAULT_INDICATOR_HEIGHT) : mIndicatorHeight;
        mIndicatorMargin = (mIndicatorMargin <= 0) ? dip2px(DEFAULT_INDICATOR_HEIGHT / 2) : mIndicatorMargin;

        mIndicatorBackgroundResId = (mIndicatorBackgroundResId <= 0) ? R.drawable.rectangle_red : mIndicatorBackgroundResId;
        mIndicatorUnselectedBackgroundResId = (mIndicatorUnselectedBackgroundResId <= 0) ? R.drawable.white_radius : mIndicatorUnselectedBackgroundResId;

        mAnimatorResId = (mAnimatorResId <= 0) ? R.animator.scale_with_alpha : mAnimatorResId;
        mAnimatorOut = createAnimatorOut(context);
        mImmediateAnimatorOut = createAnimatorOut(context);
        mImmediateAnimatorOut.setDuration(0);

        mAnimatorReverseResId = (mAnimatorReverseResId <= 0) ? R.animator.scale_with_alpha_reverse : mAnimatorReverseResId;
        mAnimatorIn = createAnimatorIn(context);
        mImmediateAnimatorIn = createAnimatorIn(context);
        mImmediateAnimatorIn.setDuration(0);
    }

    private ValueAnimator createAnimatorOut(Context context) {
        return (ObjectAnimator)AnimatorInflater.loadAnimator(context, mAnimatorResId);
    }


    private Animator createAnimatorIn(Context context) {
        return AnimatorInflater.loadAnimator(context, mAnimatorReverseResId);
    }

    public void setViewPager(ViewPager viewPager) {
        mViewpager = viewPager;
        if (mViewpager != null && mViewpager.getAdapter() != null) {
            mLastPosition = -1;
            createIndicators();
            mViewpager.removeOnPageChangeListener(mInternalPageChangeListener);
            mViewpager.addOnPageChangeListener(mInternalPageChangeListener);
            mInternalPageChangeListener.onPageSelected(mViewpager.getCurrentItem());
        }
    }

    private final OnPageChangeListener mInternalPageChangeListener = new OnPageChangeListener() {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            if (mViewpager.getAdapter() == null || mViewpager.getAdapter().getCount() <= 0) {
                return;
            }

            if (mAnimatorIn.isRunning()) {
                mAnimatorIn.end();
                mAnimatorIn.cancel();
            }

            if (mAnimatorOut.isRunning()) {
                mAnimatorOut.end();
                mAnimatorOut.cancel();
            }

            View currentIndicator;
            if (mLastPosition >= 0 && (currentIndicator = getChildAt(mLastPosition)) != null) {
                currentIndicator.setBackgroundResource(mIndicatorUnselectedBackgroundResId);

                ViewGroup.MarginLayoutParams layoutParams = (MarginLayoutParams) currentIndicator.getLayoutParams();
                layoutParams.width = dip2px(DEFAULT_INDICATOR_HEIGHT);
                layoutParams.height = dip2px(DEFAULT_INDICATOR_HEIGHT);
                layoutParams.leftMargin = dip2px(DEFAULT_INDICATOR_HEIGHT / 2);
                layoutParams.rightMargin = dip2px(DEFAULT_INDICATOR_HEIGHT / 2);
                currentIndicator.setLayoutParams(layoutParams);

                mAnimatorIn.setTarget(currentIndicator);
                mAnimatorIn.start();
            }

            final View selectedIndicator = getChildAt(position);
            if (selectedIndicator != null) {
                selectedIndicator.setBackgroundResource(mIndicatorBackgroundResId);

                final int start = dip2px(DEFAULT_INDICATOR_HEIGHT);
                final int end = dip2px(DEFAULT_INDICATOR_WIDTH);
                mAnimatorOut = ValueAnimator.ofInt(start, end);
                mAnimatorOut.setDuration(100);
                mAnimatorOut.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        int animatedValue = (int) animation.getAnimatedValue();
                        ViewGroup.MarginLayoutParams layoutParams = (MarginLayoutParams) selectedIndicator.getLayoutParams();
                        layoutParams.width = animatedValue;
                        layoutParams.height = start;
                        int delta = (end - animatedValue) / 2;
                        layoutParams.leftMargin = start / 2 + delta;
                        layoutParams.rightMargin = start / 2 + delta;
                        selectedIndicator.setLayoutParams(layoutParams);
                    }
                });
                mAnimatorOut.setTarget(selectedIndicator);
                mAnimatorOut.start();
            }
            mLastPosition = position;
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    };

    public DataSetObserver getDataSetObserver() {
        return mInternalDataSetObserver;
    }

    private DataSetObserver mInternalDataSetObserver = new DataSetObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            if (mViewpager == null) {
                return;
            }

            int newCount = mViewpager.getAdapter().getCount();
            int currentCount = getChildCount();

            if (newCount == currentCount) {  // No change
                return;
            } else if (mLastPosition < newCount) {
                mLastPosition = mViewpager.getCurrentItem();
            } else {
                mLastPosition = -1;
            }

            createIndicators();
        }
    };

    /**
     * @deprecated User ViewPager addOnPageChangeListener
     */
    @Deprecated
    public void setOnPageChangeListener(OnPageChangeListener onPageChangeListener) {
        if (mViewpager == null) {
            throw new NullPointerException("can not find Viewpager , setViewPager first");
        }
        mViewpager.removeOnPageChangeListener(onPageChangeListener);
        mViewpager.addOnPageChangeListener(onPageChangeListener);
    }

    private void createIndicators() {
        removeAllViews();
        int count = mViewpager.getAdapter().getCount();
        if (count <= 0) {
            return;
        }
        int currentItem = mViewpager.getCurrentItem();
        int orientation = getOrientation();

        for (int i = 0; i < count; i++) {
            if (currentItem == i) {
                addIndicator(orientation, mIndicatorBackgroundResId, mImmediateAnimatorOut, TYPE_SELECTED_INDICATOR);
            } else {
                addIndicator(orientation, mIndicatorUnselectedBackgroundResId,
                        mImmediateAnimatorIn, TYPE_NOT_SELECTED_INDICATOR);
            }
        }
    }

    private void addIndicator(int orientation, @DrawableRes int backgroundDrawableId,
                              Animator animator, int indicatorType) {
        if (animator.isRunning()) {
            animator.end();
            animator.cancel();
        }

        View Indicator = new View(getContext());
        Indicator.setBackgroundResource(backgroundDrawableId);
        if (indicatorType == TYPE_NOT_SELECTED_INDICATOR) {
            addView(Indicator, mIndicatorHeight, mIndicatorHeight);
        } else {
            addView(Indicator, mIndicatorWidth, mIndicatorHeight);
        }

        LayoutParams lp = (LayoutParams) Indicator.getLayoutParams();

        if (orientation == HORIZONTAL) {
            lp.leftMargin = dip2px(DEFAULT_INDICATOR_HEIGHT / 2);
            lp.rightMargin = dip2px(DEFAULT_INDICATOR_HEIGHT / 2);
        } else {
            lp.topMargin = dip2px(DEFAULT_INDICATOR_HEIGHT / 2);
            lp.bottomMargin = dip2px(DEFAULT_INDICATOR_HEIGHT / 2);
        }

        Indicator.setLayoutParams(lp);

        animator.setTarget(Indicator);
        animator.start();
    }

    private class ReverseInterpolator implements Interpolator {
        @Override
        public float getInterpolation(float value) {
            return Math.abs(1.0f - value);
        }
    }

    public int dip2px(float dpValue) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
