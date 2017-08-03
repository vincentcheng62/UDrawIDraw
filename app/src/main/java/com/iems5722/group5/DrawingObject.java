package com.iems5722.group5;


import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class DrawingObject {

    public enum DrawingType {
        TXT,
        EMOJI,
        CIRCLE,
        RECT,
        FREE
    }

    private DrawingType type;
    private String userID;
    private String timestamp; // yyyyMMddHHmmsszzz
    private Integer color;
    private Boolean isActive;

    private int textSize; //for TXT
    private String text; // for TXT

    private float localratio; // for scale correction for text/emoji
    private float x;// for TXT
    private float y;// for TXT

    private Integer emojiId; // for EMOJI

    private Point circleCenter; // for CIRCLE
    private Integer radius; // for CIRCLE

    private Rect rect; // for RECT

    private List<PointF> freeLine; // for FREE
    private Integer lineWidth; // for FREE
    private boolean isErase;

    private Path mDrawPath;
    private Paint paint;


    public DrawingObject(DrawingType type) {
        paint = new Paint();
        isActive = true;

        this.type = type;
        switch (this.type) {
            case TXT:
                paint.setStyle(Paint.Style.FILL);
                break;
            case FREE:
                paint.setAntiAlias(true);
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeJoin(Paint.Join.ROUND);
                paint.setStrokeCap(Paint.Cap.ROUND);
                freeLine = new ArrayList<PointF>();
                break;
            default:

        }
    }

    public DrawingType getType() {
        return type;
    }

    //public void setType(DrawingType type) {
    //    this.type = type;
    //}

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public Integer getColor() {
        return color;
    }

    public void setColor(Integer color) {
        this.color = color;
        paint.setColor(color);
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public Boolean getIsErase() {
        return isErase;
    }

    public void setIsErase(Boolean isErase) {
        this.isErase = isErase;
    }

    public int getTextSize() {
        return textSize;
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
        paint.setTextSize(textSize);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }


    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getlocalratio() {
        return localratio;
    }

    public void setlocalratio(float scaleratio) {
        this.localratio = scaleratio;
    }

    public Integer getEmojiId() {
        return emojiId;
    }

    public void setEmojiId(Integer emojiId) {
        this.emojiId = emojiId;
    }

    public Point getCircleCenter() {
        return circleCenter;
    }

    public void setCircleCenter(Point circleCenter) {
        this.circleCenter = circleCenter;
    }

    public Integer getRadius() {
        return radius;
    }

    public void setRadius(Integer radius) {
        this.radius = radius;
    }

    public Rect getRect() {
        return rect;
    }

    public void setRect(Rect rect) {
        this.rect = rect;
    }



    public Integer getLineWidth() {
        return lineWidth;
    }

    public void setLineWidth(Integer lineWidth) {
        this.lineWidth = lineWidth;
        paint.setStrokeWidth(lineWidth);
    }

    public void setFreeLine(List<PointF> freeLine) {
        this.freeLine = freeLine;
    }

    public List<PointF> getFreeLine() {
        return freeLine;
    }

    public void undo() {
        isActive = false;
    }

    public void setFreePath(Path mDrawPath) {
        this.mDrawPath = mDrawPath;
    }

    public Path getFreePath() {
        return mDrawPath;
    }

    public Paint getPaint() { return paint; }

    public void addPoint(float x, float y) {
        if (type != DrawingType.FREE) {
            return;
        }
        freeLine.add(new PointF(x, y));
    }
}
