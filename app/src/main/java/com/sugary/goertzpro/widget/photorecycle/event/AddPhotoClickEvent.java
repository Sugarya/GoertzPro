package com.sugary.goertzpro.widget.photorecycle.event;


import com.sugary.goertzpro.widget.custom.SelectPhotoRecyclerView;

/**
 * Created by Ethan on 2017/10/2.
 * 添加照片点击事件
 */

public class AddPhotoClickEvent {

    private SelectPhotoRecyclerView mSelectPhotoRecyclerView;

    public AddPhotoClickEvent(SelectPhotoRecyclerView selectPhotoRecyclerView) {
        mSelectPhotoRecyclerView = selectPhotoRecyclerView;
    }

    public SelectPhotoRecyclerView getSelectPhotoRecyclerView() {
        return mSelectPhotoRecyclerView;
    }
}
