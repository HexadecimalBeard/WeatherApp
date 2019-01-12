package com.hexadecimal.weatherapp;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    EditText editText;
    TextView resultTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.editText);
        resultTextView = findViewById(R.id.resultTextView);
    }
    public void getWeather (View view){

        // almak istedigimiz url'yi girdigimiz yer

        DownloadTask task = new DownloadTask();

        try {
            // encodedCityName kullanmamizin nedeni yapilan sorgularda bosluk olması gibi durumlarda
            // bosluktan kaynakli herhangi bir hataya yakalanmamak

            String encodedCityName = URLEncoder.encode(editText.getText().toString(),"UTF-8");
            task.execute("https://openweathermap.org/data/2.5/weather?q=" + encodedCityName + "&appid=b6907d289e10d714a6e88b30761fae22");

            // get weather butonu calistiginda klavyeyi asagi indirmek icin kullandik

            InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(editText.getWindowToken(),0);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),"Could not found weather", Toast.LENGTH_SHORT).show();
        }
    }

    public class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {

            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;
            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();

                while (data != -1){

                    char  current = (char) data;
                    result += current;
                    data = reader.read();
                }
                return result;
            } catch (Exception e){
                e.printStackTrace();
                Toast.makeText(getApplicationContext(),"Could not found weather", Toast.LENGTH_SHORT).show();
                return null;
            }
        }
        // asynctask islemini yaparken kullanici arayuzunde hicbir seye dokunmamali,
        // kullanici arayuzunu ilgilendiren hersey onPostExecute' metodu icinde gerceklestirilmeli

        @Override
        protected void onPostExecute(String s) {        // buradaki string ifade yukaridaki result degerini alir
            super.onPostExecute(s);

            try {
                JSONObject jsonObject = new JSONObject(s);
                String weatherInfo = jsonObject.getString("weather"); // sadece weather basligi altindaki verileri getirecek

                JSONArray arr = new JSONArray(weatherInfo);         // gelen verileri JSON tipinde olusturdugumuz array'e yerlestirdik

                String message = "";

                for(int i = 0; i<arr.length(); i++){

                    JSONObject jsonPart = arr.getJSONObject(i);   // JSON sorgusundan gelen verileri atadigimiz diziden alıp parcalara bolduk
                    String main = jsonPart.getString("main");
                    String description = jsonPart.getString("description");

                    if(!main.equals("") && !description.equals("")){
                        message += main + ": " + description + "\r\n";
                    }
                }
                if(!message.equals("")){
                    resultTextView.setText(message);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(),"Could not found weather", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
