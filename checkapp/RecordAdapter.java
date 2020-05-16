package com.example.checkapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RecordAdapter extends BaseAdapter {

    private List<Record> recordList;
    private Context context;

    public RecordAdapter(List<Record> recordList, Context context) {
        this.recordList = recordList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return recordList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;

        if(convertView == null){
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.record_item,parent,false);

            viewHolder.date = (TextView)convertView.findViewById(R.id.date);
            viewHolder.cameraID = (TextView)convertView.findViewById(R.id.cameraID);

            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder)convertView.getTag();
        }

        viewHolder.date.setText(
                DateFormat.getDateInstance(DateFormat.SHORT, Locale.CHINA)
                .format(recordList.get(position).getRecordTime())
                        +" "+DateFormat.getTimeInstance(DateFormat.SHORT, Locale.CHINA)
                        .format(recordList.get(position).getRecordTime()));
        viewHolder.cameraID.setText(recordList.get(position).getCameraID());

        return convertView;
    }

    static class ViewHolder{
        public TextView date;
        public TextView cameraID;
    }
}
