package com.sugary.goertzpro.scene.uprefresh.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sugary.goertzpro.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Ethan on 2017/10/6.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder> {

    private List<String> mDataList;

    public RecyclerAdapter() {
        mDataList = new ArrayList<>();
        mDataList.add("test1");
        mDataList.add("test2");
        mDataList.add("test3");
        mDataList.add("test4");
        mDataList.add("test5");
        mDataList.add("test6");
        mDataList.add("test7");
        mDataList.add("test8");
        mDataList.add("test9");
        mDataList.add("test10");
        mDataList.add("test11");
        mDataList.add("test12");
        mDataList.add("test13");
        mDataList.add("test14");
        mDataList.add("test15");
        mDataList.add("test16");
        mDataList.add("test17");
        mDataList.add("test18");
        mDataList.add("test19");
        mDataList.add("test20");
        mDataList.add("test21");
        mDataList.add("test22");
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_up_pull_refresh, parent, false);
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

        @BindView(R.id.tv_title)
        TextView mTvTitle;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void onBindViewHolder(String s){
            mTvTitle.setText(s);
        }
    }
}
