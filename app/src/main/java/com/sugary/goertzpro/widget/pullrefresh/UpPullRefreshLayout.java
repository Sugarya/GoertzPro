package com.sugary.goertzpro.widget.pullrefresh;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.support.annotation.IdRes;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.sugary.goertzpro.R;
import com.sugary.goertzpro.utils.RxBus;

/**
 * Created by Ethan on 2017/10/6.
 * <p>
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

    private static final int CHILD_VIEW_COUNT = 3;
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
    private TextView mTvRefreshTitle;

    public UpPullRefreshLayout(Context context) {
        super(context);
        init();
    }

    public UpPullRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.UpPullRefreshLayout);
        mFrameWidth = (int) typedArray.getDimension(R.styleable.UpPullRefreshLayout_frameWidth, mFrameWidth);
        mFrameHeight = (int) typedArray.getDimension(R.styleable.UpPullRefreshLayout_frameHeight, mFrameHeight);
        mBoundary = (int) typedArray.getDimension(R.styleable.UpPullRefreshLayout_refreshBoundary, mBoundary);
        mFrameSpeed = typedArray.getInt(R.styleable.UpPullRefreshLayout_frameSpeed, mFrameSpeed);
        typedArray.recycle();
        init();
    }

    private void init() {
        mTvRefreshTitle = new TextView(getContext());
        RelativeLayout.LayoutParams operationLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        operationLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        operationLayoutParams.topMargin = -mBoundary / 2;
        mTvRefreshTitle.setLayoutParams(operationLayoutParams);
        mTvRefreshTitle.setTextColor(Color.parseColor("#666666"));
        mTvRefreshTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        mTvRefreshTitle.setText("下拉刷新");
        addView(mTvRefreshTitle, 0);

        mImgRefresh = new ImageView(getContext());
        RelativeLayout.LayoutParams imgLayoutParams = new RelativeLayout.LayoutParams(mFrameWidth, mFrameHeight);
        imgLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        mImgRefresh.setLayoutParams(imgLayoutParams);
        mImgRefresh.setBackgroundResource(R.drawable.ic_santa_claus1);
        mImgRefresh.setVisibility(INVISIBLE);
        addView(mImgRefresh, 0);
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final View childView = getChildAt(CHILD_VIEW_COUNT - 1);
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mEnableRefresh = false;
                mIsFirstRun = true;
                mLastY = ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                float deltaY = ev.getRawY() - mLastY;
                if(deltaY > 0){
                    if (mIsFirstRun ) {
                        if (childView instanceof RecyclerView) {
                            RecyclerView.LayoutManager layoutManager = ((RecyclerView) childView).getLayoutManager();
                            if (layoutManager instanceof LinearLayoutManager) {
                                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
                                mEnableRefresh = linearLayoutManager.findFirstVisibleItemPosition() == 0;
                            } else if (layoutManager instanceof GridLayoutManager) {
                                GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
                                mEnableRefresh = gridLayoutManager.findFirstVisibleItemPosition() == 0;
                            }
                        } else if (childView instanceof ScrollView) {
                            ScrollView scrollView = (ScrollView) childView;
                            mEnableRefresh = scrollView.getScrollY() == 0;
                        } else if (childView instanceof NestedScrollView) {
                            NestedScrollView nestedScrollView = (NestedScrollView) childView;
                            mEnableRefresh = nestedScrollView.getScrollY() == 0;
                        }

                        if (mEnableRefresh && mIsFirstRun) {
                            mIsFirstRun = false;
                            mLastY = ev.getRawY();
                            mImgRefresh.setVisibility(VISIBLE);
                        }
                    }
                }
                Log.d(TAG, "onInterceptTouchEvent: mEnableRefresh = " + mEnableRefresh);
                return mEnableRefresh;
            case MotionEvent.ACTION_UP:
                mEnableRefresh = false;
                mImgRefresh.setVisibility(INVISIBLE);
                break;
            case MotionEvent.ACTION_CANCEL:
                mEnableRefresh = false;
                mImgRefresh.setVisibility(INVISIBLE);
                break;
        }

        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final View childView = getChildAt(CHILD_VIEW_COUNT - 1);
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mLastY = event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                int deltaY = (int) (event.getRawY() - mLastY) / 3;
                Log.d(TAG, "ACTION_MOVE: deltaY = " + deltaY);
                if(deltaY >= 0) {
                    RelativeLayout.LayoutParams childLayoutParams = (RelativeLayout.LayoutParams) childView.getLayoutParams();
                    childLayoutParams.topMargin = deltaY;
                    childView.setLayoutParams(childLayoutParams);

                    if (deltaY < mBoundary) {
                        mTvRefreshTitle.setText("下拉刷新");
                    } else {
                        mTvRefreshTitle.setText("松开刷新");
                    }
                    RelativeLayout.LayoutParams titleLayoutParams = (RelativeLayout.LayoutParams) mTvRefreshTitle.getLayoutParams();
                    titleLayoutParams.topMargin = -mBoundary / 2 + deltaY;
                    mTvRefreshTitle.setLayoutParams(titleLayoutParams);

                    mImgRefresh.setBackgroundResource(REFRESH_FRAME_ARRAYS[deltaY / mFrameSpeed % REFRESH_FRAME_ARRAYS.length]);
                }
                break;
            case MotionEvent.ACTION_UP:
                final int upDeltaY = (int) (event.getRawY() - mLastY) / 3;
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
                    animator.setTarget(childView);
                    animator.setInterpolator(new DecelerateInterpolator());
                    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            int animatedValue = (int) animation.getAnimatedValue();
                            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) childView.getLayoutParams();
                            layoutParams.topMargin = animatedValue;
                            childView.setLayoutParams(layoutParams);

                            RelativeLayout.LayoutParams titleLayoutParams = (RelativeLayout.LayoutParams) mTvRefreshTitle.getLayoutParams();
                            titleLayoutParams.topMargin = -mBoundary / 2 + animatedValue;
                            mTvRefreshTitle.setLayoutParams(titleLayoutParams);
                        }
                    });
                    animator.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            //超过刷新栏界限时
                            if (upDeltaY >= mBoundary) {
                                mTvRefreshTitle.setText("正在刷新");
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
                                RxBus.getInstance().send(new PullDownRefreshDataEvent(mBoundary, childView));
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
        return super.onTouchEvent(event);
    }

    private int dip2px(float dipValue) {
        float scale = getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * 改变刷新状态，从数据刷新态到结束态
     *
     * @param event
     */
    public void notifyRefreshStatusOnSuccess(PullDownRefreshDataEvent event) {
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

        mTvRefreshTitle.setText("下拉刷新");
        animator.start();
    }

    public void setEnableRefresh(boolean enableRefresh) {
        this.mEnableRefresh = enableRefresh;
    }
}
