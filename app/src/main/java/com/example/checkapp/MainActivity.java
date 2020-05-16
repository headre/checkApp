package com.example.checkapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tab_1,tab_2;
    private ViewPager myViewPager;
    private List<Fragment> fragmentList;
    private  MyFragmentPagerAdapter myFragmentPagerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUI();
        initTab();
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

}
