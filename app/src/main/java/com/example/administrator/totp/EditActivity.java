package com.example.administrator.totp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2018/4/4 0004.
 */

public class EditActivity extends BaseActivity {

    private EditText editText;
    View ll;

//    @Override
//    protected void onKeyboardHide() {
//        super.onKeyboardHide();
//        Intent intent = new Intent();
//        intent.putExtra("num", editText.getText().toString());
//       setResult(1, intent);
//       finish();
//    }


    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        editText = findViewById(R.id.et);
        ll = findViewById(R.id.ll);
        ll.addOnLayoutChangeListener(this);
        setEditTextState();
    }

    private void setEditTextState() {
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();

        Timer timer = new Timer();
        timer.schedule(new TimerTask()
                       {

                           public void run()
                           {
                               InputMethodManager  inputManager =
                                       (InputMethodManager)editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                               inputManager.showSoftInput(editText, 0);
                           }

                       },
                200);
    }
}
