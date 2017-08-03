package com.iems5722.group5;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import com.iems5722.group5.R;
/**
 * Created by AlexLiu on 31/1/17.
 */

public class ChatRoomListAdapter extends ArrayAdapter<ChatroomItem> {

    public ChatRoomListAdapter(Context context, ArrayList<ChatroomItem> rooms){
        super(context, 0, rooms);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        ChatroomItem item = getItem(position);
        System.out.println("position=" + position + " chatroom name=" + item.getName());
        // alternate colors
        if (position % 2 == 0) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.chatroom_item_a, parent, false);
        }
        else {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.chatroom_item_b, parent, false);
        }

        TextView m = (TextView) convertView.findViewById(R.id.cr_name);

        m.setText(item.getName());

        return convertView;
    }
}
