package com.iems5722.group5.AsyncTasks;

import android.app.Activity;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Toast;

import com.iems5722.group5.R;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URLEncoder;

/**
 * Created by AlexLiu on 22/4/17.
 */

public class SendEditImageInvitationAsyncTask extends AsyncTask<String, Void, String> {
    private Activity acty;
    private String roomId;
    private String userId;
    private String userName;
    private String imageName;

    public SendEditImageInvitationAsyncTask(Activity activity, String roomId, String userId, String userName,
                                String imageName) {
        acty = activity;
        this.roomId = roomId;
        this.userId = userId;
        this.userName = userName;
        this.imageName = imageName;
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            // build post message parameter string
            String data = "chatroom_id=" + roomId + "&user_id=" + userId + "&name=" + userName +
                    "&image=" + imageName;
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
    protected void onPostExecute(String result) {
        if (acty.getString(R.string.MSG_STATUS_ERROR).equals(result)) {
            Toast.makeText(acty, "error sending invitation", Toast.LENGTH_SHORT);
        }

    }

}
