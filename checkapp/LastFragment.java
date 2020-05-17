package com.example.checkapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LastFragment extends Fragment {

    private List<Record> records;
    private ListView recordListView;
    private RecordAdapter recordAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.last_record,container,false);

        initData();

        recordListView = (ListView)view.findViewById(R.id.list_record);
        recordAdapter = new RecordAdapter(records,getContext());
        recordListView.setAdapter(recordAdapter);

        return view;
    }


    private void initData(){

        records = new ArrayList<>();

        Record record_1 = new Record(new Date(),"工地南1号",1);
        Record record_2 = new Record(new Date(),"工地北2号",2);
        Record record_3 = new Record(new Date(),"工地南2号",3);
        Record record_4 = new Record(new Date(),"工地南5号",4);

        records.add(record_1);records.add(record_2);
        records.add(record_3);records.add(record_4);
    }
}
