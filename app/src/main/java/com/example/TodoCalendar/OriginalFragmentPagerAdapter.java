package com.example.TodoCalendar;

import com.example.TodoCalendar.recyclerView.MovableTaskListFragment;
import com.example.TodoCalendar.recyclerView.MovableTodayTaskListFragment;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class OriginalFragmentPagerAdapter extends FragmentStatePagerAdapter {
    private CharSequence[] tabTitles = {"カレンダー", "今日のタスク", "タスク一覧"};

    public OriginalFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new CalendarFragment();
            case 1:
                return new MovableTodayTaskListFragment();
            case 2:
                return new MovableTaskListFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tabTitles.length;
    }
}
