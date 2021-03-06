package com.iems5722.group5;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import com.iems5722.group5.DrawingObject.DrawingType;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
/******************************************************
 *  DrawingView is extracted from the project on
 *  https://gitlab.com/ankit_aggarwal/DrawingFun/tree/master
 *
 * The comment "*****IEMS5722 Group5*****" will be added
 * before a block of code  if it is added/modified by our group
 *
 *******************************************************/

public class DrawingView extends View {

    //drawing path
    private Path mDrawPath;
    //drawing and canvas paint
    private Paint mDrawPaint, mCanvasPaint;
    //initial color
    private int mPaintColor = 0xFF660000;
    //canvas
    private Canvas mDrawCanvas;
    //canvas bitmap
    private Bitmap mCanvasBitmap;

    private float mBrushSize, mLastBrushSize;
    private boolean isFilling = false;  //for flood fill

    //*****IEMS5722 Group5*****
    private boolean isAddText = false;  //for adding text
    private boolean isAddEmoji = false;
    private int emojiId;
    private Paint mTextPaint;
    public static ArrayList<DrawingObject> selfList = new ArrayList<DrawingObject>();
    public static ArrayList<DrawingObject> wholeList = new ArrayList<DrawingObject>();
    private Bitmap bgBitmap;
    private DrawingObject currentDO;

    final private int FD_move_emit_freq = 5;
    private int FD_move_emit_count=0;
    private int mTextSize;

    //for scale correction
    private int canvas_height, canvas_weight;
    private float x_trm(float local_x){ return local_x/canvas_weight;}
    private float y_trm(float local_y){ return local_y/canvas_height;}
    private float x_invtrm(float local_x){ return local_x*canvas_weight;}
    private float y_invtrm(float local_y){ return local_y*canvas_height;}

    private boolean isErase;

    public Paint getmDrawPaint(){return mDrawPaint;}

    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupDrawing();
    }

    //get drawing area setup for interaction
    private void setupDrawing() {

        mBrushSize = getResources().getInteger(R.integer.medium_size);
        mLastBrushSize = mBrushSize;

        mDrawPath = new Path();

        mDrawPaint = new Paint();
        mDrawPaint.setColor(mPaintColor);
        mDrawPaint.setAntiAlias(true);
        mDrawPaint.setStrokeWidth(mBrushSize);
        mDrawPaint.setStyle(Paint.Style.STROKE);
        mDrawPaint.setStrokeJoin(Paint.Join.ROUND);
        mDrawPaint.setStrokeCap(Paint.Cap.ROUND);

        mCanvasPaint = new Paint(Paint.DITHER_FLAG);

        //"*****IEMS5722 Group5*****"
        mTextSize = 60;
        mTextPaint = new Paint();
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setColor(mPaintColor);
        mTextPaint.setTextSize(mTextSize);
        selfList.clear();
        wholeList.clear();
    }

    //view given size
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {

        super.onSizeChanged(w, h, oldw, oldh);

        canvas_weight = w;
        canvas_height = h;

        if(bgBitmap!=null)
        {
            bgBitmap = Bitmap.createScaledBitmap(bgBitmap, w, h,true);
            Drawable drawable = new BitmapDrawable(getResources(), bgBitmap);
            this.setBackground(drawable);
            mCanvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888); // bgBitmap.copy(Bitmap.Config.ARGB_8888, true);
            mDrawCanvas = new Canvas(mCanvasBitmap);
            //mDrawCanvas = new Canvas();
        }

    }

    //draw view
    @Override
    protected void onDraw(Canvas canvas) {
        //super.onDraw(canvas);
        canvas.drawBitmap(mCanvasBitmap, 0, 0, mCanvasPaint);

        canvas.drawPath(mDrawPath, mDrawPaint);
    }

    //detect user touch
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();

        //*****IEMS5722 Group5*****
        //else
        if (isAddText) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_UP:
                    Log.d("isAddText", "DrawingBoardActivity.inputText=" + DrawingBoardActivity.inputText);
                    //Log.d("ACTION_UP", "touchX="+ touchX + ", touchY=" +touchY);
                    isAddText = false;
                    mTextPaint.setTextSize(mTextSize);
                    mDrawCanvas.drawText(DrawingBoardActivity.inputText, touchX, touchY, mTextPaint);
                    //mDrawCanvas.drawText();
                    //Log.d("mTextPaint", String.valueOf(mTextPaint.getColor()));
                    emitTextData(DrawingBoardActivity.inputText, touchX, touchY, mTextPaint);
                    break;

                    default:
                        return true;
                }
            invalidate();
        }
        else if (isAddEmoji && emojiId != 0) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_UP:
                    Log.d("isAddEmoji", "DrawingBoardActivity");
                    //Log.d("ACTION_UP", "touchX="+ touchX + ", touchY=" +touchY);
                    isAddEmoji = false;

                    Bitmap bmp = BitmapFactory.decodeResource(getResources(), emojiId);
                    int emojiSize = (int) (mBrushSize * 3.0);
                    bmp = Bitmap.createScaledBitmap(bmp, emojiSize, emojiSize, true);

                    //draw on the center
                    mDrawCanvas.drawBitmap(bmp, touchX-bmp.getScaledWidth(mDrawCanvas)*0.5f,
                            touchY-bmp.getScaledHeight(mDrawCanvas)*0.5f, mTextPaint);
                    emitEmojiData(emojiId, touchX, touchY, emojiSize);
                    //emitTextData(DrawingBoardActivity.inputText, touchX, touchY, mTextPaint);
                    break;

                default:
                    return true;
            }
            invalidate();
        }
        else
        {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mDrawPath.moveTo(touchX, touchY);
                    emit_FD_Start(touchX, touchY);
                    //Log.d("ACTION_DOWN", "touchX="+ touchX + ", touchY=" +touchY);
                    break;

                case MotionEvent.ACTION_MOVE:
                    mDrawPath.lineTo(touchX, touchY);
                    if (currentDO != null) {
                        currentDO.addPoint(touchX, touchY);

                        if (currentDO.getFreeLine().size() % FD_move_emit_freq == 0) {
                            emit_FD_Move();
                        }
                    }

                    break;

                case MotionEvent.ACTION_UP:
                    mDrawCanvas.drawPath(mDrawPath, mDrawPaint);
                    emit_FD_Finish();
                    currentDO.getFreePath().set(new Path(mDrawPath));
                    currentDO = null;

                    mDrawPath.reset();
                    //if(isErase) ReDrawAll();
                    break;

                default:
                    return false;
            }
        }
        invalidate();

        return true;
    }

    //set color
    public void setColor(String newColor) {
        invalidate();

        mPaintColor = Color.parseColor(newColor);
        mDrawPaint.setColor(mPaintColor);
        mDrawPaint.setShader(null);
        mTextPaint.setColor(mPaintColor);
    }

    //update size
    public void setBrushSize(float newSize) {
        mBrushSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                newSize, getResources().getDisplayMetrics());
        mDrawPaint.setStrokeWidth(mBrushSize);
    }

    public void setLastBrushSize(float lastSize) {
        mLastBrushSize = lastSize;
    }

    public float getLastBrushSize() {
        return mLastBrushSize;
    }

    //set mErase true or false
    public void setErase(boolean isErase) {
        this.isErase = isErase;
        setPaintErase(this.mDrawPaint, isErase);
    }

    public static void setPaintErase(Paint paint, boolean isErase) {
        if (isErase) {
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        } else {
            paint.setXfermode(null);
        }
    }

    //clear canvas
    public void startNew() {
        //mDrawCanvas.drawColor(0, PorterDuff.Mode.CLEAR);

        //*****IEMS5722 Group5*****
        mCanvasBitmap = Bitmap.createBitmap(canvas_weight, canvas_height, Bitmap.Config.ARGB_8888); // bgBitmap.copy(Bitmap.Config.ARGB_8888, true);
        mDrawCanvas = new Canvas(mCanvasBitmap);
        //mCanvasBitmap = bgBitmap.copy(bgBitmap.getConfig(), true);
        //mDrawCanvas = new Canvas(mCanvasBitmap);
        //mDrawCanvas = new Canvas();
        invalidate();
    }

    //fill effect
    public void fillColor() {
        isFilling = true;
    }

    //*****IEMS5722 Group5*****
    //addText
    public void addText() {
        isAddText = true;
    }


    //*****IEMS5722 Group5*****
    public void emitTextData(String inputText, float touchX, float touchY, Paint mTextPaint) {

        try {
            DrawingObject textObj = new DrawingObject(DrawingType.TXT);
            //textObj.setUserID("1199887766"); //TODO login user id
            textObj.setUserID(String.valueOf(SignInActivity.guser_id));
            textObj.setTimestamp(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));
            textObj.setColor(mTextPaint.getColor());

            textObj.setText(DrawingBoardActivity.inputText);
            textObj.setX(x_trm(touchX));
            textObj.setY(y_trm(touchY));
            textObj.setTextSize(mTextSize);
            textObj.setlocalratio(x_trm(1));

            selfList.add(textObj);
            wholeList.add(textObj);

            JSONObject json = new JSONObject();
            //json.put("data_type", "add_drawing");
            json.put("user_id", textObj.getUserID());
            json.put("timestamp", textObj.getTimestamp());
            json.put("type", "TXT");
            json.put("color",  textObj.getColor());
            json.put("text", textObj.getText());
            json.put("x", textObj.getX());
            json.put("y",  textObj.getY());
            json.put("textsize", textObj.getTextSize());
            json.put("localratio", textObj.getlocalratio());

            DrawingBoardActivity.socket.emit("event_draw", json);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public void emitEmojiData(Integer emojiId, float touchX, float touchY, Integer size) {

        try {
            DrawingObject emojiObj = new DrawingObject(DrawingType.EMOJI);
            //textObj.setUserID("1199887766"); //TODO login user id
            emojiObj.setUserID(String.valueOf(SignInActivity.guser_id));
            emojiObj.setTimestamp(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));
            //textObj.setColor(mTextPaint.getColor());

            emojiObj.setText(emojiId.toString());
            emojiObj.setX(x_trm(touchX));
            emojiObj.setY(y_trm(touchY));
            emojiObj.setTextSize(size);
            emojiObj.setlocalratio(x_trm(1));

            selfList.add(emojiObj);
            wholeList.add(emojiObj);

            JSONObject json = new JSONObject();
            //json.put("data_type", "add_drawing");
            json.put("user_id", emojiObj.getUserID());
            json.put("timestamp", emojiObj.getTimestamp());
            json.put("type", "EMOJI");
            json.put("text", emojiObj.getText());
            json.put("x", emojiObj.getX());
            json.put("y",  emojiObj.getY());
            json.put("textsize", emojiObj.getTextSize());
            json.put("localratio", emojiObj.getlocalratio());

            DrawingBoardActivity.socket.emit("event_draw", json);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void addEmojiData (DrawingObject obj) {
        wholeList.add(obj); //TODO
        //mDrawCanvas.drawText(obj.getText(), obj.getX(), obj.getY(), obj.getPaint());
        Bitmap bmp = BitmapFactory.decodeResource(getResources(), Integer.valueOf(obj.getText()));

        //Scale the emoji size too
        int scaledsize = (int)(obj.getTextSize()*x_invtrm(obj.getlocalratio()));
        bmp = Bitmap.createScaledBitmap(bmp, scaledsize, scaledsize, true);

        //draw on the center
        mDrawCanvas.drawBitmap(bmp, x_invtrm(obj.getX())-bmp.getScaledWidth(mDrawCanvas)*0.5f,
                y_invtrm(obj.getY())-bmp.getScaledHeight(mDrawCanvas)*0.5f, mTextPaint);
        invalidate();
    }

    //*****IEMS5722 Group5*****
    public void addTextData (DrawingObject obj) {
        wholeList.add(obj); //TODO

        //Scale the font size too
        Paint newpaint = new Paint(obj.getPaint());
        newpaint.setTextSize(newpaint.getTextSize()*x_invtrm(obj.getlocalratio()));

        mDrawCanvas.drawText(obj.getText(), x_invtrm(obj.getX()), y_invtrm(obj.getY()), newpaint);

        invalidate();
    }


    public void addEmoji() {
        isAddEmoji = true;
    }

    public boolean getEmoji(){ return isAddEmoji;}


    //retrieve the latest Path object in wholelist with that userId
    private DrawingObject findLastDrawingObject(String userId) {
        DrawingObject obj = null;

        for (int i = wholeList.size() - 1; i >= 0; i--) {
            if(wholeList.get(i).getUserID().equals((userId)))
            {
                obj = wholeList.get(i);
                break;
            }
        }
        return obj;
    }

    public void addFreeLineFinish(String userId, List<PointF> pointList) {

        DrawingObject obj = findLastDrawingObject(userId);

        if(obj != null)
        {
            //mDrawCanvas.drawPath(obj.getFreePath(), obj.getPaint());
            for(int i = 0; i < pointList.size(); i++) {
                obj.getFreePath().lineTo(x_invtrm(pointList.get(i).x), y_invtrm(pointList.get(i).y));
                //Log.d("addFreeLineMove", "X="+ x + "Y, =" +y);
                mDrawCanvas.drawPath(obj.getFreePath(), obj.getPaint());
            }

            Log.d("addFreeLineFinish", "Finish!");
            invalidate();
        }
    }

    public void addFreeLineMove(String userId, List<PointF> pointList) {

        DrawingObject obj = findLastDrawingObject(userId);

        if(obj != null)
        {
            for(int i = 0; i < pointList.size(); i++) {
                obj.getFreePath().lineTo(x_invtrm(pointList.get(i).x), y_invtrm(pointList.get(i).y));
            }
            mDrawCanvas.drawPath(obj.getFreePath(), obj.getPaint());
            invalidate();
        }
    }

    public void addFreeLineStart(DrawingObject obj) {
        wholeList.add(obj);
        obj.getFreePath().moveTo(x_invtrm(obj.getFreeLine().get(0).x), y_invtrm(obj.getFreeLine().get(0).y));
        Log.d("addFreeLineStart", "X="+ obj.getFreeLine().get(0).x + "Y, =" +obj.getFreeLine().get(0).y);

        mDrawCanvas.drawPath(obj.getFreePath(), obj.getPaint());

        invalidate();
    }

    //*****IEMS5722 Group5*****

    public void emit_FD_Start(float x, float y) {

        try {
            DrawingObject FDObj = new DrawingObject(DrawingType.FREE);
            FDObj.setUserID(String.valueOf(SignInActivity.guser_id));
            FDObj.setTimestamp(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));
            FDObj.setColor(mDrawPaint.getColor());
            FDObj.setFreePath(new Path(mDrawPath));
            FDObj.setLineWidth((int)mDrawPaint.getStrokeWidth());
            FDObj.setIsErase(isErase);
            FDObj.addPoint(x, y);
            setPaintErase(FDObj.getPaint(), isErase);

            selfList.add(FDObj);
            wholeList.add(FDObj);
            currentDO = FDObj;


            JSONObject json = new JSONObject();
            //json.put("data_type", "add_drawing");
            json.put("user_id", FDObj.getUserID());
            json.put("timestamp", FDObj.getTimestamp());
            json.put("type", "FREE");
            json.put("color",  FDObj.getColor());
            json.put("isErase", FDObj.getIsErase());

            JSONObject coord_json_pair =new JSONObject();
            coord_json_pair.put("x", x_trm(x));
            coord_json_pair.put("y", y_trm(y));
            json.put("pt", coord_json_pair);
            json.put("lw", String.valueOf(mDrawPaint.getStrokeWidth()));

            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");

            DrawingBoardActivity.socket.emit("event_FD_Start", json);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void emit_FD_Move() {

        try {
            JSONObject json = new JSONObject();
            json.put("user_id", String.valueOf(SignInActivity.guser_id));

            // put multiple points into one single message
            List<PointF> points = currentDO.getFreeLine();
            JSONArray jpoints = new JSONArray();
            for(int i = points.size() - FD_move_emit_freq; i < points.size(); i++) {
                JSONObject coord_json_pair = new JSONObject();
                coord_json_pair.put("x", x_trm(points.get(i).x));
                coord_json_pair.put("y", y_trm(points.get(i).y));
                jpoints.put(coord_json_pair);
            }
            json.put("pts", jpoints);

            DrawingBoardActivity.socket.emit("event_FD_Move", json);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void emit_FD_Finish() {

        try {
            JSONObject json = new JSONObject();
            json.put("user_id", String.valueOf(SignInActivity.guser_id));

            // put remaining points into one single message
            List<PointF> points = currentDO.getFreeLine();
            int remain = points.size() % FD_move_emit_freq;
            if (remain != 0) {
                JSONArray jpoints = new JSONArray();
                for (int i = points.size() - remain; i < points.size(); i++) {
                    JSONObject coord_json_pair = new JSONObject();
                    coord_json_pair.put("x", x_trm(points.get(i).x));
                    coord_json_pair.put("y", y_trm(points.get(i).y));
                    jpoints.put(coord_json_pair);
                }
                json.put("pts", jpoints);
            }

            DrawingBoardActivity.socket.emit("event_FD_Finish", json);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void emitUndoData(String timestamp) {

        try {
            Log.d("emitUndoData", timestamp);
            JSONObject json = new JSONObject();
            json.put("user_id", String.valueOf(SignInActivity.guser_id));
            json.put("timestamp", timestamp);

            DrawingBoardActivity.socket.emit("event_undo", json);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void ReDrawAll()
    {

        this.startNew(); //clear canvas

        for (int i = 0; i < wholeList.size(); i++ ) {
            //Log.d("wholeList", "i = " + i);
            DrawingObject obj = wholeList.get(i);
            if(obj.getType().equals(DrawingType.TXT)) {

                Paint newpaint = new Paint(obj.getPaint());
                newpaint.setTextSize(newpaint.getTextSize()*x_invtrm(obj.getlocalratio()));

                mDrawCanvas.drawText(obj.getText(), x_invtrm(obj.getX()), y_invtrm(obj.getY()), newpaint);
                Log.d("ReDrawAll", "Text is redrawed at index= " + i);
            }
            else if(obj.getType().equals(DrawingType.FREE))
            {
                mDrawCanvas.drawPath(obj.getFreePath(), obj.getPaint());
                Log.d("ReDrawAll", "FreePath is redrawed at index= " + i);
                //if(obj.getFreePath().isEmpty()) Log.d("Errorrrrr", "Path is empty!!!");
            }
            else if(obj.getType().equals(DrawingType.EMOJI)) {
                Bitmap bmp = BitmapFactory.decodeResource(getResources(), Integer.valueOf(obj.getText()));
                int scaledsize = (int)(obj.getTextSize()*x_invtrm(obj.getlocalratio()));
                //Log.d("scaledsize", "obj.getTextSize() = " + obj.getTextSize());
                //Log.d("scaledsize", "obj.getlocalratio() = " + obj.getlocalratio());
                //Log.d("scaledsize", "x_invtrm(obj.getlocalratio()) = " + x_invtrm(obj.getlocalratio()));


                bmp = Bitmap.createScaledBitmap(bmp, scaledsize, scaledsize, true);

                //draw on the center
                mDrawCanvas.drawBitmap(bmp, x_invtrm(obj.getX())-bmp.getScaledWidth(mDrawCanvas)*0.5f,
                        y_invtrm(obj.getY())-bmp.getScaledHeight(mDrawCanvas)*0.5f, mTextPaint);
                Log.d("ReDrawAll", "EMOJI is redrawed at index= " + i);
            }
        }
        invalidate();
    }

    public void undoDrawingObject(String userID, String TimeStamp){

        Log.d("undoDrawingObject", "selfList.size()=" + selfList.size());
        Log.d("undoDrawingObject", "wholeList.size()=" + wholeList.size());

        for (int i = 0; i < wholeList.size(); i++ ) {
            DrawingObject tempObj = wholeList.get(i);
            if(tempObj.getUserID().equals(userID) && tempObj.getTimestamp().equals(TimeStamp)) {
                wholeList.remove(i);

                if(String.valueOf(SignInActivity.guser_id).equals(userID))
                    selfList.remove(selfList.size()-1);

                Log.d("undoDrawingObject", "obj remove = " + i);
            }
        }
        Log.d("undoDrawingObject", "wholeList.size() after=" + wholeList.size());

        ReDrawAll();

    }

    public void undoMyDrawingObject()
    {
        if (wholeList.size() >= 1 &&  selfList.size() >=1) {
            DrawingObject objUndo = selfList.get(selfList.size() - 1); //get last object in selfList

            if(objUndo!=null) {
                undoDrawingObject(objUndo.getUserID(), objUndo.getTimestamp());
                emitUndoData(objUndo.getTimestamp());
            }
        }
    }

    public void setBgBitmap(Bitmap bmp) {
        bgBitmap = bmp.copy(bmp.getConfig(), true);
    }

    public void setEmojiId(int emojiId) {
        this.emojiId = emojiId;
    }

    public void setTextSize(int textSize) {
        mTextSize = textSize;
    }

    public int getTextSize() {
        return mTextSize;
    }

}
