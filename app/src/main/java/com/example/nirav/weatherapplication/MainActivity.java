//Program to show details of weather based on Location
//Created By Nirav Solanki on 8 November 2018
package com.example.nirav.weatherapplication;

import android.content.Context;
import android.graphics.drawable.Icon;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



public class MainActivity extends AppCompatActivity {

    EditText EnterCity; //Contains City name input by user
    TextView City,Temperature,Min,Max,MainWeather,Descript,Humidity,Cloud; // Contains various details fetched from openweathermap API
    Button GetWeather;
    ImageView WeatherIcon;
    String enteredCity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Binding various elements of the application to a variable
        EnterCity = findViewById(R.id.etEnterCity);
        City = findViewById(R.id.lblCity);
        Temperature = findViewById(R.id.lblTemperature);
        Min = findViewById(R.id.lblMin);
        Max = findViewById(R.id.lblMax);
        MainWeather = findViewById(R.id.lblMainWeather);
        Descript = findViewById(R.id.lblDescription);
        Humidity = findViewById(R.id.lblHumidity);
        Cloud = findViewById(R.id.lblCloud);
        GetWeather = findViewById(R.id.btnGetWeather);
        WeatherIcon = findViewById(R.id.lblWeatherIcon);

        //Run function when button "GetWeather" is clicked
        GetWeather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Fetch internet Connectivity details
                ConnectivityManager manager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo NetworkStatus = manager.getActiveNetworkInfo();

                //Check for internet connectivity
                if(NetworkStatus != null && NetworkStatus.isConnected())
                {
                    //fetch city name from input box
                    enteredCity = EnterCity.getText().toString();

                    //Stop function if length of city name is less then one
                    if(enteredCity.length()<1)
                    {
                        Toast emptyCityField = Toast.makeText(getApplicationContext(),"Please Write City Name",Toast.LENGTH_LONG);
                        emptyCityField.show();
                        return;
                    }
                    //Function to check and display weather information
                    GetWeatherInfo();
                }
                else
                {
                    Toast toast = Toast.makeText(getApplicationContext(),"No Internet Connection Found",Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        });
    }

    //Function to fetch weather information
    public void GetWeatherInfo()
    {
        //Url to fetch weather information according to city from openweathermap API
        String url = "http://api.openweathermap.org/data/2.5/weather?q=";
        url = url + enteredCity + "&appid=e334d551257b111cb38ff035da00eb22&units=metric"; //Adding city name, credential and unit style in url


        //Json object request to fetch information
        JsonObjectRequest weather = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                //Due to wrong city name no JSON Object could return, hence use of Try,Catch
                try {

                    JSONObject main = response.getJSONObject("main"); //Fetch main weather object
                    JSONArray weather = response.getJSONArray("weather"); //Fetch weather information stored under "weather" array
                    JSONObject obj = weather.getJSONObject(0);
                    JSONObject Clouds = response.getJSONObject("clouds"); //Fetch cloud's information
                    String temperature = main.getString("temp") + (char) 0x00B0 + "C";  //Fetch temperature
                    String min = "min." + main.getString("temp_min") + (char) 0x00B0 + "C"; //Fetch minimum temperature
                    String max = "max." + main.getString("temp_max") + (char) 0x00B0 + "C"; //Fetch maximum temperature
                    String description = obj.getString("description"); //Fetch description of weather
                    String mainWeather = obj.getString("main"); //Fetch main weather
                    String icon = obj.getString("icon");//Fetch icon name for weather
                    String humidity = main.getString("humidity") + "%" + "\nHumidity"; //Fetch humidity
                    String clouds = Clouds.getString("all") + "%" + "\nClouds"; //Fetch cloud status

                    String city = enteredCity.substring(0,1).toUpperCase() + enteredCity.substring(1); //Convert first letter into Upper Case

                    String imgURL = "http://openweathermap.org/img/w/" + icon + ".png";//Url to fetch icon

                    setIcon(imgURL);//Set Weather Icon

                    //Bind information found to elements in Application
                    City.setText(city);
                    Temperature.setText(temperature);
                    Min.setText(min);
                    Max.setText(max);
                    Descript.setText(description);
                    MainWeather.setText(mainWeather);
                    Humidity.setText(humidity);
                    Cloud.setText(clouds);

                }catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //If no value is found error is returned
                Toast.makeText(getApplicationContext(),"Enter Valid City Name",Toast.LENGTH_SHORT).show();
            }
        }
        );
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(weather);
    }

    //Function to set image fetched from openweathermap api to WeatherIcon
    void setIcon(String imgURL)
    {
        Picasso.with(this).load(imgURL).resize(150,150).into(WeatherIcon);
    }

}
