package com.iems5722.group5;

import android.app.Activity;
import android.app.Application;
import android.content.res.Resources;
import android.graphics.Bitmap;

import com.iems5722.group5.AsyncTasks.DownloadImageAsyncTask;

import java.util.Date;
import java.text.SimpleDateFormat;

/**
 * Created by AlexLiu on 31/1/17.
 */

public class MessageItem {
    private String message;
    private Date time;
    private Boolean isMyMsg;
    private String userId;
    private String user;
    private String imageName;
    private Bitmap image;

    public MessageItem(String id, String user, String msg, Boolean isMine)
    {
        this.user = user;
        userId = id;
        message = msg;
        imageName = "";
        image = null;
        time = new Date(System.currentTimeMillis());
        isMyMsg = isMine;
    }

    public MessageItem(String id, String user, String bmpname, Bitmap bmp, Boolean isMine)
    {
        this.user = user;
        userId = id;
        message = "";
        imageName = bmpname;
        image = bmp;
        time = new Date(System.currentTimeMillis());
        isMyMsg = isMine;
    }

    public MessageItem(Activity acty, String id, String user, String msg, String bmpname, Date t, Boolean isMine)
    {
        this.user = user;
        userId = id;
        message = msg;
        time = t;
        isMyMsg = isMine;
        imageName = bmpname;
        image = null;
        //downloadImage(acty);
    }

    private void downloadImage(Activity acty) {
        new DownloadImageAsyncTask(this, null).execute(
                acty.getString(R.string.URL_DOWNLOAD_IMAGE) + "?file_name=" + getImageName()
        );

    }

    public String getMessage()
    {
        return message;
    }

    public Date getTime()
    {
        return time;
    }

    public Boolean getIsMyMsg()
    {
        return isMyMsg;
    }

    public String getUser() { return user; }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getImageName() { return imageName; }

    public String getUserId() { return userId; }
}
