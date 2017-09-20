package com.sugary.goertzpro.widget.banner;

import android.animation.Animator;
import android.animation.AnimatorInflater;
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
import android.view.animation.Interpolator;
import android.widget.LinearLayout;
import com.sugary.goertzpro.R;
import static android.support.v4.view.ViewPager.OnPageChangeListener;


/**
 * Modify by Ethan 2017/09/20
 * created by ongakuer(https://github.com/ongakuer/CircleIndicator)
 */
public class PagerIndicator extends LinearLayout {

    private final static int DEFAULT_INDICATOR_WIDTH = 8;
    private final static int DEFAULT_INDICATOR_HEIGHT = 8;
    private ViewPager mViewpager;
    private int mIndicatorMargin = -1;
    private int mIndicatorWidth = -1;
    private int mIndicatorHeight = -1;
    private int mAnimatorResId = R.animator.scale_with_alpha;
    private int mAnimatorReverseResId = R.animator.scale_with_alpha_reverse;
    private int mIndicatorBackgroundResId = R.drawable.rectangle_red;
    private int mIndicatorUnselectedBackgroundResId = R.drawable.white_radius;
    private Animator mAnimatorOut;
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
        mIndicatorWidth =
                typedArray.getDimensionPixelSize(R.styleable.PagerIndicator_ci_width, -1);
        mIndicatorHeight =
                typedArray.getDimensionPixelSize(R.styleable.PagerIndicator_ci_height, -1);
        mIndicatorMargin =
                typedArray.getDimensionPixelSize(R.styleable.PagerIndicator_ci_margin, -1);

        mAnimatorResId = typedArray.getResourceId(R.styleable.PagerIndicator_ci_animator,
                R.animator.scale_with_alpha);
        mAnimatorReverseResId =
                typedArray.getResourceId(R.styleable.PagerIndicator_ci_animator_reverse, R.animator.scale_with_alpha_reverse);
        mIndicatorBackgroundResId =
                typedArray.getResourceId(R.styleable.PagerIndicator_ci_drawable,
                        R.drawable.rectangle_red);
        mIndicatorUnselectedBackgroundResId =
                typedArray.getResourceId(R.styleable.PagerIndicator_ci_drawable_unselected,
                        R.drawable.white_radius);

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
                R.animator.scale_with_alpha, 0, R.drawable.white_radius, R.drawable.white_radius);
    }

    public void configureIndicator(int indicatorWidth, int indicatorHeight, int indicatorMargin,
                                   @AnimatorRes int animatorId, @AnimatorRes int animatorReverseId,
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
        mIndicatorWidth = (mIndicatorWidth < 0) ? dip2px(DEFAULT_INDICATOR_WIDTH) : mIndicatorWidth;
        mIndicatorHeight =
                (mIndicatorHeight < 0) ? dip2px(DEFAULT_INDICATOR_HEIGHT) : mIndicatorHeight;
        mIndicatorMargin =
                (mIndicatorMargin < 0) ? dip2px(DEFAULT_INDICATOR_HEIGHT * 0.7f) : mIndicatorMargin;

        mAnimatorResId = (mAnimatorResId == 0) ? R.animator.scale_with_alpha : mAnimatorResId;
        mAnimatorReverseResId = (mAnimatorReverseResId == 0)? R.animator.scale_with_alpha_reverse : mAnimatorReverseResId;

        mAnimatorOut = createAnimatorOut(context);
        mImmediateAnimatorOut = createAnimatorOut(context);
        mImmediateAnimatorOut.setDuration(0);

        mAnimatorIn = createAnimatorIn(context);
        mImmediateAnimatorIn = createAnimatorIn(context);
        mImmediateAnimatorIn.setDuration(0);

        mIndicatorBackgroundResId = (mIndicatorBackgroundResId == 0) ? R.drawable.rectangle_red
                : mIndicatorBackgroundResId;
        mIndicatorUnselectedBackgroundResId =
                (mIndicatorUnselectedBackgroundResId == 0) ? R.drawable.white_radius
                        : mIndicatorUnselectedBackgroundResId;
    }

    private Animator createAnimatorOut(Context context) {
        return AnimatorInflater.loadAnimator(context, mAnimatorResId);
    }

    private Animator createAnimatorIn(Context context) {
//        Animator animatorIn;
//        if (mAnimatorReverseResId == 0) {
//            animatorIn = AnimatorInflater.loadAnimator(context, mAnimatorReverseResId);
//            animatorIn.setInterpolator(new ReverseInterpolator());
//        } else {
//            animatorIn = AnimatorInflater.loadAnimator(context, mAnimatorReverseResId);
//        }
        Animator animatorIn = AnimatorInflater.loadAnimator(context, mAnimatorReverseResId);
        return animatorIn;
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
                ViewGroup.MarginLayoutParams layoutParams = (MarginLayoutParams)currentIndicator.getLayoutParams();
                layoutParams.width = dip2px(DEFAULT_INDICATOR_HEIGHT);
                layoutParams.height = dip2px(DEFAULT_INDICATOR_HEIGHT);
                layoutParams.leftMargin = dip2px(DEFAULT_INDICATOR_HEIGHT / 2.5f);
                layoutParams.rightMargin = dip2px(DEFAULT_INDICATOR_HEIGHT / 2.5f);
                currentIndicator.setLayoutParams(layoutParams);
                currentIndicator.setBackgroundResource(mIndicatorUnselectedBackgroundResId);
                mAnimatorIn.setTarget(currentIndicator);
                mAnimatorIn.start();
            }

            View selectedIndicator = getChildAt(position);
            if (selectedIndicator != null) {
                ViewGroup.MarginLayoutParams layoutParams = (MarginLayoutParams)selectedIndicator.getLayoutParams();
                layoutParams.width = dip2px(DEFAULT_INDICATOR_WIDTH);
                layoutParams.height = dip2px(DEFAULT_INDICATOR_HEIGHT);
                layoutParams.leftMargin = dip2px(DEFAULT_INDICATOR_HEIGHT * 1f);
                layoutParams.rightMargin = dip2px(DEFAULT_INDICATOR_HEIGHT * 1f);
                selectedIndicator.setLayoutParams(layoutParams);
                selectedIndicator.setBackgroundResource(mIndicatorBackgroundResId);
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
                addIndicator(orientation, mIndicatorBackgroundResId, mImmediateAnimatorOut, 1);
            } else {
                addIndicator(orientation, mIndicatorUnselectedBackgroundResId,
                        mImmediateAnimatorIn, 0);
            }
        }
    }

    private void addIndicator(int orientation, @DrawableRes int backgroundDrawableId,
                              Animator animator) {
        if (animator.isRunning()) {
            animator.end();
            animator.cancel();
        }

        View Indicator = new View(getContext());
        Indicator.setBackgroundResource(backgroundDrawableId);
        addView(Indicator, mIndicatorWidth, mIndicatorHeight);
        LayoutParams lp = (LayoutParams) Indicator.getLayoutParams();

        if (orientation == HORIZONTAL) {
            lp.leftMargin = (int) (mIndicatorMargin / 2.5);
            lp.rightMargin = (int) (mIndicatorMargin / 2.5);
        } else {
            lp.topMargin = (int) (mIndicatorMargin / 2.5);
            lp.bottomMargin = (int) (mIndicatorMargin / 2.5);
        }

        Indicator.setLayoutParams(lp);

        animator.setTarget(Indicator);
        animator.start();
    }

    private void addIndicator(int orientation, @DrawableRes int backgroundDrawableId,
                              Animator animator, int indicatorType) {
        if (animator.isRunning()) {
            animator.end();
            animator.cancel();
        }

        View Indicator = new View(getContext());
        Indicator.setBackgroundResource(backgroundDrawableId);
        if(indicatorType == 0){
            addView(Indicator, mIndicatorHeight, mIndicatorHeight);
        }else{
            addView(Indicator, mIndicatorWidth, mIndicatorHeight);
        }

        LayoutParams lp = (LayoutParams) Indicator.getLayoutParams();

        if (orientation == HORIZONTAL) {
            lp.leftMargin = (int) (mIndicatorMargin / 2.5);
            lp.rightMargin = (int) (mIndicatorMargin / 2.5);
        } else {
            lp.topMargin = (int) (mIndicatorMargin / 2.5);
            lp.bottomMargin = (int) (mIndicatorMargin / 2.5);
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
