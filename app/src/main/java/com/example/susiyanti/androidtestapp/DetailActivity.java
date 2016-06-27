package com.example.susiyanti.androidtestapp;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity {

    TitleDbHelper dbHelper;
    SQLiteDatabase db;

    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        dbHelper = new TitleDbHelper(getApplicationContext());
        db = dbHelper.getWritableDatabase();

        ListView listView = (ListView) findViewById(R.id.listview);
        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, new ArrayList<String>());
        select();
        listView.setAdapter(adapter);
        
        
    }

    public void select(){
        // Define a projection that specifies which columns from the adapterbase
        // you will actually use after this query.
        String[] projection = {
                TitleContract.TitleEntry._ID,
                TitleContract.TitleEntry.COLUMN_TITLE,
        };

        // How you want the results sorted in the resulting Cursor
//        String sortOrder = TitleContract.SiswaEntry.COLUMN_NAME + " ASC";

        Cursor c = db.query(
                TitleContract.TitleEntry.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );
        if(c.getCount()>0) {
            c.moveToFirst();
            String data = c.getString(c.getColumnIndexOrThrow(TitleContract.TitleEntry.COLUMN_TITLE));
            adapter.clear();
            adapter.add(data);
            while (c.moveToNext()) {
                data = c.getString(c.getColumnIndexOrThrow(TitleContract.TitleEntry.COLUMN_TITLE));
                adapter.add(data);
            }
        }
    }
}
