package com.test.xander.carplay.Data;

import android.util.Log;


public class RecordItem  {

    private int time =0;//主索引

    private int heartRate = 0;//心率
    private int weight = 0;//体重（要除以10）
    private int hBlood = 0;//血压
    private int lBlood = 0;//低压
    private int body_temp = 0;//体温  除以10

    public int estimate(){//用于对健康情况作出评定
        if(heartRate==0)
            return 0;//无数据
        if(heartRate<40||heartRate>160)
            return 3;
        if(hBlood>140&&lBlood>90)
            return 3;//高血压
        if(body_temp>380)
            return 3;//高烧
        if(body_temp>373)
            return 2;//低烧
        return 1;//正常
    }

    public RecordItem(){//默认为使用当前Now的数据初始化
     this(0, NowData.heartRate,NowData.weight,NowData.hBlood,NowData.lBlood,NowData.body_temp);
    }

    public RecordItem(int time, RecordItem recordItem){
        this.time = time;
        this.heartRate = recordItem.heartRate;
        this.weight = recordItem.weight;
        this.hBlood = recordItem.hBlood;
        this.lBlood = recordItem.lBlood;
        this.body_temp = recordItem.body_temp;
    }

    public RecordItem(int time){
        this.time = time;
    }

    public RecordItem(int time, String heartRate, String weight, String hBlood, String lBlood, String body_temp) {
        this.time = time;
        this.heartRate = Integer.parseInt(heartRate);
        this.weight = Integer.parseInt(weight);
        this.hBlood = Integer.parseInt(hBlood);
        this.lBlood = Integer.parseInt(lBlood);
        this.body_temp = Integer.parseInt(body_temp);
    }

    public void reset(){
        this.heartRate = 0;
        this.weight = 0;
        this.hBlood = 0;
        this.lBlood = 0;
        this.body_temp = 0;
    }

    public void addUp(RecordItem recordItem){//累加
        heartRate+=recordItem.heartRate;
        weight+=recordItem.weight;
        hBlood+=recordItem.hBlood;
        lBlood+=recordItem.lBlood;
        body_temp+=recordItem.body_temp;
    }


    public RecordItem divide(int num){//除
        if(num==0) {
            Log.e("DataBaseKit","除以零！");
            return new RecordItem(0);
        }
        RecordItem recordItemResult=new RecordItem();
        recordItemResult.setHeartRate(heartRate/num);
        recordItemResult.setWeight(weight/num);
        recordItemResult.sethBlood(hBlood/num);
        recordItemResult.setlBlood(lBlood/num);
        recordItemResult.setBody_temp(body_temp/num);
        return recordItemResult;
    }

    public RecordItem multiply(int num){//乘
        RecordItem recordItemResult=new RecordItem();
        recordItemResult.setHeartRate(heartRate*num);
        recordItemResult.setWeight(weight*num);
        recordItemResult.sethBlood(hBlood*num);
        recordItemResult.setlBlood(lBlood*num);
        recordItemResult.setBody_temp(body_temp*num);
        return recordItemResult;
    }

    public RecordItem add(RecordItem recordItem){//求和
        RecordItem recordItemResult=new RecordItem();
        recordItemResult.setHeartRate(heartRate+recordItem.heartRate);
        recordItemResult.setWeight(weight+recordItem.weight);
        recordItemResult.sethBlood(hBlood+recordItem.hBlood);
        recordItemResult.setlBlood(lBlood+recordItem.lBlood);
        recordItemResult.setBody_temp(body_temp+recordItem.body_temp);
        return recordItemResult;
    }

    public void setLike(RecordItem recordItem){
        this.heartRate = recordItem.heartRate;
        this.weight = recordItem.weight;
        this.hBlood = recordItem.hBlood;
        this.lBlood = recordItem.lBlood;
        this.body_temp = recordItem.body_temp;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(int heartRate) {
        this.heartRate = heartRate;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int gethBlood() {
        return hBlood;
    }

    public void sethBlood(int hBlood) {
        this.hBlood = hBlood;
    }

    public int getlBlood() {
        return lBlood;
    }

    public void setlBlood(int lBlood) {
        this.lBlood = lBlood;
    }

    public int getBody_temp() { return body_temp; }

    public void setBody_temp(int body_temp) {
        this.body_temp = body_temp;
    }

}
