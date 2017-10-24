package com.sugary.goertzpro.widget.photorecycle;

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
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.sugary.goertzpro.BuildConfig;
import com.sugary.goertzpro.R;
import com.sugary.goertzpro.utils.ImageLoader;
import com.sugary.goertzpro.utils.RxBus;
import com.sugary.goertzpro.widget.photorecycle.event.AddPhotoClickEvent;
import com.sugary.goertzpro.widget.photorecycle.event.RemovePhotoClickEvent;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.PicassoEngine;
import com.zhihu.matisse.internal.entity.CaptureStrategy;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.sugary.goertzpro.widget.photorecycle.SelectPhotoRecyclerView.ShowTypeEnum.ONLY_DISPLAY;
import static com.sugary.goertzpro.widget.photorecycle.SelectPhotoRecyclerView.ShowTypeEnum.UP_LOADING;


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
    public static final int DEFAULT_MAX_SELECTED_COUNT_ONCE = 1;

    static {
        SHOW_TYPE_SPARSE.put(0, ONLY_DISPLAY);
        SHOW_TYPE_SPARSE.put(1, UP_LOADING);
    }


    private SelectPhotoAdapter mSelectPhotoAdapter;
    private ShowTypeEnum mShowTypeEnum = UP_LOADING;
    private int mSpanCount = DEFAULT_SPAN_COUNT;
    private int mPhotoLimitCount = DEFAULT_PHOTO_LIMIT_COUNT;
    private int mSelectedCountOnce = DEFAULT_MAX_SELECTED_COUNT_ONCE;

    public SelectPhotoRecyclerView(Context context) {
        super(context);
        init();
    }

    public SelectPhotoRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SelectPhotoRecyclerView);
        mSelectedCountOnce = typedArray.getInt(R.styleable.SelectPhotoRecyclerView_onceSelectCount, DEFAULT_MAX_SELECTED_COUNT_ONCE);
        mSpanCount = typedArray.getInt(R.styleable.SelectPhotoRecyclerView_spanCount, DEFAULT_SPAN_COUNT);
        mPhotoLimitCount = typedArray.getInt(R.styleable.SelectPhotoRecyclerView_limitCount, DEFAULT_PHOTO_LIMIT_COUNT);
        mShowTypeEnum = SHOW_TYPE_SPARSE.get(typedArray.getInt(R.styleable.SelectPhotoRecyclerView_showType, 1));
        typedArray.recycle();
        init();
    }

    private void init() {
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), mSpanCount, LinearLayoutManager.VERTICAL, false);
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
            switch (mShowTypeEnum) {
                case UP_LOADING:
                    int footerCount = getFooterCount();
                    int itemCount = getItemCount();
                    Log.d(TAG, " position = " + position + " getItemViewType: footerCount = " + footerCount + " itemCount = " + itemCount);
                    if (footerCount > 0 && position == itemCount - 1) {
                        return TYPE_FOOTER;
                    } else {
                        return super.getItemViewType(position);
                    }
                default:
                    return super.getItemViewType(position);
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Log.d(TAG, "onCreateViewHolder: viewType = " + viewType);
            Context context = parent.getContext();
            if (viewType == TYPE_FOOTER) {
                View footerView = LayoutInflater.from(context).inflate(R.layout.item_footer_add, parent, false);
                return new FooterViewHolder(footerView);
            } else {
                View bodyView = LayoutInflater.from(context).inflate(R.layout.item_submit_photo, parent, false);
                return new PhotoViewHolder(bodyView);
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
            int result;
            switch (mShowTypeEnum) {
                case ONLY_DISPLAY:
                    result = 0;
                    Log.d(TAG, "getFooterCount: ONLY_DISPLAY");
                    break;
                case UP_LOADING:
                    if(getBodyCount() >= mPhotoLimitCount){
                        result = 0;
                    }else{
                        result = 1;
                    }
                    Log.d(TAG, "getFooterCount: UP_LOADING mPhotoLimitCount = " + mPhotoLimitCount);
                    break;
                default:
                    result = 0;
            }
            return result;
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

        void addNotifyData(PhotoEntity photoEntity) {
            mDataList.add(photoEntity);
            notifyDataSetChanged();
        }

        void removeNotifyData(PhotoEntity removePhotoEntity) {
            if (mDataList.contains(removePhotoEntity)) {
                int position = mDataList.indexOf(removePhotoEntity);
                mDataList.remove(position);
                notifyItemRemoved(position);
            }
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
                String resUrl = photoEntity.getResUrl();
                if (!TextUtils.isEmpty(resUrl)) {
                    ImageLoader.display(getContext(), resUrl, mImgSubmit);
                } else {
                    mImgSubmit.setImageURI(photoUri);
                }

                setupShowState(mShowTypeEnum);
            }

            @OnClick(R.id.img_item_submit_cancel)
            void onRemovePhotoClick() {
                int position = getAdapterPosition();
                if (position >= 0 && position < mDataList.size()) {
                    PhotoEntity removePhotoEntity = mDataList.get(position);
                    RxBus.getInstance().send(new RemovePhotoClickEvent(removePhotoEntity));
                }
            }

            void setupShowState(ShowTypeEnum typeEnum) {
                switch (typeEnum) {
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
                RxBus.getInstance().send(new AddPhotoClickEvent(SelectPhotoRecyclerView.this));
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

    public void addNewPhotoData(List<PhotoEntity> photoEntityList) {
        if (mSelectPhotoAdapter != null) {
            mSelectPhotoAdapter.addNotifyData(photoEntityList);
        }
    }

    public void addNewPhotoData(PhotoEntity photoEntity) {
        if (mSelectPhotoAdapter != null) {
            mSelectPhotoAdapter.addNotifyData(photoEntity);
        }
    }

    public void removePhotoData(PhotoEntity photoEntity) {
        if (mSelectPhotoAdapter != null) {
            mSelectPhotoAdapter.removeNotifyData(photoEntity);
        }
    }

    public void notifyPhotoData(List<PhotoEntity> photoEntityList) {
        if (mSelectPhotoAdapter != null) {
            mSelectPhotoAdapter.notifyAllData(photoEntityList);
        }
    }

    /**
     * 打开相册
     *
     * @param fragment
     */
    public void openGallery(Fragment fragment) {
        String authority = BuildConfig.APPLICATION_ID + ".extend";
        Matisse.from(fragment)
                .choose(MimeType.allOf())
                .theme(R.style.Matisse_Zhihu)
                .capture(true)
                .captureStrategy(new CaptureStrategy(true, authority))
                .countable(true)
                .maxSelectable(mSelectedCountOnce)
                .gridExpectedSize(dip2px(120))
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                .thumbnailScale(0.85f)
                .imageEngine(new PicassoEngine())
                .forResult(REQUEST_PHOTO_CODE);
    }

    /**
     * 打开相册(不待拍照)
     *
     * @param fragment
     */
    public void openGalleryWithoutCapture(Fragment fragment) {
        String authority = BuildConfig.APPLICATION_ID + ".extend";
        Matisse.from(fragment)
                .choose(MimeType.allOf())
                .theme(R.style.Matisse_Zhihu)
                .capture(false)
                .captureStrategy(new CaptureStrategy(true, authority))
                .countable(true)
                .maxSelectable(mSelectedCountOnce)
                .gridExpectedSize(dip2px(120))
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                .thumbnailScale(0.85f)
                .imageEngine(new PicassoEngine())
                .forResult(REQUEST_PHOTO_CODE);
    }

    /**
     * 打开相册
     *
     * @param activity
     */
    public void openGallery(Activity activity) {
        String authority = BuildConfig.APPLICATION_ID + ".extend";
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
     *
     * @param showTypeEnum
     */
    public void setShowTypeEnum(ShowTypeEnum showTypeEnum) {
        mShowTypeEnum = showTypeEnum;
    }

    public enum ShowTypeEnum {
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
