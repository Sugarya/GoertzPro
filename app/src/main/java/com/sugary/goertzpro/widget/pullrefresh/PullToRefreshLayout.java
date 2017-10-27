package com.sugary.goertzpro.widget.pullrefresh;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.sugary.goertzpro.R;
import com.sugary.goertzpro.utils.RxBus;


/**
 * Created by Ethan on 2017/10/6.
 * 下拉刷新分成五个状态：原始态，向下拉动态，刷新态，越过态，刷新向上推动态，向上推动态, （initial, pull, refreshing, overBoundary, refreshingPush,push）
 * 状态与状态之间通过动画链接；
 * 未使用嵌套滑动机制，也即一次触摸交互只能交由一个子控件全部消费
 * onInterceptTouchEvent（）决定事件由谁来处理，OnTouchEvent负责具体的消费逻辑
 * <p>
 * 需保证在xml里该布局只有一个子布局控件（后续可拓展，消除该限制）
 */
public class PullToRefreshLayout extends RelativeLayout {

    private static final String TAG = "PullToRefreshLayout";

    private static final int CHILD_VIEW_COUNT = 3;
    private static final int DEFAULT_REFRESH_BOUNDARY = 80;
    private static final int DEFAULT_REFRESH_FRAME_SPEED = 20;
    private static final int DEFAULT_REFRESH_BACKGROUND = Color.parseColor("#00ffffff");
    private static final int DEFAULT_REFRESH_TITLE_COLOR = Color.parseColor("#aaaaaa");


    /**
     * 刷新的底部界限
     */
    private int mBoundary = dip2px(DEFAULT_REFRESH_BOUNDARY);
    /**
     * 向下滑动时，动画切换的速率（非正相关）
     */
    private int mFrameSpeed = DEFAULT_REFRESH_FRAME_SPEED;

    /**
     * 当前所处的刷新状态
     */
    private RefreshStateEnum mCurrentRefreshStateEnum = RefreshStateEnum.INITIALIZE;

    /**
     * 下拉动画控件
     */
    private ImageView mImgRefresh;
    /**
     * 下拉动画控件背景色
     */
    private int mRefreshImgBackground;
    /**
     * 下拉动画控件宽高
     */
    private int mRefreshImgWidth;
    private int mRefreshImgHeight;
    /**
     * 刷新标题控件
     */
    private TextView mTvRefreshTitle;
    /**
     * 刷新标题颜色
     */
    private int mRefreshTitleColor;
    /**
     * 下拉动画类
     */
    private AnimationDrawable mRefreshAnimationDrawable;
    /**
     * 上一次触摸Y轴位置
     */
    private float mLastY;
    private float mLastX;
    /**
     * 能否下拉
     */
    private boolean mEnableRefresh = false;
    /**
     * 是否第一次运行
     */
    private boolean mIsFirstRun = true;

    public PullToRefreshLayout(Context context) {
        super(context);
        init();
        checkValid();
    }

    public PullToRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initTypeArray(context, attrs);
        init();
    }

    private void initTypeArray(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.PullToRefreshLayout);
        mRefreshImgWidth = (int) typedArray.getDimension(R.styleable.PullToRefreshLayout_refreshImgWidth, 0);
        mRefreshImgHeight = (int) typedArray.getDimension(R.styleable.PullToRefreshLayout_refreshImgHeight, 0);
        mBoundary = (int) typedArray.getDimension(R.styleable.PullToRefreshLayout_refreshBoundary, mBoundary);
        mFrameSpeed = typedArray.getInt(R.styleable.PullToRefreshLayout_frameSpeed, mFrameSpeed);
        mRefreshImgBackground = typedArray.getColor(R.styleable.PullToRefreshLayout_refreshBackground, DEFAULT_REFRESH_BACKGROUND);
        mRefreshTitleColor = typedArray.getColor(R.styleable.PullToRefreshLayout_refreshTextColor, DEFAULT_REFRESH_TITLE_COLOR);
        Drawable refreshDrawable = typedArray.getDrawable(R.styleable.PullToRefreshLayout_refreshDrawable);
        if (refreshDrawable != null) {
            if (refreshDrawable instanceof AnimationDrawable) {
                mRefreshAnimationDrawable = (AnimationDrawable) refreshDrawable;
            } else {
                throw new RuntimeException("android:refreshDrawable need to be filled into drawable xml which root label should be <animation-list>");
            }
        }
        typedArray.recycle();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        checkValid();
    }

    private void init() {
        initRefreshTitle();
        initRefreshImage();
    }

    private void checkValid() {
        int childCount = getChildCount();
        if (childCount > CHILD_VIEW_COUNT) {
            throw new RuntimeException("PullToRefreshLayout must have only one direct child view in XML");
        }
    }

    /**
     * 初始化设置刷新标题
     */
    private void initRefreshTitle() {
        mTvRefreshTitle = new TextView(getContext());
        LayoutParams operationLayoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        operationLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        operationLayoutParams.topMargin = -mBoundary / 2;
        mTvRefreshTitle.setLayoutParams(operationLayoutParams);
        mTvRefreshTitle.setTextColor(mRefreshTitleColor);
        mTvRefreshTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        mTvRefreshTitle.setText("下拉刷新");
        addView(mTvRefreshTitle, 0);
    }

    /**
     * 初始化设置刷新动画
     */
    private void initRefreshImage() {
        mImgRefresh = new ImageView(getContext());
        if (mRefreshImgWidth == 0) {
            mRefreshImgWidth = LayoutParams.MATCH_PARENT;
        }
        if (mRefreshImgHeight == 0) {
            mRefreshImgHeight = LayoutParams.WRAP_CONTENT;
        }
        LayoutParams imgLayoutParams = new LayoutParams(mRefreshImgWidth, mRefreshImgHeight);
        imgLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        mImgRefresh.setLayoutParams(imgLayoutParams);
        if (mRefreshAnimationDrawable != null) {
            mImgRefresh.setImageDrawable(mRefreshAnimationDrawable.getFrame(0));
        }
        mImgRefresh.setBackgroundColor(mRefreshImgBackground);
        mImgRefresh.setVisibility(INVISIBLE);
        addView(mImgRefresh, 0);
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
                mLastX = ev.getRawX();
                break;
            case MotionEvent.ACTION_MOVE:
//                Log.d(TAG, "onInterceptTouchEvent ACTION_MOVE");
                float deltaY = ev.getRawY() - mLastY;
                float deltaX = ev.getRawX() - mLastX;
                if (deltaY > 13 && deltaY > Math.abs(deltaX)) {
                    if (mIsFirstRun) {
//                        Log.d(TAG, "onInterceptTouchEvent mIsFirstRun = " + mIsFirstRun);
                        if (!(childView instanceof ViewGroup)) {
                            mEnableRefresh = checkRefreshEnable(childView);
                        } else {
                            ViewGroup viewGroup = (ViewGroup) childView;
                            int childCount = viewGroup.getChildCount();
                            for (int i = 0; i < childCount; i++) {
                                View view = viewGroup.getChildAt(i);
                                mEnableRefresh = checkRefreshEnable(view);
                                if (mEnableRefresh) {
                                    break;
                                }
                            }
                        }
                    }
                    if (mEnableRefresh && mIsFirstRun) {
                        mIsFirstRun = false;
                        mLastY = ev.getRawY();
                        setupVisible();
                    }
                }
//                Log.d(TAG, "onInterceptTouchEvent: mEnableRefresh = " + mEnableRefresh);
                return mEnableRefresh;
            case MotionEvent.ACTION_UP:
                mEnableRefresh = false;
                setupInvisible();
                break;
            case MotionEvent.ACTION_CANCEL:
                mEnableRefresh = false;
                setupInvisible();
                break;
        }

        return super.onInterceptTouchEvent(ev);
    }

    /**
     * 核对子控件是否
     *
     * @param childView
     * @return
     */
    private boolean checkRefreshEnable(View childView) {
        boolean result = false;

        if (childView instanceof RecyclerView) {
            RecyclerView.LayoutManager layoutManager = ((RecyclerView) childView).getLayoutManager();
            if (layoutManager instanceof LinearLayoutManager) {
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
                int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
//                Log.d(TAG, "onInterceptTouchEvent checkRefreshEnable: firstVisibleItemPosition = " + firstVisibleItemPosition);
                result = firstVisibleItemPosition == 0;
            }
        } else if (childView instanceof ScrollView) {
            ScrollView scrollView = (ScrollView) childView;
            result = scrollView.getScrollY() == 0;
        } else if (childView instanceof NestedScrollView) {
            NestedScrollView nestedScrollView = (NestedScrollView) childView;
            result = nestedScrollView.getScrollY() == 0;
        }

        return result;
    }


//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        super.onTouchEvent(event);
//        int action = event.getAction();
//        switch (action) {
//            case MotionEvent.ACTION_DOWN:
//                mLastY = event.getRawY();
//                break;
//            case MotionEvent.ACTION_MOVE:
//                int deltaY = (int) (event.getRawY() - mLastY) / 3;
//                Log.d(TAG, "ACTION_MOVE: deltaY = " + deltaY);
//                if (deltaY >= 0) {
//                    startPullState(deltaY);
//                }
//                break;
//            case MotionEvent.ACTION_UP:
//                final int upDeltaY = (int) (event.getRawY() - mLastY) / 3;
//                Log.d(TAG, "ACTION_UP upDeltaY = " + upDeltaY);
//                if (upDeltaY > 0 && mEnableRefresh) {
//                    reactionToMoveUp(upDeltaY);
//                }
//                break;
//            case MotionEvent.ACTION_CANCEL:
//                Log.d(TAG, "ACTION_CANCEL");
//                break;
//        }
//        return super.onTouchEvent(event);
//    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mEnableRefresh = false;
                mIsFirstRun = true;
                mLastY = event.getRawY();
                mLastX = event.getRawX();
                break;
            case MotionEvent.ACTION_MOVE:
                float deltaY = (event.getRawY() - mLastY) / 3;
                float deltaX = (event.getRawX() - mLastX) / 3;
                if (deltaY > 13 && deltaY > Math.abs(deltaX)) {
//                    Log.d(TAG, "onTouchEvent ACTION_MOVE 13");
                    if (mIsFirstRun) {
                        mEnableRefresh = true;
                        mIsFirstRun = false;
                        mLastY = event.getRawY();
                        setupVisible();
                    }
                    startPullState((int) deltaY);
                }
                break;
            case MotionEvent.ACTION_UP:
                final int upDeltaY = (int) (event.getRawY() - mLastY) / 3;
//                Log.d(TAG, "onTouchEvent ACTION_UP upDeltaY = " + upDeltaY);
                if (upDeltaY > 0 && mEnableRefresh) {
                    reactionToMoveUp(upDeltaY);
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
        }
        return true;
    }

    private void setupVisible() {
        mImgRefresh.setVisibility(VISIBLE);
        mTvRefreshTitle.setVisibility(VISIBLE);
    }

    private void setupInvisible() {
        mImgRefresh.setVisibility(INVISIBLE);
        mTvRefreshTitle.setVisibility(INVISIBLE);
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
     *
     * @param upDeltaY
     */
    private void startOverBoundaryState(final int upDeltaY) {
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
                mCurrentRefreshStateEnum = RefreshStateEnum.OVER_BOUNDARY;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                //Refreshing state
                if (upDeltaY >= mBoundary) {
                    mCurrentRefreshStateEnum = RefreshStateEnum.REFRESHING;
                    startRefreshingState(upDeltaY, childView);
                }
            }
        });
        animator.start();
    }

    private void startRefreshingState(int upDeltaY, View childView) {
        mTvRefreshTitle.setText("正在刷新");
        int length = mRefreshAnimationDrawable.getNumberOfFrames();
        int index = upDeltaY / mFrameSpeed % length;

        if (mRefreshAnimationDrawable != null) {
            mRefreshAnimationDrawable.stop();
            mRefreshAnimationDrawable.selectDrawable(index);
            mImgRefresh.setImageDrawable(mRefreshAnimationDrawable);
            mRefreshAnimationDrawable.start();
        }

        RxBus.getInstance().send(new RefreshingStateEvent(mBoundary, childView));
    }

    private synchronized void startPullState(int deltaY) {
        mCurrentRefreshStateEnum = RefreshStateEnum.PULL;
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

        int index = deltaY / mFrameSpeed % mRefreshAnimationDrawable.getNumberOfFrames();
        mImgRefresh.setImageDrawable(mRefreshAnimationDrawable.getFrame(index));
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
                mCurrentRefreshStateEnum = RefreshStateEnum.REFRESHING_PUSH;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (mRefreshAnimationDrawable != null) {
                    mRefreshAnimationDrawable.stop();
                }
                mCurrentRefreshStateEnum = RefreshStateEnum.INITIALIZE;
                mImgRefresh.setVisibility(GONE);
                mTvRefreshTitle.setVisibility(GONE);
            }
        });

        mTvRefreshTitle.setText("下拉刷新");
        animator.start();
    }

    private void startPushState(final int upDeltaY) {
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
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                mCurrentRefreshStateEnum = RefreshStateEnum.INITIALIZE;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mCurrentRefreshStateEnum = RefreshStateEnum.PUSH;
                mImgRefresh.setVisibility(GONE);
                mTvRefreshTitle.setVisibility(GONE);
            }
        });

        animator.start();
    }

    private int dip2px(float dipValue) {
        float scale = getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    //********************************************对外提供的方法


    /**
     * 开启刷新动画
     */
    public void startRefreshing() {
        if (mCurrentRefreshStateEnum != RefreshStateEnum.INITIALIZE) {
            return;
        }

        final int endValue = mBoundary + 40;
        ValueAnimator refreshAnimator = ValueAnimator.ofInt(0, endValue);
        refreshAnimator.setDuration(300);
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
                setupVisible();
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                reactionToMoveUp(endValue);
            }
        });

        refreshAnimator.start();
    }

    /**
     * 改变刷新状态，从数据刷新态到结束态
     * 该方法适用在刷新完成时做调用
     *
     * @param event
     */
    public void notifyRefreshOnSuccess(RefreshingStateEvent event) {
        if (event == null) {
            return;
        }
        final int startValue = event.getRefreshBarHeight();
        final View childView = event.getBodyView();
        startRefreshingPushState(startValue, childView);
    }

    public void completeRefresh() {
        View childView = getChildAt(CHILD_VIEW_COUNT - 1);
        startRefreshingPushState(mBoundary, childView);
    }


    public void setEnableRefresh(boolean enableRefresh) {
        this.mEnableRefresh = enableRefresh;
    }

    public AnimationDrawable getRefreshAnimationDrawable() {
        return mRefreshAnimationDrawable;
    }

    public void setRefreshAnimationDrawable(AnimationDrawable refreshAnimationDrawable) {
        if (refreshAnimationDrawable == null) {
            return;
        }
        mRefreshAnimationDrawable = refreshAnimationDrawable;
        mImgRefresh.setImageDrawable(mRefreshAnimationDrawable.getFrame(0));
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
