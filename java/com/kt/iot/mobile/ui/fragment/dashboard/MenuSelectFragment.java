package com.kt.iot.mobile.ui.fragment.dashboard;

/**
 * Created by User on 2017-02-23.
 */

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kt.iot.mobile.android.R;
import com.kt.iot.mobile.ui.activity.CameraActivity;
import com.kt.iot.mobile.ui.activity.MainActivity;
import com.kt.iot.mobile.ui.activity.MenuSelectActivity;
import com.kt.iot.mobile.ui.activity.RecordActivity;
import com.kt.iot.mobile.ui.activity.plantbook.Plantbook_MainActivity;
import com.kt.iot.mobile.utils.ApplicationPreference;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.kt.gigaiot_sdk.data.Device;
import com.kt.gigaiot_sdk.data.SvcTgt;
import com.kt.iot.mobile.android.R;
import com.kt.iot.mobile.ui.fragment.dashboard.DashboardFragment;
import com.kt.iot.mobile.ui.fragment.device.DeviceFragment;
import com.kt.iot.mobile.ui.fragment.device.list.DeviceListFragment;
import com.kt.iot.mobile.ui.fragment.drawer.DrawerMenuFragment;
import com.kt.iot.mobile.utils.BitmapUtil;
import com.kt.iot.mobile.utils.ModifyDeviceMgr;
import com.kt.iot.mobile.utils.Util;
/**
 * Created by User on 2017-01-10.
 */

public class MenuSelectFragment extends Fragment{

    public static final String EXTRA_MEMBER_ID = "member_id";

    private String mMbrId;
    View view;
    private ImageView mIvMainBg;
    private String mBgImgPath;
    private int mBgRsrcId;



    Button log, control, plant_type, plant_data, plant_image, plant_picture, plant_record, extra;

    public MenuSelectFragment(){

    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_menu, container,false);



    //    mMbrId=bundle.getString(EXTRA_MEMBER_ID);
       // mMbrId = getIntent().getStringExtra(EXTRA_MEMBER_ID);
        log=(Button)view.findViewById(R.id.Log);
        log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.putExtra(MainActivity.EXTRA_MEMBER_ID, mMbrId);
                startActivity(intent);
               // finish();
            }
        });

        plant_type=(Button)view.findViewById(R.id.plant_type);
        plant_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(getActivity(), CameraActivity.class);
                // intent2.putExtra(.EXTRA_MEMBER_ID, mMbrId);
                startActivity(intent2);
                //finish();

            }
        });



        plant_data=(Button)view.findViewById(R.id.data);
        plant_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(getActivity(), Plantbook_MainActivity.class);
                // intent2.putExtra(.EXTRA_MEMBER_ID, mMbrId);
                startActivity(intent2);
                //finish();

            }
        });









       /* plant_data=(Button)findViewById(R.id.plant_data);
        plant_data.setOnClickListener(this);*/
        plant_image=(Button)view.findViewById(R.id.plantImage);
        plant_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent4 = new Intent(Intent.ACTION_VIEW, Uri.parse("http://203.252.118.95/Capture"));
                // intent2.putExtra(.EXTRA_MEMBER_ID, mMbrId);
                startActivity(intent4);
                //finish();
            }
        });

        plant_record=(Button)view.findViewById(R.id.Write);
        plant_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent5 = new Intent(getActivity(), RecordActivity.class);
                // intent2.putExtra(.EXTRA_MEMBER_ID, mMbrId);
                startActivity(intent5);
                //finish();
            }
        });
return view;





    }

    public void setMbrId(String id){
        mMbrId=id;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

      //  Log.d(TAG, "onOptionsItemSelected!!");

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();

        ((ActionBarActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((ActionBarActivity)getActivity()).getSupportActionBar().setHomeAsUpIndicator(R.drawable.homemenu);

        ((ActionBarActivity)getActivity()).getSupportActionBar().setCustomView(R.layout.actionbar_custom_logo_menu);
        ((ActionBarActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        ((ActionBarActivity)getActivity()).getSupportActionBar().setDisplayShowCustomEnabled(true);

        setBackground();

        ModifyDeviceMgr.setModifyDevice(null);

    }
    private void setBackground(){

         mIvMainBg=new ImageView(getContext());
        mIvMainBg.setBackgroundResource(R.drawable.bg_02);


    }

}

