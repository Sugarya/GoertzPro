package com.sugary.goertzpro.scene.filterbar.model;


import com.sugary.goertzpro.base.BaseModel;

/**
 * Created by Ethan on 2017/8/14.
 * 订单类型
 */

public class OrderTypeModel extends BaseModel {

    private String orderid;
    private String ordername;
    private boolean isSelected = false;

    public OrderTypeModel(String orderid, String ordername) {
        this.orderid = orderid;
        this.ordername = ordername;
    }

    public String getOrderid() {
        return orderid;
    }

    public void setOrderid(String orderid) {
        this.orderid = orderid;
    }

    public String getOrdername() {
        return ordername;
    }

    public void setOrdername(String ordername) {
        this.ordername = ordername;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
