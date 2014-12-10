package com.example.weather;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;


public class SecondClass extends Activity
{
    TextView cityname;
    TextView tempmin;
    TextView tempmax;
    Handler hand;
    String strFromServ;
    JSONObject jsonFromServ;
    ImageView img;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_class);
        cityname = (TextView) findViewById(R.id.textView);
        tempmin = (TextView) findViewById(R.id.textView10);
        tempmax = (TextView) findViewById(R.id.textView12);
        Intent SecAct = getIntent();
        img = (ImageView) findViewById(R.id.imageView);



        getWeather(SecAct.getStringExtra("param"));
        //Showimg("http://risovach.ru/upload/2012/12/mem/pidrila-ebanaya_7033866_orig_.jpeg");



        hand = new Handler() {
            public void handleMessage(android.os.Message msg) {
                if  (msg.obj.toString().substring(0,1).equals("{"))
                {
                    parseJSON(msg.obj.toString());
                }
                else
                {
                    getfromDatabase(msg.obj.toString());
                }
            }
        };

    }
    void getImage(String str){
        String[][] values = {{"Clouds","Haze","Snow","Mist"},
                {"http://yandex.st/weather/1.2.83/i/icons/48x48/ovc.png","","http://yandex.st/weather/1.2.83/i/icons/48x48/ovc_-sn.png","http://yandex.st/weather/1.2.83/i/icons/48x48/bkn_-ra_d.png"}};
        String[] first = values[0];
        for (int i = 0; i<first.length; i++){
            if (str.equals(first[i])){
                Showimg(values [1][i]);
               }

        }


    }

    void Showimg(String link ){
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).build();

        ImageLoader il = ImageLoader.getInstance();
        il.init(config);
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .resetViewBeforeLoading(true)
                .cacheOnDisk(true)
                .build();
        il.displayImage(link, img, options);
    }

    void WriteDB(Integer Id,String name,Double Tmin, Double Tmax,String Main){
        // Инициализируем наш класс-обёртку
        DatabaseHelper dbh = new DatabaseHelper(this);
        // База нам нужна для записи и чтения
        SQLiteDatabase sqdb = dbh.getWritableDatabase();



        // Добавляем записи в таблицу
        ContentValues values = new ContentValues();
        values.put("ids", Id);
        values.put("nm", name);
        values.put("Tmin", Tmin);
        values.put("Tmax", Tmax);
        values.put("Main", Main);
        sqdb.insert(dbh.TABLE_NAME, null, values);

        sqdb.close();
        dbh.close();
    }


    void getfromDatabase(String param){
        param = param.substring(0,param.length()-3);

        DatabaseHelper dbh = new DatabaseHelper(this);
        ArrayList<String> val = dbh.reload(param);
        cityname.setText(val.get(2));
        tempmin.setText(val.get(3));
        tempmax.setText(val.get(4));
        getImage(val.get(5));
    }

    void parseJSON (String str){
        try {
            jsonFromServ = new JSONObject(str);
            JSONObject mainObject = jsonFromServ.getJSONObject("main");
            JSONArray weatherObject = jsonFromServ.getJSONArray("weather");
            cityname.setText(jsonFromServ.getString("name"));
            double TTmin,TTmax;
            TTmin = mainObject.getDouble("temp_min") - 272.15;
            tempmin.setText(String.valueOf(roundUp(TTmin, 1)));
            TTmax = mainObject.getDouble("temp_max") - 272.15;
            tempmax.setText(String.valueOf(roundUp(TTmax, 1)));
            JSONObject ww = weatherObject.getJSONObject(0);
            String sky = ww.getString("main");
            WriteDB(0,jsonFromServ.getString("name"),mainObject.getDouble("temp_min") - 272.15,mainObject.getDouble("temp_max") - 272.15,
                    ww.getString("main"));
            getImage(sky);

        } catch (Throwable t) {
            Log.e("MyLogs", "Could not parse malformed JSON: \"" + strFromServ + "\"");
        }
    }
    void getWeather(final String param){
        AsyncTask getW = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {
                HttpClient httpClient = new DefaultHttpClient();
                HttpGet httpget = new HttpGet("http://api.openweathermap.org/data/2.5/weather?q=" + param);
                try {
                    HttpResponse resp = httpClient.execute(httpget);
                    HttpEntity httpEnt = resp.getEntity();
                    strFromServ = EntityUtils.toString(httpEnt, "UTF-8");
                    //Log.d("Mylogs", strFromServ);
                    Message msg = Message.obtain();
                    msg.obj = strFromServ;
                    hand.sendMessage(msg);
                } catch (IOException e) {
                    Message msg = Message.obtain();
                    msg.obj = param;
                    hand.sendMessage(msg);



                    e.printStackTrace();
                }
                return null;
            }
        };
        getW.execute();

    }

    public BigDecimal roundUp(double value, int digits){
        return new BigDecimal(""+value).setScale(digits, BigDecimal.ROUND_HALF_UP);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.second_class, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
