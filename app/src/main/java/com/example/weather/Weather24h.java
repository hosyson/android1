package com.example.weather;
import adapter.CustomAdapter24h;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.PixelCopy;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import java.util.Date;
import java.util.Locale;
import Model.WeatherData24h;

public class Weather24h extends AppCompatActivity {

    ImageView imgback;
    TextView txtname;
    ListView lv;
    CustomAdapter24h customAdapter;
    ArrayList<WeatherData24h> mangthoitiet;
    String tenthanhpho;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_weather24h);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Anhxa();
        Intent intent = getIntent();
        String city = intent.getStringExtra("name");
        imgback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        if (city.equals("city")){
            tenthanhpho = "Ha Noi";
            txtname.setText(tenthanhpho);
            Get24HoursData(tenthanhpho);
        } else {
            tenthanhpho = city;
            txtname.setText(tenthanhpho);
            Get24HoursData(tenthanhpho);
        }


    }

    private void Anhxa() {
        imgback = (ImageView) findViewById(R.id.imageviewBack);
        txtname = (TextView) findViewById(R.id.textviewTenthanhpho);
        lv = (ListView) findViewById(R.id.listviewWeather24h);
        mangthoitiet = new ArrayList<WeatherData24h>();
        customAdapter = new CustomAdapter24h(Weather24h.this,mangthoitiet);
        lv.setAdapter(customAdapter);
    }

    private void Get24HoursData(String tenthanhpho) {
        // Yêu cầu để lấy key của thành phố
        String urlcitykey = "https://dataservice.accuweather.com/locations/v1/cities/search?apikey=QbGB0fmYi9oCmAPLZSzOb1gwJIBU3CDQ&q="+tenthanhpho ;
        RequestQueue requestQueueCityKey = Volley.newRequestQueue(Weather24h.this);
        StringRequest stringRequestCityKey = new StringRequest(Request.Method.GET, urlcitykey,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            // Lấy key thành phố từ phản hồi JSON
                            JSONArray jsonArrayList = new JSONArray(response);
                            JSONObject jsonObjectList = jsonArrayList.getJSONObject(0);
                            String keyCity = jsonObjectList.getString("Key");
                            Log.d("JSON Key city","Json: " + response);

                            // Sau khi nhận được keyCity, thực hiện yêu cầu thứ hai để lấy dữ liệu thời tiết
                            String url = "https://dataservice.accuweather.com/forecasts/v1/hourly/12hour/"+keyCity+"?apikey=QbGB0fmYi9oCmAPLZSzOb1gwJIBU3CDQ";
                            RequestQueue requestQueue = Volley.newRequestQueue(Weather24h.this);
                            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            try {
                                                // Xử lý phản hồi JSON và thêm dữ liệu vào danh sách
                                                JSONArray jsonArrayList = new JSONArray(response);
                                                for (int i = 0; i < jsonArrayList.length(); i++) {
                                                    JSONObject jsonObjectList = jsonArrayList.getJSONObject(i);
                                                    String ngay = jsonObjectList.getString("EpochDateTime");

                                                    long l = Long.valueOf(ngay);
                                                    Date date = new Date(l * 1000L);
                                                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE", Locale.ENGLISH);
                                                    String Day = simpleDateFormat.format(date);

                                                    SimpleDateFormat hourFormat = new SimpleDateFormat("HH:mm a");
                                                    String hour = hourFormat.format(date);

                                                    JSONObject jsonObjectTemp = jsonObjectList.getJSONObject("Temperature");
                                                    String temp = jsonObjectTemp.getString("Value");
                                                    int intdoC = (int) ((Double.parseDouble(temp) - 32) / 1.8);
                                                    String stringdoC = String.valueOf(intdoC);

                                                    String status = jsonObjectList.getString("IconPhrase");
                                                    String icon = jsonObjectList.getString("WeatherIcon");

                                                    // Thêm dữ liệu thời tiết vào danh sách
                                                    mangthoitiet.add(new WeatherData24h(Day, status, icon, hour, stringdoC));
                                                }

                                                // Cập nhật giao diện
                                                customAdapter.notifyDataSetChanged();
                                            } catch (JSONException e) {
                                                throw new RuntimeException(e);
                                            }
                                        }
                                    }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.e("Loi tra ve json 1", "Error occurred: " + error.getMessage());
                                }
                            });
                            // Thực hiện yêu cầu thứ hai để lấy dữ liệu thời tiết
                            requestQueue.add(stringRequest);

                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Loi tra ve json 2", "Error occurred: " + error.getMessage());
            }
        });
        // Thực hiện yêu cầu đầu tiên để lấy key của thành phố
        requestQueueCityKey.add(stringRequestCityKey);
    }

}
