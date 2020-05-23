package com.example.checkapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;


import okhttp3.OkHttpClient;
import okhttp3.Request;

public class HomeFragment extends Fragment {

    private String URL = "http://10.0.2.2:5000";
    private List<Record> records;
    private ListView recordListView;
    private RecordAdapter recordAdapter;
    private TextView record_today;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.today_record, container, false);

        initData();
        initUI(view);
        countDown();
        int record_num = records.size();
        record_today.setText("今日检测到未带安全帽的次数：" + record_num);
        return view;

    }

    public void initUI(View view) {
        record_today = (TextView) view.findViewById(R.id.today_num);

        recordListView = (ListView) view.findViewById(R.id.list_record);
        recordAdapter = new RecordAdapter(records, getContext());
        recordListView.setAdapter(recordAdapter);
        recordListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Record record = records.get(position);
                final Dialog dia = new Dialog(getContext());
                dia.setContentView(R.layout.dialog_view);
                ImageView imageView = (ImageView) dia.findViewById(R.id.record_img);
                Button confirm = (Button)dia.findViewById(R.id.bt_confirm);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(1000,
                        1200);//两个400分别为添加图片的大小
                imageView.setImageResource(R.mipmap.record);
                imageView.setLayoutParams(params);            //此处放入图片数据
                //选择true的话点击其他地方可以使dialog消失，为false的话不会消失
                dia.setCanceledOnTouchOutside(true);
                Window w = dia.getWindow();
                WindowManager.LayoutParams lp = w.getAttributes();
                lp.x = 0;
                lp.y = 40;
                dia.onWindowAttributesChanged(lp);
                confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dia.dismiss();      //关闭dialog
                    }
                });
                dia.show();
            }
        });
    }

    private void initData() {
        records = new ArrayList<>();
        records.clear();
        try {
            String response = getTodayData();
            JSONArray jsonArray = new JSONArray(response);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                Date date;
                date = format.parse(jsonObject.getString("time"));
                records.add(new Record(date, jsonObject.getString("place"), jsonObject.getInt("id")));
                Log.e("result", jsonObject.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void updateUI(){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                recordAdapter = new RecordAdapter(records, getContext());
                recordListView.setAdapter(recordAdapter);
                int record_num = records.size();
                record_today.setText("今日检测到未带安全帽的次数：" + record_num);
            }
        });
    }

    private String getTodayData() {
        final String[] response = {null};
        // 查询代码
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                Date new_date = new Date(System.currentTimeMillis());
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                String dateS = format.format(new_date);
                Log.e("date", dateS);
                OkHttpClient okHttpClient = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(URL + "/search?q=" + dateS)
                        .build();
                try {
                    response[0] = okHttpClient.newCall(request).execute().body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return response[0];
    }


    private Boolean newRecord() {
        final Boolean[] hasNew = {false};
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient okHttpClient = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(URL + "/checkit")
                        .build();
                String response = null;
                try {
                    response = okHttpClient.newCall(request).execute().body().string();
                    if (Boolean.valueOf(response)) {
                        hasNew[0] = true;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.e("response", response);

            }
        });
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return hasNew[0];
    }

    private void countDown() {
        handler.post(task);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void fresh() throws IOException {
        if (newRecord()) {
            initData();
            updateUI();
            MainActivity mainActivity = (MainActivity)getActivity();
            mainActivity.notification();
        }
    }
    private Handler handler = new Handler();

    private Runnable task = new Runnable() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        public void run() {
            handler.postDelayed(this,5*1000);//设置延迟时间，此处是5秒
            try {
                fresh();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };
}
