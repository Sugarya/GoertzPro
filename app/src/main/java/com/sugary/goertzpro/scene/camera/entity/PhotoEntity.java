package com.sugary.goertzpro.scene.camera.entity;

import android.net.Uri;

/**
 * Created by Ethan on 2017/10/2.
 * 照片实体类
 */

public class PhotoEntity {

    private Uri photoUri;

    public PhotoEntity(Uri photoUri) {
        this.photoUri = photoUri;
    }

    public Uri getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(Uri photoUri) {
        this.photoUri = photoUri;
    }
}
