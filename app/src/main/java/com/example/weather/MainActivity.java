package com.example.weather;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.renderscript.Element;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    TextView localization;
    Button searchButton;
    TextView show;
    String url;


    class getWeather extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            StringBuilder result = new StringBuilder();
            try {
                    URL url = new URL(urls[0]);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.connect();

                    InputStream inputStream = urlConnection.getInputStream();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                    String line = "";

                    while ((line = reader.readLine()) != null)
                    {
                        result.append(line).append("\n");
                    }

                    return result.toString();

                } catch (Exception e)
            {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {
                JSONObject jsonObject = new JSONObject(result);
                String weatherInfo = jsonObject.getString("main");
                weatherInfo = weatherInfo.replace("temp", "Temperature ");
                weatherInfo = weatherInfo.replace(",", "\n");
                weatherInfo = weatherInfo.replace("feels_like", "Feels Like");
                weatherInfo = weatherInfo.replace("\"", " ");
                weatherInfo = weatherInfo.replace("{", "");
                weatherInfo = weatherInfo.replace("}", "");
                weatherInfo = weatherInfo.replace(":", ":  ");
                weatherInfo = weatherInfo.replace("_min", " (minimal)");
                weatherInfo = weatherInfo.replace("_max", " (maximal)");
                weatherInfo = weatherInfo.replace("pressure", "Pressure (hPa)");
                weatherInfo = weatherInfo.replace("humidity", "Humidity (%)");
                weatherInfo = weatherInfo.replace("_", " ");


                show.setText(weatherInfo);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            setContentView(R.layout.activity_main);

            localization = findViewById(R.id.localization);
            searchButton = findViewById(R.id.searchButton);
            show = findViewById(R.id.show);
            SimpleDateFormat simpleDateFormat;


            final String[] temp = {""};

            searchButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(MainActivity.this, "Searching...", Toast.LENGTH_SHORT).show();

                    String city = localization.getText().toString();


                    try {
                            if (city != null) {
                                 url = "http://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=ef9b8c884e9fe726247aff1ab528d6a8&units=metric";
                                }
                            else{
                                    Toast.makeText(MainActivity.this, "Enter City", Toast.LENGTH_SHORT).show();
                                }
                            Calendar calendar = Calendar.getInstance();
                            String pattern = "yyyy-MM-dd HH:mm:ss";
                            String currentDate = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.MEDIUM, Locale.GERMAN).format(calendar.getTime());
                            TextView textViewDate = findViewById(R.id.text_view_date);
                            textViewDate.setText("Weather for: " + currentDate);

                            getWeather task = new getWeather();

                            temp[0] = task.execute(url).get();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            }
                    if (temp[0] == null)
                    {
                        show.setText("Cannot find weather");
                    }
                }
            });
        }
    }