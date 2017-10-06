package com.sugary.goertzpro.widget.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.sugary.goertzpro.R;

/**
 * Created by Ethan on 2017/8/28.
 */

public class ShadowLayout extends FrameLayout {
    /**
     * 阴影颜色
     **/
    private int mShadowColor;
    /**
     * 阴影范围大小
     **/
    private float mShadowRadius;
    /**
     * 阴影圆角光滑度
     **/
    private float mCornerRadius;
    /**
     * 阴影偏离原位置x坐标多少
     **/
    private float mDx;
    /**
     * 阴影偏离原位置y坐标多少
     **/
    private float mDy;

    private int w;
    private int h;
    private boolean isChange = true;

    public ShadowLayout(Context context) {
        super(context);
        initView(context, null);
    }

    public ShadowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public ShadowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        initAttributes(context, attrs);
        //x偏离量
        int xPadding = (int) (mShadowRadius + Math.abs(mDx));
        //偏离量
        int yPadding = (int) (mShadowRadius + Math.abs(mDy));
        //设置偏离量，分别为left,top,right,bottom
        setPadding(xPadding, yPadding, xPadding, yPadding);
    }

    /**
     * 获取TypedArray
     *
     * @return
     * @paramcontext
     * @paramattributeSet
     * @paramattr
     */
    private TypedArray getTypedArray(Context context, AttributeSet attributeSet, int[] attr) {
        return context.obtainStyledAttributes(attributeSet, attr, 0, 0);
    }

    /**
     * 初始化 initAttributes
     *
     * @paramcontext
     * @paramattrs
     */
    private void initAttributes(Context context, AttributeSet attrs) {
        TypedArray attr = getTypedArray(context, attrs, R.styleable.ShadowLayout);
        if (attr == null) {
            return;
        }
        try {
            mCornerRadius = attr.getDimension(R.styleable.ShadowLayout_sl_cornerRadius, 4f);
            mShadowRadius = attr.getDimension(R.styleable.ShadowLayout_sl_shadowRadius, 4f);
            mDx = attr.getDimension(R.styleable.ShadowLayout_sl_dx, 0);
            mDy = attr.getDimension(R.styleable.ShadowLayout_sl_dy, 0);
            if (!isChange) {
                mShadowColor = attr.getColor(R.styleable.ShadowLayout_sl_shadowColor, getResources().getColor(android.R.color.transparent));
            }
        } finally {
            attr.recycle();
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w > 0 && h > 0) {
            this.w = w;
            this.h = h;
            setBackgroundCompat(w, h);
        }
    }

    @SuppressWarnings("deprecation")
    private void setBackgroundCompat(int w, int h) {
        Bitmap bitmap = createShadowBitmap(w, h, mCornerRadius, mShadowRadius, mDx, mDy, mShadowColor, Color.TRANSPARENT);
        BitmapDrawable drawable = new BitmapDrawable(getResources(), bitmap);
        //判断版本，设置背景
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN) {
            setBackgroundDrawable(drawable);
        } else {
            setBackground(drawable);
        }
    }

    /**
     * 产生阴影Bitmap
     *
     * @return
     * @paramshadowWidth
     * @paramshadowHeight
     * @paramcornerRadius
     * @paramshadowRadius
     * @paramdx
     * @paramdy
     * @paramshadowColor
     * @paramfillColor
     */
    private Bitmap createShadowBitmap(int shadowWidth, int shadowHeight, float cornerRadius, float shadowRadius,
                                      float dx, float dy, int shadowColor, int fillColor) {
        Bitmap output = Bitmap.createBitmap(shadowWidth, shadowHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        RectF shadowRect = new RectF(
                shadowRadius,
                shadowRadius,
                shadowWidth - shadowRadius,
                shadowHeight - shadowRadius);
        if (dy > 0) {
            shadowRect.top += dy;
            shadowRect.bottom -= dy;
        } else if (dy < 0) {
            shadowRect.top += Math.abs(dy);
            shadowRect.bottom -= Math.abs(dy);
        }
        if (dx > 0) {
            shadowRect.left += dx;
            shadowRect.right -= dx;
        } else if (dx < 0) {
            shadowRect.left += Math.abs(dx);
            shadowRect.right -= Math.abs(dx);
        }
        if (dx == 0) {
            shadowRect.left = shadowRect.left + 5;
            shadowRect.right = shadowRect.right - 5;
        }
        Paint shadowPaint = new Paint();
        shadowPaint.setAntiAlias(true);
        shadowPaint.setColor(fillColor);
        shadowPaint.setStyle(Paint.Style.FILL);
        if (!isInEditMode()) {
            setLayerType(LAYER_TYPE_SOFTWARE, null);
            shadowPaint.setShadowLayer(shadowRadius, dx, dy, shadowColor);
        }
        canvas.drawRoundRect(shadowRect, cornerRadius, cornerRadius, shadowPaint);
        return output;
    }

    public void setShadowColor(int id) {
        isChange = true;
        mShadowColor = id;
    }

    public void reDraw(int id) {
        mShadowColor = id;
        if (w > 0 && h > 0) {
            setBackgroundCompat(w, h);
        }
    }
}
