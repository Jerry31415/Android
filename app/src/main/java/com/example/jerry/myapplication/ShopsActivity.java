package com.example.jerry.myapplication;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.*;
import android.location.Location;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import java.util.concurrent.TimeUnit;

public class ShopsActivity extends AppCompatActivity implements OnMapReadyCallback{

    TabHost tabHost; // вкладки: список и карта
    SupportMapFragment mapFragment; // карта
    ListView lvShops; // список
    SimpleAdapter list_adapter; // адаптер для списка
    static Tracker tr; // отслеживает местоположение

    // вычисление центра камеры (арифметическое среднее координат магазинов)
    public LatLng getCameraCenter(){
        double m_gps_e=0;
        double m_gps_n=0;
        for(int i=0;i<MainActivity.shops.size();++i){
            LatLng L = MainActivity.shops.get(i).gps_pos;
            m_gps_e += L.longitude;
            m_gps_n += L.latitude;
        }
        m_gps_e /= MainActivity.shops.size();
        m_gps_n /= MainActivity.shops.size();
        return new LatLng(m_gps_n, m_gps_e);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // устанавиливает стили подписей маркеров
        googleMap.setInfoWindowAdapter(UI.getGoogleMapInfoWindowAdapter(this));
        // если массив магазинов был проинициализирован
        if(MainActivity.shops!= null){
            LatLng camera_center_pos = getCameraCenter();
            // установка маркеров магазинов на карту
            for(int i=0;i<MainActivity.shops.size();++i){
                Shop s = MainActivity.shops.get(i);
                MarkerOptions marker_shop = new MarkerOptions();
                marker_shop.title(s.getTitle());
                marker_shop.snippet(s.getDescription(tr.loc));
                marker_shop.position(MainActivity.shops.get(i).gps_pos);
                googleMap.addMarker(marker_shop);
            }
            // перемещаем центр камеры и изменяем масштаб
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(camera_center_pos, 11F));
            // если координаты местоположения былы получены, тогда
            // установим маркер пользователя на катру
            if(tr!=null){
                if(tr.loc!=null){
                    Location loc = tr.loc;
                    MarkerOptions user_marker = new MarkerOptions();
                    user_marker.position(new LatLng(loc.getLatitude(), loc.getLongitude()));
                    user_marker.title(getResources().getString(R.string.user_marker_title));
                    user_marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                    googleMap.addMarker(user_marker);
                }
            }
        }
    }

    // поток ожидания gps координат
    // обновляет список и карту при получении gps координат
    private class ListUpdater extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... params) {
            while(true) {
                if (list_adapter != null && tr != null) {
                    if (!list_adapter.isEmpty() && tr.loc != null) {
                        if (MainActivity.shops != null) {
                            break;
                        }
                    }
                }
                try {
                    TimeUnit.MILLISECONDS.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
        protected void onPostExecute(Void p) {
            // Список
            list_adapter = UI.getShopListAdapter(ShopsActivity.this, tr.loc);
            lvShops.setAdapter(list_adapter);
            // Карта
            mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(ShopsActivity.this);
        }
    }

    // Создает потоки ожидающие активации функции определения
    // местоположения. Затем производит запуск Tracker
    private class TrackingStarter extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... params) {
            while(!tr.isLocationEnabled()){
                try {
                    TimeUnit.MILLISECONDS.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                tr.updateStatus();
            }
            return null;
        }
        protected void onPostExecute(Void p) {
            tr.start();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shops);
        tr = new Tracker(this);
        // запускаем отслеживание местоположения
        new TrackingStarter().execute();
        // вкладки: список и карта
        tabHost = UI.GetTabShopMapUI(this);
        // список
        lvShops = (ListView) findViewById(R.id.lvShops);
        list_adapter = UI.getShopListAdapter(this, tr.loc);
        lvShops.setAdapter(list_adapter);
        // Карта
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        // запускаем поток ожидания gps координат
        // как только координаты будут получены - список и карта будут обновлены
        new ListUpdater().execute();
    }

    @Override
    public void onResume() {
        super.onResume();
        // текущая вкладка - список
        tabHost.setCurrentTab(0);

    }

}
