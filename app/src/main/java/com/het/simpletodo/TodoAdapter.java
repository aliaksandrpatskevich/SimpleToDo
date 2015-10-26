package com.het.simpletodo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class TodoAdapter extends ArrayAdapter<Todo> {
    public TodoAdapter(Context context, ArrayList<Todo> todo) {
        super(context, 0, todo);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Todo todo = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_todo, parent, false);
        }
        // Lookup view for data population
        TextView tvText = (TextView) convertView.findViewById(R.id.tvText);
        TextView tvDate = (TextView) convertView.findViewById(R.id.tvDateAdapter);
        TextView tvComments = (TextView) convertView.findViewById(R.id.tvComments);
        ImageView ivLevel = (ImageView) convertView.findViewById(R.id.ivLevel);
        // Populate the data into the template view using the data object
        tvText.setText(todo.text);

        if (todo.date.length()>1) {
            tvDate.setText(todo.date);
            tvDate.setVisibility(View.VISIBLE);
        } else tvDate.setVisibility(View.GONE);//hide icon without date

        tvComments.setText(todo.comments);

//        hide tvComments if empty
        if (tvComments.length() == 0) {
            tvComments.setVisibility(View.GONE);
        } else {
            tvComments.setVisibility(View.VISIBLE);
        }

        switch (todo.level) {
            case "high":
                ivLevel.setBackgroundResource(R.drawable.ic_priority_high);
                break;
            case "medium":
                ivLevel.setBackgroundResource(R.drawable.ic_priority_medium);
                break;
            case "low":
                ivLevel.setBackgroundResource(R.drawable.ic_priority_low);
                break;
        }

        // Return the completed view to render on screen
        return convertView;
    }
}
