package com.sugary.goertzpro.scene.sortbar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.sugary.goertzpro.R;
import com.sugary.goertzpro.widget.sortbar.IndicatorStatusEnum;
import com.sugary.goertzpro.widget.sortbar.SortBarLayout;
import com.sugary.goertzpro.widget.sortbar.SortBarUnit;
import com.sugary.goertzpro.widget.sortbar.ItemSortable;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SortBarActivity extends AppCompatActivity {

    @BindView(R.id.indicator_sort_bar)
    SortBarLayout mSortBarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sort_bar);
        ButterKnife.bind(this);



    }

    public void onToggleClick(View view){

    }
}
