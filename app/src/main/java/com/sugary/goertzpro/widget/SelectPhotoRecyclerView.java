package com.sugary.goertzpro.widget;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.sugary.goertzpro.R;
import com.sugary.goertzpro.scene.camera.entity.PhotoEntity;
import com.sugary.goertzpro.scene.camera.event.AddPhotoClickEvent;
import com.sugary.goertzpro.utils.RxBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Ethan on 2017/10/4.
 */

public class SelectPhotoRecyclerView extends RecyclerView {

    private static final int DEFAULT_SPAN_COUNT = 3;
    private SelectPhotoAdapter mSelectPhotoAdapter;

    public SelectPhotoRecyclerView(Context context) {
        super(context);
        init();
    }

    public SelectPhotoRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init(){
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), DEFAULT_SPAN_COUNT, LinearLayoutManager.VERTICAL, false);
        mSelectPhotoAdapter = new SelectPhotoAdapter();

        setLayoutManager(layoutManager);
        setAdapter(mSelectPhotoAdapter);
    }

    class SelectPhotoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int LIMIT_COUNT = 5;

        private static final int TYPE_FOOTER = 11;

        private List<PhotoEntity> mDataList;

        private SelectPhotoAdapter() {
            mDataList = new ArrayList<>();
        }

        @Override
        public int getItemViewType(int position) {
            if (getFooterCount() > 0 && position > getBodyCount() - 1) {
                return TYPE_FOOTER;
            }
            return super.getItemViewType(position);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
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
            if (mDataList.size() >= LIMIT_COUNT) {
                return 0;
            }
            return 1;
        }


        public void notifyAllData(List<PhotoEntity> photoEntityList) {
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

        public void addNotifyData(List<PhotoEntity> photoEntityList) {
            mDataList.addAll(photoEntityList);
            notifyDataSetChanged();
        }

        public List<PhotoEntity> getDataList() {
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


    public SelectPhotoAdapter getSelectPhotoAdapter() {
        return mSelectPhotoAdapter;
    }

}
