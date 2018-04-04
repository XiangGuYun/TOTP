package com.example.administrator.totp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.List;

public abstract class BaseActivity extends FragmentActivity implements View.OnLayoutChangeListener{

    protected Toast mToast;
    private boolean LeftIsFinish = true;
    public static List<Activity> activityList = new ArrayList<>();


    public void visible(View v){
        v.setVisibility(View.VISIBLE);
    }

    public void invisible(View v){
        v.setVisibility(View.INVISIBLE);
    }

    public void gone(View v){
        v.setVisibility(View.GONE);
    }

    public void showToast(String text) {
        dismissToast();
        mToast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
        mToast.show();
    }

    public <T extends Activity> void go(Class<T> tClass){
        startActivity(new Intent(this, tClass));
    }

    protected void showToastLong(String text) {
        dismissToast();
        mToast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG);
        mToast.show();
    }

    protected View getView(int select_head_icon) {
        return LayoutInflater.from(this).inflate(select_head_icon, null);
    }

    public TextView tv(View parent, int id){
        return parent.findViewById(id);
    }

    public View view(int layoutId){
        return getLayoutInflater().inflate(layoutId,null);
    }

    protected void dismissToast() {
        if (mToast != null) {
            mToast.cancel();
        }
    }

    public boolean isEmpty(String str){
        return TextUtils.isEmpty(str);
    }

    public void show(View view){
        if(view.getVisibility()==View.INVISIBLE||view.getVisibility()==View.GONE){
            view.setVisibility(View.VISIBLE);
        }
    }

    protected String removeZero(String format) {
        return format.replace("01月","1月")
        .replace("02月","2月")
        .replace("03月","3月")
        .replace("04月","4月")
        .replace("05月","5月")
        .replace("06月","6月")
        .replace("07月","7月")
        .replace("08月","8月")
        .replace("09月","9月")
        .replace("01日","1日")
        .replace("02日","2日")
        .replace("03日","3日")
        .replace("04日","4日")
        .replace("05日","5日")
        .replace("06日","6日")
        .replace("07日","7日")
        .replace("08日","8日")
        .replace("09日","9日");
    }

    // 判断左上角按钮为返回或其他操作
    public void setLeftIsFinish(boolean leftIsFinish) {
        LeftIsFinish = leftIsFinish;
    }

    public boolean isLeftIsFinish() {
        return LeftIsFinish;
    }

    // 可以把常量单独放到一个Class中
    public static final String ACTION_NETWORK_CHANGE = "android.net.conn.CONNECTIVITY_CHANGE";
    public static final String ACTION_PUSH_DATA = "fm.data.push.action";
    public static final String ACTION_NEW_VERSION = "apk.update.action";
    protected Context mContext;
    // 这个地方有点“模板方法“的设计模式样子
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityList.add(this);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mContext=this;
    }

    @Override
    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int
            oldTop, int oldRight, int oldBottom) {
        // old是改变前的左上右下坐标点值，没有old的是改变后的左上右下坐标点值
        // 现在认为只要控件将Activity向上推的高度超过了1/5屏幕高，就认为软键盘弹起
        int keyHeight = this.getWindowManager().getDefaultDisplay().getHeight()/5;
        // 阀值设置为屏幕高度的1/5
        if (oldBottom != 0 && bottom != 0 && (oldBottom - bottom > keyHeight)) {
            // 监听到软键盘弹起
            onKeyboardShow();
        } else if (oldBottom != 0 && bottom != 0
                && (bottom - oldBottom > keyHeight)) {
            // 监听到软件盘关闭
            onKeyboardHide();
        }
    }

    protected void onKeyboardHide() {
    }

    protected void onKeyboardShow() {
    }


    // 初始化UI，setContentView等
    protected void loadData(){

    };

    // 横竖屏切换，键盘等
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        try {
            super.onRestoreInstanceState(savedInstanceState);
        } catch (Exception e) {
        }
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        // 你可以添加多个Action捕获
//        IntentFilter filter = new IntentFilter();
//        filter.addAction(ACTION_NETWORK_CHANGE);
//        filter.addAction(ACTION_PUSH_DATA);
//        filter.addAction(ACTION_NEW_VERSION);
//        registerReceiver(receiver, filter);
//        // 还可能发送统计数据，比如第三方的SDK 做统计需求
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        if (receiver != null) {
//            unregisterReceiver(receiver);
//            // 还可能发送统计数据，比如第三方的SDK 做统计需求
//        }
//    }

//    BroadcastReceiver receiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            // 处理各种情况
//            String action = intent.getAction();
//            if (ACTION_NETWORK_CHANGE.equals(action)) { // 网络发生变化
//                // 处理网络问题
//            } else if (ACTION_PUSH_DATA.equals(action)) { // 可能有新数据
//
//            } else if (ACTION_NEW_VERSION.equals(action)) { // 可能发现新版本
//                // VersionDialog 可能是版本提示是否需要下载的对话框
//            }
//        }
//    };

    /**
     * @return boolean
     * @description 判断软盘
     * @author tanhuohui
     */
//    @Override
//    public boolean dispatchTouchEvent(MotionEvent ev) {
//        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
//            View v = getCurrentFocus();
//            if (isShouldHideInput(v, ev)) {
//
//                hideSoftInput(v);
//            }
//            return super.dispatchTouchEvent(ev);
//        }
//        // 必不可少，否则所有的组件都不会有TouchEvent了
//        return getWindow().superDispatchTouchEvent(ev) || onTouchEvent(ev);
//    }

    public boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = {0, 0};
            // 获取输入框当前的location位置
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right && event.getY() > top && event.getY() < bottom) {
                // 点击的是输入框区域，保留点击EditText的事件
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    /**
     * @return boolean
     * @description 隐藏软盘
     * @author tanhuohui
     */
    public boolean hideSoftInput(View v) {
        InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        return imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
    }

}