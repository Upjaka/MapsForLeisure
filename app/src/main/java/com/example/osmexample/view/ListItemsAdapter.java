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

import java.util.List;

public class ListItemsAdapter extends ArrayAdapter<ListItem> {
    public ListItemsAdapter(Context context, List<ListItem> itemList) {
        super(context, 0, itemList);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        // получение или создание ViewHolder
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_list, parent, false);
            holder = new ViewHolder();
            holder.imageView = convertView.findViewById(R.id.imageView);
            holder.textView1 = convertView.findViewById(R.id.textView1);
            holder.textView2 = convertView.findViewById(R.id.textView2);
            holder.dateTime = convertView.findViewById(R.id.textView3);
            holder.imageButton1 = convertView.findViewById(R.id.imageButton1);
            holder.imageButton2 = convertView.findViewById(R.id.imageButton2);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // установка значений элементов разметки из данных элемента списка
        ListItem item = getItem(position);
        if (item != null) {
            holder.imageView.setImageResource(item.getImageId1());
            holder.textView1.setText(item.getText1());
            holder.textView2.setText(item.getText2());
            holder.dateTime.setText(item.getDateTime());
            holder.imageButton1.setImageResource(item.getImageId2());
            holder.imageButton2.setImageResource(item.getImageId3());
            // Обработчики кнопок
            holder.imageButton1.setOnClickListener(v -> ((MainActivity) getContext()).onDeleteButtonClick(position));
            holder.imageButton2.setOnClickListener(v -> ((MainActivity) getContext()).onDisplayButtonClick(position));
        }

        return convertView;
    }

    static class ViewHolder {
        ImageView imageView;
        TextView textView1;
        TextView textView2;
        TextView dateTime;
        ImageView imageButton1;
        ImageView imageButton2;
    }
}
