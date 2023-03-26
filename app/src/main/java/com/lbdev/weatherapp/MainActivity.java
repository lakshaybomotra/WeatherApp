package com.lbdev.weatherapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewStructure;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    private RelativeLayout homeRl;
    private ProgressBar loadingPB;
    private ProgressBar miniloadingPB;
    private TextView cityNameTV, temperatureTV, conditionTV, aqiTV, pressureTV, humidityTV, windsTV, pressureN, aqiN, humidityN, windN;
    private TextInputEditText cityEdt;
    private ImageView backIV, iconIV, searchIV, locationIV;
    private RecyclerView weatherRV;
    private ArrayList<WeatherRVModal> weatherRVModalArrayList;
    private WeatherRVAdapter weatherRVAdapter;
    private LocationManager locationManager;
    private int PERMISSION_CODE = 1, data = 0;
    private String cityName, aqiD;
    Button button, aqiB, humidityB, pressureB, windSB;
    private double pressuredata, humiditydata, winddata;
    private ProgressBar spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);


        setContentView(R.layout.activity_main);
        homeRl = findViewById(R.id.idRLHome);
        loadingPB = findViewById(R.id.idPBLoading);
        cityNameTV = findViewById(R.id.idTVCityName);
        temperatureTV = findViewById(R.id.idTVTemperature);
        conditionTV = findViewById(R.id.idTVCondition);
        weatherRV = findViewById(R.id.idRVWeather);
        cityEdt = findViewById(R.id.idEdtCity);
        backIV = findViewById(R.id.idIVBack);
        iconIV = findViewById(R.id.idIVIcon);
        searchIV = findViewById(R.id.idIVSearch);
        weatherRVModalArrayList = new ArrayList<>();
        weatherRVAdapter = new WeatherRVAdapter(this, weatherRVModalArrayList);
        weatherRV.setAdapter(weatherRVAdapter);
        locationIV = findViewById(R.id.idIVLocation);
        pressureTV = findViewById(R.id.idTVPressure);
        aqiTV = findViewById(R.id.idTVAQI);
        humidityTV = findViewById(R.id.idTVHumidity);
        windsTV = findViewById(R.id.idTVWindS);
        aqiB = findViewById(R.id.idBTAQI);
        humidityB = findViewById(R.id.idBTHumidity);
        pressureB = findViewById(R.id.idBTPressure);
        windSB = findViewById(R.id.idBTWindS);
        miniloadingPB = findViewById(R.id.progressBarData);


        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_CODE);
        }


        try {
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            cityName = getCityName(location.getLongitude(), location.getLatitude());
        } catch (Exception e) {

        }
        getWeatherInfo(cityName);

        locationIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String ccity = cityName;
                    if (ccity.isEmpty()) {
                        Toast.makeText(MainActivity.this, "Can't fetch current location", Toast.LENGTH_SHORT).show();
                    } else {
                        cityNameTV.setText(cityName);
                        getWeatherInfo(cityName);
                        cityEdt.getText().clear();
                        Toast.makeText(MainActivity.this, "Current Location", Toast.LENGTH_SHORT).show();
                        aqiTV.setText("");
                        humidityTV.setText("");
                        pressureTV.setText("");
                        windsTV.setText("");
                        miniloadingPB.setVisibility(View.VISIBLE);
                        Runnable runnable = new Runnable() {
                            @Override
                            public void run() {
                                miniloadingPB.setVisibility(View.GONE);
                            }
                        };

                        aqiB.postDelayed(runnable, 1500);
                    }

                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "Location Issue Open in real device", Toast.LENGTH_SHORT).show();
                }
            }


        });

        searchIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String city = cityEdt.getText().toString();
                if (city.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please enter city name", Toast.LENGTH_SHORT).show();
                } else {
                    cityNameTV.setText(cityName);
                    getWeatherInfo(city);
                    cityEdt.getText().clear();

                    aqiTV.setText("");
                    humidityTV.setText("");
                    pressureTV.setText("");
                    windsTV.setText("");
                    miniloadingPB.setVisibility(View.VISIBLE);
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            miniloadingPB.setVisibility(View.GONE);
                        }
                    };

                    aqiB.postDelayed(runnable, 1500);
                }
            }
        });

        aqiB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                aqiTV.setText(aqiD);
            }
        });

        humidityB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                humidityTV.setText(humiditydata + "%");
            }
        });

        pressureB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pressureTV.setText(pressuredata + " mb");
            }
        });

        windSB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                windsTV.setText(winddata + " km/h");
            }
        });
    }
    

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==PERMISSION_CODE){
            if (grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Permissions Granted", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(this, "Please provide the permissions", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private String getCityName(double longitude, double latitude){
        String cityName = "Not Found";
        Geocoder gcd = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = gcd.getFromLocation(latitude,longitude,10);

            if (addresses.size() > 0) {
                for (Address adr : addresses) {
                    if (adr != null) {
                        String city = addresses.get(0).getLocality();
                        if (city != null && !city.equals("")) {
                            cityName = city;
                        } else {
                            Log.d("TAG", "CITY NOT FOUND");
                            Toast.makeText(this, "User City Not Found..", Toast.LENGTH_SHORT).show();

                        }
                    }
                }
            }
            if (cityName==null||cityName=="Not Found"){
                cityName="Toronto";
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return cityName;
    }

    private void getWeatherInfo(String cityName) {
        String url = "https://api.weatherapi.com/v1/forecast.json?key=c6197c4b130745f682b183405222801&q=" + cityName + "&days=1&aqi=yes&alerts=yes";
        cityNameTV.setText(cityName);
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                loadingPB.setVisibility(View.GONE);
                homeRl.setVisibility(View.VISIBLE);
                weatherRVModalArrayList.clear();


                try {
                    String temperature = response.getJSONObject("current").getString("temp_c");
                    temperatureTV.setText(temperature + "°c");
                    int isDay = response.getJSONObject("current").getInt("is_day");
                    String condition = response.getJSONObject("current").getJSONObject("condition").getString("text");
                    String conditionIcon = response.getJSONObject("current").getJSONObject("condition").getString("icon");
                    Picasso.get().load("https:".concat(conditionIcon)).into(iconIV);
                    conditionTV.setText(condition);
                    if (condition.contains("snow")) {
                        Picasso.get().load("https://i.pinimg.com/originals/73/58/6d/73586d57b2727f2df5b367dfaa5b7aa3.jpg").into(backIV);
                    } else if (condition.contains("rain") || condition.contains("drizzle")){
                        Picasso.get().load("https://images.unsplash.com/photo-1534274988757-a28bf1a57c17?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=435&q=80").into(backIV);
                    } else if (isDay == 1) {
                        Picasso.get().load("https://images.pexels.com/photos/2931915/pexels-photo-2931915.jpeg").into(backIV);
                    }
                    else if (isDay == 0){
                        Picasso.get().load("https://images.pexels.com/photos/127577/nature-stars-night-galaxy-127577.jpeg").into(backIV);
                    }

                    int aqi = response.getJSONObject("current").getJSONObject("air_quality").getInt("us-epa-index");
                    switch (aqi){
                        case 1:
                            aqiD = "Good";
                            break;
                        case 2:
                            aqiD = "Moderate";
                            break;
                        case 3:
                        case 4:
                            aqiD = "Unhealthy";
//                            aqiTV.setTextSize(20);
                            break;
                        case 5:
                            aqiD = "Very Unhealthy";
                            aqiTV.setTextSize(22);
                            break;
                        case 6:
                            aqiD = "Hazardous";
//                            aqiTV.setTextSize(21);
                            break;
                        default:
                            aqiD = "Can't Fetch";
//                            aqiTV.setTextSize(21);
                            break;
                    }

                     pressuredata = response.getJSONObject("current").getDouble("pressure_mb");

                    humiditydata = response.getJSONObject("current").getDouble("humidity");

                    winddata = response.getJSONObject("current").getDouble("wind_kph");

                    JSONObject forecastObj = response.getJSONObject("forecast");
                    JSONObject forecastO = forecastObj.getJSONArray("forecastday").getJSONObject(0);
                    JSONArray hourArray = forecastO.getJSONArray("hour");

                    for (int i = 0; i < hourArray.length(); i++) {
                        JSONObject hourObj = hourArray.getJSONObject(i);
                        String time = hourObj.getString("time");
                        String temper = hourObj.getString("temp_c");
                        String img = hourObj.getJSONObject("condition").getString("icon");
                        String wind = hourObj.getString("wind_kph");
                        weatherRVModalArrayList.add(new WeatherRVModal(time, temper, img, wind));
                    }
                    weatherRVAdapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Please enter valid city name..", Toast.LENGTH_SHORT).show();
            }
        });

        requestQueue.add(jsonObjectRequest);

    }

}
