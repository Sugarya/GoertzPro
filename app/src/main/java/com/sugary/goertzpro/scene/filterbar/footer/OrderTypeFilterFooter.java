package com.sugary.goertzpro.scene.filterbar.footer;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sugary.goertzpro.R;
import com.sugary.goertzpro.scene.filterbar.event.OrderTypeItemSelectedEvent;
import com.sugary.goertzpro.scene.filterbar.model.OrderTypeModel;
import com.sugary.goertzpro.utils.RxBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by Ethan on 2017/8/14.
 */

public class OrderTypeFilterFooter extends RelativeLayout {

    private static final String TAG = "OrderTypeFilterFooter";


    private OrderTypeAdapter mOrderTypeAdapter;
    private List<OrderTypeModel> mOrderTypeModels = new ArrayList<>();

    public OrderTypeFilterFooter(Context context) {
        super(context);
        init();
    }

    public OrderTypeFilterFooter(Context context, List<OrderTypeModel> orderTypeModels) {
        super(context);
        if(orderTypeModels != null) {
            mOrderTypeModels.addAll(orderTypeModels);
        }
        init();
    }

    public OrderTypeFilterFooter(Context context, AttributeSet attrs) {
        super(context, attrs);
        initRecyclerView();
    }

    public OrderTypeFilterFooter(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initRecyclerView();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        Log.d(TAG, "onFinishInflate");
        fetchData();
    }

    private void init() {
        Log.d(TAG, "init");
        initRecyclerView();
        if (mOrderTypeModels == null || mOrderTypeModels.size() == 0) {
            fetchData();
        }
    }

    private void initRecyclerView() {
        RecyclerView recyclerView = addRecyclerView();

        if (mOrderTypeModels == null) {
            mOrderTypeModels = new ArrayList<>();
        }
        mOrderTypeAdapter = new OrderTypeAdapter(mOrderTypeModels);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mOrderTypeAdapter);
    }

    @NonNull
    private RecyclerView addRecyclerView() {
        RecyclerView recyclerView = new RecyclerView(getContext());
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT,
                RecyclerView.LayoutParams.WRAP_CONTENT);
        recyclerView.setLayoutParams(lp);
        addView(recyclerView);
        return recyclerView;
    }

    private void fetchData() {

//        DataLayer.getInstance()
//                .getModuleService()
//                .getAfterSaleServer()
//                .fetchOrderTypeList()
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Action1<OrderTypeListWrapper>() {
//                    @Override
//                    public void call(OrderTypeListWrapper orderTypeListWrapper) {
//                        if(orderTypeListWrapper == null){
//                            return;
//                        }
//                        List<OrderTypeModel> dataList = orderTypeListWrapper.getDataList();
//                        if(dataList == null){
//                            return;
//                        }
//                        if(mOrderTypeModels != null) {
//                            mOrderTypeModels.clear();
//                            mOrderTypeModels.addAll(dataList);
//                        }
//                        if(mOrderTypeAdapter != null){
//                            mOrderTypeAdapter.notifyDataSetChanged();
//                        }
//                    }
//                }, new Action1<Throwable>() {
//                    @Override
//                    public void call(Throwable throwable) {
//                        throwable.printStackTrace();
//                    }
//                });
    }

    private void initMock() {
        mOrderTypeModels.add(new OrderTypeModel("1", "全部"));
        mOrderTypeModels.add(new OrderTypeModel("2", "厂直"));
        mOrderTypeModels.add(new OrderTypeModel("3", "统销"));
        mOrderTypeModels.add(new OrderTypeModel("4", "代购"));
        mOrderTypeModels.add(new OrderTypeModel("5", "经销"));
        mOrderTypeModels.add(new OrderTypeModel("6", "核销"));

        if (mOrderTypeAdapter != null) {
            mOrderTypeAdapter.notifyDataSetChanged();
        }
    }

    public void notifyFilterViewData(List<OrderTypeModel> orderTypeModelList) {
        if (orderTypeModelList == null) {
            return;
        }
        mOrderTypeModels.clear();
        mOrderTypeModels.addAll(orderTypeModelList);
        if (mOrderTypeAdapter != null) {
            mOrderTypeAdapter.notifyDataSetChanged();
        }
    }


    class OrderTypeAdapter extends RecyclerView.Adapter<OrderTypeAdapter.OrderTypeHolder> {

        private List<OrderTypeModel> mDataList;

        OrderTypeAdapter(List<OrderTypeModel> dataList) {
            mDataList = dataList;
        }

        @Override
        public OrderTypeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_filter_footer_order_type, parent, false);
            return new OrderTypeHolder(itemView);
        }

        @Override
        public void onBindViewHolder(OrderTypeHolder holder, int position) {
            OrderTypeModel orderTypeModel = mDataList.get(position);
            if (orderTypeModel == null) {
                return;
            }
            holder.onBindHolder(orderTypeModel);
        }

        @Override
        public int getItemCount() {
            return mDataList != null ? mDataList.size() : 0;
        }


        class OrderTypeHolder extends RecyclerView.ViewHolder {

            @BindView(R.id.container_filter_item_order_type)
            RelativeLayout mContainer;

            @BindView(R.id.tv_item_filter_order_type_title)
            TextView mTvTitle;

            @BindView(R.id.img_item_filter_order_type_icon)
            ImageView mImgIcon;

            OrderTypeHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }

            void onBindHolder(OrderTypeModel model) {
                String orderName = model.getOrdername();
                if (!TextUtils.isEmpty(orderName)) {
                    mTvTitle.setText(orderName);
                }
                if (model.isSelected()) {
                    mTvTitle.setTextColor(getResources().getColor(R.color.font_blue));
                    mImgIcon.setVisibility(VISIBLE);
                } else {
                    mTvTitle.setTextColor(getResources().getColor(R.color.font_black_light));
                    mImgIcon.setVisibility(GONE);
                }
            }

            @OnClick(R.id.container_filter_item_order_type)
            public void onItemClick(View view) {
                int position = getAdapterPosition();
                int size = mDataList.size();
                for (int i = 0; i < size; i++) {
                    if (i == position) {
                        mDataList.get(i).setSelected(true);
                    } else {
                        mDataList.get(i).setSelected(false);
                    }
                }
                notifyDataSetChanged();
                if (position < size && position >= 0) {
                    OrderTypeModel orderTypeModel = mDataList.get(position);
                    RxBus.getInstance().send(new OrderTypeItemSelectedEvent(orderTypeModel));
                }
            }
        }
    }
}
