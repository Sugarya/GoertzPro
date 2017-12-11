package com.sugary.goertzpro.scene.sortbar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.sugary.goertzpro.R;
import com.sugary.goertzpro.scene.sortbar.model.SortBarModel;
import com.sugary.goertzpro.widget.sortbar.IndicatorStatusEnum;
import com.sugary.goertzpro.widget.sortbar.SortBarLayout;
import com.sugary.goertzpro.widget.sortbar.SortBarUnit;
import com.sugary.goertzpro.widget.sortbar.ItemSortable;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SortBarActivity extends AppCompatActivity {

    private static final String TAG = "SortBarActivity";

    @BindView(R.id.indicator_sort_bar)
    SortBarLayout mSortBarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sort_bar);
        ButterKnife.bind(this);

        mSortBarLayout.setOnItemClickListener(new SortBarLayout.OnItemClickListener() {
            @Override
            public void onItemClick(int index, ItemSortable itemSortable, IndicatorStatusEnum statusEnum) {
                Log.d(TAG, "onItemClick: index = " + index + " title = " + itemSortable.getTitle() + " statusEnum = " + statusEnum.toString());
            }
        });
    }

    public void onToggleClick(View view){
        List<SortBarModel> sortBarModelList = new ArrayList<>();
        for(int i = 0; i < 5; i++){
            boolean hasSortView = true;
            if(i == 0 || i == 1 || i == 4){
                hasSortView = false;
            }
            SortBarModel model = new SortBarModel("测试" + i, "" + i, hasSortView);
            sortBarModelList.add(model);
        }
        mSortBarLayout.bindData(sortBarModelList);
    }
}
