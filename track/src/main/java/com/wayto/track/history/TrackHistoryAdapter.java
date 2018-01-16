package com.wayto.track.history;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wayto.track.R;
import com.wayto.track.storage.TrackTable;
import com.wayto.track.utils.IUtils;
import com.wayto.track.widget.RecyclerViewBaseAdapter;

import java.text.DecimalFormat;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * author: hezhiWu <hezhi.woo@gmail.com>
 * version: V1.0
 * created at 2018/1/8 11:38
 * <p>
 * Copyright (c) 2017 Shenzhen O&M Cloud Co., Ltd. All rights reserved.
 */
public class TrackHistoryAdapter extends RecyclerViewBaseAdapter<TrackTable> {

    public TrackHistoryAdapter(Context context) {
        super(context);
    }

    @Override
    public RecyclerView.ViewHolder onBaseCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(mInflater.inflate(R.layout.item_track_history, parent, false));
    }

    @Override
    public void onBaseBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.dateTextView.setText(IUtils.formatDate(mList.get(position).getStartTime(), "MM月dd日"));
        viewHolder.startTimeTextView.setText(String.format(mContent.getResources().getString(R.string.start_time),
                IUtils.formatDate(mList.get(position).getStartTime(), "HH:mm")));
        viewHolder.endTimeTextView.setText(String.format(mContent.getResources().getString(R.string.end_time),
                IUtils.formatDate(mList.get(position).getFinishTime(), "HH:mm")));
        viewHolder.distanceTextView.setText(String.valueOf(new DecimalFormat("######0.00").format(mList.get(position).getDistance() / 1000)));
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.Item_Track_His_Date)
        TextView dateTextView;
        @BindView(R.id.Item_Track_His_StartTime)
        TextView startTimeTextView;
        @BindView(R.id.Item_Track_His_EndTime)
        TextView endTimeTextView;
        @BindView(R.id.Item_Track_His_Distance)
        TextView distanceTextView;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

}
