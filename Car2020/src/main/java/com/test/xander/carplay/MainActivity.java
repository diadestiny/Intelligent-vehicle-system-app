package com.test.xander.carplay;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.widget.Toast;

import com.friendlyarm.AndroidSDK.HardwareControler;

import com.iflytek.aiui.demo.chat.ui.chat.ChatFragment;
import com.iflytek.aiui.demo.chat.ui.settings.SettingsFragment;
import com.test.xander.carplay.Adaptors.FragmentAdapter;
import com.test.xander.carplay.Fragments.CarFragment;
import com.test.xander.carplay.Fragments.HealthFragment;
import com.test.xander.carplay.Utils.AlertHander;
import com.test.xander.carplay.Utils.FakeDataUtil;
import com.test.xander.carplay.Utils.IntterInfo;
import com.test.xander.carplay.Data.NowData;
import com.test.xander.carplay.Fragments.RoadSituationFragment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import javax.inject.Inject;

import butterknife.ButterKnife;
import dagger.android.AndroidInjection;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;
import devlight.io.library.ntb.NavigationTabBar;


public class MainActivity extends AppCompatActivity implements  HasSupportFragmentInjector {
    @Inject
    DispatchingAndroidInjector<Fragment> fragmentInjector;

    private BluetoothDevice selectDevice;
    private AcceptThread acceptThread;
    BluetoothAdapter mBluetoothAdapter;
    private final UUID MY_UUID = UUID
            .fromString("00001101-0000-1000-8000-00805F9B34FB");
    private String btMsg;//蓝牙传来的数据

    private IntterInfo inter;

    private final int MAXLINES = 200;
    private StringBuilder remoteData = new StringBuilder(256 * MAXLINES);
    private String devName = "/dev/ttyUSB0";
    private int speed = 115200;
    private int dataBits = 8;
    private int stopBits = 1;
    private int devfd = -1;

    private String hBlood = "0";
    private String lBlood = "0";
    private String heartRate = "0";
    private String weight = "0";

    private String condition = "0";
    private String temperature = "0";
    private String tired = "未发现";
    private String drunk = "0";
    private String body_temp = "0";
    private int lastRefresh = 0;

    private Handler healthHandler;
    private Handler carHandler;

    // 矢量图兼容支持
    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Override
    public void onDestroy() {
        timer.cancel();
        if (devfd != -1) {
            HardwareControler.close(devfd);
            devfd = -1;
        }
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidInjection.inject(MainActivity.this);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);


        AlertHander.activity = this;//传activity

        initUI();
        //临时的NowData数据，在没有连接蓝牙硬件时的测试数据
        NowData.setTempNowData(FakeDataUtil.generateFakeData());
        btMsg = new String("L0,H0,W0,A0,S0,T0,B0,X0");
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        acceptThread = new AcceptThread();
        acceptThread.start();
        connectThreaded();

        // 讯飞SDK初始化
        //SpeechUtility.createUtility(context, SpeechConstant.APPID +"=83f02b42a4571fc7557708be0da868a6");
    }

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return fragmentInjector;
    }


    void initUI() {
        ArrayList<Fragment> fragmentList = new ArrayList<>();
        final ViewPager mViewPager = findViewById(R.id.vp_horizontal_ntb);

        fragmentList.add(CarFragment.newInstance());
        fragmentList.add(HealthFragment.newInstance());
        fragmentList.add(new ChatFragment());
        fragmentList.add(new RoadSituationFragment());
        fragmentList.add(new SettingsFragment());
        mViewPager.setAdapter(new FragmentAdapter(getSupportFragmentManager(), fragmentList));
        mViewPager.setOffscreenPageLimit(3);//预加载个数

        //多彩底部导航栏加载
        loadNavigation(mViewPager);
    }

    private void loadNavigation(ViewPager mViewPager) {
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


    public void setHealthHandler(android.os.Handler handler) {
        this.healthHandler = handler;
    }

    public void setCarHandler(Handler handler) {
        this.carHandler = handler;
    }

    /**
     * 以下为串口连接内容
     **/
    private Timer timer = new Timer();
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    lastRefresh++;
                    if (lastRefresh >= 20) {
                        hBlood = "0";
                        lBlood = "0";
                        heartRate = "0";
                        weight = "0";
                        condition = "0 KM/H";
                        temperature = "0 °C";
                        tired = "未发现";
                        drunk = "0";
                    }
                    if (true) {//HardwareControler.select(devfd, 0, 0) == 1
                        //int retSize = HardwareControler.read(devfd, buf, BUFSIZE);
                        if (true) {//retSize > 0
                            lastRefresh = 0;
                            //String str = new String(buf, 0, retSize);
                            //Log.e("Port", str);
                            String str = btMsg;
                            remoteData.append(str);
                            //str = str.replace("\t","");
                            String[] args = str.split(",");
                            for (String s : args) {
                                //condition = String.valueOf(new Random().nextInt(10) + 39) + " KM/H" ;
                                if (s.charAt(0) == 'X') {
                                    heartRate = s.substring(1);
                                    int i_heart = Integer.valueOf(heartRate);
                                    if (i_heart >= 120)
                                        heartRate = "0";
                                    else if (i_heart >= 85)
                                        heartRate = String.valueOf(i_heart - 5);
                                    else if (i_heart >= 95)
                                        heartRate = String.valueOf(i_heart - 10);
                                    else if (i_heart >= 115)
                                        heartRate = String.valueOf(i_heart - 25);
                                } else if (s.charAt(0) == 'L') {
                                    hBlood = s.substring(1);
                                    //Log.i("btMsg",hBlood);
                                }
                                if (s.charAt(0) == 'H')
                                    lBlood = s.substring(1);
                                if (s.charAt(0) == 'W') {
                                    weight = s.substring(1);
                                }
                                if (s.charAt(0) == 'A') {
                                    drunk = s.substring(1);
                                }
                                if (s.charAt(0) == 'S') {
                                    condition = s.substring(1);
                                }
                                if (s.charAt(0) == 'T') {
                                    temperature = s.substring(1);
                                }
                                if (s.charAt(0) == 'B')
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
        @Override
        public void run() {
            Message message = new Message();
            message.what = 1;
            handler.sendMessage(message);
            updateData();
        }
    };

    private void updateData() {
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
        if (healthHandler != null)
            healthHandler.sendMessage(healthMessage);

        /* 更新传递数据到Fragment */
        Message carMessage = new Message();
        Bundle carBundle = new Bundle();
        carBundle.putString("condition", condition);
        carBundle.putString("temperature", temperature);
        carBundle.putString("tired", tired);
        carBundle.putString("drunk", drunk);
        carMessage.setData(carBundle);
        if (carHandler != null)
            carHandler.sendMessage(carMessage);
    }

    private void connectThreaded() {
        new Thread(() -> {
            while (true) {
                inter = new IntterInfo();
                runOnUiThread(() -> {
                    Toast.makeText(this, "服务器连接成功", Toast.LENGTH_LONG).show();
                });
                inter.listen();
                runOnUiThread(() -> {
                    Toast.makeText(this, "服务器断开", Toast.LENGTH_LONG).show();
                });
            }
        }).start();
    }


    //蓝牙设备服务端监听客户端的线程类
    private class AcceptThread extends Thread {
        BluetoothSocket mSocket;
        private boolean connect() {
            try {
                mSocket.connect();
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("btMsg", "连接失败");
                return false;
            }
            return true;
        }

        public AcceptThread() {
            selectDevice = mBluetoothAdapter.getRemoteDevice("00:18:E4:36:0E:F5");//地址
            //serverSocket = mBluetoothAdapter.listenUsingRfcommWithServiceRecord("HC-06", MY_UUID);
            try {
                mSocket = selectDevice.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void run() {
            while (true) {
                try {
                    while (!connect()) {
                        sleep(1000);
                    }
                    runOnUiThread(() -> {
                        Toast.makeText(MainActivity.this, "蓝牙设备连接成功", Toast.LENGTH_LONG).show();
                    });
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
                    Log.d("btMsg", "连接到");
                    timer.schedule(task, 0, 500);
                    while (true) {
                        btMsg = bufferedReader.readLine();
                        Log.d("btMsg", btMsg);
                    }
                } catch (Exception e) {
                    runOnUiThread(() -> {
                        Toast.makeText(MainActivity.this, "蓝牙设备断开", Toast.LENGTH_LONG).show();
                    });

                }
            }
        }
    }

}
