package com.example.susiyanti.androidtestapp;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    TitleDbHelper dbHelper;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new TitleDbHelper(getApplicationContext());
        db = dbHelper.getWritableDatabase();

        updateTitle();

        Button btnShow = (Button)findViewById(R.id.btn_show);
        btnShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplication(), DetailActivity.class);
                startActivity(i);
            }
        });
    }

    public void updateTitle(){
        FetchTitleTask f = new FetchTitleTask();
        f.execute();
    }

    public class FetchTitleTask extends AsyncTask<Void, Void, Title[]> {
        private final String LOG_TAG = FetchTitleTask.class.getSimpleName();

        public Title[] getTitleDataFromJson(String titleJsonStr) throws JSONException {

            JSONArray titleArray = new JSONArray(titleJsonStr);

            Title titles[] = new Title[titleArray.length()];
            for (int i=0; i<titleArray.length();i++){
                JSONObject oneTitle = titleArray.getJSONObject(i);
                titles[i] = new Title();
                titles[i].setId(oneTitle.getInt("id"));
                titles[i].setUserId(oneTitle.getInt("userId"));
                titles[i].setTitle(oneTitle.getString("title"));
                titles[i].setBody(oneTitle.getString("body"));

            }
            return titles;
        }

        @Override
        protected Title[] doInBackground(Void... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String titleJsonStr = null;


            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are available at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                final String baseUrl = "http://jsonplaceholder.typicode.com/posts";


                Uri builtUri = Uri.parse(baseUrl).buildUpon().build();
                URL url = new URL(builtUri.toString());

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                titleJsonStr = buffer.toString();
                //Log.v(LOG_TAG,"Title JSON String: "+titleJsonStr);

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attempting
                // to parse it.
                return null;
            } finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            try {
                return getTitleDataFromJson(titleJsonStr);
            }catch(JSONException e){
                Log.e(LOG_TAG, e.getMessage(),e);
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Title[] data) {
            if (data != null) {

                for (Title title : data){
                    // Create a new map of values, where column names are the keys
                    ContentValues values = new ContentValues();
                    values.put(TitleContract.TitleEntry.COLUMN_ID, title.getId());
                    values.put(TitleContract.TitleEntry.COLUMN_USERID, title.getUserId());
                    values.put(TitleContract.TitleEntry.COLUMN_TITLE, title.getTitle());
                    values.put(TitleContract.TitleEntry.COLUMN_BODY, title.getBody());

                    // Insert the new row, returning the primary key value of the new row
                    long newRowId;
                    newRowId = db.insertOrThrow(TitleContract.TitleEntry.TABLE_NAME, "",values);
                }
            }
        }
    }
}
