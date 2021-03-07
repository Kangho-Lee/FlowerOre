package com.kt.iot.mobile.ui.activity.plantbook;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.kt.iot.mobile.android.R;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

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
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link third_plant.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link third_plant#newInstance} factory method to
 * create an instance of this fragment.
 */
public class third_plant extends Fragment {

    //public Context context;
    String JSON_STRING;
    ArrayList<sandle> data = new ArrayList<>();

    public third_plant() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getJSON();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.test_jsonparsing_display_listview, container, false);
        Context context = view.getContext();
        ListView listView = (ListView) view.findViewById(R.id.listview);
        plantAdapter adapter = new plantAdapter(context, data);
        listView.setAdapter(adapter);

        return view;
    }

    public void getJSON(){
        new BackgroudTask().execute();
    }

    class BackgroudTask extends AsyncTask<Void,Void,String>
    {
        String json_url;

        @Override
        protected void onPreExecute() {
            json_url = "http://52.78.162.29/sandle_json_return.php";
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
            System.out.println("json ~~~ 3333: " + JSON_STRING);
            parseJSON();
        }
    }

    public void parseJSON(){

        try{
            JSONObject jsonObject = new JSONObject(JSON_STRING);
            JSONArray jsonArray = jsonObject.getJSONArray("server_response");
            int count = 0;
            String id, name, engname;
            String desc, type, goodliving, othername, flower, harvest;
            String use, deco, url;
            String real_url;

            while(count < jsonArray.length()){

                JSONObject JO = jsonArray.getJSONObject(count);

                id = JO.getString("id");
                name = JO.getString("name");
                engname = JO.getString("engname");
                desc = JO.getString("desc");
                type = JO.getString("type");
                goodliving = JO.getString("goodliving");
                othername = JO.getString("othername");
                flower = JO.getString("flower");
                harvest = JO.getString("harvest");
                use = JO.getString("use");
                deco = JO.getString("deco");

                url = JO.getString("url");
                real_url = url.replaceAll("\\/", "/");

                sandle contents = new sandle(id, name, engname, desc, type, goodliving, othername, flower, harvest, use, deco, real_url);
                data.add(contents);
                count++;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    //Custom ListView 만들기
    private static class plantAdapter extends BaseAdapter {
        private ArrayList<sandle> mData;
        String passid, passname, passengname;
        String passdesc, passtype, passgoodliving, passothername, passflower, passharvest, passuse, passdeco;
        String imgURL;
        private Context context;

        public plantAdapter(Context context, ArrayList<sandle> data) {
            this.context = context;
            mData = data;
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public Object getItem(int position) {
            return mData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        //한 칸에 대한 처리 -> view holder를 만들어야 함
        //convertView에 ListView의 각 한칸한칸의 내용이 들어오게 됨
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if(convertView == null){
                holder = new ViewHolder();

                // 최초 -> convertView에 레이아웃을 씌어져야 함 by layoutinflater
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_plant, parent, false);
                //ImageView image = (ImageView) convertView.findViewById(R.id.image_view);
                CircularImageView cirImg = (CircularImageView) convertView.findViewById(R.id.img_view);
                TextView name = (TextView) convertView.findViewById(R.id.name);
                TextView engName = (TextView) convertView.findViewById(R.id.engname);
                TextView type = (TextView) convertView.findViewById(R.id.type);
                Button btn = (Button) convertView.findViewById(R.id.btn);

                //holder.image = image;
                holder.image = cirImg;
                holder.name = name;
                holder.engName = engName;
                holder.type = type;
                holder.btn = btn;

                holder.btn.setTag(position);
                convertView.setTag(holder);
            }
            else{
                // 재사용
                holder = (ViewHolder) convertView.getTag();
            }

            // 데이터 셋팅 (in position)
            sandle plant = mData.get(position);
            passid = plant.getId();
            passname = plant.getName();
            passengname = plant.getEngname();
            passdesc = plant.getDesc();
            passtype = plant.getType();
            passgoodliving = plant.getGoodliving();
            passothername = plant.getOthername();
            passflower = plant.getFlower();
            passharvest = plant.getHarvest();
            passuse = plant.getUse();
            passdeco = plant.getDeco();
            imgURL = plant.getUrl();

            Picasso.with(context).load(imgURL).fit().into(holder.image);
            holder.name.setText(plant.getName());
            holder.engName.setText(plant.getEngname());
            holder.type.setText(plant.getType());
            holder.btn.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    int pos = (Integer)v.getTag();
                    System.out.println("position num: " + pos);

                    Intent intent = new Intent(context, sandle_detail_activity.class);
                    intent.putExtra("id", mData.get(pos).getId());
                    intent.putExtra("name", mData.get(pos).getName());
                    intent.putExtra("engname", mData.get(pos).getEngname());
                    intent.putExtra("desc", mData.get(pos).getDesc());
                    intent.putExtra("type", mData.get(pos).getType());
                    intent.putExtra("goodliving", mData.get(pos).getGoodliving());
                    intent.putExtra("othername", mData.get(pos).getOthername());
                    intent.putExtra("flower", mData.get(pos).getFlower());
                    intent.putExtra("harvest", mData.get(pos).getHarvest());
                    intent.putExtra("use", mData.get(pos).getUse());
                    intent.putExtra("deco", mData.get(pos).getDeco());
                    intent.putExtra("url", mData.get(pos).getUrl());
                    context.startActivity(intent);
                }
            });

            return convertView;
        }

        //holder를 쓰는이유? 1) 빠르게 사용 2) 메모리 절약
        static class ViewHolder {
            ImageView image;
            TextView name, engName, type;
            Button btn;
        }
    }

}
