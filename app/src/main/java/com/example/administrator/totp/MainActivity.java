package com.example.administrator.totp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.crypto.KeyGenerator;

public class MainActivity extends BaseActivity{

    TextView tv;
    TextView et;
    private SimpleDateFormat sdf, sdfmm;
    private long num1;
    private String leftStr;
    private ViewGroup ll;
    FrameLayout fl;

    @Override
    public void onBackPressed() {
        new MaterialDialog.Builder(this)
                .title("确定要退出吗？")
                .positiveText("确定")
                .negativeText("取消")
                .positiveColor(Color.BLACK)
                .negativeColor(Color.GRAY)
                .onPositive((dialog, which) -> finish()
                )
                .onNegative((dialog, which) -> dialog.dismiss())
                .show();
    }

    private static int getStatusBarHeight(Context context) {
        int statusBarHeight = 0;
        Resources res = context.getResources();
        int resourceId = res.getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = res.getDimensionPixelSize(resourceId);
        }
        return statusBarHeight;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1){
            et.setText(data.getStringExtra("num"));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        ViewGroup decorViewGroup = (ViewGroup) window.getDecorView();
        View statusBarView = new View(window.getContext());
        int statusBarHeight = getStatusBarHeight(window.getContext());
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, statusBarHeight);
        params.gravity = Gravity.TOP;
        statusBarView.setLayoutParams(params);
        statusBarView.setBackgroundColor(Color.parseColor("#FF711B"));
        decorViewGroup.addView(statusBarView);
        setContentView(R.layout.activity_main);
        fl = findViewById(R.id.fl);
        ll = findViewById(R.id.ll);
        ll.addOnLayoutChangeListener(this);
        tv = findViewById(R.id.tv);
        et = findViewById(R.id.et);
        sdf = new SimpleDateFormat("yyMMddHH");
        sdfmm = new SimpleDateFormat("mm");
        //ll.addOnLayoutChangeListener(this);
        et.setOnClickListener(view->{
            startActivityForResult(new Intent(MainActivity.this, EditActivity.class), 1);
        });
    }

    public void generate(View view) {
        if(TextUtils.isEmpty(et.getText().toString())){
            return;
        }
        if(et.getText().toString().length()<6){
            Toast.makeText(this,"至少要6位数字", Toast.LENGTH_SHORT).show();
            return;
        }
        int mm = Integer.parseInt(sdfmm.format(new Date())) ;
        int newmm = mm-mm%5;
        if(newmm<10){
            leftStr = sdf.format(new Date())+"0"+newmm;
        }else {
            leftStr = sdf.format(new Date())+newmm;
        }
        String rightStr = et.getText().toString();
        String finalStr = leftStr+rightStr;
        String h = SHA1.encode(finalStr);
        StringBuilder builder = new StringBuilder("");
        char lastChar = h.charAt(h.length()-1);
        int index = Integer.valueOf(String.valueOf(lastChar), 16);
        for (int i = 0; i < h.toCharArray().length; i++) {
            if (i >= index*2 && i <= index*2+7) {
                builder.append(h.toCharArray()[i]);
            }
        }
        long num = Long.valueOf(builder.toString(), 16);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            num1 = Math.floorMod(num, (long) Math.pow(10, 8));
            String result = String.valueOf(num1);
            if(result.length()<8){
                tv.setText("0"+result);
            }else {
                tv.setText(result);
            }
        }else {
            num1 =  num - floorDiv(num, (long) Math.pow(10, 8)) * (long) Math.pow(10, 8);
            String result = String.valueOf(num1);
            if(result.length()<8){
                tv.setText("0"+result);
            }else {
                tv.setText(result);
            }
        }
    }

    public static long floorDiv(long x, long y) {
        long r = x / y;
        // if the signs are different and modulo not zero, round down
        if ((x ^ y) < 0 && (r * y != x)) {
            r--;
        }
        return r;
    }

}
