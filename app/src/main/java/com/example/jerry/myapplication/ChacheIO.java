package com.example.jerry.myapplication;

import java.io.*;

import android.content.Context;

public class ChacheIO {

    // Производит сохранение строки content в файл с именем name
    static boolean SaveFile(String name, String content, Context context){
        FileOutputStream os = null;
        try {
            os = context.openFileOutput(name, Context.MODE_PRIVATE);
            os.write(content.getBytes());
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    // Производит чтение файла с именем name.
    // Реузльтат возвращает в виде строки.
    static String ReadFile(String name, Context context){
        String content = new String("");
        FileInputStream fs = null;
        InputStreamReader is = null;

        try {
            fs = context.openFileInput(name);
            is = new InputStreamReader(fs);

            BufferedReader bufferedReader = new BufferedReader(is);
            String line = "";
            while ((line = bufferedReader.readLine()) != null)
                content += line;
            fs.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content;
    }

}
