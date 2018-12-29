package com.sugary.goertzpro.utils;

import android.content.Context;
import android.util.TypedValue;

/**
 * Created by Ethan on 2017/8/15.
 *
 *
 * 另一个方法，由系统提供的方法替换  TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 7f, resources.displayMetrics)
 */
public class DensityUtil {
    public static int dip2px(Context context,float dipValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static float dip2px2(Context context, float dipValue){
       return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, context.getResources().getDisplayMetrics());
    }


}
