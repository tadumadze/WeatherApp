package ge.tsu.wheatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;


public class MainActivity extends AppCompatActivity {

  private EditText mCity;
  private TextView mPressure;
  private TextView mTemperature;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    mCity = findViewById(R.id.city);
    mPressure = findViewById(R.id.pressure);
    mTemperature = findViewById(R.id.temperature);
  }

  public void getData(View view) {
    String cityName = mCity.getText().toString();
    if (cityName.trim().isEmpty()) {
      Toast.makeText(this, "Please Enter Name", Toast.LENGTH_SHORT).show();
      return;
    }
    GetWeatherInfoAsyncTask getWeatherInfoAsyncTask = new GetWeatherInfoAsyncTask();
    getWeatherInfoAsyncTask.execute(cityName.trim());
  }

  public void showCaveaData(View view) {
    Intent intent = new Intent(this, CaveaActivity.class);
    startActivity(intent);
  }

  public class GetWeatherInfoAsyncTask extends AsyncTask<String, Void, WeatherInfo> {
    @Override
    protected void onPreExecute() {
      super.onPreExecute();
    }

    @Override
    protected WeatherInfo doInBackground(String... strings) {
      //Worker Thread
      Log.d("doInBackground", Thread.currentThread().getName());

      OkHttpClient client = new OkHttpClient();

      HttpUrl.Builder urlBuilder = HttpUrl.parse("https://samples.openweathermap.org/data/2.5/weather").newBuilder();
      urlBuilder.addQueryParameter("q", strings[0]);
      urlBuilder.addQueryParameter("appid", "b6907d289e10d714a6e88b30761fae22");
      Request request = new Request.Builder().url(urlBuilder.build().toString()).build();
      try {
        Response response = client.newCall(request).execute();
        WeatherInfo weatherInfo = new Gson().fromJson(response.body().string(), WeatherInfo.class);
        return weatherInfo;
      } catch (IOException e) {
        Log.e("doInBackground", "Unable to retrieve data from wheater server", e);
        return null;
      }
    }

    @Override
    protected void onPostExecute(WeatherInfo weatherInfo) {
      Log.d("onPostExecute", Thread.currentThread().getName());
      //Mai n Thread
      if (weatherInfo != null && weatherInfo.getMain() != null) {
        mPressure.setText(weatherInfo.getMain().getPressure() + "");
        mTemperature.setText(weatherInfo.getMain().getTemp() + "");
      } else {
        Toast.makeText(MainActivity.this, "Unable to get data from server", Toast.LENGTH_SHORT).show();
      }
    }
  }
}
