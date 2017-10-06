package com.sugary.goertzpro.scene.uprefresh;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.widget.ScrollView;

import com.sugary.goertzpro.R;
import com.sugary.goertzpro.scene.uprefresh.adapter.RecyclerAdapter;
import com.sugary.goertzpro.utils.RxBus;
import com.sugary.goertzpro.widget.enhancerecycler.EnhanceRecyclerView;
import com.sugary.goertzpro.widget.pullrefresh.PullDownRefreshDataEvent;
import com.sugary.goertzpro.widget.pullrefresh.UpPullRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.functions.Action1;

public class PullRefreshActivity extends AppCompatActivity {

    private static final String TAG = "PullRefreshActivity";

    @BindView(R.id.container_body)
    UpPullRefreshLayout mContainerBody;

    @BindView(R.id.recycler_refresh)
    EnhanceRecyclerView mRecyclerRefresh;

    @BindView(R.id.scroll_body)
    ScrollView mScrollView;

    private List<String> mTitleList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_up_pull_to_refresh);
        ButterKnife.bind(this);

        initRxBus();
        initRecyclerView();
    }

    private void initRxBus() {
        RxBus.getInstance().toSubscription(PullDownRefreshDataEvent.class, new Action1<PullDownRefreshDataEvent>() {
            @Override
            public void call(final PullDownRefreshDataEvent pullDownRefreshDataEvent) {
                Log.d(TAG, "call: PullDownRefreshDataEvent");
                mContainerBody.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mContainerBody.notifyRefreshStatusOnSuccess(pullDownRefreshDataEvent);
                    }
                }, 1500);
            }
        });
    }

    private void initRecyclerView() {
        mTitleList = new ArrayList<>();
        mTitleList.add("test1");
        mTitleList.add("test2");
        mTitleList.add("test3");
        mTitleList.add("test4");
        mTitleList.add("test5");
        mTitleList.add("test6");
        mTitleList.add("test7");
        mTitleList.add("test8");
        mTitleList.add("test9");
        mTitleList.add("test10");
        mTitleList.add("test11");
        mTitleList.add("test12");
        mTitleList.add("test13");
        mTitleList.add("test14");
        mTitleList.add("test15");
        mTitleList.add("test16");
        mTitleList.add("test17");
        mTitleList.add("test18");
        mTitleList.add("test19");
        mTitleList.add("test20");


        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        RecyclerAdapter adapter = new RecyclerAdapter(mTitleList);
        mRecyclerRefresh.setLayoutManager(layoutManager);
        mRecyclerRefresh.setAdapter(adapter);

        mRecyclerRefresh.setOnLoadMoreListener(new EnhanceRecyclerView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                mRecyclerRefresh.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        int originSize = mTitleList.size();
                        mRecyclerRefresh.loadMoreOnSuccess();
                        mTitleList.add("test21");
                        mTitleList.add("test22");
                        mRecyclerRefresh.notifyItemRangeInserted(originSize, 2);
                    }
                }, 1200);
            }
        });
    }


}
