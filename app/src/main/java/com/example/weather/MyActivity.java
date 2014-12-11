package com.example.weather;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;


public class MyActivity extends Activity {
    ListView LV;
    String[] Sity={"Саранск","Москва","Пенза","Лондон","Сидней","Ульяновск"};
    String[] SityID={"Saransk,ru","Moscow,ru","Penza,ru","London,uk","Sydney,au","Ulyanovsk,ru"};
    ImageView Im1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        Im1 = (ImageView) findViewById(R.id.imageView2);
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).build();

        здесь был юрик
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.init(config);
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .resetViewBeforeLoading(true)
                .cacheOnDisk(true)
                .build();
        imageLoader.displayImage("http://samroid.ru/files/082013/Yandex_Weather_Mini.png", Im1,options);
        LV=(ListView) findViewById(R.id.listView);
        LV.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,Sity);
        LV.setAdapter(adapter);

        LV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("MyLOG", ""+position);
                SecondActivity(position);
            }
        });
    }
    void SecondActivity (int position)
    {
        Intent SecAct = new Intent(this,SecondClass.class);
        SecAct.putExtra("param", SityID[position]);
        startActivity(SecAct);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
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
