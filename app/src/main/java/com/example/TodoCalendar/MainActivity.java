package com.example.TodoCalendar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.tabs.TabLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

public class MainActivity extends AppCompatActivity {

    int currentTabNumber = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);

        SetView();

        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart(){
        super.onStart();


    }

    @Override
    public void onRestart(){
        super.onRestart();
    }

    // 全てのViewをセットする
    private void SetView(){
        setContentView(R.layout.activity_main);

        // タブレイアウトの初期表示をどのタブにするか決める
        Intent i = getIntent();
        int tabPosition = i.getIntExtra("tab_pos", 0);

        OriginalFragmentPagerAdapter adapter = new OriginalFragmentPagerAdapter(getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.viewPager);
        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                currentTabNumber = position;
            }
            @Override
            public void onPageSelected(int position) {
                currentTabNumber = position;
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        // タブ新規追加画面から遷移した場合はカレンダーに遷移する
        // タブ変更画面から遷移した場合はタスクリストに遷移する
        viewPager.setCurrentItem(tabPosition);

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        menu.clear();

        switch (currentTabNumber) {
            case 0:
                return false;
            case 1:
                // 参照するリソースは上でリソースファイルに付けた名前と同じもの
                getMenuInflater().inflate(R.menu.sortmenu, menu);

                final MenuItem changeInvisible = menu.getItem(0);

                SharedPreferences preferences = getSharedPreferences("TodoConf", Context.MODE_PRIVATE);
                boolean hideAchevedTask = preferences.getBoolean("hideAchevedTask", false);

                if (hideAchevedTask) {
                    changeInvisible.setTitle(getString(R.string.visible_tasklow));
                } else {
                    changeInvisible.setTitle(getString(R.string.invisible_tasklow));
                }



                return super.onCreateOptionsMenu(menu);
        }

        return false;

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("SaveInstanc"," instance");
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("onDestroy");
    }


    @Override
    public void onPause() {
        super.onPause();
        System.out.println("onPause");
    }

}

