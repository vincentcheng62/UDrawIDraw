// ref: http://stacktips.com/tutorials/android/loading-image-asynchronously-in-android-listview
package com.iems5722.group5.AsyncTasks;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.iems5722.group5.DownloadImageCompleteInterface;
import com.iems5722.group5.MessageItem;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by AlexLiu on 21/4/17.
 */

public class DownloadImageAsyncTask extends AsyncTask<String, Void, Bitmap> {
    private final WeakReference<ImageView> imageViewReference;
    private MessageItem msgItem;
    private DownloadImageCompleteInterface acty;

    public DownloadImageAsyncTask(MessageItem msg, ImageView imageView) {
        imageViewReference = new WeakReference<ImageView>(imageView);
        msgItem = msg;
        acty = null;
    }

    public DownloadImageAsyncTask setActivity(DownloadImageCompleteInterface inActy) {
        acty = inActy;
        return this;
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        try {
            return BitmapFactory.decodeStream((InputStream) new URL(params[0]).getContent());
        } catch(IOException e)
        {
            e.printStackTrace();
        }
        return null;
        //return downloadBitmap(params[0]);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (isCancelled()) {
            bitmap = null;
        }
        if (bitmap != null) {
            msgItem.setImage(bitmap);

            ImageView imageView = imageViewReference.get();
            if (imageView != null) {
                imageView.setImageBitmap(bitmap);
            }
            if (acty != null) {
                acty.onDownloadComplete(msgItem);
            }
        }
    }

}
