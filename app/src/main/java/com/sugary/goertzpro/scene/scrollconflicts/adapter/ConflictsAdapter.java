package com.sugary.goertzpro.scene.scrollconflicts.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sugary.goertzpro.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Ethan on 2017/10/12.
 */

public class ConflictsAdapter extends RecyclerView.Adapter<ConflictsAdapter.ConflictsViewHolder> {

    private static final String TAG = "ConflictsAdapter";

    private List<String> mDataList;

    public ConflictsAdapter(List<String> dataList) {
        mDataList = dataList;
    }

    @Override
    public ConflictsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_conflicts, parent, false);
        return new ConflictsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ConflictsViewHolder holder, int position) {
        String s = mDataList.get(position);
        holder.bindViewHolder(s);
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    class ConflictsViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.tv_title)
        TextView mTvTitle;

        public ConflictsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }

        void bindViewHolder(String s){
            mTvTitle.setText(s);
        }
    }
}
