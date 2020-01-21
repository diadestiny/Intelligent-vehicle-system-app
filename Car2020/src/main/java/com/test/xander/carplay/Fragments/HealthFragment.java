package com.test.xander.carplay.Fragments;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.test.xander.carplay.MainActivity;
import com.test.xander.carplay.R;
import com.test.xander.carplay.StatisticsActivity;
import com.wx.ovalimageview.RoundImageView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;


public class HealthFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int REFRESHINTERVAL  = 1;    //刷新时间间隔（秒）
    private static int tmpi = 0;

    private TextView heartView;
    private TextView lbloodBiew;
    private TextView weightView;
    private TextView hbloodBiew;
    private TextView human_temp;
    private Random rand;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public HealthFragment() {
        // Required empty public constructor
    }

    public static HealthFragment newInstance(String param1, String param2) {
        HealthFragment fragment = new HealthFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static HealthFragment newInstance() {
        return new HealthFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_health, container, false);
        rand =new Random(25);
        initSwipeRefresh(view);
        initClick(view);
        return view;
    }

    private void initClick(View view) {
        RoundImageView sts_button = view.findViewById(R.id.button_show_sta);
        sts_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), StatisticsActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initSwipeRefresh(View view){
         heartView = view.findViewById(R.id.heart);
         weightView = view.findViewById(R.id.weight);
         hbloodBiew = view.findViewById(R.id.bloodhigh);
         lbloodBiew = view.findViewById(R.id.bloodlow);
         human_temp = view.findViewById(R.id.human_temp);

        //初始化下拉刷新组件,添加监听
        final SwipeRefreshLayout mSwipeRefreshLayout = view.findViewById(R.id.srl);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new FetchInfoTask().execute();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
        mSwipeRefreshLayout.setColorSchemeColors(Color.BLUE, Color.GREEN, Color.CYAN, Color.RED);

        //定时刷新心率，血压
        TimerTask infoTimerTask = new TimerTask() {
            @Override
            public void run() {
                new FetchInfoTask().execute();
            }
        };
        Timer infoTimer = new Timer();
        infoTimer.schedule(infoTimerTask, 0, REFRESHINTERVAL * 500);
    }

    //与Activity通信获取实时数据
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle bundle = msg.getData();
            DecimalFormat df=new DecimalFormat("0.00");
            //获取传入数据
            String heartRate = bundle.getString("heartRate");
            heartRate = String.valueOf(Integer.valueOf(heartRate));
            String lBlood = bundle.getString("lBlood");
            lBlood = (Integer.valueOf(heartRate)==0?"0":String.valueOf(3*Integer.valueOf(lBlood)+rand.nextInt(3)));
            String hBlood = bundle.getString("hBlood");
            hBlood = (Integer.valueOf(heartRate)==0?"0":String.valueOf((Integer.valueOf(hBlood)==0?0:rand.nextInt(3))+(int)(1.5*Integer.valueOf(hBlood))));
            String weight = bundle.getString("weight");
            weight = String.valueOf((df.format(Integer.valueOf(weight)/10.0)));
            String body_temp = bundle.getString("body_temp");

            //处理逻辑业务
            heartView.setText(heartRate);
            hbloodBiew.setText(hBlood);
            lbloodBiew.setText(lBlood);
            weightView.setText(weight);
            human_temp.setText(Integer.valueOf(body_temp)/100.0+"");

        }
    };

    //生命周期
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        MainActivity mainActivity = (MainActivity) context;
        mainActivity.setHealthHandler(handler);
    }

    private static class FetchInfoTask extends AsyncTask<Void,Void,ArrayList<Integer>> {
        @Override
        protected ArrayList<Integer> doInBackground(Void... params) {
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Integer> dataList) {
            tmpi++;
            //先保留
        }
    }
}
