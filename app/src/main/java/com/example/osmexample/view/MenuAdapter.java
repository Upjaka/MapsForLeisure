package com.example.osmexample.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.osmexample.R;

import java.util.ArrayList;

public class MenuAdapter extends ArrayAdapter<MenuItem> {
    private final ArrayList<MenuItem> menuItems;
    private final Context mContext;

    public MenuAdapter(Context context, ArrayList<MenuItem> items) {
        super(context, 0, items);
        mContext = context;
        menuItems = items;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null) {
            listItem = LayoutInflater.from(mContext).inflate(R.layout.item_menu, parent, false);
        }

        MenuItem currentItem = menuItems.get(position);

        ImageView imageView = listItem.findViewById(R.id.imageView);
        imageView.setImageResource(currentItem.getImageId());

        TextView textView = listItem.findViewById(R.id.textView);
        textView.setText(currentItem.getText());

        return listItem;
    }
}
