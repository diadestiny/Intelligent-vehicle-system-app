package com.test.xander.carplay.Utils;

import android.app.Activity;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.iflytek.aiui.demo.chat.repository.TTSManager;
import com.iflytek.aiui.demo.chat.ui.chat.PlayerViewModel;
import com.test.xander.carplay.R;

public class AlertHander {
    static public Activity activity;
    static public TTSManager ttsManager;
    static public PlayerViewModel mPlayer;
    static AlertDialog mdialog;
    static String lastVoice;
    static String lastPop_up;

    static public void init(Activity activity_, TTSManager ttsManager_){
        activity=activity_;
        ttsManager=ttsManager_;
    }

    static public void voice(String string){//语音提示
        Log.d("mtest","voice:"+string);
        if(string.equals(lastVoice))
            return;
        lastVoice=string;
        ttsManager.startTTS(string,() -> {
            mPlayer.play();

        },false);
    }

    static public void pop_up(String string){//弹窗提示
        Log.d("mtest","pop_up:"+string);
        if(string.equals(lastPop_up))
            return;
        lastPop_up=string;
        activity.runOnUiThread(()->{
                if(mdialog!=null){
                    mdialog.dismiss();
                    mdialog=null;
                }
                AlertDialog dialog = new AlertDialog.Builder(activity)
                        . setIcon(R.drawable.alert)//设置标题的图片
                        .setTitle("警告")
                        .setMessage(string)
                        .setPositiveButton("确定",(d,w)->{
                            d.dismiss();
                        })
                        .create();
                mdialog=dialog;
                mdialog.show();
            Log.d("mtest","pop_up show:");
            new Thread(()->{
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                activity.runOnUiThread(()->{
                    if(mdialog!=null){
                        mdialog.dismiss();
                        mdialog=null;
                    }

                });
            }).start();

        });
    }

}
