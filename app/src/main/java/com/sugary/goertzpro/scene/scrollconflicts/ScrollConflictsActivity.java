package com.sugary.goertzpro.scene.scrollconflicts;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.LinearLayout;

import com.sugary.goertzpro.R;
import com.sugary.goertzpro.scene.scrollconflicts.adapter.ConflictsAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ScrollConflictsActivity extends AppCompatActivity {

    @BindView(R.id.recycler_scroll)
    RecyclerView mRecyclerView;

    private List<String> mTitleList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scroll_conflicts);
        ButterKnife.bind(this);
        initRecyclerView();
    }

    private void initRecyclerView(){
        mTitleList.clear();
        mTitleList.add("Test0");
        mTitleList.add("Test1");
        mTitleList.add("Test2");
        mTitleList.add("Test3");
        mTitleList.add("Test4");
        mTitleList.add("Test5");
        mTitleList.add("Test6");
        mTitleList.add("Test7");
        mTitleList.add("Test8");
        mTitleList.add("Test9");
        mTitleList.add("Test10");
        mTitleList.add("Test11");
        mTitleList.add("Test12");
        mTitleList.add("Test13");
        mTitleList.add("Test14");
        mTitleList.add("Test15");
        mTitleList.add("Test16");

        GridLayoutManager layoutManager = new GridLayoutManager(this, 2, LinearLayoutManager.VERTICAL, false){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        ConflictsAdapter adapter = new ConflictsAdapter(mTitleList);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(adapter);
    }
}
