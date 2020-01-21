package com.test.xander.carplay.Fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.test.xander.carplay.Utils.AlertHander;
import com.test.xander.carplay.R;
import com.test.xander.carplay.Utils.IntterInfo;

public class RoadSituationFragment extends Fragment {
    Activity activity;
    TextView textView_car_lane_line;
    TextView textView_road_traffic_light;
    TextView textView_road_pedestrian;
    TextView textView_road_front_car;

    static String[][] TRAS = {
            {"车道线检测正常", "", "", "无车道线", "正常驾驶", "", "", "偏离车道", "", ""},
            {"无灯", "", "", "绿灯", "黄灯", "", "", "红灯", "", ""},
            {"无车辆", "", "", "有车辆", "车距正常", "", "", "不宜超车", "车距过近", ""},
            {"无行人", "", "", "出现行人", "", "", "", "行人过近", "", ""},
    };

    public RoadSituationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_road_situation, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        activity = getActivity();
        textView_car_lane_line = activity.findViewById(R.id.textView_car_lane_line);
        textView_road_traffic_light = activity.findViewById(R.id.textView_road_traffic_light);
        textView_road_pedestrian = activity.findViewById(R.id.textView_road_pedestrian);
        textView_road_front_car = activity.findViewById(R.id.textView_road_front_car);

        IntterInfo.roadListen = new RoadListen();
        new Thread(() -> {
            try {
                Thread.sleep(10000);
                getActivity().runOnUiThread(() -> {
                    String[] ins = {"0", "0", "7", "7", "0", "0", "0", "0"};
                    StringBuilder stringBuilderVoice = new StringBuilder();
                    StringBuilder stringBuilderPop_up = new StringBuilder();
                    for (int i = 1; i <= 4; i++) {
                        int warring = Integer.parseInt(ins[i]);
                        ins[i] = TRAS[i - 1][warring];
                        if (warring >= 3) {
                            stringBuilderVoice.append(ins[i] + "\n");
                        }
                        if (warring >= 7) {
                            stringBuilderPop_up.append(ins[i] + "\n");
                        }
                    }
                    if (stringBuilderPop_up.length() > 0) {
                        AlertHander.voice(stringBuilderPop_up.toString());
                        AlertHander.pop_up(stringBuilderPop_up.toString());
                    } else if (stringBuilderVoice.length() > 0) {
                        AlertHander.voice(stringBuilderVoice.toString());
                    }
                    activity.runOnUiThread(() -> {
                        textView_car_lane_line.setText(ins[1]);
                        textView_road_traffic_light.setText(ins[2]);
                        textView_road_front_car.setText(ins[3]);
                        textView_road_pedestrian.setText(ins[4]);
                    });
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public class RoadListen {
        public void put(String[] ins) {
            new Thread(() -> {
                if (ins.length < 6)
                    return;
                StringBuilder stringBuilderVoice = new StringBuilder();
                StringBuilder stringBuilderPop_up = new StringBuilder();
                for (int i = 1; i <= 4; i++) {
                    int warring = Integer.parseInt(ins[i]);
                    ins[i] = TRAS[i - 1][warring];
                    if (warring >= 3) {
                        stringBuilderVoice.append(ins[i] + "\n");
                    }
                    if (warring >= 7) {
                        stringBuilderPop_up.append(ins[i] + "\n");
                    }
                }
                if (stringBuilderPop_up.length() > 0) {
                    AlertHander.voice(stringBuilderPop_up.toString());
                    AlertHander.pop_up(stringBuilderPop_up.toString());
                } else if (stringBuilderVoice.length() > 0) {
                    AlertHander.voice(stringBuilderVoice.toString());
                    //AlertHander.pop_up(stringBuilderVoice.toString());
                }
                activity.runOnUiThread(() -> {
                    textView_car_lane_line.setText(ins[1]);
                    textView_road_traffic_light.setText(ins[2]);
                    textView_road_front_car.setText(ins[3]);
                    textView_road_pedestrian.setText(ins[4]);
                });
            }).start();
        }
    }
}
