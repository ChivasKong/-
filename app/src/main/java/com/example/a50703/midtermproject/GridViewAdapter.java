package com.example.a50703.midtermproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by 50703 on 2017/11/25.
 */

public class GridViewAdapter extends BaseAdapter {
    Context context;
    List<MainActivity.NATION> list;
    public GridViewAdapter(Context _context, List<MainActivity.NATION> _list) {
        this.list = _list;
        this.context = _context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        viewHolder = new ViewHolder();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        convertView = layoutInflater.inflate(R.layout.nation_item, null);
        viewHolder.imageView = (ImageView) convertView.findViewById(R.id.ItemImage);
        convertView.setTag(viewHolder);
        viewHolder.imageView.setImageResource(list.get(position).getPic());
        return convertView;
    }
    private class ViewHolder{
        public ImageView imageView;
    }
}
