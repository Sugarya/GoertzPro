package com.sugary.goertzpro.widget.custom;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.TypedArray;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.sugary.goertzpro.BuildConfig;
import com.sugary.goertzpro.R;
import com.sugary.goertzpro.scene.camera.entity.PhotoEntity;
import com.sugary.goertzpro.scene.camera.event.AddPhotoClickEvent;
import com.sugary.goertzpro.utils.RxBus;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.PicassoEngine;
import com.zhihu.matisse.internal.entity.CaptureStrategy;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.sugary.goertzpro.widget.custom.SelectPhotoRecyclerView.ShowTypeEnum.ONLY_DISPLAY;
import static com.sugary.goertzpro.widget.custom.SelectPhotoRecyclerView.ShowTypeEnum.UP_LOADING;

/**
 * Created by Ethan on 2017/10/4.
 * 照片选择器，使用Matisse知乎照片浏览库，通过RxBus发送事件与Fragment／Activity交互
 */

public class SelectPhotoRecyclerView extends RecyclerView {

    private static final String TAG = "SelectPhotoRecyclerView";

    /**
     * onActivityResult(）回调请求码
     */
    public static final int REQUEST_PHOTO_CODE = 2;

    private static final int DEFAULT_SPAN_COUNT = 3;
    private static final int DEFAULT_PHOTO_LIMIT_COUNT = 5;
    private static final SparseArray<ShowTypeEnum> SHOW_TYPE_SPARSE = new SparseArray<>();
    static {
        SHOW_TYPE_SPARSE.put(0, ONLY_DISPLAY);
        SHOW_TYPE_SPARSE.put(1, UP_LOADING);
    }


    private SelectPhotoAdapter mSelectPhotoAdapter;
    private ShowTypeEnum mShowTypeEnum = UP_LOADING;



    public SelectPhotoRecyclerView(Context context) {
        super(context);
        init();
    }

    public SelectPhotoRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SelectPhotoRecyclerView);
        mShowTypeEnum = SHOW_TYPE_SPARSE.get(typedArray.getInt(R.styleable.SelectPhotoRecyclerView_showType, 1));
        typedArray.recycle();
        init();
    }

    private void init(){
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), DEFAULT_SPAN_COUNT, LinearLayoutManager.VERTICAL, false);
        mSelectPhotoAdapter = new SelectPhotoAdapter();

        setLayoutManager(layoutManager);
        setAdapter(mSelectPhotoAdapter);
    }

    class SelectPhotoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int TYPE_FOOTER = 11;

        private List<PhotoEntity> mDataList;

        private SelectPhotoAdapter() {
            mDataList = new ArrayList<>();
        }

        @Override
        public int getItemViewType(int position) {
            switch (mShowTypeEnum){
                case UP_LOADING:
                    if (getFooterCount() > 0 && position > getBodyCount() - 1) {
                        return TYPE_FOOTER;
                    }else {
                        return super.getItemViewType(position);
                    }
                default:
                    return super.getItemViewType(position);
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            if (viewType == TYPE_FOOTER) {
                View footerView = LayoutInflater.from(context).inflate(R.layout.item_footer_add, parent, false);
                return new FooterViewHolder(footerView);
            } else {
                View bodyView = LayoutInflater.from(context).inflate(R.layout.item_submit_photo, parent, false);
                PhotoViewHolder photoViewHolder = new PhotoViewHolder(bodyView);
                photoViewHolder.setupShowState(mShowTypeEnum);
                return photoViewHolder;
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof PhotoViewHolder) {
                PhotoViewHolder photoViewHolder = (PhotoViewHolder) holder;
                if (position >= 0 && position < mDataList.size()) {
                    PhotoEntity photoEntity = mDataList.get(position);
                    photoViewHolder.onBindViewHolder(photoEntity);
                }
            }
        }

        @Override
        public int getItemCount() {
            return getBodyCount() + getFooterCount();
        }

        private int getBodyCount() {
            return mDataList.size();
        }

        private int getFooterCount() {
            if (mDataList.size() >= DEFAULT_PHOTO_LIMIT_COUNT) {
                return 0;
            }
            return 1;
        }

        void notifyAllData(List<PhotoEntity> photoEntityList) {
            if (photoEntityList == null || photoEntityList.isEmpty()) {
                mDataList.clear();
                notifyDataSetChanged();
                return;
            }
            if (!mDataList.isEmpty()) {
                mDataList.clear();
                notifyDataSetChanged();
            }

            mDataList.addAll(photoEntityList);
            int itemCount = photoEntityList.size();
            notifyItemRangeChanged(0, itemCount);
        }

        void addNotifyData(List<PhotoEntity> photoEntityList) {
            mDataList.addAll(photoEntityList);
            notifyDataSetChanged();
        }

        List<PhotoEntity> getDataList() {
            return mDataList;
        }

        class PhotoViewHolder extends RecyclerView.ViewHolder {

            @BindView(R.id.img_item_submit_photo)
            ImageView mImgSubmit;

            @BindView(R.id.img_item_submit_cancel)
            ImageView mImgCancel;


            PhotoViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }

            void onBindViewHolder(PhotoEntity photoEntity) {
                if (photoEntity == null) {
                    return;
                }
                Uri photoUri = photoEntity.getPhotoUri();
                mImgSubmit.setImageURI(photoUri);
            }

            @OnClick(R.id.img_item_submit_cancel)
            void onCancelClick() {
                int position = getAdapterPosition();
                if (position >= 0 && position < mDataList.size()) {
                    mDataList.remove(position);
                    notifyItemRemoved(position);
                }
            }

            void setupShowState(ShowTypeEnum typeEnum){
                switch (typeEnum){
                    case ONLY_DISPLAY:
                        mImgCancel.setVisibility(INVISIBLE);
                        break;
                    default:
                        mImgCancel.setVisibility(VISIBLE);
                }
            }
        }

        class FooterViewHolder extends RecyclerView.ViewHolder {

            @BindView(R.id.container_footer_add)
            RelativeLayout mContainerFooterAdd;

            FooterViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }

            @OnClick(R.id.container_footer_add)
            void onItemClick() {
                RxBus.getInstance().send(new AddPhotoClickEvent());
            }
        }

    }

    private int dip2px(float dipValue) {
        float scale = getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    //**********************************************对外提供的方法

    public SelectPhotoAdapter getSelectPhotoAdapter() {
        return mSelectPhotoAdapter;
    }

    public void addNewPhotoData(List<PhotoEntity> photoEntityList){
        if(mSelectPhotoAdapter != null) {
            mSelectPhotoAdapter.addNotifyData(photoEntityList);
        }
    }

    /**
     * 打开相册
     * @param fragment
     */
    public void openGallery(Fragment fragment) {
        String authority = BuildConfig.APPLICATION_ID + ".extend.group";
        Matisse.from(fragment)
                .choose(MimeType.allOf())
                .theme(R.style.Matisse_Zhihu)
                .capture(true)
                .captureStrategy(new CaptureStrategy(true, authority))
                .countable(true)
                .maxSelectable(1)
                .gridExpectedSize(dip2px(120))
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                .thumbnailScale(0.85f)
                .imageEngine(new PicassoEngine())
                .forResult(REQUEST_PHOTO_CODE);
    }

    /**
     * 打开相册
     * @param activity
     */
    public void openGallery(Activity activity) {
        String authority = BuildConfig.APPLICATION_ID + ".extend.group";
        Matisse.from(activity)
                .choose(MimeType.allOf())
                .theme(R.style.Matisse_Zhihu)
                .capture(true)
                .captureStrategy(new CaptureStrategy(true, authority))
                .countable(true)
                .maxSelectable(1)
                .gridExpectedSize(dip2px(120))
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                .thumbnailScale(0.85f)
                .imageEngine(new PicassoEngine())
                .forResult(REQUEST_PHOTO_CODE);
    }


    public ShowTypeEnum getShowTypeEnum() {
        return mShowTypeEnum;
    }

    /**
     * 设置展示类型
     * @param showTypeEnum
     */
    public void setShowTypeEnum(ShowTypeEnum showTypeEnum) {
        mShowTypeEnum = showTypeEnum;
    }

    public enum ShowTypeEnum{
        /**
         * 可进行上传图片操作
         */
        UP_LOADING,

        /**
         * 只展示图片
         */
        ONLY_DISPLAY
    }

}
