package com.sugary.goertzpro.scene.filterbar;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sugary.goertzpro.R;
import com.sugary.goertzpro.scene.filterbar.event.OrderTypeItemSelectedEvent;
import com.sugary.goertzpro.scene.filterbar.model.OrderTypeModel;
import com.sugary.goertzpro.utils.RxBus;
import com.sugary.goertzpro.widget.filterbar.FilterBarLayout;
import com.sugary.goertzpro.scene.filterbar.footer.OrderTypeFilterFooter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import rx.Subscription;
import rx.functions.Action1;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @BindView(R.id.filter_bar)
    FilterBarLayout mFilterBar;

//    @BindView(R.id.footer_order)
//    OrderTypeFilterFooter mOrderTypeFilterFooter;



    private Unbinder mUnBinder;
    private List<OrderTypeModel> mOrderTypeModels;
    private OrderTypeFilterFooter mFilterFooter;
    private Subscription mSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mUnBinder = ButterKnife.bind(this);
        initRxBus();
        initMock();
    }

    private void initRxBus() {
        mSubscription = RxBus.getInstance()
                .toSubscription(OrderTypeItemSelectedEvent.class, new Action1<OrderTypeItemSelectedEvent>() {
                    @Override
                    public void call(OrderTypeItemSelectedEvent orderTypeItemSelectedEvent) {
                        OrderTypeModel orderTypeModel = orderTypeItemSelectedEvent.getOrderTypeModel();
                        String ordername = orderTypeModel.getOrdername();
                        mFilterBar.back(1, ordername);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                        initRxBus();
                    }
                });
    }


    private void initMock() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mOrderTypeModels = new ArrayList<>();
                mOrderTypeModels.add(new OrderTypeModel("1", "全部"));
                mOrderTypeModels.add(new OrderTypeModel("2", "厂直"));
                mOrderTypeModels.add(new OrderTypeModel("3", "统销"));
                mOrderTypeModels.add(new OrderTypeModel("4", "代购"));
                mOrderTypeModels.add(new OrderTypeModel("5", "经销"));
                mOrderTypeModels.add(new OrderTypeModel("6", "核销"));

                initFilterBarLayout(mOrderTypeModels);
            }
        }, 1200);
    }

    private void initFilterBarLayout(List<OrderTypeModel> orderTypeModelList) {
        int size = orderTypeModelList.size();
        float unitHeight = getResources().getDimension(R.dimen.filter_footer_order_type_item_height);
        Log.d(TAG, "OrderTypeList unitHeight = " + unitHeight);
        int footerViewHeight = (int) (size * unitHeight);
        mFilterFooter = new OrderTypeFilterFooter(this, orderTypeModelList);
        FilterBarLayout.LayoutParams layoutParams =
                new FilterBarLayout.LayoutParams(FilterBarLayout.LayoutParams.MATCH_PARENT, footerViewHeight);
        mFilterFooter.setLayoutParams(layoutParams);
        mFilterFooter.setBackgroundColor(getResources().getColor(R.color.white));
        mFilterBar.addFooterView(1, mFilterFooter, FilterBarLayout.FooterMode.MODE_TRANSLATE);
    }


    @Override
    public void onBackPressed() {
        if (mFilterBar != null && mFilterBar.isShowing()) {
            mFilterBar.back();
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUnBinder.unbind();
        mSubscription.unsubscribe();
    }

}
