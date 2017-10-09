package com.sugary.goertzpro.scene.uprefresh.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sugary.goertzpro.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Ethan on 2017/10/6.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder> {

    private List<String> mDataList;
    private Context mContext;

    public RecyclerAdapter(List<String> dataList) {
        mDataList = dataList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_up_pull_refresh, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        String s = mDataList.get(position);
        holder.onBindViewHolder(s);
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.container_item_refresh)
        LinearLayout mContainerItem;

        @BindView(R.id.tv_title)
        TextView mTvTitle;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void onBindViewHolder(String s){
            mTvTitle.setText(s);
        }

        @OnClick(R.id.container_item_refresh)
        void onItemClick(){
            int position = getAdapterPosition();
            Toast.makeText(mContext, "item click position = " + position, Toast.LENGTH_SHORT).show();
        }
    }
}
