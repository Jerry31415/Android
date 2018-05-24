package com.example.jerry.myapplication;


import android.app.Activity;
import android.content.Context;
import android.location.Location;
import com.google.android.gms.maps.model.LatLng;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ShopList extends ArrayList<Shop> {

    public static String GetValueByTag(String in, String tag){
        return StringUtils.substringBetween(in, "\""+tag+"\":\"", "\"");
    }

    // Инициализирует shops соответствующими объектами
    // Производит парсинг полей объектов shop из строки in
    public void InitFromString(String in){
        String data = StringUtils.substringBetween(in, "[", "]");
        String [] strList = StringUtils.split(data,  "{}");
        for (int i = 0;i<strList.length;++i){
            if(!",".equals(strList[i])) {
                Shop sh = new Shop();
                sh.name = GetValueByTag(strList[i], "NAME");
                sh.address = GetValueByTag(strList[i], "ADDRESS");
                sh.phones = GetValueByTag(strList[i], "PHONES");
                sh.whours = GetValueByTag(strList[i], "WHOURS");
                double gps_n = Double.parseDouble(GetValueByTag(strList[i], "GPSN"));
                double gps_e = Double.parseDouble(GetValueByTag(strList[i], "GPSE"));
                sh.gps_pos = new LatLng(gps_n, gps_e);
                sh.br = GetValueByTag(strList[i], "BR");
                add(sh);
            }
        }
    }

    public void Load(String url, Context context){
        AsyncWebRequest req = new AsyncWebRequest(url);
        req.start();
        while(!req.is_ready){
            try {
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException ex) {
                Logger.getLogger(MainActivity.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        String strSL = new String(req.getUnescapeRequest());
        InitFromString(strSL);
        ChacheIO.SaveFile("sList", req.getUnescapeRequest(), context);
    }

}