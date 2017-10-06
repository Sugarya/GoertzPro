package com.sugary.goertzpro.scene.uprefresh;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;

import com.sugary.goertzpro.R;
import com.sugary.goertzpro.scene.uprefresh.adapter.RecyclerAdapter;
import com.sugary.goertzpro.utils.RxBus;
import com.sugary.goertzpro.widget.pullrefresh.PullRefreshFetchDataEvent;
import com.sugary.goertzpro.widget.pullrefresh.UpPullRefreshLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.functions.Action1;

public class PullRefreshActivity extends AppCompatActivity {

    private static final String TAG = "PullRefreshActivity";

    @BindView(R.id.container_body)
    UpPullRefreshLayout mContainerBody;

//    @BindView(R.id.recycler_refresh)
//    RecyclerView mRecyclerRefresh;

    @BindView(R.id.scroll_body)
    ScrollView mScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_up_pull_to_refresh);
        ButterKnife.bind(this);

        initRxBus();
        initRecyclerView();
    }

    private void initRxBus(){
        RxBus.getInstance().toSubscription(PullRefreshFetchDataEvent.class, new Action1<PullRefreshFetchDataEvent>() {
            @Override
            public void call(final PullRefreshFetchDataEvent pullRefreshFetchDataEvent) {
                Log.d(TAG, "call: PullRefreshFetchDataEvent");
                mContainerBody.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mContainerBody.notifyRefreshStatusOnSuccess(pullRefreshFetchDataEvent);
                    }
                }, 1500);
            }
        });
    }

    private void initRecyclerView() {
//        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
//        RecyclerAdapter adapter = new RecyclerAdapter();
//        mRecyclerRefresh.setLayoutManager(layoutManager);
//        mRecyclerRefresh.setAdapter(adapter);
    }

    private void initScrollView(){

    }

}
