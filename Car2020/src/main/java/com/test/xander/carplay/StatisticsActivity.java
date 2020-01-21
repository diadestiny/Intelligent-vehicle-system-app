package com.test.xander.carplay;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.test.xander.carplay.Adaptors.FragmentAdapter;
import com.test.xander.carplay.Fragments.DayStsFragment;
import com.test.xander.carplay.Fragments.MonthStsFragment;
import com.test.xander.carplay.Fragments.WeekStsFragment;

import java.util.ArrayList;

import devlight.io.library.ntb.NavigationTabBar;

public class StatisticsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        initUI();
    }

    void initUI(){
        ArrayList<Fragment> fragmentList = new ArrayList<>();
        final ViewPager mViewPager = findViewById(R.id.vp_horizontal_sts);
        fragmentList.add(DayStsFragment.newInstance());
        fragmentList.add(WeekStsFragment.newInstance());
        fragmentList.add(MonthStsFragment.newInstance());

        mViewPager.setAdapter(new FragmentAdapter(getSupportFragmentManager(), fragmentList));
        mViewPager.setOffscreenPageLimit(3);//预加载个数

        final String[] colors = getResources().getStringArray(R.array.default_preview);
        final NavigationTabBar navigationTabBar = findViewById(R.id.sts_horizontal);
        navigationTabBar.setBgColor(Color.WHITE);
        final ArrayList<NavigationTabBar.Model> models = new ArrayList<>();
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.mipmap.day),
                        Color.parseColor(colors[1]))
                        .selectedIcon(getResources().getDrawable(R.mipmap.day))
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.mipmap.week),
                        Color.parseColor(colors[2]))
                        .selectedIcon(getResources().getDrawable(R.mipmap.week))
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.mipmap.month),
                        Color.parseColor(colors[4]))
                        .selectedIcon(getResources().getDrawable(R.mipmap.month))
                        .build()
        );

        navigationTabBar.setModels(models);
        navigationTabBar.setViewPager(mViewPager, 0);
        navigationTabBar.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(final int position, final float positionOffset, final int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(final int position) {
                navigationTabBar.getModels().get(position).hideBadge();
            }

            @Override
            public void onPageScrollStateChanged(final int state) {

            }
        });

        navigationTabBar.postDelayed(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < navigationTabBar.getModels().size(); i++) {
                    final NavigationTabBar.Model model = navigationTabBar.getModels().get(i);
                    navigationTabBar.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            model.showBadge();
                        }
                    }, i * 100);
                }
            }
        }, 500);
    }
}
