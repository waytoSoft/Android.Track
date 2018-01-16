/*
 * Copyright 2015 Eric Liu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.wayto.track.widget;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;

import com.wayto.track.R;
import com.wayto.track.utils.IDensityUtil;


/**
 * Activity 滑动删除
 * <p>
 * author: hezhiWu <wuhezhi007@gmail.com>
 * version: V1.0.0
 * created at 2017/3/14 10:18
 * <p>
 * Copyright (c) 2017 Shenzhen O&M Cloud Co., Ltd. All rights reserved.
 */
public class SwipeBackLayout extends FrameLayout {

    //private View backgroundLayer;用来设置滑动时的背景色
    private Drawable leftShadow;

    /**
     * 是否可以滑动关闭页面
     */
    protected boolean swipeEnabled = true;

    /**
     * 是否可以在页面任意位置右滑关闭页面，如果是false则从左边滑才可以关闭。
     */
    protected boolean swipeAnyWhere = false;

    private boolean swipeFinished = false;

    public SwipeBackLayout(Context context) {
        super(context);
    }

    public SwipeBackLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SwipeBackLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void replaceLayer(Activity activity) {
        leftShadow = activity.getResources().getDrawable(R.mipmap.left_shadow);
        touchSlop = (int) (touchSlopDP * activity.getResources().getDisplayMetrics().density);
        sideWidth = (int) (sideWidthInDP * activity.getResources().getDisplayMetrics().density);
        mActivity = activity;
        screenWidth = IDensityUtil.getScreenW(activity);
        setClickable(true);
        final ViewGroup root = (ViewGroup) activity.getWindow().getDecorView();
        content = root.getChildAt(0);
        content.setBackgroundColor(getResources().getColor(R.color.transparency));
        ViewGroup.LayoutParams params = content.getLayoutParams();
        ViewGroup.LayoutParams params2 = new ViewGroup.LayoutParams(-1, -1);
        root.removeView(content);

        this.addView(content, params2);
        root.addView(this, params);
    }

    @Override
    protected boolean drawChild(@NonNull Canvas canvas, @NonNull View child, long drawingTime) {
        boolean result = super.drawChild(canvas, child, drawingTime);
        final int shadowWidth = leftShadow.getIntrinsicWidth();
        int left = (int) (getContentX()) - shadowWidth;
        leftShadow.setBounds(left, child.getTop(), left + shadowWidth, child.getBottom());
        leftShadow.draw(canvas);
        return result;
    }

    boolean canSwipe = false;
    /**
     * 超过了touchslop仍然没有达到没有条件，则忽略以后的动作
     */
    boolean ignoreSwipe = false;
    View content;
    Activity mActivity;
    int sideWidthInDP = 16;
    int sideWidth = 72;
    int screenWidth = 1080;
    VelocityTracker tracker;

    float downX;
    float downY;
    float lastX;
    float currentX;
    float currentY;

    int touchSlopDP = 30;
    int touchSlop = 60;

    @Override
    public boolean dispatchTouchEvent(@NonNull MotionEvent ev) {
        try {
            if (swipeEnabled && !canSwipe && !ignoreSwipe) {
                if (swipeAnyWhere) {
                    switch (ev.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            downX = ev.getX();
                            downY = ev.getY();
                            currentX = downX;
                            currentY = downY;
                            lastX = downX;
                            break;
                        case MotionEvent.ACTION_MOVE:
                            float dx = ev.getX() - downX;
                            float dy = ev.getY() - downY;
                            if (dx * dx + dy * dy > touchSlop * touchSlop) {
                                if (dy == 0f || Math.abs(dx / dy) > 1) {
                                    downX = ev.getX();
                                    downY = ev.getY();
                                    currentX = downX;
                                    currentY = downY;
                                    lastX = downX;
                                    canSwipe = true;
                                    tracker = VelocityTracker.obtain();
                                    return true;
                                } else {
                                    ignoreSwipe = true;
                                }
                            }
                            break;
                    }
                } else if (ev.getAction() == MotionEvent.ACTION_DOWN && ev.getX() < sideWidth) {
                    canSwipe = true;
                    tracker = VelocityTracker.obtain();
                    return true;
                }
            }
            if (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_CANCEL) {
                ignoreSwipe = false;
            }
            return super.dispatchTouchEvent(ev);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return true;
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return canSwipe || super.onInterceptTouchEvent(ev);
    }

    boolean hasIgnoreFirstMove;

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        if (canSwipe) {
            tracker.addMovement(event);
            int action = event.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    downX = event.getX();
                    downY = event.getY();
                    currentX = downX;
                    currentY = downY;
                    lastX = downX;
                    break;
                case MotionEvent.ACTION_MOVE:
                    currentX = event.getX();
                    currentY = event.getY();
                    float dx = currentX - lastX;
                    if (dx != 0f && !hasIgnoreFirstMove) {
                        hasIgnoreFirstMove = true;
                        dx = dx / dx;
                    }
                    if (getContentX() + dx < 0) {
                        setContentX(0);
                    } else {
                        setContentX(getContentX() + dx);
                    }
                    lastX = currentX;
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    tracker.computeCurrentVelocity(10000);
                    tracker.computeCurrentVelocity(1000, 20000);
                    canSwipe = false;
                    hasIgnoreFirstMove = false;
                    int mv = screenWidth / 200 * 1000;
                    if (Math.abs(tracker.getXVelocity()) > mv) {
                        animateFromVelocity(tracker.getXVelocity());
                    } else {
                        if (getContentX() > screenWidth / 2) {
                            animateFinish(false);
                        } else {
                            animateBack(false);
                        }
                    }
                    tracker.recycle();
                    break;
                default:
                    break;
            }
        }
        return super.onTouchEvent(event);
    }

    ObjectAnimator animator;

    public void cancelPotentialAnimation() {
        if (animator != null) {
            animator.removeAllListeners();
            if (Looper.myLooper()!=null) {
                animator.cancel();
            }
        }
    }

    public void setContentX(float x) {
        int ix = (int) x;
        content.setX(ix);
        invalidate();
    }

    public float getContentX() {
        return content.getX();
    }


    /**
     * 弹回，不关闭，因为left是0，所以setX和setTranslationX效果是一样的
     *
     * @param withVel 使用计算出来的时间
     */
    private void animateBack(boolean withVel) {
        cancelPotentialAnimation();
        animator = ObjectAnimator.ofFloat(this, "contentX", getContentX(), 0);
        int tmpDuration = withVel ? ((int) (duration * getContentX() / screenWidth)) : duration;
        if (tmpDuration < 100) {
            tmpDuration = 100;
        }
        animator.setDuration(tmpDuration);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.start();
    }

    private void animateFinish(boolean withVel) {
        cancelPotentialAnimation();
        animator = ObjectAnimator.ofFloat(this, "contentX", getContentX(), screenWidth);
        int tmpDuration = withVel ? ((int) (duration * (screenWidth - getContentX()) / screenWidth)) : duration;
        if (tmpDuration < 100) {
            tmpDuration = 100;
        }
        animator.setDuration(tmpDuration);
        animator.setInterpolator(new DecelerateInterpolator());

        animator.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (!mActivity.isFinishing()) {
                    swipeFinished = true;
                    mActivity.finish();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }
        });
        animator.start();
    }

    private final int duration = 200;

    private void animateFromVelocity(float v) {
        if (v > 0) {
            if (getContentX() < screenWidth / 2 && v * duration / 1000 + getContentX() < screenWidth / 2) {
                animateBack(false);
            } else {
                animateFinish(true);
            }
        } else {
            if (getContentX() > screenWidth / 2 && v * duration / 1000 + getContentX() > screenWidth / 2) {
                animateFinish(false);
            } else {
                animateBack(true);
            }
        }
    }

    public void setSwipeAnyWhere(boolean swipeAnyWhere) {
        this.swipeAnyWhere = swipeAnyWhere;
    }

    public boolean isSwipeAnyWhere() {
        return swipeAnyWhere;
    }

    public void setSwipeEnabled(boolean swipeEnabled) {
        this.swipeEnabled = swipeEnabled;
    }

    public boolean isSwipeEnabled() {
        return swipeEnabled;
    }

    public boolean isSwipeFinished() {
        return swipeFinished;
    }

    public void setSwipeFinished(boolean swipeFinished) {
        this.swipeFinished = swipeFinished;
    }
}
