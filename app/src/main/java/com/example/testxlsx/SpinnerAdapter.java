package com.example.testxlsx;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.net.ContentHandler;
import java.util.List;

public class SpinnerAdapter extends BaseAdapter {

    List<String> data;
    Context context;

    public SpinnerAdapter(List<String> data, Context context) {
        this.data = data;
        this.context = context;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        view = LayoutInflater.from(context).inflate(R.layout.spinner_item, null);
        TextView tvItem = view.findViewById(R.id.tvItem);
        ImageView imgvArrow = view.findViewById(R.id.imgvArrow);
        imgvArrow.setImageResource(R.drawable.ic_arrow_dropdown);

        tvItem.setText(data.get(position));
        if(position != 0){
            imgvArrow.setVisibility(View.INVISIBLE);
        }

        return view;
    }
}
