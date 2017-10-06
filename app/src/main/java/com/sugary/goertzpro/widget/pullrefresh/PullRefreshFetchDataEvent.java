package com.sugary.goertzpro.widget.pullrefresh;

import android.view.View;

/**
 * Created by Ethan on 2017/10/6.
 */

public class PullRefreshFetchDataEvent {

    private int refreshBarHeight;
    /**
     * 滑动的主体视图
     */
    private View bodyView;

    public PullRefreshFetchDataEvent(int refreshBarHeight, View bodyView) {
        this.refreshBarHeight = refreshBarHeight;
        this.bodyView = bodyView;
    }

    public int getRefreshBarHeight() {
        return refreshBarHeight;
    }

    public View getBodyView() {
        return bodyView;
    }
}
