package com.sugary.goertzpro.scene.filterbar.footer;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import com.sugary.goertzpro.R;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Ethan on 2017/8/16.
 */

public class ServiceOrderDetailFilterFooter extends LinearLayout {

    @BindView(R.id.iv_product)
    ImageView ivProduct;

    @BindView(R.id.tv_product_name)
    TextView tvProductName;

    @BindView(R.id.tv_hope_account_info)
    TextView tvHope;




    public ServiceOrderDetailFilterFooter(Context context) {
        super(context);
        init();
    }

    public ServiceOrderDetailFilterFooter(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ServiceOrderDetailFilterFooter(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

    }

    private void init(){
        View itemView = LayoutInflater.from(getContext()).inflate(R.layout.filter_footer_service_order_detail, this, false);
        addView(itemView);
        ButterKnife.bind(this,itemView);

    }

    public void bindData(){
        tvProductName.setText("test name");
    }




}
