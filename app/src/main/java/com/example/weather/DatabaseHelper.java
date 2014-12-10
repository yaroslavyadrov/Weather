package com.example.weather;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import static android.database.sqlite.SQLiteDatabase.openOrCreateDatabase;

public class DatabaseHelper extends SQLiteOpenHelper {

    private final Context fContext;
    public static final String DATABASE_NAME = "batteries_database.db";
    public static final String TABLE_NAME = "battable";

   public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        fContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String str = "CREATE TABLE " + TABLE_NAME + " ("
                + "_id INTEGER PRIMARY KEY, " + "ids INTEGER, " + "nm TEXT, " + "Tmax REAL, "  + "Tmin REAL, "
                + "Main TEXT "  + ");";

        Log.d("MyLogs",str);
        db.execSQL(str);
    }

    public ArrayList<String> reload(String name){


        // База нам нужна для записи и чтения
        SQLiteDatabase sqdb = getReadableDatabase();
        ArrayList<String> my_array = new ArrayList<String>();
        try {
           // sqdb = openOrCreateDatabase(DATABASE_NAME, , null);
           // sqdb = getReadableDatabase();
            String request = "SELECT * FROM "+TABLE_NAME +" WHERE nm LIKE '" + name + "'";
            Log.d("MyLogs", request);
            Cursor allrows = sqdb.rawQuery(request, null);
            //Cursor allrows = sqdb.query(dbh.TABLE_NAME, null, "nm = ?", null, null, null, null);
            //Cursor allrows = sqdb.rawQuery("SELECT * FROM battable WHERE 0", null);

            System.out.println("COUNT : " + allrows.getCount());
            allrows.moveToLast();
            for (int i = 0; i < allrows.getColumnCount(); i++){
                String nm = allrows.getString(i);
                my_array.add(nm);
            }

            allrows.close();
            sqdb.close();

        } catch (Exception e) {

        }


return my_array;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
