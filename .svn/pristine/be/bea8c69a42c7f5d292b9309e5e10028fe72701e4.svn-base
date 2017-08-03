package com.iems5722.group5;

import android.graphics.PointF;

import java.util.List;

/**
 * Created by AlexLiu on 24/4/17.
 */

public class FreeDrawRunnable implements Runnable {
    private DrawingView view;
    private String userId;
    private List<PointF> pointList;

    public FreeDrawRunnable(DrawingView v) {
        view = v;
    }

    @Override
    public void run() {
        view.addFreeLineMove(userId, pointList);
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setPointList(List<PointF> pointList) {
        this.pointList = pointList;
    }
}
