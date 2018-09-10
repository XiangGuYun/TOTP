package com.example.administrator.totp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXTextObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.timqi.sectorprogressview.ColorfulRingProgressView;

import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends BaseActivity{

    TextView tv;
    EditText et;
    private SimpleDateFormat sdf, sdfmm, sdfHHmm, sdfAll;
    private long num1;
    private String leftStr;
    private ViewGroup ll;
    FrameLayout fl;
    private TextView tvClock;
    private long endTime;
    private long startTime;
    private int secs;
    CircleImageView civ;
    ColorfulRingProgressView sectorProgressView;
    MyHandler myHandler;
    private static final String APP_ID = "wx54c9985a2afe745a";    //这个APP_ID就是注册APP的时候生成的
    private static final String APP_SECRET = "b01b712e083d4939c42b76950b1826fe";
    private IWXAPI api;
    private CharSequence phoneNum = "1391654344";
    private SimpleDateFormat sdfYMD;
    private SimpleDateFormat sdf_mm_ss;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    msg();
                } else {
                    Toast.makeText(this, "权限授予未成功", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    protected void onKeyboardHide() {
        super.onKeyboardHide();
        showToast("down");
        et.setCursorVisible(false);
    }

    @Override
    protected void onKeyboardShow() {
        super.onKeyboardShow();
        showToast("up");
        et.setCursorVisible(true);
    }

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
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        sdfHHmm = new SimpleDateFormat("HH:mm");
        sdfAll = new SimpleDateFormat("HH:mm:ss");
        sdfYMD = new SimpleDateFormat("yyyy年MM月dd日 ");
        fl = findViewById(R.id.fl);
        ll = findViewById(R.id.ll);
        sectorProgressView = findViewById(R.id.prsView);
        tvClock = findViewById(R.id.tvClock);
        civ = findViewById(R.id.civ);
        ll.addOnLayoutChangeListener(this);
        tv = findViewById(R.id.tv);
        et = findViewById(R.id.et);
        sdf = new SimpleDateFormat("yyMMddHH");
        sdfmm = new SimpleDateFormat("mm");
        ll.addOnLayoutChangeListener(this);
       et.setOnClickListener(v-> et.setCursorVisible(true));
       sdf_mm_ss = new SimpleDateFormat("mm:ss");
       myHandler = new MyHandler(new WeakReference<>(this));
        try {
            int mm = Integer.parseInt(sdfmm.format(new Date())) ;
            int newmm = mm-mm%5;
            endTime = sdfHHmm.parse(getRightClock(newmm)).getTime();
            String startTimeStr = sdfAll.format(new Date());
            startTime = sdfAll.parse(startTimeStr).getTime();
            secs = (int) ((endTime-startTime)/(1000L));
        } catch (ParseException e) {
            e.printStackTrace();
        }

//        Timer timer = new Timer();
//        timer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                myHandler.sendEmptyMessage(0x123);
//            }
//        }, new Date(), 1000);

        new Thread(()->{
            try {
                while (true){
                    myHandler.sendEmptyMessage(0x123);
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        api = WXAPIFactory.createWXAPI(this, APP_ID, true);
        api.registerApp(APP_ID);

    }

    /**
     * 短信分享
     * @param view
     */
    public void email(View view) {
        if(ContextCompat.checkSelfPermission(
                this, Manifest.permission.SEND_SMS
        )!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS }, 1);
        }else{
            msg();
        }
    }

    //"请在2018年10月1日 08时05分之前使用临时密码898989"
    private void msg() {
        if(tv.getText().toString().equals("000000")){
            return;
        }
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:"));
        int mm = Integer.parseInt(sdfmm.format(new Date()));
        int newmm = mm-mm%5;
        String leftStr = sdfYMD.format(new Date());
        String rightStr = getRightClock(newmm);
        intent.putExtra("sms_body", TextUtils.concat("温馨提示\n","请在",
                leftStr,
                rightStr.replace(":","时").concat("分"),
                "之前使用临时密码",
                tv.getText().toString(),
                "开门-24小时安保服务专家，保家家平安。"));
        startActivity(intent);
    }

    private String buildTransaction(String type){
        return type==null?String.valueOf(System.currentTimeMillis()):type+System.currentTimeMillis();
    }

    public void weichat(View view) {
        //api.openWXApp();//启动微信
        if(tv.getText().toString().equals("000000")){
            return;
        }
        WXTextObject textObject = new WXTextObject();
        int mm = Integer.parseInt(sdfmm.format(new Date()));
        int newmm = mm-mm%5;
        String leftStr = sdfYMD.format(new Date());
        String rightStr = getRightClock(newmm);
        String text = TextUtils.concat("温馨提示\n","请在",
                leftStr,
                rightStr.replace(":","时").concat("分"),
                "之前使用临时密码",
                tv.getText().toString(),
                "开门-24小时安保服务专家，保家家平安。").toString();
        textObject.text = text;
        WXMediaMessage message = new WXMediaMessage();
        message.mediaObject = textObject;
        message.description = text;
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.message = message;
        req.transaction = buildTransaction("text");
        req.scene = SendMessageToWX.Req.WXSceneSession;
        api.sendReq(req);
    }

    static class MyHandler extends Handler{

        private WeakReference<MainActivity> act;

        public MyHandler(WeakReference<MainActivity> act) {
            this.act = act;
        }

        @Override
        public void handleMessage(Message msg) {
            if(msg.what==0x123){
                act.get().tvClock.setText(act.get().sdf_mm_ss.format(act.get().secs*1000L));
                //act.get().sectorProgressView.setPercent(100f*(299-act.get().secs)/299);
                act.get().secs--;
                if(act.get().secs==-1){
                    act.get().secs=299;
                    act.get().gen();
                }
//                if(act.get().secs==0){
//                    act.get().secs=299;
//                }
//                if(act.get().secs==288){
//                    act.get().gen();
//                }
            }
        }
    }

    public void generate(View view) {
        gen();
    }

    private void gen() {
        et.setCursorVisible(false);
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
            num1 = Math.floorMod(num, (long) Math.pow(10, 6));
            String result = String.valueOf(num1);
            if(result.length()==5){
                tv.setText("0"+result);
            }else {
                Log.d("Test", result);
                tv.setText(result);
            }
        }else {
            num1 =  num - floorDiv(num, (long) Math.pow(10, 6)) * (long) Math.pow(10, 6);
            String result = String.valueOf(num1);
            if(result.length()==5){
                tv.setText("0"+result);
            }else {
                tv.setText(result);
            }
        }
    }

    private String getRightClock(int newmm) {
        if(newmm+5!=60){
            return new SimpleDateFormat("HH:").format(new Date())+((newmm+5)<10?("0"+(newmm+5)):(newmm+5));
        }else {
            int hour = Integer.parseInt(new SimpleDateFormat("HH").format(new Date()));
            if(hour==23){
                hour = 0;
            }else {
                hour++;
            }
            return hour+":"+"00";
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
