package com.example.checkapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class HomeFragment extends Fragment {

    private Button flush;
    private TextView today_num;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.today_record,container,false);


        initUI(view);

        flush.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getContext())
                        .setTitle("警告")
                        .setMessage("检测到未带安全帽的对象")
                        .setPositiveButton("确认",null)
                        .show();
//                Intent intent = new Intent(getActivity(),MainActivity.class);
//
//                startActivity(intent);
//                getActivity().finish();
            }
        });

        return view;

    }

    public void initUI(View view){
        today_num = (TextView)view.findViewById(R.id.today_records);
        flush = (Button)view.findViewById(R.id.flsuh);
    }
}
