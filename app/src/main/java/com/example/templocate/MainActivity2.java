package com.example.templocate;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity2 extends AppCompatActivity {

    // Jacob Driving now
    private boolean debug_flag = false;
    Button submitButton;
    Button showButton;
    EditText userInput;
    TextView textWeather;
    TextView outTest;
    private static String codd = "map_location_id_42";
    private static String getCode = "codeMe";
    private String information = "";

    private String[] coord = new String[2];

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        textWeather = (TextView) findViewById(R.id.weatherText);
        submitButton = (Button) findViewById(R.id.find_button);
        userInput = (EditText) findViewById(R.id.user_address_text);
        outTest = (TextView) findViewById(R.id.showCor);
        showButton = (Button) findViewById(R.id.show_button);

        showButton.setVisibility(View.INVISIBLE);

        // When user submits their address
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Tyler Driving now
                // Get the coordinates
                GetCoordinates cord = new GetCoordinates();
                cord.execute(userInput.getText().toString().replace(" ", "+"));

                // Jacob Driving now
                // Show only if the debug flag is for testing
                if (debug_flag) {
                    Toast myToast = Toast.makeText(getApplicationContext(), userInput.getText().toString().replace(" ", "+"), Toast.LENGTH_SHORT);
                    myToast.show();

                }
            }
        });


    }

    public void showMap(View view) {
        // Jacob driving now
        Intent mapIntent = new Intent(this, MapsActivity.class);
        mapIntent.putExtra(codd, coord);
        mapIntent.putExtra(getCode, information);
        startActivity(mapIntent);
    }


    private class GetCoordinates extends AsyncTask<String, Void, String> {
        ProgressDialog dialog = new ProgressDialog(MainActivity2.this);

        // Please wait while address is processing
        @Override
        protected void onPreExecute() {

            // Tyler driving now
            super.onPreExecute();
            dialog.setMessage("Please wait while we get the coordinates....");
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();

        }

        //
        @Override
        protected String doInBackground(String... strings) {
            String response;
            try {
                // Jacob driving now
                String address = strings[0];
                HttpHandler http = new HttpHandler();
                String url = String.format("https://maps.googleapis.com/maps/api/geocode/json?address=%s&key=AIzaSyB7FQBynh2Bw37hqUgC_7GKsQpz45D-D04", address);
                response = http.getHttpData(url);
                System.out.println(response);
                return response;
            } catch (Exception ex) {
                Toast mToast = Toast.makeText(getApplicationContext(), "ERROR", Toast.LENGTH_SHORT);
                mToast.show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            try {

                // Tyler driving now

                JSONObject jsonObject = new JSONObject(s);

                // Get the lat from the JSONArray
                String lat = ((JSONArray) jsonObject.get("results")).getJSONObject(0).getJSONObject("geometry")
                        .getJSONObject("location").get("lat").toString();

                // Put the coordinate inside our array
                coord[0] = lat;

                // Get the long from the JSONArray
                String lng = ((JSONArray) jsonObject.get("results")).getJSONObject(0).getJSONObject("geometry")
                        .getJSONObject("location").get("lng").toString();

                // Put the coordinate inside our array
                coord[1] = lng;

                // Show the coordinates in screen
                outTest.setText(String.format("Coordinates : %s / %s ", lat, lng));

                // Dismiss the loading dialog
                if (dialog.isShowing())
                    dialog.dismiss();

                // Show Maps button becomes visible
                showButton.setVisibility(View.VISIBLE);

                new GetWeather().execute(userInput.getText().toString().replace(" ", "+"));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class GetWeather extends AsyncTask<String, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(String... strings) {
            try {

                // Jacob Driving now

                //  String address = strings[0];
                // HttpHandler http = new HttpHandler();

                if (debug_flag)
                    System.out.println("test" + coord[0] + " lat " + coord[1]);

                String url = String.format("https://api.darksky.net/forecast/cfda02f3a33a16f4f72241a2bd4e5721/" + coord[0] + "," + coord[1]);

                // String url = String.format("https://api.darksky.net/forecast/53e80a669dc75ce56eeab42a033af526/%s,%s",coord[0],coord[1]);

                // response = http.getHttpData(url);
                URL obj = new URL(url);
                HttpURLConnection con = (HttpURLConnection) obj.openConnection();

                con.setRequestMethod("GET");
                con.setRequestProperty("User-Agent", "Mozilla/5.0");

                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null)
                    response.append(inputLine);

                if (debug_flag)
                    System.out.println("This should be a response: " + response);
                in.close();

                return new JSONObject(response.toString());


            } catch (Exception ex) {

            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonO) {
            try {
                // temperature, humidity, wind speed, precipitation
                JSONObject current = jsonO.getJSONObject("currently");

                // Jacob driving now
                Double temp = (Double) current.getDouble("temperature");
                Double humidity = (Double) current.getDouble("humidity");
                Double wind = (Double) current.getDouble("windSpeed");
                Double precipProb = (Double) current.getDouble("precipProbability");
                String precip;
                try {
                    precip = current.getString("precipType");
                } catch (Exception e) {
                    precip = null;
                }

                precipProb *= 100;
                humidity *= 100;
                String output;
                // Tyler driving now
                if (precip != null) {
                    output = "Temperature: " + temp.toString() + "° Fahrenheit" +
                            "\n" + "Humidity: " + humidity.toString() + "%" +
                            "\n" + "Wind Speed: " + wind.toString() + " mph" +
                            "\n" + precipProb.toString() + "% chance of " + precip;
                } else {
                    output = "Temperature: " + temp.toString() + "° Fahrenheit" +
                            "\n" + "Humidity: " + humidity.toString() + "%" +
                            "\n" + "Wind Speed: " + wind.toString() + " mph" +
                            "\n" + "There is no precipitation right now.";
                }

                information = output;
                textWeather.setText(output);


                /*if(dialog.isShowing())
                    dialog.dismiss();*/

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
