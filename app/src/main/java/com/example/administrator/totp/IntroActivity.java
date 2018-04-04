package com.example.administrator.totp;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import java.util.Arrays;

public class IntroActivity extends BaseActivity {

    ViewPager viewPager;
    FragPagerUtils fragPagerUtils;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(!SPUtils.getBoolean(this, "first", false)){
            SPUtils.saveBoolean(this, "first", true);
        }else {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_intro2);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);//隐藏状态栏
        viewPager = findViewById(R.id.viewPager);
        fragPagerUtils = new FragPagerUtils(this,viewPager, Arrays.asList(
                new IntroFragment(R.drawable.pic,false),
                new IntroFragment(R.drawable.pic2,false),
                new IntroFragment(R.drawable.pic3,true)));


    }



}
