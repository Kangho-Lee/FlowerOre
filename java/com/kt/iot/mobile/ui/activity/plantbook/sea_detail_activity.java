package com.kt.iot.mobile.ui.activity.plantbook;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.kt.iot.mobile.android.R;
import com.squareup.picasso.Picasso;

/**
 * Created by 강호 리 on 2017-03-01.
 */

public class sea_detail_activity extends ActionBarActivity {

    String name, engname, desc, type, goodliving, othername, flower, harvest, use, deco, url;
    Button back;
    TextView Name, Engname, Detail, Desc;
    ImageView URL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nonbat_detail);

        back = (Button) findViewById(R.id.back);
        Name = (TextView) findViewById(R.id.name);
        Engname = (TextView) findViewById(R.id.engname);
        URL = (ImageView) findViewById(R.id.image_view2);
        Desc = (TextView) findViewById(R.id.desc);
        Detail = (TextView) findViewById(R.id.detail);

        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        engname = intent.getStringExtra("engname");
        desc = intent.getStringExtra("desc");
        type = intent.getStringExtra("type");
        goodliving = intent.getStringExtra("goodliving");
        othername = intent.getStringExtra("othername");
        flower = intent.getStringExtra("flower");
        harvest = intent.getStringExtra("harvest");
        use = intent.getStringExtra("use");
        deco = intent.getStringExtra("deco");
        url = intent.getStringExtra("url");

        showPlantInfo();

    }

    public void back(View view){
        Intent intent = new Intent(this, Plantbook_MainActivity.class);
        startActivity(intent);
    }

    public void showPlantInfo(){
        Name.setText(name);
        Engname.setText(engname);
        Picasso.with(this).load(url).fit().into(URL);
        Desc.setText(" 분류: " + type + "\n 잘 자라는 곳: " + goodliving +"\n 다른이름: " + othername + "\n 꽃 피는 때: " + flower+"\n 수확시기: " + harvest + "\n 쓰임: "+ use + "\n 가꾸기: "+deco);
        Detail.setText(desc);
        //Detail.setMovementMethod(new ScrollingMovementMethod());
    }
}
