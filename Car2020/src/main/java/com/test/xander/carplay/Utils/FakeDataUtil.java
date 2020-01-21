package com.test.xander.carplay.Utils;

import com.test.xander.carplay.Data.RecordItem;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class FakeDataUtil {
    static public RecordItem[] getHourRecord() {
        RecordItem[] recordItems = new RecordItem[24];
        int hour = getHour();
        for (int i = 23; i >= 0; i--) {
            hour--;
            if (hour < 0)
                hour = 24 + hour;
            if (hour <= 6 || hour >= 23) {
                recordItems[i] = new RecordItem(hour, "0", "0", "0", "0", "0");
                continue;
            }
            recordItems[i] = generateFakeData();
            recordItems[i].setTime(hour);
        }
        return recordItems;
    }

    static public RecordItem[] getWeekRecord() {
        RecordItem[] recordItems = new RecordItem[8];
        for (int i = 0; i < 8; i++) {
            recordItems[i] = generateFakeData();
            recordItems[i].setTime(7 - i);
        }
        return recordItems;
    }

    static public RecordItem[] getMonRecord() {
        RecordItem[] recordItems = new RecordItem[3];
        for (int i = 0; i < 3; i++) {
            recordItems[i] = generateFakeData();
            recordItems[i].setTime(2 - i);
        }
        return recordItems;
    }

    static private int getHour() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    static public RecordItem generateFakeData() {
        RecordItem recordItem = new RecordItem();
        Random random = new Random();
        int tmp = random.nextInt(7);
        int con = 0;
        if (tmp == 0)
            con = 2;
        else if (tmp == 1)
            con = 3;
        else
            con = 1;
/*
正常范围收缩压90～139mmHg，舒张压60～89mmHg
高血压：未使用抗高血压药的前提下，18岁以上成人收缩压≥140mmHg和（或）舒张压≥90mmHg
低血压：血压低于90/60mmHg。

体温36.1℃～37℃（腋窝）比口腔温度约低0.2℃～0.4℃
根据发热程度的高低（口腔温度），可以区分为：低热：37.4℃～38℃；中等度热：38.1℃～39℃：高热：39.1℃～41℃；超高热：41℃以上。

心率是指正常人安静状态下每分钟心跳的次数，也叫安静心率，一般为60～100次/分
理想心率应为55～70次/分钟
如果心率超过160次/分钟，或低于40次/分钟，大多见于心脏病患者
成人安静时心率超过100次/分钟（一般不超过160次/分钟），称为窦性心动过速
成人安静时心率低于60次/分钟（一般在45次/分钟以上），称为窦性心动过缓，
*/
        switch (con) {
            case 1:
                recordItem.sethBlood(random.nextInt(50) + 90);
                recordItem.setlBlood(random.nextInt(30) + 60);
                recordItem.setHeartRate(random.nextInt(16) + 55);
                //Log.d(TAG,con+"nowdata"+NowData.getHeartRate()+"");
                recordItem.setBody_temp(+random.nextInt(10) + 361);
                recordItem.setWeight(+random.nextInt(10) + 600);
                break;
            case 2:
                recordItem.sethBlood(random.nextInt(50) + 90);
                recordItem.setlBlood(random.nextInt(30) + 60);
                recordItem.setHeartRate(random.nextInt(16) + 55);
                //Log.d(TAG,con+"nowdata"+NowData.getHeartRate()+"");
                recordItem.setBody_temp(random.nextInt(10) + 370);
                recordItem.setWeight(random.nextInt(10) + 600);
                break;
            case 3:
                recordItem.sethBlood(random.nextInt(40) + 130);
                recordItem.setlBlood(random.nextInt(40) + 80);
                recordItem.setHeartRate(random.nextInt(30) + 100);
                recordItem.setBody_temp(random.nextInt(20) + 385);
                recordItem.setWeight(random.nextInt(10) + 600);
                break;
            default:
                recordItem.sethBlood(0);
                recordItem.setlBlood(0);
                recordItem.setHeartRate(0);
                recordItem.setBody_temp(0);
                recordItem.setWeight(0);
                break;
        }
        return recordItem;
        //Log.d(TAG,"nowdata"+NowData.getHeartRate());
    }

}
