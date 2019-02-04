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

public class MainActivity2 extends AppCompatActivity {
    private boolean debug_flag = true;
    Button submitButton;
    Button showButton;
    EditText userInput;
    TextView outTest;
    private static String codd = "map_location_id_42";

    private String[] coord = new String[2];
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        submitButton = (Button)findViewById(R.id.find_button);
        userInput = (EditText)findViewById(R.id.user_address_text);
        outTest = (TextView)findViewById(R.id.showCor);
        showButton = (Button)findViewById(R.id.show_button);

        showButton.setVisibility(View.INVISIBLE);


        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new GetCoordinates().execute(userInput.getText().toString().replace(" ","+"));
                if(debug_flag) {
                    Toast myToast = Toast.makeText(getApplicationContext(), userInput.getText().toString().replace(" ","+"), Toast.LENGTH_SHORT);
                    myToast.show();
                }
            }
        });



    }
    public void showMap (View view) {
        Intent mapIntent = new Intent(this, MapsActivity.class);
        mapIntent.putExtra(codd,coord);
        startActivity(mapIntent);
    }
    private class GetCoordinates extends AsyncTask<String,Void,String> {
        ProgressDialog dialog = new ProgressDialog(MainActivity2.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Please wait....");
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();

        }

        @Override
        protected String doInBackground(String... strings) {
            String response;
            try{
                String address = strings[0];
                HttpHandler http = new HttpHandler();
                String url = String.format("https://maps.googleapis.com/maps/api/geocode/json?address=%s&key=AIzaSyB7FQBynh2Bw37hqUgC_7GKsQpz45D-D04",address);
                response = http.getHttpData(url);
                System.out.println(response);
                return response;
            }
            catch (Exception ex)
            {
                Toast mToast = Toast.makeText(getApplicationContext(),"ERROR", Toast.LENGTH_SHORT);
                mToast.show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            try{
                JSONObject jsonObject = new JSONObject(s);

                String lat = ((JSONArray)jsonObject.get("results")).getJSONObject(0).getJSONObject("geometry")
                        .getJSONObject("location").get("lat").toString();
                coord[0] = lat;
                String lng = ((JSONArray)jsonObject.get("results")).getJSONObject(0).getJSONObject("geometry")
                        .getJSONObject("location").get("lng").toString();
                coord[1] = lng;
                outTest.setText(String.format("Coordinates : %s / %s ",lat,lng));

                //show weather here

                if(dialog.isShowing())
                    dialog.dismiss();
                //launch the maps
                showButton.setVisibility(View.VISIBLE);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
