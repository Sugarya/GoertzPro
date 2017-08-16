package com.sugary.goertzpro.scene.filterbar.event;


import com.sugary.goertzpro.scene.filterbar.model.OrderTypeModel;

/**
 * Created by Ethan on 2017/8/14.
 */

public class OrderTypeItemSelectedEvent {

    private OrderTypeModel mOrderTypeModel;

    public OrderTypeItemSelectedEvent(OrderTypeModel orderTypeModel) {
        mOrderTypeModel = orderTypeModel;
    }

    public OrderTypeModel getOrderTypeModel() {
        return mOrderTypeModel;
    }

    public void setOrderTypeModel(OrderTypeModel orderTypeModel) {
        mOrderTypeModel = orderTypeModel;
    }

}
