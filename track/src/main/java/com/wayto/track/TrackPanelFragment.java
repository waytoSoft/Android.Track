package com.wayto.track;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.wayto.track.common.TrackConstant;
import com.wayto.track.data.TrackContract;
import com.wayto.track.history.TrackHistoryActivity;
import com.wayto.track.service.FloatingService;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 足迹面板模式
 * <p>
 * author: hezhiWu <hezhi.woo@gmail.com>
 * version: V1.0
 * created at 2017/11/22 15:43
 * <p>
 * Copyright (c) 2017 Shenzhen O&M Cloud Co., Ltd. All rights reserved.
 */
public class TrackPanelFragment extends Fragment implements TrackContract.TrackPanelView {

    @BindView(R.id.Track_Panel_GPS_Status)
    TextView TrackPanelGPSStatus;
    @BindView(R.id.Track_Panel_Distance)
    TextView TrackPanelDistance;
    @BindView(R.id.Track_Panel_Time)
    TextView TrackPanelTime;
    @BindView(R.id.Track_Panel_Speed)
    TextView TrackPanelSpeed;
    @BindView(R.id.Track_Panel_Dis_layout)
    RelativeLayout TrackPanelDisLayout;
    @BindView(R.id.Track_start_button)
    Button TrackStartButton;
    @BindView(R.id.Track_stop_button)
    ImageView TrackStopButton;
    @BindView(R.id.Track_continue_button)
    ImageView TrackContinueButton;
    @BindView(R.id.Track_end_button)
    ImageView TrackEndButton;
    @BindView(R.id.Track_Panel_Action_layout)
    RelativeLayout TrackPanelActionLayout;
    @BindView(R.id.Track_Panel_Timer_textView)
    TextView TrackPanelTimerTextView;
    Unbinder unbinder;

    private TrackContract.Presenter mPresenter;
    private long trackId;
    private int status;

    public TrackPanelFragment() {

    }

    public static TrackPanelFragment newInstance() {
        return new TrackPanelFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        trackId = getArguments().getLong("trackId", 0);
        status = getArguments().getInt("status", -1);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_track_panel, null);
        unbinder = ButterKnife.bind(this, view);
        mPresenter.onCheckTrack(trackId);
        initUI();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();

        mPresenter.dettach();
    }

    /**
     * 初始化UI
     * <p>
     * author: hezhiWu
     * created at 2017/11/29 11:56
     */
    private void initUI() {
        if (status == TrackConstant.TRACK_START) {
            mPresenter.onStartTrackGather(false);
        } else if (status == TrackConstant.TRACK_STOP) {
            mPresenter.onStopTrackGather();
        } else if (status == TrackConstant.TRACK_CONTINUE) {
            mPresenter.onContinueTrackGather();
        }
    }

    @OnClick({R.id.Track_Panel_back_ImageView, R.id.Track_Panel_history_ImageView, R.id.Track_Panel_Map_ImageView, R.id.Track_start_button, R.id.Track_stop_button, R.id.Track_continue_button, R.id.Track_end_button})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.Track_Panel_back_ImageView:/*返回*/
                FloatingService.addFloatingButton(getActivity());
                getActivity().finish();
                break;
            case R.id.Track_Panel_history_ImageView:/*历史*/
                Intent intent = new Intent(getActivity(), TrackHistoryActivity.class);
                startActivity(intent);
                break;
            case R.id.Track_Panel_Map_ImageView:/*切换地图模式*/
                mPresenter.onSwitchFragment(TrackConstant.TRACK_MAP_FRAGMENT);
                break;
            case R.id.Track_start_button:/*开始*/
                mPresenter.onStartTrackGather(true);
                break;
            case R.id.Track_stop_button:/*停止*/
                mPresenter.onStopTrackGather();
                break;
            case R.id.Track_continue_button:/*继续*/
                mPresenter.onContinueTrackGather();
                break;
            case R.id.Track_end_button:/*结束*/
                mPresenter.onEndTrackGater();
                break;
        }
    }

    @Override
    public void setmPresenter(TrackContract.Presenter mPresenter) {
        this.mPresenter = mPresenter;
    }

    @Override
    public void onCountDownViewVisibility(int visibility) {
        TrackPanelTimerTextView.setVisibility(visibility);
    }

    @Override
    public void showCountDownViewNumber(final int number) {
        TrackPanelTimerTextView.setText(String.valueOf(number));
    }

    @Override
    public void onTrackPanelActionLayoutVisibility(int visibility) {
        TrackPanelActionLayout.setVisibility(visibility);
    }

    @Override
    public void onTrackStartButtonVisibility(int visibility) {
        TrackStartButton.setVisibility(visibility);
    }

    @Override
    public void onTrackContinueButtonVisibility(int visibility) {
        TrackContinueButton.setVisibility(visibility);
    }

    @Override
    public void onTrackStopButtonVisibility(int visibility) {
        TrackStopButton.setVisibility(visibility);
    }

    @Override
    public void onTrackEndButtonVisibility(int visibility) {
        TrackEndButton.setVisibility(visibility);
    }

    @Override
    public void resetView() {
        TrackPanelDistance.setText("0.00");
        TrackPanelTime.setText("00:00:00");
        TrackPanelSpeed.setText("0.00");
    }

    @Override
    public void onShowTrackDistance(String distance) {
        TrackPanelDistance.setText(distance);
    }

    @Override
    public void onShowTrackSpeed(String speed) {
        TrackPanelSpeed.setText(speed);
    }

    @Override
    public void onShowTrackTime(String time) {
        TrackPanelTime.setText(time);
    }

    @Override
    public void onShowTrackGpsStatues(int status) {
        if (status== AMapLocation.GPS_ACCURACY_UNKNOWN){
            TrackPanelGPSStatus.setText(R.string.gps_unknown);
        }else if (status==AMapLocation.GPS_ACCURACY_BAD){
            TrackPanelGPSStatus.setText(R.string.gps_bad);
        }else if (status==AMapLocation.GPS_ACCURACY_GOOD){
            TrackPanelGPSStatus.setText(R.string.gps_good);
        }
    }
}
