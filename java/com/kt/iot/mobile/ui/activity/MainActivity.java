package com.kt.iot.mobile.ui.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.kt.gigaiot_sdk.PushApi;
import com.kt.gigaiot_sdk.data.Device;
import com.kt.gigaiot_sdk.data.SvcTgt;
import com.kt.iot.mobile.android.R;
import com.kt.iot.mobile.ui.fragment.dashboard.DashboardFragment;
import com.kt.iot.mobile.ui.fragment.device.DeviceFragment;
import com.kt.iot.mobile.ui.fragment.device.list.DeviceListFragment;
import com.kt.iot.mobile.ui.fragment.drawer.DrawerMenuFragment;
import com.kt.iot.mobile.utils.ApplicationPreference;
import com.kt.iot.mobile.utils.Util;

/**
 * Created by NP1014425901 on 2015-02-26.
 */
public class MainActivity extends ActionBarActivity implements DeviceListFragment.OnDeviceListSelectedListener,
        DashboardFragment.OnSvcTgtListSelectedListener, DrawerMenuFragment.OnDrawerMenuSelectListener {

    private DrawerMenuFragment mDrawerMenuFragment;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    private Device mDevice;

    private static final String TAG = MainActivity.class.getSimpleName();

    public static final String EXTRA_MEMBER_ID = "member_id";
    String mbrId;


    /********* about dialog *********/
    TextView text;
    String str="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);

        mbrId = getIntent().getStringExtra(EXTRA_MEMBER_ID);
      //  Toast.makeText(MainActivity.this, "여기다111111111111111111111111111", Toast.LENGTH_SHORT).show();
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mDrawerLayout = (DrawerLayout) inflater.inflate(R.layout.decor, null);

        text= (TextView)findViewById(R.id.text);



        ViewGroup decor = (ViewGroup) getWindow().getDecorView();
        View child = decor.getChildAt(0);
        decor.removeView(child);

        FrameLayout contaniner = (FrameLayout) mDrawerLayout.findViewById(R.id.container);
        contaniner.addView(child);
        mDrawerLayout.findViewById(R.id.drawer_menu).setPadding(0, Util.getStatusBarHeight(this), 0, 0);

        decor.addView(mDrawerLayout);

        mDrawerMenuFragment = (DrawerMenuFragment) getFragmentManager().findFragmentById(R.id.drawer_menu);
        mDrawerMenuFragment.setUserName(mbrId);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.desc_open_drawer, R.string.desc_close_drawer);


        initDashboardFragment(mbrId); // 맨처음에 이름 나오는 부분 인 듯하다

        getSupportActionBar().setElevation(0);






    }

    private void initDashboardFragment(String mbrId){
        DashboardFragment dashboardFragment = new DashboardFragment();
        dashboardFragment.setMbrId(mbrId);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.replace(R.id.fragment_container, dashboardFragment);

        transaction.commit();

    }

    private void initializeDeviceListFragment(SvcTgt svcTgt) {

        DeviceListFragment fragment = (DeviceListFragment) getSupportFragmentManager().findFragmentById(R.id.device_list_fragment);

        if (fragment != null) {                 //태블릿

            fragment.setSvcTgt(svcTgt);
            fragment.refresh();

        } else {                                //폰

            Gson gson = new Gson();
            String strSvcTgt = gson.toJson(svcTgt, SvcTgt.class);
           // Toast.makeText(MainActivity.this, "여기다222222222222222222222222222", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivity.this, DeviceListActivity.class);
            intent.putExtra(DeviceListActivity.EXTRA_SVCTGT, strSvcTgt);

            startActivity(intent);

            /*fragment = new DeviceListFragment();
            fragment.setSvcTgtSeq(svcTgtSeq);

            fragment.setArguments(getIntent().getExtras());

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            transaction.replace(R.id.fragment_container, fragment);
            transaction.addToBackStack(null);

            transaction.commit();*/
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
      /*  MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_actions, menu);*/
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Log.w(TAG, "onOptionsItemSelected!!");

        switch (item.getItemId()) {

            case android.R.id.home:
                Log.w(TAG, "onOptionsItemSelected!! id = android.R.id.home");

                if(getSupportFragmentManager().getBackStackEntryCount() > 0){

                    getSupportFragmentManager().popBackStack();
                    return true;
                }else {

                    mDrawerToggle.onOptionsItemSelected(item);
                    return true;
                }

            default :
                return super.onOptionsItemSelected(item);
        }
    }

    public void onDeviceSelected(int position, Device device) {

        mDevice = device;

        DeviceFragment fragment = (DeviceFragment) getSupportFragmentManager().findFragmentById(R.id.device_fragment);

        if (fragment != null) {             //Tablet
            fragment.setDevice(device);
//            fragment.refresh();

        } else {                            //mobile

            Gson gson = new Gson();
            String strDevice = gson.toJson(mDevice);
           // Toast.makeText(MainActivity.this, "여기다!!!!!!!!!!!!!!!!!!!!!!!!!", Toast.LENGTH_SHORT).show();
            // 여기는 안 되는 거 같음!!
            Intent intent = new Intent(MainActivity.this, DeviceActivity.class);
            intent.putExtra(DeviceActivity.EXTRA_DEVICE, strDevice);

            startActivity(intent);

            /*fragment = new DeviceFragment();
            fragment.setDevice(device);

            fragment.setArguments(getIntent().getExtras());

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            transaction.replace(R.id.fragment_container, fragment);
            transaction.addToBackStack(null);

            transaction.commit();*/

        }
    }

    @Override
    public void onSvcTgtSelected(SvcTgt svcTgt) {
        Log.i(TAG, "onSvcTgtSelected! svcTgtSeq = " + svcTgt);
        initializeDeviceListFragment(svcTgt);
    }

    @Override
    public void onBackPressed() {

        Log.i(TAG, "onBackPressed()!");

        if(getSupportFragmentManager().getBackStackEntryCount() > 0){

            getSupportFragmentManager().popBackStack();
            return;
        }

        super.onBackPressed();
    }

    @Override
    public void onMenuSelected(int menuIndex) {

        switch(menuIndex){
                case DrawerMenuFragment.MENU_INDEX_SETTINS: {
                /*Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
                startActivity(intent);*/
            }
                break;

            case DrawerMenuFragment.MENU_INDEX_PLANT_SETTING: {

                LayoutInflater inflater=getLayoutInflater();

                final View dialogView= inflater.inflate(R.layout.dialog_plant, null);



                //멤버의 세부내역 입력 Dialog 생성 및 보이기

                AlertDialog.Builder buider= new AlertDialog.Builder(this); //AlertDialog.Builder 객체 생성

                buider.setTitle("PLANT SETTING"); //Dialog 제목

                buider.setIcon(android.R.drawable.ic_menu_add);

                buider.setView(dialogView);

                buider.setPositiveButton("확인", new DialogInterface.OnClickListener() {


                    @Override

                    public void onClick(DialogInterface dialog, int which) {



                        EditText edit_name= (EditText)dialogView.findViewById(R.id.dialog_edit);
                        RadioGroup rg= (RadioGroup)dialogView.findViewById(R.id.dialog_rg);

                        String name= edit_name.getText().toString();
                        int checkedId= rg.getCheckedRadioButtonId();



                        //Check 된 RadioButton의 ID로 라디오버튼 객체 찾아오기
                        RadioButton rb= (RadioButton)rg.findViewById(checkedId);

                        String plant= rb.getText().toString();



                        //TextView의 이전 Text에 새로 입력된 멤버의 데이터를 추가하기

                        //TextView로 멤버리스트를 보여주는 것은 바람직하지 않음.다음 포스트에서 ListView로 처리합니다.

                        String s= name+" "+plant+"\n";
                        str+= s;



                        Toast.makeText(MainActivity.this, str+" 무럭무럭 자라라!!", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();

                    }

                });

                buider.setNegativeButton("취소", new DialogInterface.OnClickListener() {

                    //Dialog에 "Cancel"이라는 타이틀의 버튼을 설정



                    @Override

                    public void onClick(DialogInterface dialog, int which) {


                        dialog.dismiss();

                    }

                });

                //설정한 값으로 AlertDialog 객체 생성
                AlertDialog dialog=buider.create();
                //Dialog의 바깥쪽을 터치했을 때 Dialog를 없앨지 설정
                dialog.setCanceledOnTouchOutside(false);//없어지지 않도록 설정
                //Dialog 보이기

                dialog.show();

            }
            break;

            case DrawerMenuFragment.MENU_INDEX__PLANT_SHOW: {

            }
            break;

            case DrawerMenuFragment.MENU_INDEX_LOGOUT: {
                ApplicationPreference.getInstance().setPrefAccountId("");
                new PushSessionDeleteTask().execute();                      //Push Session 등록해제

               /* getActivity().setResult(getActivity().RESULT_OK);
                getActivity().finish();
*/

                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
            break;

        }

    }
    public class PushSessionDeleteTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {


            PushApi pushApi = new PushApi(ApplicationPreference.getInstance().getPrefAccessToken());
            pushApi.gcmSessionDelete(ApplicationPreference.getInstance().getPrefGcmRegId());

            return null;
        }
    }
}
