package com.example.checkapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.os.Vibrator;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import android.os.Handler;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tab_1,tab_2;
    private ViewPager myViewPager;
    private List<Fragment> fragmentList;
    private  MyFragmentPagerAdapter myFragmentPagerAdapter;
    private Handler mHandler;
    private Socket socket;
    private ExecutorService mThreadPool;

    InputStream is;

    // 输入流读取器对象
    InputStreamReader isr ;
    BufferedReader br ;

    // 接收服务器发送过来的消息
    String response;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUI();
        initTab();
        socketInit();
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

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.bottom_Home){
            showFragment(0);
        }
        if(v.getId()==R.id.bottom_Record){
            showFragment(1);
        }
    }

    private void socketInit(){
        // 初始化线程池
        mThreadPool = Executors.newCachedThreadPool();
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:
                        vibrate();
                        break;
                }
            }
        };
        connect();
        receive_data();
    }

    private void connect(){
        mThreadPool.execute(new Runnable() {
            @Override
            public void run() {

                try {

                    // 创建Socket对象 & 指定服务端的IP 及 端口号
                    socket = new Socket("localhost", 8080);

                    // 判断客户端和服务器是否连接成功
                    System.out.println(socket.isConnected());

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });


    }

    private void receive_data(){
        mThreadPool.execute(new Runnable() {
            @Override
            public void run() {

                try {
                    // 步骤1：创建输入流对象InputStream
                    is = socket.getInputStream();

                    // 步骤2：创建输入流读取器对象 并传入输入流对象
                    // 该对象作用：获取服务器返回的数据
                    isr = new InputStreamReader(is);
                    br = new BufferedReader(isr);

                    // 步骤3：通过输入流读取器对象 接收服务器发送过来的数据
                    response = br.readLine();

                    // 步骤4:通知主线程,将接收的消息显示到界面
                    Message msg = Message.obtain();
                    msg.what = 0;
                    mHandler.sendMessage(msg);

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });

    }

    private void vibrate(){
        Vibrator vibrator = (Vibrator)this.getSystemService(this.VIBRATOR_SERVICE);
        vibrator.vibrate(1000);
    }

}
