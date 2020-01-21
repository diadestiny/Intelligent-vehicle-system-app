package com.test.xander.carplay.Utils;

import android.util.Log;

import com.iflytek.aiui.demo.chat.handler.Answer;
import com.iflytek.aiui.demo.chat.handler.IntentHandler;
import com.iflytek.aiui.demo.chat.model.SemanticResult;
import com.iflytek.aiui.demo.chat.ui.chat.ChatViewModel;
import com.iflytek.aiui.demo.chat.ui.chat.PlayerViewModel;
import com.iflytek.aiui.demo.chat.ui.common.PermissionChecker;
import com.test.xander.carplay.Data.NowData;

import org.json.JSONArray;
import org.json.JSONObject;


public class carMsgHandler extends IntentHandler{
    private final String SPEED="speed";
    private final String TMEPERATURE ="temperature";
    private final String DRUNK ="drunk";
    private final String TIRED ="tired";
    private final String HEARTRATE="heartRate";
    private final String WEIGHT="weight";
    private final String BLOOD="blood";
    private final String BODY_TEMP="body_temp";
    private final String BODY_INFO="body_info";
    private final String CAR_INFO="car_info";


    public carMsgHandler(ChatViewModel model, PlayerViewModel player, PermissionChecker checker) {
        super(model, player, checker);
        Log.d("test","in");
    }

    @Override
    public Answer getFormatContent(SemanticResult result) {
        Log.d("test","result");
        String answer=null;
        JSONArray slots = result.semantic.optJSONArray("slots");
        JSONObject item = slots.optJSONObject(0);
        String askItem=item.optString("normValue");
        Log.d("test",askItem);
        switch (askItem){
            case SPEED:{
                answer=new String("当前车速是"+NowData.getSpeed()/10.0+"公里每小时");
            }break;
            case TMEPERATURE:{
                answer=new String("车内温度为"+Integer.parseInt(NowData.getTemperature())+"度");
            }break;
            case DRUNK:{
                answer=new String(NowData.getDrunk().compareTo("900")>0?"你可能酒驾":"未检测到酒驾，放心开车");
            }break;
            case TIRED:{
                answer=new String(TIRED.compareTo(NowData.getTired())==0?"你可能已经疲劳驾驶，休息一下吧":"未疲劳驾驶，放心");
            }break;
            case HEARTRATE:{
                answer=new String("您的心率是"+ Integer.parseInt(NowData.getHeartRate()));
            }break;
            case WEIGHT:{
                answer=new String("您的体重是"+1.2*Integer.parseInt(NowData.getWeight())/10.0+"公斤");
            }break;
            case BLOOD:{
                answer=new String("您的血压为 高压"+1.5*Integer.parseInt(NowData.gethBlood())+" ，低压"+3*Integer.parseInt(NowData.getlBlood()));
            }break;
            case BODY_TEMP:{
                answer="您的体温为 "+Integer.parseInt(NowData.getBody_temp())/10.0+"度";
            }break;
            case BODY_INFO:{
                answer="您的心率"+Integer.parseInt(NowData.getHeartRate())
                        +",体重"+ Integer.parseInt(NowData.getWeight())/10.0+"公斤"
                        +",血压 高压" + Integer.parseInt(NowData.gethBlood())
                        +" ,低压" + Integer.parseInt(NowData.getlBlood())
                        + ",体温"+Integer.parseInt(NowData.getBody_temp())/10.0+"度";
            }break;
            case CAR_INFO:{
                answer="您的车速是"+NowData.getSpeed()/10.0+"公里每小时,车内温度"+
                        +Integer.parseInt(NowData.getTemperature())+"度";
            }break;
            case "test":{
                answer="我测试成功了";
                break;
            }
            default:answer="您想知道哪些数据呢";
        }
        return new Answer(answer);
    }

}



