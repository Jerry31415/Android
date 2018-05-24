package com.example.jerry.myapplication;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import org.apache.commons.lang.StringEscapeUtils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;
import java.io.*;

// Выполняет асинхронный http запрос для заданного url
public class AsyncWebRequest extends Thread{
    
    public Boolean is_ready;  
    public String url_;
    public String request;

    public String getUnescapeRequest(){
        return StringEscapeUtils.unescapeJava(request);
    }
    
    public String getRequest(){
        return request;
    }
    
    public AsyncWebRequest(String url){
        is_ready = false;
        url_ = url;
    }
      
    public static String get(String _url){
        String content = new String("");
        HttpURLConnection connection = null;
        try {
            URL url = new URL(_url);
            connection =  (HttpURLConnection) url.openConnection();
            int code = connection.getResponseCode();
            if(code==200){
                InputStream in = new BufferedInputStream(connection.getInputStream());
                if (in != null) {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
                    String line = "";
                    while ((line = bufferedReader.readLine()) != null)
                        content += line;
                }
                in.close();
            }
        }catch ( IOException ex ) {
            System.out.println("ERR:" + ex.getMessage());
        }
        connection.disconnect();
        return content;
    }

    static boolean isEnabledNetworkConnection(Activity activity) {
        String cs = Context.CONNECTIVITY_SERVICE;
        ConnectivityManager cm = (ConnectivityManager)activity.getSystemService(cs);
        return (cm.getActiveNetworkInfo() != null);
    }

    @Override
    public void run()
    {
        is_ready = false;
        request = get(url_);
        is_ready = true;
    }
}
