package com.example.jerry.myapplication;

import android.app.Activity;
import android.content.*;
import android.graphics.*;
import android.location.Location;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.view.*;
import android.widget.*;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import java.util.ArrayList;
import java.util.HashMap;

public class UI {

    static AlertDialog.Builder GetConfigAlertDialogUI(final Activity activity){
        AlertDialog.Builder config_alert = new AlertDialog.Builder(activity);
        config_alert.setTitle("Определение местоположения отключено");
        config_alert.setMessage("Активируйте для работы данного приложения");
        config_alert.setPositiveButton("Настройки", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                activity.startActivity(myIntent);

            }
        });
        config_alert.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
            }
        });
        config_alert.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                Intent back = new Intent(activity, MainActivity.class);
                activity.startActivity(back);
            }
        });
        return config_alert;
    }

    static TabHost GetTabShopMapUI(final Activity activity){
        TabHost tabHost = (TabHost) activity.findViewById(R.id.tabHost);
        tabHost.setup();
        // вкладка со списком
        TabHost.TabSpec tabSpec = tabHost.newTabSpec("shop_list_tab");
        tabSpec.setContent(R.id.slist_layout);
        tabSpec.setIndicator(activity.getResources().getString(R.string.first_tab_name));
        tabHost.addTab(tabSpec);
        // вкладка с картой
        tabSpec = tabHost.newTabSpec("map_tab");
        tabSpec.setContent(R.id.map_layout);
        tabSpec.setIndicator(activity.getResources().getString(R.string.second_tab_name));
        tabHost.addTab(tabSpec);
        return tabHost;
    }

    static void ShowMessage(String message, Activity activity){
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
    }

    static SimpleAdapter getShopListAdapter(Activity activity, Location loc){
        ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
        for(int i =0;i<MainActivity.shops.size();++i){
            String title = MainActivity.shops.get(i).getTitle();
            String description = MainActivity.shops.get(i).getDescription(loc);
            HashMap<String, String> hm = new HashMap<String, String>();
            hm.put("title", title);
            hm.put("description", description);
            list.add(hm);
        }
        return new SimpleAdapter(activity, list,
                android.R.layout.simple_list_item_2,
                new String[]{"title", "description"},
                new int[]{android.R.id.text1, android.R.id.text2});

    }

    static GoogleMap.InfoWindowAdapter getGoogleMapInfoWindowAdapter(final Activity activity){
        return new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {

                LinearLayout info = new LinearLayout(activity);
                info.setOrientation(LinearLayout.VERTICAL);

                TextView title = new TextView(activity);
                title.setTextColor(Color.BLACK);
                title.setGravity(Gravity.CENTER);
                title.setTypeface(null, Typeface.BOLD);
                title.setText(marker.getTitle());

                TextView snippet = new TextView(activity);
                snippet.setTextColor(Color.GRAY);
                snippet.setText(marker.getSnippet());

                info.addView(title);
                info.addView(snippet);

                return info;
            }
        };
    }

}
