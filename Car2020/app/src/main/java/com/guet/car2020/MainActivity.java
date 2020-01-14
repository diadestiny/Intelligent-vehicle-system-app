package com.guet.car2020;

import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import Adaptors.FragmentAdapter;
import Data.NowData;
import Fragments.SimpleFragment;
import devlight.io.library.ntb.NavigationTabBar;

public class MainActivity extends AppCompatActivity {

    private ViewPager mViewPager;
    private String btMsg;//蓝牙传来的数据
    private final int MAXLINES = 200;
    private StringBuilder remoteData = new StringBuilder(256 * MAXLINES);
    private String devName = "/dev/ttyUSB0";
    private int speed = 115200;
    private int dataBits = 8;
    private int stopBits = 1;
    private int devfd = -1;

    private Handler healthHandler;
    private Handler carHandler;
    private Timer timer = new Timer();


    // 矢量图兼容支持
    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();
    }

    @Override
    protected void onDestroy() {
        timer.cancel();
        if (devfd != -1) {
            HardwareControler.close(devfd);
            devfd = -1;
        }
        super.onDestroy();
    }

    private void initUI() {
        ArrayList<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(new SimpleFragment());

        //viewPager加载
        mViewPager = findViewById(R.id.vp_horizontal_ntb);
        mViewPager.setAdapter(new FragmentAdapter(getSupportFragmentManager(),fragmentList));
        mViewPager.setOffscreenPageLimit(1);//预加载个数

        //多彩底部导航栏加载
        loadNavigation();

    }

    private void loadNavigation() {
        final String[] colors = getResources().getStringArray(R.array.default_preview);
        final NavigationTabBar navigationTabBar = (NavigationTabBar) findViewById(R.id.ntb_horizontal);
        final ArrayList<NavigationTabBar.Model> models = new ArrayList<>();
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.mipmap.car_1),
                        Color.parseColor(colors[0]))
                        .selectedIcon(getResources().getDrawable(R.mipmap.car_2))
                        .title("驾驶")
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.mipmap.health_1),
                        Color.parseColor(colors[1]))
                        .selectedIcon(getResources().getDrawable(R.mipmap.health_2))
                        .title("健康")
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.mipmap.assistant_1),
                        Color.parseColor(colors[2]))
                        .selectedIcon(getResources().getDrawable(R.mipmap.assistant_2))
                        .title("语音助手")
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.mipmap.road1),
                        Color.parseColor(colors[3]))
                        .selectedIcon(getResources().getDrawable(R.mipmap.road2))
                        .title("路面情况")
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_menu_settings),
                        Color.parseColor(colors[4]))
                        .selectedIcon(getResources().getDrawable(R.drawable.ic_menu_settings))
                        .title("设置")
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


    /**以下为串口连接内容**/

    private String hBlood = "0";
    private String lBlood = "0";
    private String heartRate = "0";
    private String weight = "0";
    private String condition = "0" ;
    private String temperature = "0" ;
    private String tired = "未发现";
    private String drunk = "0";
    private String body_temp = "0";
    private int lastRefresh = 0;


    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    lastRefresh++;
                    if(lastRefresh >= 20){
                        hBlood = "0";
                        lBlood = "0";
                        heartRate = "0";
                        weight = "0";

                        condition = "0 KM/H" ;
                        temperature = "0 °C" ;
                        tired = "未发现";
                        drunk = "0";
                    }
                    if (true) {//HardwareControler.select(devfd, 0, 0) == 1
                        //int retSize = HardwareControler.read(devfd, buf, BUFSIZE);
                        if (true) {//retSize > 0
                            lastRefresh = 0;
                            //String str = new String(buf, 0, retSize);
                            //Log.e("Port", str);
                            String str=btMsg;
                            remoteData.append(str);
                            //str = str.replace("\t","");
                            String[] args = str.split(",");
                            for (String s: args) {
                                //condition = String.valueOf(new Random().nextInt(10) + 39) + " KM/H" ;
                                if(s.charAt(0) == 'X'){
                                    heartRate = s.substring(1);
                                    int i_heart = Integer.valueOf(heartRate);
                                    if (i_heart >= 120)
                                        heartRate = "0";
                                    else if(i_heart >=85)
                                        heartRate = String.valueOf(i_heart - 5);
                                    else if(i_heart >=95)
                                        heartRate = String.valueOf(i_heart - 10);
                                    else if(i_heart >=115)
                                        heartRate = String.valueOf(i_heart - 25);
                                }

                                else if(  s.charAt(0) == 'L' ) {
                                    hBlood = s.substring(1);
                                    //Log.i("btMsg",hBlood);
                                }
                                if( s.charAt(0) == 'H')
                                    lBlood = s.substring(1);
                                if(s.charAt(0) == 'W' ){
                                    weight = s.substring(1);
                                }
                                if( s.charAt(0) == 'A' ){
                                    drunk = s.substring(1);
                                }
                                if(s.charAt(0) == 'S' ){
                                    condition = s.substring(1);
                                }
                                if(s.charAt(0) == 'T' ){
                                    temperature = s.substring(1);
                                }
                                if(s.charAt(0) == 'B')
                                    body_temp = s.substring(1);
                            }
                            Log.e("Port", str);
                        }
                    }
                    break;
            }
            //NowData.setAll(speed,temperature,drunk,tired,heartRate,weight,hBlood,lBlood,body_temp);//装载到当前数据
            //DataBaseKit.record();//记录这次数据
            super.handleMessage(msg);
        }
    };
    private TimerTask task = new TimerTask() {
        public void run() {
            Message message = new Message();
            message.what = 1;
            handler.sendMessage(message);

            NowData.sethBlood(hBlood);
            NowData.setlBlood(lBlood);
            NowData.setWeight(weight);
            NowData.setBody_temp(body_temp);
            NowData.setHeartRate(heartRate);

            NowData.setTemperature(temperature);
            NowData.setSpeed(Integer.parseInt(condition));


            /* 更新传递数据到Fragment */
            Message healthMessage = new Message();
            Bundle healthBundle = new Bundle();
            healthBundle.putString("hBlood", hBlood);
            healthBundle.putString("lBlood", lBlood);
            healthBundle.putString("heartRate", heartRate);
            healthBundle.putString("weight", weight);
            healthBundle.putString("body_temp", body_temp);
            healthMessage.setData(healthBundle);
            if(healthHandler != null)
                healthHandler.sendMessage(healthMessage);

            /* 更新传递数据到Fragment */
            Message carMessage = new Message();
            Bundle carBundle = new Bundle();
            carBundle.putString("condition", condition);
            carBundle.putString("temperature", temperature);
            carBundle.putString("tired", tired);
            carBundle.putString("drunk", drunk);
            carMessage.setData(carBundle);
            if(carHandler != null)
                carHandler.sendMessage(carMessage);
        }
    };

}
