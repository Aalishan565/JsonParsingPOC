package com.jsonparsingpoc;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private TextView mTvJsonData;
    private Button mBtnHitMe;
    private HttpURLConnection connection = null;
    private BufferedReader reader = null;
    private ProgressDialog pd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pd = new ProgressDialog(MainActivity.this);
        mTvJsonData = (TextView) findViewById(R.id.tv_json_data);
        mBtnHitMe = (Button) findViewById(R.id.btn_hit);
        mBtnHitMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DownloadMovieAsyncTask().execute();
            }


        });

    }

    private class DownloadMovieAsyncTask extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setMessage("loading");
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                URL url = new URL("http://jsonparsing.parseapp.com/jsonData/moviesDemoList.txt");
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                String line = "";
                StringBuffer buffer = new StringBuffer();
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);

                }
                String finalData = buffer.toString();
                JSONObject jsonObject = new JSONObject(finalData);
                JSONArray jsonArray = jsonObject.getJSONArray("movies");
                StringBuffer finalBuffer = new StringBuffer();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonFinalObject = jsonArray.getJSONObject(i);
                    String movieName = jsonFinalObject.getString("movie");
                    int year = jsonFinalObject.getInt("year");
                    finalBuffer.append(movieName + "-" + year + "\n");
                }
                return finalBuffer.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if (connection != null)
                    connection.disconnect();
                try {
                    if (reader != null)
                        reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }


            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            mTvJsonData.setText(s.toString());
            pd.dismiss();
        }
    }

}
