package com.kt.iot.mobile.push;

import android.os.AsyncTask;

import com.kt.iot.mobile.ui.activity.MainTwoActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by kangholee on 2017-03-30.
 */

public class PlantTip {

    private static String plantType = MainTwoActivity.plant;
    String tipURL = "";
    String planttip = "initial";
    String JSON_STRING;



    public String plantTip(){

        //System.out.println("Type222:  " + plantType);
        switch(plantType){
            case "오이":
                tipURL = "http://52.78.162.29/push_cucumber.php";
                break;

            case "상추":
                tipURL = "http://52.78.162.29/push_lettuce.php";
                break;

            case "장미":
                tipURL = "http://52.78.162.29/push_rose.php";
                break;

            case "토마토":
                tipURL = "http://52.78.162.29/push_tomato.php";
                break;

            default:
                return null;
        }
        System.out.println("Url:  " + tipURL);

        getJSON();

        System.out.println("tip:    " + planttip);
        return planttip;

    }

    public void getJSON(){
        new BackgroudTask().execute();
    }

    class BackgroudTask extends AsyncTask<Void,Void,String>
    {
        String json_url;

        @Override
        protected void onPreExecute() {
            json_url = tipURL;
        }

        @Override
        protected String doInBackground(Void... params) {
            try{
                URL url = new URL(json_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder();
                while((JSON_STRING = bufferedReader.readLine()) != null){
                    stringBuilder.append(JSON_STRING+"\n");
                }

                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();

                return stringBuilder.toString().trim();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String result) {
            JSON_STRING = result;
            parseJSON();
        }
    }

    public void parseJSON(){

        try{
            JSONObject jsonObject = new JSONObject(JSON_STRING);
            JSONArray jsonArray = jsonObject.getJSONArray("server_response");
            int count = 0;
            String tip;

            while(count < jsonArray.length()){

                JSONObject JO = jsonArray.getJSONObject(count);

                tip = JO.getString("tips");
                count++;

                planttip = tip;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


}



