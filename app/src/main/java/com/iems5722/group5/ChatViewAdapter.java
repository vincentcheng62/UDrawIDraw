package com.iems5722.group5;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import com.iems5722.group5.AsyncTasks.DownloadImageAsyncTask;
import com.iems5722.group5.R;
/**
 * Created by AlexLiu on 31/1/17.
 */

public class ChatViewAdapter extends ArrayAdapter<MessageItem> {

    private SimpleDateFormat displayDateFmt;

    public ChatViewAdapter(Context context, ArrayList<MessageItem> msgs){
        super(context, 0, msgs);
        displayDateFmt = new SimpleDateFormat("HH:mm");
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        MessageItem item = getItem(position);
        //System.out.println("position=" + position + " msg=" + item.getMessage());

        // right align for msg sent by me
        if (!item.getIsMyMsg()) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.message_item_left, parent, false);
        }
        else {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.message_item_right, parent, false);
        }

        TextView n = (TextView) convertView.findViewById(R.id.msg_user);
        TextView m = (TextView) convertView.findViewById(R.id.msg_msg);
        TextView t = (TextView) convertView.findViewById(R.id.msg_time);

        ImageView iv = (ImageView) convertView.findViewById(R.id.msg_image);
        if (item.getImage() != null) {
            iv.setImageBitmap(item.getImage());
        }
        else if (item.getImageName() != null && !"".equals(item.getImageName())) {
            // has image name and no image itself, download from server
            new DownloadImageAsyncTask(item, iv).execute(
                    getContext().getString(R.string.URL_DOWNLOAD_IMAGE) + "?file_name=" + item.getImageName()
            );

        }
        else {
            // hide the empty image view
            iv.setVisibility(View.GONE); // fix by vincent Apr14
        }


        n.setText("User: " + item.getUser());
        if (!"".equals(item.getMessage())) {
            m.setText(item.getMessage());
        }
        t.setText(displayDateFmt.format(item.getTime()));

        return convertView;
    }
}
