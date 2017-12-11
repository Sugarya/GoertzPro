package com.sugary.goertzpro.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.text.method.ScrollingMovementMethod;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

/**
 * Created by Ethan on 2017/12/12.
 * 支持嵌套滚动的TextView
 */

public class ScrollTextView extends AppCompatTextView {

    private static final String TAG = "ScrollTextView";

    public ScrollTextView(Context context) {
        super(context);
        init();
    }

    public ScrollTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init(){
        setMovementMethod(ScrollingMovementMethod.getInstance());
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        int height = getMeasuredHeight();
        int maxHeight = getMaxHeight();
        Log.d(TAG, "onMeasure: maxHeight = " + maxHeight + "  height = " + height);
        if(height >= maxHeight){
            getParent().requestDisallowInterceptTouchEvent(true);
        }else{
            getParent().requestDisallowInterceptTouchEvent(false);
        }
        return super.dispatchTouchEvent(event);
    }

}
