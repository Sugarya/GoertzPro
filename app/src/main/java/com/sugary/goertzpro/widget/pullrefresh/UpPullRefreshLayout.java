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
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.sugary.goertzpro.R;
import com.sugary.goertzpro.utils.RxBus;

/**
 * Created by Ethan on 2017/10/6.
 * 下拉刷新分成四个状态：原始态，向下拉动态，刷新态，越过态，刷新向上推动态，向上推动态, （initial, pull, refreshing, overBoundary,refreshingPush,push）
 * 状态与状态之间通过动画链接；下拉操作改变原始态到数据刷新态，或者回到原始态
 * 未使用嵌套滑动机制，也即一次触摸交互只能由一个控件完全消费
 * onInterceptTouchEvent（）决定事件由谁来处理，OnTouchEvent负责具体的消费逻辑
 */
public class UpPullRefreshLayout extends RelativeLayout {

    private static final String TAG = "UpPullRefreshLayout";

    private
    @IdRes
    int[] mRefreshFrameArrays = {
            R.drawable.ic_gold_00001,
            R.drawable.ic_gold_00002,
            R.drawable.ic_gold_00003,
            R.drawable.ic_gold_00004,
            R.drawable.ic_gold_00005,
            R.drawable.ic_gold_00006,
            R.drawable.ic_gold_00007,
            R.drawable.ic_gold_00008,
            R.drawable.ic_gold_00009,
            R.drawable.ic_gold_00010,
            R.drawable.ic_gold_00011,
            R.drawable.ic_gold_00012,
            R.drawable.ic_gold_00013,
            R.drawable.ic_gold_00014,
            R.drawable.ic_gold_00015,
            R.drawable.ic_gold_00016,
            R.drawable.ic_gold_00017,
            R.drawable.ic_gold_00018,
            R.drawable.ic_gold_00019
    };

    private static final int CHILD_VIEW_COUNT = 3;
    private static final int DEFAULT_REFRESH_FRAME_WIDTH_DP = 70;
    private static final int DEFAULT_REFRESH_FRAME_HEIGHT_DP = 70;
    private static final int DEFAULT_REFRESH_BOUNDARY = 80;
    private static final int DEFAULT_REFRESH_FRAME_SPEED = 20;

    private RefreshStateEnum mRefreshStateEnum = RefreshStateEnum.INITIALIZE;
    private int mFrameWidth;
    private int mFrameHeight;
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
        mFrameWidth = (int) typedArray.getDimension(R.styleable.UpPullRefreshLayout_frameWidth, 0);
        mFrameHeight = (int) typedArray.getDimension(R.styleable.UpPullRefreshLayout_frameHeight, 0);
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
        mTvRefreshTitle.setTextColor(Color.parseColor("#999999"));
        mTvRefreshTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        mTvRefreshTitle.setText("下拉刷新");
        addView(mTvRefreshTitle, 0);

        mImgRefresh = new ImageView(getContext());

        if (mFrameWidth == 0) {
            mFrameWidth = LayoutParams.MATCH_PARENT;
        }
        if (mFrameHeight == 0) {
            mFrameHeight = LayoutParams.WRAP_CONTENT;
        }
        RelativeLayout.LayoutParams imgLayoutParams = new RelativeLayout.LayoutParams(mFrameWidth, mFrameHeight);
        imgLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        mImgRefresh.setLayoutParams(imgLayoutParams);
        mImgRefresh.setBackgroundResource(mRefreshFrameArrays[0]);
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
                if (deltaY > 13) {
                    if (mIsFirstRun) {
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
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mLastY = event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                int deltaY = (int) (event.getRawY() - mLastY) / 3;
                Log.d(TAG, "ACTION_MOVE: deltaY = " + deltaY);
                if (deltaY >= 0) {
                    startPullState(deltaY);
                }
                break;
            case MotionEvent.ACTION_UP:
                final int upDeltaY = (int) (event.getRawY() - mLastY) / 3;
                Log.d(TAG, "ACTION_UP upDeltaY = " + upDeltaY);
                if (upDeltaY > 0 && mEnableRefresh) {
                    reactionToMoveUp(upDeltaY);
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                Log.d(TAG, "ACTION_CANCEL");
                break;
        }
        return super.onTouchEvent(event);
    }

    private void startPushState(final int upDeltaY){
        final View childView = getChildAt(CHILD_VIEW_COUNT - 1);
        int startValue = upDeltaY;
        int endValue = 0;

        ValueAnimator animator = ValueAnimator.ofInt(startValue, endValue);
        animator.setDuration(160);
        animator.setTarget(childView);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int animatedValue = (int) animation.getAnimatedValue();
                LayoutParams layoutParams = (LayoutParams) childView.getLayoutParams();
                layoutParams.topMargin = animatedValue;
                childView.setLayoutParams(layoutParams);

                //刷新标题
                LayoutParams titleLayoutParams = (LayoutParams) mTvRefreshTitle.getLayoutParams();
                titleLayoutParams.topMargin = -mBoundary / 2 + animatedValue;
                mTvRefreshTitle.setLayoutParams(titleLayoutParams);
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mRefreshStateEnum = RefreshStateEnum.PUSH;
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                mRefreshStateEnum = RefreshStateEnum.INITIALIZE;
            }
        });

        animator.start();
    }

    private void reactionToMoveUp(final int upDeltaY) {
        if (upDeltaY >= mBoundary) {
            startOverBoundaryState(upDeltaY);
        } else {
            startPushState(upDeltaY);
        }
    }

    /**
     * 开启越过态
     * @param upDeltaY
     */
    private void startOverBoundaryState(final int upDeltaY){
        final View childView = getChildAt(CHILD_VIEW_COUNT - 1);
        int startValue = upDeltaY;
        int endValue = mBoundary;

        ValueAnimator animator = ValueAnimator.ofInt(startValue, endValue);
        animator.setDuration(160);
        animator.setTarget(childView);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int animatedValue = (int) animation.getAnimatedValue();
                LayoutParams layoutParams = (LayoutParams) childView.getLayoutParams();
                layoutParams.topMargin = animatedValue;
                childView.setLayoutParams(layoutParams);

                //刷新标题
                LayoutParams titleLayoutParams = (LayoutParams) mTvRefreshTitle.getLayoutParams();
                titleLayoutParams.topMargin = -mBoundary / 2 + animatedValue;
                mTvRefreshTitle.setLayoutParams(titleLayoutParams);
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                mRefreshStateEnum = RefreshStateEnum.OVER_BOUNDARY;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                //Refreshing state
                if (upDeltaY >= mBoundary) {
                    mRefreshStateEnum = RefreshStateEnum.REFRESHING;
                    startRefreshingState(upDeltaY, childView);
                }
            }
        });
        animator.start();
    }

    private void startRefreshingState(int upDeltaY, View childView) {
        mTvRefreshTitle.setText("正在刷新");
        mAnimationDrawable = new AnimationDrawable();
        int length = mRefreshFrameArrays.length;
        int index = upDeltaY / mFrameSpeed % length;
        for (int i = 0; i < length; i++) {
            if (index == length) {
                index = 0;
            }
            mAnimationDrawable.addFrame(getResources().getDrawable(mRefreshFrameArrays[index++]), 100);
        }
        if (Build.VERSION.SDK_INT >= 16) {
            mImgRefresh.setBackground(mAnimationDrawable);
        } else {
            mImgRefresh.setBackgroundDrawable(mAnimationDrawable);
        }
        mAnimationDrawable.start();
        RxBus.getInstance().send(new PullDownRefreshDataEvent(mBoundary, childView));
    }


    private void reactionToMoveUp2(final int upDeltaY) {
        final View childView = getChildAt(CHILD_VIEW_COUNT - 1);
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
                LayoutParams layoutParams = (LayoutParams) childView.getLayoutParams();
                layoutParams.topMargin = animatedValue;
                childView.setLayoutParams(layoutParams);

                //刷新标题
                LayoutParams titleLayoutParams = (LayoutParams) mTvRefreshTitle.getLayoutParams();
                titleLayoutParams.topMargin = -mBoundary / 2 + animatedValue;
                mTvRefreshTitle.setLayoutParams(titleLayoutParams);
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                //超过刷新栏界限时 overRefreshing state
                if (upDeltaY >= mBoundary) {
                    mTvRefreshTitle.setText("正在刷新");
                    mAnimationDrawable = new AnimationDrawable();
                    int length = mRefreshFrameArrays.length;
                    int index = upDeltaY / mFrameSpeed % length;
                    for (int i = 0; i < length; i++) {
                        if (index == length) {
                            index = 0;
                        }
                        mAnimationDrawable.addFrame(getResources().getDrawable(mRefreshFrameArrays[index++]), 100);
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

    private synchronized void startPullState(int deltaY) {
        mRefreshStateEnum = RefreshStateEnum.PULL;
        View childView = getChildAt(CHILD_VIEW_COUNT - 1);
        LayoutParams childLayoutParams = (LayoutParams) childView.getLayoutParams();
        childLayoutParams.topMargin = deltaY;
        childView.setLayoutParams(childLayoutParams);

        if (deltaY < mBoundary) {
            mTvRefreshTitle.setText("下拉刷新");
        } else {
            mTvRefreshTitle.setText("松开刷新");
        }
        LayoutParams titleLayoutParams = (LayoutParams) mTvRefreshTitle.getLayoutParams();
        titleLayoutParams.topMargin = -mBoundary / 2 + deltaY;
        mTvRefreshTitle.setLayoutParams(titleLayoutParams);

        int index = deltaY / mFrameSpeed % mRefreshFrameArrays.length;
        Log.d(TAG, "startPullState: index = " + index);
        mImgRefresh.setBackgroundResource(mRefreshFrameArrays[index]);
    }

    private void startRefreshingPushState(int startValue, final View childView) {
        ValueAnimator animator = ValueAnimator.ofInt(startValue, 0);
        animator.setDuration(160);
        animator.setTarget(childView);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int animatedValue = (int) animation.getAnimatedValue();
                LayoutParams layoutParams = (LayoutParams) childView.getLayoutParams();
                layoutParams.topMargin = animatedValue;
                childView.setLayoutParams(layoutParams);
            }
        });

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                mRefreshStateEnum = RefreshStateEnum.REFRESHING_PUSH;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mRefreshStateEnum = RefreshStateEnum.INITIALIZE;
                if (mAnimationDrawable != null && mAnimationDrawable.isRunning()) {
                    mAnimationDrawable.stop();
                }
            }
        });

        mTvRefreshTitle.setText("下拉刷新");
        animator.start();
    }

    private int dip2px(float dipValue) {
        float scale = getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    //********************************************对外提供的方法


    /**
     * 改变刷新状态，从数据刷新态到结束态
     * 该方法适用在刷新完成时做调用
     *
     * @param event
     */
    public void notifyRefreshStatusOnSuccess(PullDownRefreshDataEvent event) {
        final int startValue = event.getRefreshBarHeight();
        final View childView = event.getBodyView();
        startRefreshingPushState(startValue, childView);
    }

    /**
     * 开启刷新动画
     */
    public void startRefreshing() {
        if(mRefreshStateEnum != RefreshStateEnum.INITIALIZE){
            return;
        }

        final int endValue = mBoundary + 60;
        ValueAnimator refreshAnimator = ValueAnimator.ofInt(0, endValue);
        refreshAnimator.setDuration(600);
        refreshAnimator.setInterpolator(new DecelerateInterpolator());
        refreshAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int delta = (Integer) animation.getAnimatedValue();
                Log.d(TAG, "onAnimationUpdate: delta = " + delta);
                startPullState(delta);
            }
        });
        refreshAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                mImgRefresh.setVisibility(VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                reactionToMoveUp(endValue);
            }
        });

        refreshAnimator.start();
    }

    public void completeRefresh() {
        View childView = getChildAt(CHILD_VIEW_COUNT - 1);
        startRefreshingPushState(mBoundary, childView);
    }

    public int[] getRefreshFrameArrays() {
        return mRefreshFrameArrays;
    }

    public void setRefreshFrameArrays(int[] refreshFrameArrays) {
        mRefreshFrameArrays = refreshFrameArrays;
    }

    public void setEnableRefresh(boolean enableRefresh) {
        this.mEnableRefresh = enableRefresh;
    }

    /**
     * 上拉刷新的状态枚举
     */
    enum RefreshStateEnum {
        /**
         * 初始态
         */
        INITIALIZE,
        /**
         * 向下拉动态
         */
        PULL,
        /**
         * 刷新态
         */
        REFRESHING,
        /**
         * 越过态
         */
        OVER_BOUNDARY,

        /**
         * 处在刷新态向上推送态
         */
        REFRESHING_PUSH,

        /**
         * 向上推动态
         */
        PUSH

    }
}



