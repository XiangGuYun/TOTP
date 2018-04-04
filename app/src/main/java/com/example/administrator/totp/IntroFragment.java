package com.example.administrator.totp;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

/**
 * Created by Administrator on 2018/4/4 0004.
 */

@SuppressLint("ValidFragment")
public class IntroFragment extends Fragment {

    private int id;
    private boolean isIntro;

    @SuppressLint("ValidFragment")
    public IntroFragment(int id, boolean isIntro) {
        this.id = id;
        this.isIntro = isIntro;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.fragment_intro, container, false);
        ImageView iv = view.findViewById(R.id.iv);
        Button btn = view.findViewById(R.id.btn);
        if(isIntro){
            btn.setVisibility(View.VISIBLE);
            btn.setOnClickListener(v -> {
                startActivity(new Intent(getActivity(), MainActivity.class));
                getActivity().finish();
            });
        }
        iv.setImageResource(id);
        return view;
    }
}
