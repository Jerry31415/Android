package com.example.jerry.myapplication;

import android.support.v7.app.*;
import android.os.Bundle;
import android.widget.*;
import android.view.View;
import android.content.*;
import android.net.ConnectivityManager;

public class MainActivity extends AppCompatActivity {

    AlertDialog.Builder config_alert;
    static ShopList shops; // контейнер - содержит список магазинов
    String url; // ссылка для http запроса

    static boolean is_data_empty; // истина если кэш отсутствует
    static boolean network_enabled; // истина если есть подключение к сети

    Intent intentShopsActivity; // предназначен для перехода на ShopsActivity

    // возвращает истину, если подключение к сети активно
    boolean isEnabledNetworkConnection() {
        String cs = Context.CONNECTIVITY_SERVICE;
        ConnectivityManager cm = (ConnectivityManager)getSystemService(cs);
        return (cm.getActiveNetworkInfo() != null);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        is_data_empty = false;
        // диалог настроек позиционирования
        config_alert = UI.GetConfigAlertDialogUI(this);

        // проверяем имеется ли разрешение на использование данных о местоположении
        // если разрешение отсутствует, то запрашиваем его
        boolean FLP = Tracker.isFineLocationPermissionGaranted(this);
        boolean CLP = Tracker.isCoarseLocationPermissionGaranted(this);
        if (!FLP || !CLP){
            Tracker.SendRequestPermissions(this);
        }

        // если определение местоположения отключено, то выводим диалог config_alert
        if(!Tracker.lsLocationEnabled(this)) {
            config_alert.show();
        }
        url = "http://kenguru.ru/api/?api_method=shop2&region=1949";
        intentShopsActivity = new Intent(MainActivity.this, ShopsActivity.class);
    }

    @Override
    public void onResume() {
        super.onResume();
        shops = new ShopList();

        // проверяем подключение к сети
        network_enabled = isEnabledNetworkConnection();
        // читаем кэш
        String strSL = ChacheIO.ReadFile("sList", getApplicationContext());

        // Сеть и кэш отсутствуют
        if(!network_enabled && strSL.isEmpty()){
            UI.ShowMessage(getResources().getString(R.string.connection_error_message), this);
            is_data_empty = true;
        }
        else {
            // Сети нет. Кэш есть
            if(!network_enabled){
                shops.InitFromString(strSL);
            }
            // Сеть есть. Обновляем кэш
            else {
                shops.Load(url, getApplicationContext());
            }
        }

        // список магазинов
        ListView lvMain = (ListView) findViewById(R.id.lvMain);
        // создаем адаптер
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.menu_items_name));
        // присваиваем адаптер списку
        lvMain.setAdapter(adapter);
        lvMain.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View itemClicked, int position, long id) {
                if(!is_data_empty) {
                    startActivity(intentShopsActivity);
                }
                else {
                    UI.ShowMessage(getResources().getString(R.string.connection_error_message), MainActivity.this);
                }
            }
        });
    }

}
