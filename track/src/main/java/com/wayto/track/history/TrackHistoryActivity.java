package com.wayto.track.history;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.wayto.track.R;
import com.wayto.track.history.data.TrackHistoryContract;
import com.wayto.track.history.data.TrackHistoryPresenter;
import com.wayto.track.storage.TrackTable;
import com.wayto.track.widget.PullToRefresRecyclerView;
import com.wayto.track.widget.PullToRefreshRecyclerViewListener;
import com.wayto.track.widget.RecyclerViewBaseAdapter;
import com.wayto.track.widget.SwipeBackLayout;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 本地历史
 * <p>
 * author: hezhiWu <hezhi.woo@gmail.com>
 * version: V1.0
 * created at 2017/12/29 15:32
 * <p>
 * Copyright (c) 2017 Shenzhen O&M Cloud Co., Ltd. All rights reserved.
 */
public class TrackHistoryActivity extends AppCompatActivity implements PullToRefreshRecyclerViewListener, TrackHistoryContract.QueryTrackHistoryView{

    @BindView(R.id.Track_toolbar)
    Toolbar mToolbar;
    @BindView(R.id.Track_History_RecyclerView)
    PullToRefresRecyclerView mRecyclerView;

    private ActionBar mActionBar;

    private TrackHistoryPresenter mPresenter;

    private TrackHistoryAdapter mAdapter;

    private int indext=0;
    private int pageSize=20;

    private SwipeBackLayout swipeBackLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_history);
        ButterKnife.bind(this);

        swipeBackLayout=new SwipeBackLayout(this);
        swipeBackLayout.replaceLayer(this);

        mRecyclerView.setPullToRefreshRecyclerViewListener(this);

        //init Toolbar
        mToolbar = findViewById(R.id.Track_toolbar);
        setSupportActionBar(mToolbar);
        mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mPresenter=new TrackHistoryPresenter(this);

        mAdapter=new TrackHistoryAdapter(this);
        mRecyclerView.setRecyclerViewAdapter(mAdapter);
        mRecyclerView.setOnItemClickListener(new RecyclerViewBaseAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, Object data, int position) {
                TrackTable table=(TrackTable)data;

                Bundle bundle=new Bundle();
                bundle.putLong("id",table.getId());

                Intent intent=new Intent(TrackHistoryActivity.this,TrackDetailsActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        mRecyclerView.startDownRefresh();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.dettach();
        mPresenter.dettach();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0,R.anim.slide_out_right);
    }

    @Override
    public void onDownRefresh() {
        indext=0;
        mPresenter.queryTrack(indext,pageSize);
    }

    @Override
    public void onPullRefresh() {
        indext++;
        mPresenter.queryTrack(indext,pageSize);
    }

    @Override
    public void onQueryTrackSuccess(List<TrackTable> trackTables) {
        mAdapter.clearList();
        mAdapter.addItems(trackTables);
    }

    @Override
    public void onQueryTrackMoreSuccess(List<TrackTable> trackTables) {
        mAdapter.addItems(mAdapter.getItemCount(),trackTables);
    }

    @Override
    public void onQueryTrackFailure(String msg) {
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onQueryEmpty() {
//        mRecyclerView.showEmptyTextView();
        mRecyclerView.showEmptyTextView(R.string.empty_history);
    }

    @Override
    public void onCloseDownRefresh() {
        mRecyclerView.onCloseDownRefresh();
    }

    @Override
    public void onClosePullRefresh() {
        mRecyclerView.onCloseLoadMore();
    }

    @Override
    public void onLoadMoreComplete() {
        mRecyclerView.onLoadMoreComplete();
    }
}
