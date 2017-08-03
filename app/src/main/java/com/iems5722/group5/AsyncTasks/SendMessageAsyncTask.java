package com.iems5722.group5.AsyncTasks;

import android.app.Activity;
import android.os.AsyncTask;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URLEncoder;

import com.iems5722.group5.R;

/**
 * Created by AlexLiu on 14/2/17.
 */

public class SendMessageAsyncTask extends AsyncTask<String, Void, String> {
    private RelativeLayout layProgress;
    private Activity acty;
    private String roomId;
    private String userId;
    private String userName;
    private String msg;

    public SendMessageAsyncTask(Activity activity, String roomId, String userId, String userName,
                                String msg) {
        acty = activity;
        this.roomId = roomId;
        this.userId = userId;
        this.userName = userName;
        this.msg = msg;
        layProgress = (RelativeLayout) acty.findViewById(R.id.layProgress);
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            // build post message parameter string
            String data = "chatroom_id=" + roomId + "&user_id=" + userId + "&name=" + userName +
                    "&message=" + URLEncoder.encode(msg, "UTF-8");
            java.net.URL url = new java.net.URL(params[0]);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(data);
            wr.flush();

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

            return sts.toUpperCase();
        }catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
        return acty.getString(R.string.MSG_STATUS_ERROR);
    }

    @Override
    protected void onPreExecute() {
        layProgress.setVisibility(View.VISIBLE);
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String result) {
        if (acty.getString(R.string.MSG_STATUS_ERROR).equals(result)) {
            Toast.makeText(acty, "error sending message", Toast.LENGTH_SHORT);
        }

        layProgress.setVisibility(View.GONE);
    }
}
