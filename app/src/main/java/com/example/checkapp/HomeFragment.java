package com.example.checkapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import okhttp3.Call;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

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
            public void onClick(View v) {  flushHint();  }
        });
        Thread t = new Thread(new Runnable() {
            String response = null;
            @Override
            public void run() {
                try {
                    response = flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setFlushData(response);
                    }
                });
            }
        });
        t.start();

        return view;

    }

    public void initUI(View view){
        today_num = (TextView)view.findViewById(R.id.today_records);
        flush = (Button)view.findViewById(R.id.flsuh);
    }

    private void flushHint(){
        new AlertDialog.Builder(getContext())
                .setTitle("警告")
                .setMessage("检测到未带安全帽的对象")
                .setPositiveButton("确认",null)
                .show();
    }

    private String flush() throws IOException {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://10.0.2.2:5000/all_data")
                .build();
        String response = okHttpClient.newCall(request).execute().body().string();
        Log.e("response",response);
        return response;
    }

    private void setFlushData(String data){
    }
}
