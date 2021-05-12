package com.example.weatherak;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadCity();
    }

    public void CheckWeather(View view) {

        if(checkInternetConnection()) {
            EditText gettingCity = findViewById(R.id.CityName);
            String settingCity = gettingCity.getText().toString();
            saveCity(settingCity);
            Intent sendingCity = new Intent(this, WeatherActivity.class);
            sendingCity.putExtra("KEY_C", settingCity);
            startActivity(sendingCity);
        }
        else {
            Toast.makeText(getApplicationContext(),"No Internet access!",Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkInternetConnection() {
        ConnectivityManager cm = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo nInfo = cm.getActiveNetworkInfo();
        if(nInfo != null && nInfo.isAvailable() && nInfo.isConnected())
            return true;
        else return false;
    }

    public void saveCity(String settingCity) {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("CITY_KEY", settingCity);
        editor.apply();
    }
    public void loadCity(){
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        String LoadCity = sharedPreferences.getString("CITY_KEY", "");
        EditText gettingCity = findViewById(R.id.CityName);
        gettingCity.setText(LoadCity);
    }

}
