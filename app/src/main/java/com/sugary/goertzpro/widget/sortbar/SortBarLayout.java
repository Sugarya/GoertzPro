package com.sugary.goertzpro.widget.sortbar;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.List;

/**
 * Created by Ethan on 2017/10/24.
 */

public class SortBarLayout extends RelativeLayout {

    private static final String TAG = "SortBarLayout";

    private static final int DEFAULT_TXT_COLOR = Color.parseColor("#333333");
    private static final int DEFAULT_SELECTED_TXT_COLOR = Color.parseColor("#E4393C");

    /**
     * 排序条上方容器布局
     */
    private LinearLayout mContainerUnitLayout;
    /**
     * 底部指示器条
     */
    private LinearLayout mIndicatorLayout;
    private OnItemClickListener mOnItemClickListener;

    private int mTxtColor = DEFAULT_TXT_COLOR;
    private int mTxtSelectedColor = DEFAULT_SELECTED_TXT_COLOR;

    private SortBarUnit mLastSortBarUnit;

    public SortBarLayout(Context context) {
        super(context);
        init();
    }

    public SortBarLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
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
        mIndicatorLayout.setGravity(CENTER_HORIZONTAL);
    }

    public void bindData(List<ItemSortable> itemSortableList){
        if(itemSortableList == null){
            return;
        }
        mContainerUnitLayout.removeAllViews();

        int unitCount = itemSortableList.size();
        for(int i = 0; i < unitCount; i++){
            RelativeLayout sorBarUnitContainerLayout = new RelativeLayout(getContext());
            sorBarUnitContainerLayout.setGravity(CENTER_VERTICAL);
            LinearLayout.LayoutParams unitLayoutParams = new LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.MATCH_PARENT, 1);
            sorBarUnitContainerLayout.setLayoutParams(unitLayoutParams);

            final SortBarUnit sortBarUnit = new SortBarUnit(getContext());
            RelativeLayout.LayoutParams sortBarUnitLayoutParams = new  RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            sortBarUnitLayoutParams.addRule(CENTER_VERTICAL);
            sortBarUnit.setLayoutParams(sortBarUnitLayoutParams);
            sorBarUnitContainerLayout.addView(sortBarUnit);
            mContainerUnitLayout.addView(sorBarUnitContainerLayout);

            final ItemSortable itemSortable = itemSortableList.get(i);
            final int index = i;
            sortBarUnit.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {


                    if(mOnItemClickListener != null){
                        mOnItemClickListener.onItemClick(index, itemSortable);
                        sortBarUnit.toggleUnder();
                    }
                }
            });


            if(index == 0){
                sortBarUnit.setUnitUpwardSelected(true);
            }else{
                sortBarUnit.setUnitUpwardSelected(false);
            }
            sortBarUnit.bindUnitData(itemSortable);
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener{
        void onItemClick(int index, ItemSortable itemSortable);
    }

}
