package com.example.myapplication.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.myapplication.Fragments.Calander;
import com.example.myapplication.Fragments.SubjectDashboard;

public class SectionPagerAdapter extends FragmentPagerAdapter {
    String batch, branch;
    public SectionPagerAdapter(@NonNull FragmentManager fm, String batch, String branch) {
        super(fm);
        this.batch=batch;
        this.branch=branch;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new SubjectDashboard(batch,branch);
            case 1:
                return new Calander(batch,branch);
              default:
                return null;
        }
    }
    @Override
    public int getCount() {
        return 2;
    }
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Subjects";
            case 1:
                return "Classes";
            default:
                return null;
        }
    }
}
