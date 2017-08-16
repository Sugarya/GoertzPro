package com.sugary.goertzpro.widget.filterbar;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.sugary.goertzpro.widget.filterbar.FilterBarLayout;

/**
 * Created by Ethan on 2017/8/15.
 * <p>
 * 筛选条单元实体类
 */

public class IndicatorUnit {

    /**
     * 各个筛选单元的标题
     */
    private String unitTitle;
    /**
     * 打开下拉视图时，是否需要暗色屏幕显示
     */
    private boolean screenDimAvailable;
    /**
     * 下拉视图动画模式 1.平移  2.折叠
     */
    private FilterBarLayout.FooterMode footerMode;

    private View indicatorUnit;
    /**
     * 筛选标题控件
     */
    private TextView tvUnit;
    /**
     * 筛选单元图片
     */
    private ImageView imgUnit;
    /**
     * 是否处在下拉状态
     */
    private boolean isExpanded = false;

    private View footerView;

    public IndicatorUnit(String unitTitle, boolean screenDimAvailable) {
        this.unitTitle = unitTitle;
        this.screenDimAvailable = screenDimAvailable;
    }

    public IndicatorUnit(String unitTitle, boolean screenDimAvailable, FilterBarLayout.FooterMode footerMode, View indicatorUnit, TextView tvUnit, ImageView imgUnit, boolean isExpanded, View footerView) {
        this.unitTitle = unitTitle;
        this.screenDimAvailable = screenDimAvailable;
        this.footerMode = footerMode;
        this.indicatorUnit = indicatorUnit;
        this.tvUnit = tvUnit;
        this.imgUnit = imgUnit;
        this.isExpanded = isExpanded;
        this.footerView = footerView;
    }

    public String getUnitTitle() {
        return unitTitle;
    }

    public void setUnitTitle(String unitTitle) {
        this.unitTitle = unitTitle;
    }

    public boolean isScreenDimAvailable() {
        return screenDimAvailable;
    }

    public void setScreenDimAvailable(boolean screenDimAvailable) {
        this.screenDimAvailable = screenDimAvailable;
    }

    public FilterBarLayout.FooterMode getFooterMode() {
        return footerMode;
    }

    public void setFooterMode(FilterBarLayout.FooterMode footerMode) {
        this.footerMode = footerMode;
    }

    public View getIndicatorUnit() {
        return indicatorUnit;
    }

    public void setIndicatorUnit(View indicatorUnit) {
        this.indicatorUnit = indicatorUnit;
    }

    public TextView getTvUnit() {
        return tvUnit;
    }

    public void setTvUnit(TextView tvUnit) {
        this.tvUnit = tvUnit;
    }

    public ImageView getImgUnit() {
        return imgUnit;
    }

    public void setImgUnit(ImageView imgUnit) {
        this.imgUnit = imgUnit;
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }

    public View getFooterView() {
        return footerView;
    }

    public void setFooterView(View footerView) {
        this.footerView = footerView;
    }
}
