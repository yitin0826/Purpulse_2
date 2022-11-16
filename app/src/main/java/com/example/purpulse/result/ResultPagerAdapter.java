package com.example.purpulse.result;

import android.content.ComponentName;
import android.content.Context;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;

import java.util.List;

public class ResultPagerAdapter extends FragmentPagerAdapter {

    private FragmentManager fragmentManager;
    private FragmentTransaction mCurtransaction;
    private List<Fragment> mFragments;
    private Context mcontext;

    public ResultPagerAdapter(@NonNull FragmentManager fm, Context ctx, List<Fragment> fragmentList) {
        super(fm);
        fragmentManager = fm;
        this.mFragments = fragmentList;
        mcontext = ctx;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {//选择性实现
        if (position == 0){
            return "心電散點圖";
        }else{
            return "太極圖";
        }
    }

    public void clear(ViewGroup container){
        if(this.mCurtransaction == null){
            this.mCurtransaction = this.fragmentManager.beginTransaction();
        }

        for (int i=0; i<mFragments.size(); i++){
            long itemId = this.getItemId(i);
            String name = makeFragmentName(container.getId(), itemId);
            Fragment fragment = this.fragmentManager.findFragmentByTag(name);
            if (fragment != null){
                mCurtransaction.remove(fragment);
            }
        }
        mCurtransaction.commitNowAllowingStateLoss();
    }

    private static String makeFragmentName(int viewId, long id){
        return "android:switcher:" + viewId + ":" + id;
    }
}
