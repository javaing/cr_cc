package com.aliee.quei.mo.ui.main.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.aliee.quei.mo.R;

import java.util.List;

public class IconListAdapter extends ArrayAdapter<ItemData> {
    public IconListAdapter(Context context, int resource, List<ItemData> objects) {
        super(context, resource, objects);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
// 獲取圖示的資料
        ItemData item = getItem(position);
// 建立佈局
        View oneTeacherView = LayoutInflater.from(getContext()).inflate(R.layout.listview_item, parent, false);
// 獲取ImageView和TextView
        ImageView imageView = (ImageView) oneTeacherView.findViewById(R.id.ic_image);
        TextView textView = (TextView) oneTeacherView.findViewById(R.id.label);
// 根據圖示資料設定ImageView和TextView的展現
        imageView.setImageResource(item.getImageId());
        textView.setText(item.getText());
        return oneTeacherView;
    }
}