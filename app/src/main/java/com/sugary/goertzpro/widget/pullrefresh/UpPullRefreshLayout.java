package com.sugary.goertzpro.widget.pullrefresh;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.support.annotation.IdRes;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.sugary.goertzpro.R;
import com.sugary.goertzpro.utils.RxBus;

/**
 * Created by Ethan on 2017/10/6.
 * 下拉刷新分成两个状态：原始态，数据刷新态，结束态
 * 状态变化有如下情况：1.原始态- 结束态  2.原始态- 数据刷新态 - 结束态
 * 状态与状态之间通过动画链接；下拉操作改变原始态；到数据刷新态，或者回到原始态
 */

public class UpPullRefreshLayout extends RelativeLayout {

    private static final String TAG = "UpPullRefreshLayout";

    private static final
    @IdRes
    int[] REFRESH_FRAME_ARRAYS = {
            R.drawable.ic_santa_claus1,
            R.drawable.ic_santa_claus2,
            R.drawable.ic_santa_claus3,
            R.drawable.ic_santa_claus4,
            R.drawable.ic_santa_claus5,
            R.drawable.ic_santa_claus6,
            R.drawable.ic_santa_claus7,
            R.drawable.ic_santa_claus8,
            R.drawable.ic_santa_claus9,
            R.drawable.ic_santa_claus10,
            R.drawable.ic_santa_claus11,
            R.drawable.ic_santa_claus12};

    private static final int DEFAULT_REFRESH_FRAME_WIDTH_DP = 70;
    private static final int DEFAULT_REFRESH_FRAME_HEIGHT_DP = 70;
    private static final int DEFAULT_REFRESH_BOUNDARY = 80;
    private static final int DEFAULT_REFRESH_FRAME_SPEED = 20;

    private int mFrameWidth = dip2px(DEFAULT_REFRESH_FRAME_WIDTH_DP);
    private int mFrameHeight = dip2px(DEFAULT_REFRESH_FRAME_HEIGHT_DP);
    private int mBoundary = dip2px(DEFAULT_REFRESH_BOUNDARY);
    private int mFrameSpeed = DEFAULT_REFRESH_FRAME_SPEED;

    /**
     * 下拉动画容器
     */
    private ImageView mImgRefresh;
    /**
     * 下拉动画类
     */
    private AnimationDrawable mAnimationDrawable;
    /**
     * 上一次触摸Y轴位置
     */
    private float mLastY;
    /**
     * 能否下拉
     */
    private boolean mEnableRefresh = false;
    private boolean mIsFirstRun = true;

    public UpPullRefreshLayout(Context context) {
        super(context);
        init();
    }

    public UpPullRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.UpPullRefreshLayout);
        mFrameWidth = (int)typedArray.getDimension(R.styleable.UpPullRefreshLayout_frameWidth, mFrameWidth);
        mFrameHeight = (int)typedArray.getDimension(R.styleable.UpPullRefreshLayout_frameHeight, mFrameHeight);
        mBoundary = (int)typedArray.getDimension(R.styleable.UpPullRefreshLayout_refreshBoundary, mBoundary);
        mFrameSpeed = typedArray.getInt(R.styleable.UpPullRefreshLayout_frameSpeed, mFrameSpeed);
        typedArray.recycle();
        init();
    }

    private void init() {
        mImgRefresh = new ImageView(getContext());
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(mFrameWidth, mFrameHeight);
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        mImgRefresh.setLayoutParams(layoutParams);
        mImgRefresh.setBackgroundResource(R.drawable.ic_santa_claus1);

        addView(mImgRefresh, 0);
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (getChildCount() < 2) {
            return super.dispatchTouchEvent(ev);
        }

        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mEnableRefresh = false;
                mIsFirstRun = true;
                mLastY = ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
//                Log.d(TAG, "ACTION_MOVE mLastY = " + mLastY);
                View childView = getChildAt(1);
                if(mIsFirstRun) {
                    if (childView instanceof RecyclerView) {
                        RecyclerView.LayoutManager layoutManager = ((RecyclerView) getChildAt(1)).getLayoutManager();
                        if (layoutManager instanceof LinearLayoutManager) {
                            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
                            mEnableRefresh = linearLayoutManager.findFirstVisibleItemPosition() == 0;
                        }else if(layoutManager instanceof GridLayoutManager){
                            GridLayoutManager gridLayoutManager = (GridLayoutManager)layoutManager;
                            mEnableRefresh = gridLayoutManager.findFirstVisibleItemPosition() == 0;
                        }
                    } else if (childView instanceof ScrollView) {
                        ScrollView scrollView = (ScrollView) getChildAt(1);
                        mEnableRefresh = scrollView.getScrollY() == 0;
                    } else if (childView instanceof NestedScrollView) {
                        NestedScrollView nestedScrollView = (NestedScrollView) childView;
                        mEnableRefresh = nestedScrollView.getScrollY() == 0;
                    }
                    if (mEnableRefresh && mIsFirstRun) {
                        mIsFirstRun = false;
                        mLastY = ev.getRawY();
                    }
                }

                int deltaY = (int) (ev.getRawY() - mLastY) / 3;
                Log.d(TAG, "ACTION_MOVE: deltaY = " + deltaY);
                if (deltaY > 0 && mEnableRefresh) {
                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) childView.getLayoutParams();
                    layoutParams.topMargin = deltaY;
                    childView.setLayoutParams(layoutParams);

                    mImgRefresh.setBackgroundResource(REFRESH_FRAME_ARRAYS[deltaY / mFrameSpeed % REFRESH_FRAME_ARRAYS.length]);
                }
                break;
            case MotionEvent.ACTION_UP:
                final int upDeltaY = (int) (ev.getRawY() - mLastY) / 3;
                Log.d(TAG, "ACTION_UP upDeltaY = " + upDeltaY);
                if (upDeltaY > 0 && mEnableRefresh) {
                    int startValue;
                    int endValue;
                    if (upDeltaY >= mBoundary) {
                        startValue = upDeltaY;
                        endValue = mBoundary;
                    } else {
                        startValue = upDeltaY;
                        endValue = 0;
                    }

                    ValueAnimator animator = ValueAnimator.ofInt(startValue, endValue);
                    animator.setDuration(160);
                    animator.setTarget(getChildAt(1));
                    animator.setInterpolator(new DecelerateInterpolator());
                    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            int animatedValue = (int) animation.getAnimatedValue();
                            View childView = getChildAt(1);
                            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) childView.getLayoutParams();
                            layoutParams.topMargin = animatedValue;
                            childView.setLayoutParams(layoutParams);
                        }
                    });
                    animator.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            //超过刷新栏阀值时
                            if (upDeltaY >= mBoundary) {
                                mAnimationDrawable = new AnimationDrawable();
                                int length = REFRESH_FRAME_ARRAYS.length;
                                int index = upDeltaY / mFrameSpeed % length;
                                for (int i = 0; i < length; i++) {
                                    if (index == length) {
                                        index = 0;
                                    }
                                    mAnimationDrawable.addFrame(getResources().getDrawable(REFRESH_FRAME_ARRAYS[index++]), 100);
                                }
                                if (Build.VERSION.SDK_INT >= 16) {
                                    mImgRefresh.setBackground(mAnimationDrawable);
                                } else {
                                    mImgRefresh.setBackgroundDrawable(mAnimationDrawable);
                                }
                                mAnimationDrawable.start();
                                RxBus.getInstance().send(new PullRefreshFetchDataEvent(mBoundary, getChildAt(1)));
                            }
                        }
                    });
                    animator.start();
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                Log.d(TAG, "ACTION_CANCEL");
                break;
        }

        return super.dispatchTouchEvent(ev);
    }


    private int dip2px(float dipValue) {
        float scale = getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * 改变刷新状态，从数据刷新态到结束态
     * @param event
     */
    public void notifyRefreshStatusOnSuccess(PullRefreshFetchDataEvent event) {
        final int startValue = event.getRefreshBarHeight();
        final View bodyView = event.getBodyView();

        ValueAnimator animator = ValueAnimator.ofInt(startValue, 0);
        animator.setDuration(160);
        animator.setTarget(bodyView);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int animatedValue = (int) animation.getAnimatedValue();
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) bodyView.getLayoutParams();
                layoutParams.topMargin = animatedValue;
                bodyView.setLayoutParams(layoutParams);
            }
        });

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (mAnimationDrawable != null && mAnimationDrawable.isRunning()) {
                    mAnimationDrawable.stop();
                }
            }
        });

        animator.start();
    }

    public void setEnableRefresh(boolean enableRefresh) {
        this.mEnableRefresh = enableRefresh;
    }
}
