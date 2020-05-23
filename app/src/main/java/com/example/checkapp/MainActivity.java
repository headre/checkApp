package com.example.checkapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tab_1,tab_2;
    private ViewPager myViewPager;
    private List<Fragment> fragmentList;
    private  MyFragmentPagerAdapter myFragmentPagerAdapter;
    private String URL = "http://10.0.2.2:5000";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUI();
        initTab();
        //countDown();
    }

    private void initUI(){
        tab_1 = (TextView)findViewById(R.id.bottom_Home);
        tab_2 = (TextView)findViewById(R.id.bottom_Record);

        tab_1.setOnClickListener(this);
        tab_2.setOnClickListener(this);

        myViewPager = (ViewPager)findViewById(R.id.view_record);

    }

    private void initTab(){
        HomeFragment fragment_1 = new HomeFragment();
        LastFragment fragment_2 = new LastFragment();

        fragmentList = new ArrayList<>();
        fragmentList.add(fragment_1);
        fragmentList.add(fragment_2);

        myFragmentPagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager(),fragmentList);
        myViewPager.setAdapter(myFragmentPagerAdapter);

        showFragment(0);
    }

    private void showFragment(int num){
        myViewPager.setCurrentItem(num);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.bottom_Home){

            showFragment(0);
        }
        if(v.getId()==R.id.bottom_Record){
            showFragment(1);
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void notification() {
        Intent intent = new Intent(this, MainActivity.class);
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        //8.0 以后需要加上channelId 才能正常显示！！！ 属实把我恶心到了

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            String channelId = "default";
            String channelName = "默认通知";
            manager.createNotificationChannel(new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH));
        }

        PendingIntent pintent = PendingIntent.getActivity(this,0,intent,0);

        Notification notification = new NotificationCompat.Builder(this, "default")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("警告")
                .setContentText("检测到未佩戴安全帽的对象！！")
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setContentIntent(pintent)
                .build();

        manager.notify(1, notification);
    }
}
