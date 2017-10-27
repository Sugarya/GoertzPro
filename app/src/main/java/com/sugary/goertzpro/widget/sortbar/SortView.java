package com.sugary.goertzpro.widget.sortbar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import static com.sugary.goertzpro.widget.sortbar.IndicatorStatusEnum.INITIAL;
import static com.sugary.goertzpro.widget.sortbar.IndicatorStatusEnum.UNDER_CHECKED;
import static com.sugary.goertzpro.widget.sortbar.IndicatorStatusEnum.UPWARD_CHECKED;

/**
 * Created by Ethan on 2017/10/24.
 * 排序的图标
 */

public class SortView extends View {

    private static final String TAG = "SortIndicator";

//    private static final int DEFAULT_INITIAL_TRIANGLE_COLOR = Color.parseColor("#cccccc");
//    private static final int DEFAULT_SELECTED_TRIANGLE_COLOR = Color.parseColor("#ff0000");

    private IndicatorStatusEnum mStatusEnum = INITIAL;
    private Paint mPaintUpward;
    private Paint mPaintUnder;
    private Path mPathUpward;
    private Path mPathUnder;

    private int mTriangleColor;
    private int mTriangleSelectedColor;

    public SortView(Context context) {
        super(context);
        init();
    }

    public SortView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setVisibility(GONE);
        mPathUpward = new Path();
        mPaintUpward = new Paint();
//        mPaintUpward.setColor(mTriangleColor);
        mPaintUpward.setStyle(Paint.Style.FILL);
        mPaintUpward.setAntiAlias(true);

        mPathUnder = new Path();
        mPaintUnder = new Paint();
//        mPaintUnder.setColor(mTriangleColor);
        mPaintUnder.setStyle(Paint.Style.FILL);
        mPaintUnder.setAntiAlias(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int peakX = widthSize / 2;
        int upPointY = heightSize * 3 / 8;
        int underPointY = heightSize * 5 / 8;

        mPathUpward.reset();
        mPathUpward.moveTo(peakX, 0);
        mPathUpward.lineTo(0, upPointY);
        mPathUpward.lineTo(widthSize, upPointY);

        mPathUnder.reset();
        mPathUnder.moveTo(peakX, heightSize);
        mPathUnder.lineTo(0, underPointY);
        mPathUnder.lineTo(widthSize, underPointY);
        Log.d(TAG, "onMeasure: widthSize = " + widthSize + " heightSize = " + heightSize);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        Log.d(TAG, "onLayout: left = " + left + " top = " + top);
        Log.d(TAG, "onLayout: right = " + right + " bottom = " + bottom);

    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        switch (mStatusEnum){
            case INITIAL:
                mPaintUpward.setColor(mTriangleColor);
                mPaintUnder.setColor(mTriangleColor);
                break;
            case UPWARD_CHECKED:
                mPaintUpward.setColor(mTriangleSelectedColor);
                mPaintUnder.setColor(mTriangleColor);
                break;
            case UNDER_CHECKED:
                mPaintUpward.setColor(mTriangleColor);
                mPaintUnder.setColor(mTriangleSelectedColor);
                break;
            default:
                mPaintUpward.setColor(mTriangleColor);
                mPaintUnder.setColor(mTriangleColor);
        }
        canvas.drawPath(mPathUpward, mPaintUpward);
        canvas.drawPath(mPathUnder, mPaintUnder);
    }

    //***************************************对外提供的方法


    public void checkUpwardTriangle(){
        setVisibility(VISIBLE);
        mStatusEnum = UPWARD_CHECKED;
        postInvalidate();
    }

    public void checkUnderTriangle(){
        setVisibility(VISIBLE);
        mStatusEnum = UNDER_CHECKED;
        postInvalidate();
    }

    public void checkInitialTriangle(){
        setVisibility(VISIBLE);
        mStatusEnum = INITIAL;
        postInvalidate();
    }

    public void checkTriangle(IndicatorStatusEnum statusEnum){
        if(statusEnum == null){
            return;
        }
        setVisibility(VISIBLE);
        mStatusEnum = statusEnum;
        postInvalidate();
    }

    public void checkTriangle(){
        if(mStatusEnum == null){
            return;
        }
        setVisibility(VISIBLE);
        postInvalidate();
    }

    public void toggleUpward(){
        if(mStatusEnum == null){
            return;
        }

        switch (mStatusEnum){
            case INITIAL:
                mStatusEnum = UPWARD_CHECKED;
                checkTriangle(UPWARD_CHECKED);
                break;
            case UPWARD_CHECKED:
                mStatusEnum = UNDER_CHECKED;
                checkTriangle(UNDER_CHECKED);
                break;
            case UNDER_CHECKED:
                mStatusEnum = UPWARD_CHECKED;
                checkTriangle(UPWARD_CHECKED);
                break;
            default:
                mStatusEnum = INITIAL;
                checkTriangle(INITIAL);
        }
    }

    public void toggleUnder(){
        if(mStatusEnum == null){
            return;
        }

        switch (mStatusEnum){
            case INITIAL:
                mStatusEnum = UNDER_CHECKED;
                checkTriangle(UNDER_CHECKED);
                break;
            case UPWARD_CHECKED:
                mStatusEnum = UNDER_CHECKED;
                checkTriangle(UNDER_CHECKED);
                break;
            case UNDER_CHECKED:
                mStatusEnum = UPWARD_CHECKED;
                checkTriangle(UPWARD_CHECKED);
                break;
            default:
                mStatusEnum = INITIAL;
                checkTriangle(INITIAL);

        }
    }

    public void toggle(){
        if(mStatusEnum == null){
            return;
        }

        switch (mStatusEnum){
            case UPWARD_CHECKED:
                mStatusEnum = UNDER_CHECKED;
                checkTriangle(UNDER_CHECKED);
                break;
            case UNDER_CHECKED:
                mStatusEnum = UPWARD_CHECKED;
                checkTriangle(UPWARD_CHECKED);
                break;
        }
    }

    public IndicatorStatusEnum getStatusEnum() {
        return mStatusEnum;
    }

    public void setTriangleColor(int triangleColor) {
        mTriangleColor = triangleColor;
    }

    public void setTriangleSelectedColor(int triangleSelectedColor) {
        mTriangleSelectedColor = triangleSelectedColor;
    }
}
