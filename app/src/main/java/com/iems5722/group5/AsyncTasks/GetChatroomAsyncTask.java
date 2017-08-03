package com.iems5722.group5.AsyncTasks;

import android.app.Activity;
import android.os.AsyncTask;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.ArrayList;

import com.iems5722.group5.ChatroomItem;
import com.iems5722.group5.R;
import com.iems5722.group5.ChatRoomListAdapter;

/**
 * Created by AlexLiu on 14/2/17.
 */

public class GetChatroomAsyncTask extends AsyncTask<String, Void, String> {
    private LinearLayout layProgress;
    private Activity acty;
    private ChatRoomListAdapter roomAdapter;
    private ArrayList<ChatroomItem> roomList;

    public GetChatroomAsyncTask(Activity activity, ChatRoomListAdapter adapter) {
        acty = activity;
        roomAdapter = adapter;
        roomList = new ArrayList<ChatroomItem>();
        layProgress = (LinearLayout) acty.findViewById(R.id.layProgress);
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            java.net.URL url = new java.net.URL(params[0]);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            InputStream is = new BufferedInputStream(conn.getInputStream());
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();

            String rx;
            while((rx = br.readLine()) != null) {
                sb.append(rx);
            }

            JSONObject topLevel = new JSONObject(sb.toString());
            String sts = topLevel.getString(acty.getString(R.string.MSG_FIELD_STATUS));

            if (acty.getString(R.string.MSG_STATUS_OK).equals(sts.toUpperCase())) {
                roomList.clear();

                JSONArray arRooms = topLevel.getJSONArray(acty.getString(R.string.MSG_FIELD_DATA));
                for(int i = 0; i < arRooms.length(); i++) {
                    String n = arRooms.getJSONObject(i).getString(acty.getString(R.string.MSG_FIELD_CHATROOM_NAME));
                    Integer id = arRooms.getJSONObject(i).getInt(acty.getString(R.string.MSG_FIELD_CHATROOM_ID));
                    ChatroomItem cr = new ChatroomItem(n, id);
                    roomList.add(cr);
                }

                return acty.getString(R.string.MSG_STATUS_OK);
            }
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
        if (acty.getString(R.string.MSG_STATUS_OK).equals(result))
        {
            roomAdapter.clear();
            for (ChatroomItem cr : roomList) {
                roomAdapter.add(cr);
            }
        }
        else
        {
            Toast.makeText(acty.getApplicationContext(), "Error getting chatroom list", Toast.LENGTH_SHORT).show();
        }
        layProgress.setVisibility(View.GONE);
    }
}
