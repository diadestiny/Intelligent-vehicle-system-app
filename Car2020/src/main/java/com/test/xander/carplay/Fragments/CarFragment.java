package com.test.xander.carplay.Fragments;


import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.test.xander.carplay.MainActivity;
import com.test.xander.carplay.R;
import com.test.xander.carplay.Utils.AlertHander;
import com.test.xander.carplay.Utils.IntterInfo;
import com.test.xander.carplay.Data.NowData;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;



public class CarFragment extends Fragment {

    static public String tiredFromSocket = null;

    CarListen mlisten;


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int REFRESHINTERVAL = 3;    //刷新时间间隔（秒）
    private static int i = 0;
    private static int drunk_i = 0;

    private static int fati_o = 0;
    private static int fati_c = 0;
    private static int fati_k = 0;

    TextView conditionView;
    TextView temperatureView;
    TextView tiredView;
    TextView drunkView;

    private String mParam1;
    private String mParam2;

    public CarFragment() {
        // Required empty public constructor
    }

    public static CarFragment newInstance(String param1, String param2) {
        CarFragment fragment = new CarFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static CarFragment newInstance() {
        CarFragment fragment = new CarFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
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
        View view = inflater.inflate(R.layout.fragment_car, container, false);
        initSwipeRefresh(view);
        mlisten = new CarListen();
        IntterInfo.carListen = mlisten;
        return view;
    }

    private void initSwipeRefresh(View view) {
        conditionView = view.findViewById(R.id.textView_car_condi);
        temperatureView = view.findViewById(R.id.textView_car_temp);
        drunkView = view.findViewById(R.id.textView_car_drunk);
        tiredView = view.findViewById(R.id.textView_car_tired);

        //初始化下拉刷新组件,添加监听
        final SwipeRefreshLayout mSwipeRefreshLayout = view.findViewById(R.id.sr_car);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new CarFragment.FetchInfoTask().execute();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
        mSwipeRefreshLayout.setColorSchemeColors(Color.BLUE, Color.GREEN, Color.CYAN, Color.RED);
    }


    //与Activity通信获取实时数据
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle bundle = msg.getData();
            //获取传入数据
            float speed = Float.valueOf(bundle.getString("condition")) / 10;
            String condition = String.valueOf(speed) + " KM/H";
            String temperature = bundle.getString("temperature") + " °C";
//          String tired = bundle.getString("tired");
            String drunk = bundle.getString("drunk");
            int drunk_int = Integer.valueOf(drunk);
            if (drunk_int >= 500)//900
                drunk_i++;
            else {
                drunk_i = 0;
                drunkView.setText("未发现");
                drunkView.setTextColor(Color.BLACK);
            }
            if (drunk_i == 8) {
                AlertDialog dialog = new AlertDialog.Builder(getContext()).setTitle("警告").setMessage("检测到醉驾，请勿酒后开车！").
                        setPositiveButton("确定", null).create();
                dialog.show();
                MediaPlayer mediaPlayer = MediaPlayer.create(getActivity(), R.raw.tts_drunk);
                mediaPlayer.start();
                drunkView.setText("醉驾");
                drunkView.setTextColor(Color.RED);
            }

            //处理逻辑业务
            conditionView.setText(condition);
            temperatureView.setText(temperature);
            //  tiredView.setText(tired);
            drunkView.setText(drunk.substring(0, 1) + "." + drunk.substring(1));
        }
    };

    //生命周期
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        MainActivity mainActivity = (MainActivity) context;
        mainActivity.setCarHandler(handler);
    }

    public String callService() {
        try {
            // 命名空间
            String nameSpace = "http://example";
            // 调用的方法名称
            String methodName = "sayHelloWorldFrom";
            // SOAP Action
            String soapAction = nameSpace + methodName;
            SoapObject request = new SoapObject(nameSpace, methodName);
            //request.addProperty("from", "dfsg");
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                    SoapEnvelope.VER11);
            envelope.dotNet = false;
            envelope.bodyOut = request;
            envelope.setOutputSoapObject(request);
            envelope.encodingStyle = "UTF-8";
            HttpTransportSE transport = new HttpTransportSE(
                    "http://23239i0a13.iok.la:59829");// wsdl文档  "http://192.168.1.161:8080/services/HelloWorld?wsdl"
            try {
                // 调用WebService
                transport.call(soapAction, envelope);
                // transport.call(null, envelope);
            } catch (Exception e) {
                e.printStackTrace();
                return "0";
            }
            SoapPrimitive obj = null;
            if (envelope.bodyIn instanceof SoapFault) {
                final SoapFault sf = (SoapFault) envelope.bodyIn;
                Log.e("btMsg", sf.toString());//Log.e("PortError", sf.toString());
                return "-1";
            } else obj = (SoapPrimitive) envelope.bodyIn;

            if (obj == null)
                return "0";
            else return obj.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private class FetchInfoTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            String s = callService();
            Log.e("btMsg", "疲劳驾驶接收到 " + s);
            if (s != null && s.equals("睁眼")) {
                fati_k++;
                fati_o++;
            }
            if (s != null && s.equals("闭眼")) {
                fati_k++;
                fati_c++;
            }
            return s;
        }

        @Override
        protected void onPostExecute(String tired) {
            tiredView.setText(tired);
            if (getView() != null) {
                if (fati_c >= 4) {
                    AlertDialog dialog = new AlertDialog.Builder(getContext()).setTitle("警告").setMessage("检测到疲劳驾驶的倾向，请注意预防！").
                            setPositiveButton("确定", null).create();
                    dialog.show();
                    MediaPlayer mediaPlayer = MediaPlayer.create(getActivity(), R.raw.tts_fatigue);
                    mediaPlayer.start();
                    tiredView.setText("疲劳驾驶");

                    //疲劳驾驶**************************************************
                    NowData.setTired("tired");
                    fati_c = 0;
                    fati_o = 0;
                }
                if (fati_k == 10) {
                    //疲劳驾驶复位**************************************************
                    NowData.setTired("not");
                    fati_k = 0;
                    fati_c = 0;
                    fati_o = 0;
                }
            }
        }
    }

    public class CarListen {
        public void put(String[] ins) {
            Log.d("mtest", "CarListen.put" + ins.length);
            if (ins.length < 7)
                return;
            String s = ins[5];
            if (s.equals("1")) {
                AlertHander.voice("检测到疲劳驾驶的倾向，请注意预防！");
                getActivity().runOnUiThread(() -> {
                    tiredView.setText("疲劳");
                    android.support.v7.app.AlertDialog dialog = new android.support.v7.app.AlertDialog.Builder(getActivity())
                            .setIcon(R.drawable.alert)//设置标题的图片
                            .setTitle("警告")
                            .setMessage("检测到疲劳驾驶的倾向，请注意预防！")
                            .setPositiveButton("确定", (d, w) -> {
                                d.dismiss();
                            })
                            .create();
                    dialog.show();
                });
            } else {
                getActivity().runOnUiThread(() -> {
                    tiredView.setText("正常");
                });
            }
        }

    }
}
