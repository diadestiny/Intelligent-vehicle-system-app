package com.test.xander.carplay.Utils;

import android.util.Log;

import com.test.xander.carplay.Fragments.CarFragment;
import com.test.xander.carplay.Fragments.RoadSituationFragment;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class IntterInfo {

    Socket socket;
    BufferedOutputStream bos;
    BufferedReader bis;
    public static RoadSituationFragment.RoadListen roadListen;
    public static CarFragment.CarListen carListen;


    public IntterInfo(){
        try {
            while (bis==null) {
                //112.124.5.227 diadestiny服务器
                Log.d("mtest","connect wait");
                socket = new Socket("114.116.175.90", 10222);
                Log.d("mtest","connect ok");
                bos = new BufferedOutputStream(socket.getOutputStream());
                bis = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                Thread.sleep(5000);
            }
            bos.write("B\n".getBytes());
            bos.flush();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void listen(){
        while (true) {
            String re = get();
            Log.d("mtest",re);
            if (roadListen != null) {
                String[] ins=re.split(",");
                if(ins.length>=6)
                {
                    roadListen.put(ins);
                    carListen.put(ins);
                }
            }
        }
    }


    public String get() {
        try {
            if(bis!=null)
                return bis.readLine();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
