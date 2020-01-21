package com.test.xander.carplay.Data;

public class NowData {


    static int speed = 850;//车速
    static String temperature = "26";//车温
    static String drunk = "0";//酒驾
    static String tired = "未发现";//疲劳

    static String heartRate = "0";//心率
    static String weight = "0";//体重
    static String hBlood = "0";//血压
    static String lBlood = "0";//低压

    static String body_temp = "0";//体温


    public NowData(int speed, String temperature, String drunk, String tired, String heartRate, String weight, String hBlood, String lBlood, String body_temp) {
        setAll(speed, temperature, drunk, tired, heartRate, weight, hBlood, lBlood, body_temp);
    }

    static public void setAll(int speed, String temperature, String drunk, String tired, String heartRate, String weight, String hBlood, String lBlood, String body_temp) {
        NowData.speed = speed;
        NowData.temperature = temperature;
        NowData.drunk = drunk;
        NowData.tired = tired;
        NowData.heartRate = heartRate;
        NowData.weight = weight;
        NowData.hBlood = hBlood;
        NowData.lBlood = lBlood;
        NowData.body_temp = body_temp;
    }


    public static int getSpeed() {
        return speed;
    }

    public static void setSpeed(int speed) {
        NowData.speed = speed;
    }

    public static String getTemperature() {
        return temperature;
    }

    public static void setTemperature(String temperature) {
        NowData.temperature = temperature;
    }

    public static String getDrunk() {
        return drunk;
    }

    public static void setDrunk(String drunk) { NowData.drunk = drunk; }

    public static String getTired() { return tired; }

    public static void setTired(String tired) {
        NowData.tired = tired;
    }

    public static String getHeartRate() {
        return heartRate;
    }

    public static void setHeartRate(String heartRate) {
        NowData.heartRate = heartRate;
    }

    public static String getWeight() { return weight; }

    public static void setWeight(String weight) {
        NowData.weight = weight;
    }

    public static String gethBlood() { return hBlood; }

    public static void sethBlood(String hBlood) {
        NowData.hBlood = hBlood;
    }

    public static String getlBlood() {
        return lBlood;
    }

    public static void setlBlood(String lBlood) {
        NowData.lBlood = lBlood;
    }

    public static String getBody_temp() {
        return body_temp;
    }

    public static void setBody_temp(String body_temp) {
        NowData.body_temp = body_temp;
    }

    public static void setTempNowData(RecordItem recordItem){
        NowData.setAll(getSpeed(),getTemperature(),NowData.getDrunk(),getTired(),
                recordItem.getHeartRate()+"",recordItem.getWeight()+"",recordItem.gethBlood()+"",
                recordItem.getlBlood()+"",recordItem.getBody_temp()+"");
    }
}
