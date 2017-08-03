package com.iems5722.group5;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iems5722.group5.AsyncTasks.DownloadImageAsyncTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by AlexLiu on 31/1/17.
 */

public class EmojiViewAdapter extends ArrayAdapter<Integer> {

    private Integer[] images;
    public EmojiViewAdapter(Context context, Integer[] images) {
        super(context, R.layout.emoji_item, images);
        this.images = images;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getImageForPosition(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getImageForPosition(position);
    }

    private View getImageForPosition(int position) {
        ImageView imageView = new ImageView(getContext());
        imageView.setBackgroundResource(images[position]);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(120, 120);
        imageView.setLayoutParams(layoutParams);
        return imageView;
    }

//    @Override
//    public View getView(int position, View convertView, ViewGroup parent){
//        //int itemResourceId = Integer.parseInt(getItem(position));
//        convertView = LayoutInflater.from(getContext()).inflate(R.layout.emoji_item, parent, false);
//
//        //ImageView image = (ImageView) convertView.findViewById(R.id.emoji_imageView);
//        //image.setImageResource(R.drawable.emoji_2);
//        return convertView;
//    }
}
