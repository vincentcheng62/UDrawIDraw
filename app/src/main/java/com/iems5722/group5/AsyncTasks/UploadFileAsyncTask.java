package com.iems5722.group5.AsyncTasks;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.iems5722.group5.R;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
//import org.apache.http.*;

/**
 * Created by AlexLiu on 7/4/17.
 */

public class UploadFileAsyncTask extends AsyncTask<String, Void, String> {
    private Activity acty;
    private String roomId;
    private String userId;
    private String userName;
    private String filePath;
    private String fileName;
    private String timeStamp;
    private Uri uriImage;

    public UploadFileAsyncTask(Activity activity, String roomId, String userId, String userName,
                               Uri uri, String timestamp) {
        acty = activity;
        this.roomId = roomId;
        this.userId = userId;
        this.userName = userName;
        uriImage = uri;
        timeStamp = timestamp;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params){
        String responseString = null;
        HttpURLConnection conn = null;
        DataOutputStream os = null;
//        DataInputStream inputStream = null;


        int bytesRead, bytesAvailable, bufferSize, bytesUploaded = 0;
        byte[] buffer;
        int maxBufferSize = 2*1024*1024;

        try
        {
            FileInputStream fis = new FileInputStream(new File(uriImage.getPath()) );

            bytesAvailable = fis.available();


            URL url = new URL(params[0]);
            conn = (HttpURLConnection) url.openConnection();

            // POST settings.
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setConnectTimeout(2000); // allow 2 seconds timeout.
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            Integer len = new Integer(bytesAvailable);
            conn.setRequestProperty("Content-Length", len.toString());
            conn.setRequestProperty("chatroomid", roomId);
            conn.setRequestProperty("userid", userId);
            conn.setRequestProperty("name", userName);
            conn.setRequestProperty("timestamp", timeStamp);
            conn.connect();

            os = new DataOutputStream(conn.getOutputStream());

            System.out.println("available: " + String.valueOf(bytesAvailable));
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];

            bytesRead = fis.read(buffer, 0, bufferSize);


            bytesUploaded += bytesRead;
            while (bytesRead > 0) {
                os.write(buffer, 0, bufferSize);
                bytesAvailable = fis.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fis.read(buffer, 0, bufferSize);
                bytesUploaded += bytesRead;
            }

            System.out.println("uploaded: "+String.valueOf(bytesUploaded));
            fis.close();
            os.flush();
            os.close();

            // Responses from the server (code and message)
            int rcode = conn.getResponseCode();

            if(rcode==200)
            {
                // get response
                InputStream is = new BufferedInputStream(conn.getInputStream());
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                StringBuilder sb = new StringBuilder();
                String rx;

                while((rx = br.readLine()) != null) {
                    sb.append(rx);
                }

                JSONObject topLevel = new JSONObject(sb.toString());
                String sts = topLevel.getString(acty.getString(R.string.MSG_FIELD_STATUS));
                System.out.println("status: "+ sts);
                //System.out.println("message: "+ topLevel.getString("message"));

            }

        }
        catch (Exception ex)
        {
            Log.d("========", ex.getMessage());
            ex.printStackTrace();
            //return false;
        }

        return responseString;
    }
}