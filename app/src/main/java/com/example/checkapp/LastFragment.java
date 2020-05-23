package com.example.checkapp;

import android.app.Dialog;
import android.content.DialogInterface;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LastFragment extends Fragment implements View.OnClickListener,DatePicker.OnDateChangedListener {

    private List<Record> records;
    private ListView recordListView;
    private RecordAdapter recordAdapter;

    private TextView queryBt;
    private TextView inputDate;

    private StringBuffer date;
    private int year, month, day;
    private static String URL="http://10.0.2.2:5000";

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.last_record,container,false);

        // 保存日期为String
        date = new StringBuffer();
        initData(null);

        initDateTime();
        initDateView(view);

        recordListView = (ListView)view.findViewById(R.id.list_record);
        recordAdapter = new RecordAdapter(records,getContext());
        recordListView.setAdapter(recordAdapter);
        recordListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Record record = records.get(position);
                final Dialog dia = new Dialog(getContext());
                dia.setContentView(R.layout.dialog_view);
                ImageView imageView = (ImageView) dia.findViewById(R.id.record_img);
                Button confirm = (Button)dia.findViewById(R.id.bt_confirm);
                imageView.setImageResource(R.mipmap.error_icon);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(1000, 1200);//两个400分别为添加图片的大小
                imageView.setImageResource(R.mipmap.record);
                imageView.setLayoutParams(params);//此处放入图片数据//此处放入图片数据
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
        return view;
    }

    private void initDateView(View view) {
        queryBt = (TextView)view.findViewById(R.id.query_button);
        inputDate = (TextView)view.findViewById(R.id.date_input);
        queryBt.setOnClickListener(this);
        inputDate.setOnClickListener(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void initDateTime() {
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
    }

    @Override
    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        this.year = year;
        this.month = monthOfYear;
        this.day = dayOfMonth;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.query_button:
                String dateinfo = date.toString();
                if(dateinfo.isEmpty()){
                    Toast.makeText(getContext(),"请输入需查询的日期",Toast.LENGTH_SHORT).show();
                }
                else {
                    // 查询代码
                    Thread t = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            OkHttpClient okHttpClient = new OkHttpClient();
                            Request request = new Request.Builder()
                                    .url(URL + "/search?q=" + date)
                                    .build();
                            try {
                                String result = okHttpClient.newCall(request).execute().body().string();
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        initData(result);
                                        recordAdapter = new RecordAdapter(records,getContext());
                                        recordListView.setAdapter(recordAdapter);
                                    }
                                });
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }
                    });
                    t.start();
                    try{
                        t.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(getContext(),"查询成功",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.date_input:
                // 显示日期选择界面
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (date.length() > 0) { //清除上次记录的日期
                            date.delete(0, date.length());
                        }
                        inputDate.setText(date.append(String.valueOf(year)).append("-").append(String.valueOf(month+1)).append("-").append(day));
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                final AlertDialog dialog = builder.create();
                View dialogView = View.inflate(getContext(), R.layout.dialog_date, null);
                final DatePicker datePicker = (DatePicker) dialogView.findViewById(R.id.datePicker);
                dialog.setTitle("设置日期");
                dialog.setView(dialogView);
                dialog.show();
                //初始化日期监听事件
                datePicker.init(year, month, day, this);
                break;
        }
    }

    //取得当前日期
    private void initData(@Nullable String args){
        records = new ArrayList<>();
        if(args==null) {
            records.clear();
            try{
                String response = flush();
                JSONArray jsonArray = new JSONArray(response);
                for(int i =0;i<jsonArray.length();i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    Date date;
                    date = format.parse(jsonObject.getString("time"));
                    records.add(new Record(date,jsonObject.getString("place"),jsonObject.getInt("id")));
                    Log.e("result",jsonObject.toString());
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }else{
            records.clear();
            try{
                JSONArray jsonArray = new JSONArray(args);
                for(int i =0;i<jsonArray.length();i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    Date date;
                    date = format.parse(jsonObject.getString("time"));
                    records.add(new Record(date,jsonObject.getString("place"),jsonObject.getInt("id")));
                    Log.e("result",jsonObject.toString());
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    private String flush() throws IOException {
        final String[] response = {null};
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient okHttpClient = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url("http://10.0.2.2:5000/all_data")
                            .build();
                    response[0] = okHttpClient.newCall(request).execute().body().string();
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });
        t.start();
        try{
            t.join();
        }catch (Exception e){
            e.printStackTrace();
        }
        Log.e("response", response[0]);
        return response[0];
    }
}
