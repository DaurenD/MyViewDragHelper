package com.example.user.vdhlearn;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by User on 7/22/2017.
 */

public class DragLayout extends LinearLayout {

    private String TAG = "DragLayout";
    private int mDragBorder;

    private class DragHelperCallback extends ViewDragHelper.Callback {

        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return true;
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            int topBound = getPaddingTop();
            int bottomBound = mDragVerticalRange;

            int newTop = Math.min(Math.max(topBound, top), bottomBound);
            return newTop;
        }

        @Override
        public int getViewVerticalDragRange(View child) {
            Log.d(TAG, "getViewVerticalDragRange: " + mDragVerticalRange);
            return mDragVerticalRange;
        }

        @Override
        public void onEdgeTouched(int edgeFlags, int pointerId) {
            Log.d(TAG, "onEdgeTouched: " + pointerId);
            super.onEdgeTouched(edgeFlags, pointerId);
        }

        @Override
        public void onEdgeDragStarted(int edgeFlags, int pointerId) {
            mDragHelper.captureChildView(mDragView, pointerId);
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            mDragBorder = top;
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            boolean isOpen = false;

            if (yvel > 800) {
                isOpen = true;
            } else if (yvel < -800) {
                isOpen = false;
            } else if (mDragBorder > mDragVerticalRange / 2) {
                isOpen = true;
            } else if (mDragBorder < mDragVerticalRange / 2) {
                isOpen = false;
            }
            int settleYDest = isOpen ? mDragVerticalRange : 0;

            Log.d(TAG, "onViewReleased: settleYDest: " + settleYDest + "\tmDragBorder: " + mDragBorder + "\trange: " + mDragVerticalRange);
            if (mDragHelper.smoothSlideViewTo(releasedChild, releasedChild.getLeft(), settleYDest)) {
                ViewCompat.postInvalidateOnAnimation(DragLayout.this);
            }
        }
    }

    ViewDragHelper mDragHelper;
    View mDragView;
    int mDragVerticalRange;

    public DragLayout(Context context) {
        super(context);
        init();
    }

    public DragLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DragLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mDragHelper = ViewDragHelper.create(this, 1.f, new DragHelperCallback());
        mDragHelper.setEdgeTrackingEnabled(ViewDragHelper.EDGE_ALL);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mDragView = getChildAt(0);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int action = MotionEventCompat.getActionMasked(ev);
        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            mDragHelper.cancel();
            return false;
        }
        return mDragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mDragHelper.processTouchEvent(event);
        return true;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        mDragVerticalRange = (int) (0.9 * getMeasuredHeight());
        super.onLayout(changed, l, t, r, b);
    }
}
