package com.example.jerry.myapplication;

import android.location.Location;
import com.google.android.gms.maps.model.LatLng;

public class Shop {
    public String name;
    public String address;
    public String phones;
    public String whours;
    public LatLng gps_pos;
    public String br;

    public float getDistanceFromUser(Location user_location) {
        float dist = -1;
        if(user_location!=null){
            Location shop_location = new Location("shop");
            shop_location.setLatitude(gps_pos.latitude);
            shop_location.setLongitude(gps_pos.longitude);
            return user_location.distanceTo(shop_location);
        }
        return dist;
    }

    public String getTitle(){
        return name;
    }

    public String getDescription(Location user_location){
        String res = new String();
        res += "Адрес: " + address + "\n";
        res += "Тел.: " + phones + "\n";
        res += "Режим работы: "+whours + "\n";
        if(user_location!=null){
            double dist = getDistanceFromUser(user_location);
            res += "Расстояние: ";
            if(dist<1000){
                res += Math.round(dist) + " м.";
            }
            else {
                dist = Math.round(10.*(dist/1000.))/10.;
                if (dist==(int)dist){
                    res += (int)dist + " км.";
                }
                else {
                    res += dist + " км.";
                }
            }
        }
        return res;
    }
}
