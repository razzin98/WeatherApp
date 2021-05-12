package com.example.weatherak;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WeatherActivity extends AppCompatActivity {
    static boolean active=true;
    @Override
    public void onStart() {
        super.onStart();
        active = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        active = false;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);


        final SwipeRefreshLayout pullToRefresh = findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresher();
                pullToRefresh.setRefreshing(false);
            }
        });

        if (checkInternetConnection()) {   //standardowe wysyłanie zapytania
            sendQuery();
        }
        else {
            Toast.makeText(getApplicationContext(),"No Internet access!",Toast.LENGTH_SHORT).show();
        }

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable(){
            public void run(){
                if(active) {
                    refresher();
                    handler.postDelayed(this, 300000);  //30 000 =5 minut;
                }
            }
        }, 6000);
    }



    private void refresher() {
        if (checkInternetConnection()) {
            sendQuery();
            Toast.makeText(getApplicationContext(),"Weather successfully updated!",Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(getApplicationContext(),"No Internet access!",Toast.LENGTH_SHORT).show();
        }
    }

    private void sendQuery() {
        Intent sendingCity = getIntent();
        final String cityName = sendingCity.getStringExtra("KEY_C");
        final TextView showCityName = findViewById(R.id.showCityName);
        final TextView showTemp = findViewById(R.id.showTemp);
        final TextView showPressure = findViewById(R.id.showPressure);
        final TextView showHumidity = findViewById(R.id.showHumidity);
        final TextView showTempMin = findViewById(R.id.showTempMin);
        final TextView showTempMax = findViewById(R.id.showTempMax);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        JsonPlaceholderAPI jsonPlaceholderAPI = retrofit.create(JsonPlaceholderAPI.class);
        Call<WeatherInCities> call = jsonPlaceholderAPI.getWeatherInCities(cityName, "749561a315b14523a8f5f1ef95e45864", "metric");
        call.enqueue(new Callback<WeatherInCities>() {
            @Override
            public void onResponse(Call<WeatherInCities> call, Response<WeatherInCities> response) {
                try {
                    WeatherInCities weatherInCities = response.body();
                    Main main = weatherInCities.getMain();
                    ArrayList<Weather> weatherList = weatherInCities.getWeatherList();
                    Weather weather = weatherList.get(0);
                    setTime();
                    setIcon(weather.getIcon());
                    showCityName.setText(weatherInCities.getName());
                    showTemp.setText((main.getTemp()) + " C");
                    showPressure.setText((main.getPressure()) + "hPa");
                    showHumidity.setText((main.getHumidity()) + "%");
                    showTempMin.setText((main.getTemp_min()) + " C");
                    showTempMax.setText((main.getTemp_max()) + " C");
                } catch (NullPointerException e) {
                    Intent back = new Intent(WeatherActivity.this, MainActivity.class);
                    Bundle sendBackBundle = new Bundle();
                    sendBackBundle.putString("error", "Name not found!");
                    startActivity(back);
                }
            }

            @Override
            public void onFailure(Call<WeatherInCities> call, Throwable t) {
                System.out.println(t.getMessage() + "MAM PROBLEM, COS NIE DZIALA");
            }
        });
    }

    private void setIcon(String icon) {
        ImageView iconContainer =  findViewById(R.id.icon);
        Picasso.get().load("https://openweathermap.org/img/wn/"+icon+"@2x.png").fit().centerCrop().into(iconContainer);
    }

    private void setTime() {
        TextClock phoneDate = findViewById(R.id.showTime);
        phoneDate.setTimeZone("Europe/Warsaw");
        phoneDate.setFormat12Hour(null);
        phoneDate.setFormat24Hour("hh:mm:ss a");
    }
    private boolean checkInternetConnection() {
        ConnectivityManager cm = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo nInfo = cm.getActiveNetworkInfo();
        if(nInfo != null && nInfo.isAvailable() && nInfo.isConnected())
            return true;
        else return false;
    }
}
