package com.sugary.goertzpro.widget.photorecycle;

import android.net.Uri;

/**
 * Created by Ethan on 2017/10/2.
 * 公共组件服务单详情 照片实体类
 */

public class PhotoEntity {

    private Uri photoUri;
    private String resUrl;

    public PhotoEntity(Uri photoUri) {
        this.photoUri = photoUri;
    }

    public PhotoEntity(String resUrl) {
        this.resUrl = resUrl;
    }

    public PhotoEntity(Uri photoUri, String resUrl) {
        this.photoUri = photoUri;
        this.resUrl = resUrl;
    }

    public String getResUrl() {
        return resUrl;
    }

    public void setResUrl(String resUrl) {
        this.resUrl = resUrl;
    }

    public Uri getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(Uri photoUri) {
        this.photoUri = photoUri;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PhotoEntity)) return false;

        PhotoEntity that = (PhotoEntity) o;

        if (getPhotoUri() != null ? !getPhotoUri().equals(that.getPhotoUri()) : that.getPhotoUri() != null)
            return false;
        return getResUrl() != null ? getResUrl().equals(that.getResUrl()) : that.getResUrl() == null;

    }

    @Override
    public int hashCode() {
        int result = getPhotoUri() != null ? getPhotoUri().hashCode() : 0;
        result = 31 * result + (getResUrl() != null ? getResUrl().hashCode() : 0);
        return result;
    }
}
