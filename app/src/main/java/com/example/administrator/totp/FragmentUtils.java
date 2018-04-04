package com.example.administrator.totp;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by asus on 2016/7/20.
 * Activity托管Fragment的便捷工具类
 */
public class FragmentUtils<T extends FragmentActivity> {

    private FragmentManager manager;
    List<Fragment> fragments = new ArrayList<>();
    private int contentId;

    public FragmentUtils(T fa, List<Fragment> list, int contentId){
        manager = fa.getSupportFragmentManager();
        this.contentId = contentId;
        FragmentTransaction transaction = manager.beginTransaction();
        fragments.addAll(list);
        transaction.replace(contentId, list.get(0));
        transaction.commit();

    }

    public void remove(Fragment fragment){
        FragmentTransaction transaction = manager.beginTransaction();
        fragments.remove(fragment);
        transaction.remove(fragment);
        transaction.commit();
    }

    /**
     * 加载、显示、隐藏Fragment的便捷方法
     * @param targetFragment 需要显示的Fragment
     */
    public boolean switchFragment(Fragment targetFragment) {
        fragments.remove(targetFragment);
        FragmentTransaction transaction = manager.beginTransaction();
        if (!targetFragment.isAdded()) {    // 先判断是否被add过
            for (int i = 0; i < fragments.size(); i++) {
                if(fragments.get(i).isAdded()) transaction.hide(fragments.get(i));
            }
            transaction.add(contentId, targetFragment).commit(); // 隐藏当前的fragment，add下一个到Activity中
        } else {
            for (int i = 0; i < fragments.size(); i++) {
                if(fragments.get(i).isAdded()) transaction.hide(fragments.get(i));
            }
            transaction.show(targetFragment).commit(); // 隐藏当前的fragment，显示下一个
        }
        fragments.add(targetFragment);
        return true;
    }

    /**
     * 切换到对应的Fragment并将其加入到回退栈中
     */
    public void switchFragmentWithStack(Fragment targetFragment) {
        fragments.remove(targetFragment);
        FragmentTransaction transaction = manager.beginTransaction();
        if (!targetFragment.isAdded()) {    // 先判断是否被add过
            for (int i = 0; i < fragments.size(); i++) {
                if(fragments.get(i).isAdded()) transaction.hide(fragments.get(i));
            }
            transaction.add(contentId, targetFragment)// 隐藏当前的fragment，add下一个到Activity中
                    .addToBackStack(null)
                    .commit();

        } else {
            for (int i = 0; i < fragments.size(); i++) {
                if(fragments.get(i).isAdded()) transaction.hide(fragments.get(i));
            }
            transaction.show(targetFragment).commit(); // 隐藏当前的fragment，显示下一个
        }
        fragments.add(targetFragment);
    }

}
