package com.sugary.goertzpro.utils;

import android.content.Context;
/**
 * Created by Ethan on 2017/8/15.
 */

public class DensityUtil {
    public static int dip2px(Context context,float dipValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }
}
