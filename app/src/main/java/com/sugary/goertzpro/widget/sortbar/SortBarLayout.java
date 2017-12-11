package com.sugary.goertzpro.widget.sortbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.sugary.goertzpro.R;

import java.util.List;

import static android.view.Gravity.CENTER;

/**
 * Created by Ethan on 2017/10/24.
 * 排序条
 */

public class SortBarLayout extends RelativeLayout {

    private static final String TAG = "SortBarLayout";

    private static final int DEFAULT_INITIAL_TRIANGLE_COLOR = Color.parseColor("#cccccc");
    private static final int DEFAULT_CHECK_TRIANGLE_COLOR = Color.parseColor("#ff0000");
    private static final int DEFAULT_TXT_COLOR = Color.parseColor("#333333");
    private static final int DEFAULT_SELECTED_TXT_COLOR = Color.parseColor("#E4393C");
    private static final int DEFAULT_LINE_COLOR = Color.parseColor("#e7e7e7");
    private static final int DEFAULT_TXT_SIZE_SP = 15;
    private static final int DEFAULT_INDICATOR_LEFT_MARGIN_DP = 3;

    private int mTriangleColor = DEFAULT_INITIAL_TRIANGLE_COLOR;
    private int mTriangleSelectedColor = DEFAULT_CHECK_TRIANGLE_COLOR;
    private int mTriangleLeftMargin = dip2px(DEFAULT_INDICATOR_LEFT_MARGIN_DP);
    private int mTxtSize = dip2px(DEFAULT_TXT_SIZE_SP);
    private int mTxtColor = DEFAULT_TXT_COLOR;
    private int mTxtSelectedColor = DEFAULT_SELECTED_TXT_COLOR;
    private int mLineColor = DEFAULT_LINE_COLOR;

    /**
     * 排序条上方容器布局
     */
    private LinearLayout mContainerUnitLayout;
    /**
     * 底部指示器条
     */
    private LinearLayout mIndicatorLayout;
    private SortBarUnit mLastSortBarUnit;
    private OnItemClickListener mOnItemClickListener;

    public SortBarLayout(Context context) {
        super(context);
        init();
    }

    public SortBarLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SortBarLayout);
        mTxtColor = typedArray.getColor(R.styleable.SortBarLayout_titleColor, mTxtColor);
        mTxtSelectedColor = typedArray.getColor(R.styleable.SortBarLayout_titleSelectedColor, mTxtSelectedColor);
        mTriangleColor = typedArray.getColor(R.styleable.SortBarLayout_triangleColor, mTriangleColor);
        mTriangleSelectedColor = typedArray.getColor(R.styleable.SortBarLayout_triangleSelectedColor, mTriangleSelectedColor);
        mTxtSize = (int)typedArray.getDimension(R.styleable.SortBarLayout_titleSize, mTxtSize);
        mTriangleLeftMargin = (int)typedArray.getDimension(R.styleable.SortBarLayout_triangleLeftMargin, mTriangleLeftMargin);
        typedArray.recycle();
        init();
    }

    private void init() {
        mContainerUnitLayout = new LinearLayout(getContext());
        mContainerUnitLayout.setOrientation(LinearLayout.HORIZONTAL);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        layoutParams.addRule(CENTER_VERTICAL);
        mContainerUnitLayout.setLayoutParams(layoutParams);
        mContainerUnitLayout.setGravity(Gravity.CENTER_VERTICAL);

        mIndicatorLayout = new LinearLayout(getContext());
        RelativeLayout.LayoutParams indicatorLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, dip2px(5));
        indicatorLayoutParams.addRule(ALIGN_PARENT_BOTTOM);
        mIndicatorLayout.setLayoutParams(indicatorLayoutParams);
        mIndicatorLayout.setGravity(CENTER_HORIZONTAL);

        View lineView = new View(getContext());
        lineView.setBackgroundColor(mLineColor);
        RelativeLayout.LayoutParams lineLayoutParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, 1);
        lineLayoutParams.addRule(ALIGN_PARENT_BOTTOM);
        lineView.setLayoutParams(lineLayoutParams);

        addView(mContainerUnitLayout);
        addView(mIndicatorLayout);
        addView(lineView);
    }

    /**
     * 设置SortBarUnit显示的属性
     * @param sortBarUnit
     */
    private void setupSortBarUnitShow(SortBarUnit sortBarUnit) {
        sortBarUnit.setTitleColor(mTxtColor);
        sortBarUnit.setTitleSelectedColor(mTxtSelectedColor);
        sortBarUnit.setTriangleColor(mTriangleColor);
        sortBarUnit.setTriangleSelectedColor(mTriangleSelectedColor);

        sortBarUnit.setTitleSize(mTxtSize);
        sortBarUnit.setTriangleLeftMargin(mTriangleLeftMargin);
    }

    private int dip2px(float dipValue) {
        float scale = getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    //********************************************对外提供方法

    public void bindData(List<? extends ItemSortable> itemSortableList) {
        if (itemSortableList == null) {
            return;
        }
        mContainerUnitLayout.removeAllViews();

        int unitCount = itemSortableList.size();
        for (int i = 0; i < unitCount; i++) {
            RelativeLayout sorBarUnitContainerLayout = new RelativeLayout(getContext());
            sorBarUnitContainerLayout.setGravity(CENTER);
            LinearLayout.LayoutParams unitLayoutParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1);
            sorBarUnitContainerLayout.setLayoutParams(unitLayoutParams);

            final SortBarUnit sortBarUnit = new SortBarUnit(getContext());
            RelativeLayout.LayoutParams sortBarUnitLayoutParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            sortBarUnitLayoutParams.addRule(CENTER);
            sortBarUnit.setLayoutParams(sortBarUnitLayoutParams);
            setupSortBarUnitShow(sortBarUnit);

            sorBarUnitContainerLayout.addView(sortBarUnit);
            mContainerUnitLayout.addView(sorBarUnitContainerLayout);

            final ItemSortable itemSortable = itemSortableList.get(i);
            final int index = i;
            sortBarUnit.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (sortBarUnit == mLastSortBarUnit) {
                        IndicatorStatusEnum statusEnum = sortBarUnit.toggleAlternative();
                        if (mOnItemClickListener != null) {
                            mOnItemClickListener.onItemClick(index, itemSortable, statusEnum);
                        }
                        return;
                    }

                    mLastSortBarUnit.restore();
                    IndicatorStatusEnum statusEnum = sortBarUnit.toggleTriangle();
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(index, itemSortable, statusEnum);
                    }

                    mLastSortBarUnit = sortBarUnit;
                }
            });

            if (index == 0) {
                sortBarUnit.setUnitUpwardSelected(true);
                mLastSortBarUnit = sortBarUnit;
            } else {
                sortBarUnit.setUnitUpwardSelected(false);
            }
            sortBarUnit.bindUnitData(itemSortable);
        }
    }

    public int getTriangleColor() {
        return mTriangleColor;
    }

    public void setTriangleColor(int triangleColor) {
        mTriangleColor = triangleColor;
    }

    public int getTriangleSelectedColor() {
        return mTriangleSelectedColor;
    }

    public void setTriangleSelectedColor(int triangleSelectedColor) {
        mTriangleSelectedColor = triangleSelectedColor;
    }

    public int getTriangleLeftMargin() {
        return mTriangleLeftMargin;
    }

    public void setTriangleLeftMargin(int triangleLeftMargin) {
        mTriangleLeftMargin = triangleLeftMargin;
    }

    public int getTxtSize() {
        return mTxtSize;
    }

    public void setTxtSize(int txtSize) {
        mTxtSize = txtSize;
    }

    public int getTxtColor() {
        return mTxtColor;
    }

    public void setTxtColor(int txtColor) {
        mTxtColor = txtColor;
    }

    public int getTxtSelectedColor() {
        return mTxtSelectedColor;
    }

    public void setTxtSelectedColor(int txtSelectedColor) {
        mTxtSelectedColor = txtSelectedColor;
    }

    public int getLineColor() {
        return mLineColor;
    }

    public void setLineColor(int lineColor) {
        mLineColor = lineColor;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(int index, ItemSortable itemSortable, IndicatorStatusEnum statusEnum);
    }

}
