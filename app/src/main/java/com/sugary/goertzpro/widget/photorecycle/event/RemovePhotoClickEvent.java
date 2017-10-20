package com.sugary.goertzpro.widget.photorecycle.event;


import com.sugary.goertzpro.widget.photorecycle.PhotoEntity;

/**
 * Created by Ethan on 2017/10/15.
 * 删除照片点击事件
 */

public class RemovePhotoClickEvent {
    private PhotoEntity mPhotoEntity;

    public RemovePhotoClickEvent(PhotoEntity photoEntity) {
        mPhotoEntity = photoEntity;
    }

    public PhotoEntity getPhotoEntity() {
        return mPhotoEntity;
    }

    public void setPhotoEntity(PhotoEntity photoEntity) {
        mPhotoEntity = photoEntity;
    }
}
