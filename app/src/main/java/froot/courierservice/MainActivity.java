
package froot.courierservice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import android.Manifest;
import android.accessibilityservice.AccessibilityService;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.text.GetChars;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import froot.courierservice.LocationService.LocationService;
import froot.courierservice.db.UserDb;
import froot.courierservice.retorfit.JSONPlaceHolderApi;
import froot.courierservice.retorfit.NetworkClient;
import froot.courierservice.retorfit.getIsBusy;
import froot.courierservice.retorfit.getItems;
import froot.courierservice.retorfit.getJson;
import froot.courierservice.retorfit.getOrders;
import froot.courierservice.retorfit.getProfile;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends Activity {

    ListView lv;
    String UserToken;
    ListViewAdapter adapter;
    ProgressDialog pDialog;
    ArrayList<HashMap<String, String>> menuItems;
    ArrayList<HashMap<String, ArrayList<getItems>>> inItems;

    static final String KEY_ORDERS_ID = "OrdersId";
    static final String KEY_ORDERS_NAME = "Name";
    static final String KEY_ORDERS_ADDRESS = "Address";
    static final String KEY_ORDERS_STORE_ID = "StoreId";
    static final String KEY_ORDERS_DATE = "Date";
    static final String KEY_ORDERS_IS_CARD = "IsCard";
    static final String KEY_ORDERS_TAKEN = "Taken";
    static final String KEY_ORDERS_IS_BCC_CARD = "IsBccCard";
    static final String KEY_ORDERS_TOTAL = "Total";
    static final String KEY_ORDERS_STATUS = "Status";
    static final String KEY_ORDERS_STATUS_ID = "StatusId";
    static final String KEY_ORDERS_DELIVERY_TYPE = "DeliveryType";
    static final String KEY_ORDERS_DELIVERY_PRICE = "DeliveryPrice";
    static final String KEY_ORDERS_PHONE = "Phone";
    static final String KEY_ORDERS_ITEMS = "Items";
    static final String KEY_ORDERS_ITEMS_Title = "Title";
    static final String KEY_ORDERS_RUNNER = "runner";

    int isBusy;
    String username;
    int current_page = 1;
    Button btnLoadMore;
    static MainActivity instance;
    LocationService gpsService;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (!notificationManager.areNotificationsEnabled()){
                askPermission();
            }
        }

        Intent intent = getIntent();
        UserToken = intent.getStringExtra("token");
        instance = this;

        if (checkLocationPermission()){
            Intent backService = new Intent(this.getApplication(), LocationService.class);
            backService.putExtra("token",UserToken);
            this.getApplication().startService(backService);
            this.getApplication().bindService(intent,serviceConnection, Context.BIND_AUTO_CREATE);
        };

        Retrofit retrofit = NetworkClient.getRetrofitClient();
        JSONPlaceHolderApi jp = retrofit.create(JSONPlaceHolderApi.class);
        Call<getIsBusy> call = jp.isBusy(UserToken);
        call.enqueue(new Callback<getIsBusy>() {
            @Override
            public void onResponse(Call<getIsBusy> call, Response<getIsBusy> response) {
                if (response.isSuccessful()){
                    isBusy = response.body().getBusy();
                    username = response.body().getUsername();
                }
            }
            @Override
            public void onFailure(Call<getIsBusy> call, Throwable t) {

            }
        });

        Execute();
    }


    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Включите геоданные")
                        .setMessage("Пожалуйста, дайте разрешение на передачу геоданных")
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        //Request location updates:
                        Intent intent = getIntent();
                        Intent backService = new Intent(this.getApplication(), LocationService.class);
                        backService.putExtra("token",UserToken);
                        this.getApplication().startService(backService);
                        this.getApplication().bindService(intent,serviceConnection, Context.BIND_AUTO_CREATE);
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                }
                return;
            }

        }
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            String name = className.getClassName();
            if (name.endsWith("LocationService")) {
                gpsService = ((LocationService.LocationServiceBinder) service).getService();
            }
        }
        public void onServiceDisconnected(ComponentName className) {
            if (className.getClassName().equals("BackgroundService")) {
                gpsService = null;
            }
        }
    };

    public void log_out_btn(View view){
        new logOut().execute();
    }

    public void open_profile(View view){
        new profile().execute();
    }

    public void askPermission(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilder.setMessage("Пожалуйста, включите уведомления для этого приложения, чтобы не пропускать свежие заказы!");
        alertDialogBuilder.setNegativeButton("Не хочу",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        alertDialogBuilder.setPositiveButton("Хорошо",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        startActivityForResult(new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).putExtra(Settings.EXTRA_APP_PACKAGE, getApplicationContext().getPackageName()), 0);
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void Execute(){

        if ((getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK) ==
                Configuration.SCREENLAYOUT_SIZE_LARGE) {
            new loadMoreListView().execute();
        }

        lv = (ListView) findViewById(R.id.list);
        ///

        menuItems = new ArrayList<HashMap<String, String>>();
        inItems = new ArrayList<HashMap<String, ArrayList<getItems>>>();

        Retrofit retrofit = NetworkClient.getRetrofitClient();
        JSONPlaceHolderApi jp = retrofit.create(JSONPlaceHolderApi.class);
        Call<getJson> call = jp.getOrders(UserToken, current_page);
        call.enqueue(new Callback<getJson>() {
            @Override
            public void onResponse(Call<getJson> call, Response<getJson> response) {
                if (response.isSuccessful()){
                    btnLoadMore = new Button(MainActivity.this);
                    btnLoadMore.setText("Загрузить еще");
                    btnLoadMore.setTextColor(getResources().getColor(R.color.blackColor));
                    btnLoadMore.setBackground(getResources().getDrawable(R.drawable.accept_refuse_btn));
                    btnLoadMore.setPadding(0,10,0,10);

                    lv.addFooterView(btnLoadMore);
                    lv.setFooterDividersEnabled(true);

                    btnLoadMore.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View arg0) {
                            new loadMoreListView().execute();
                        }
                    });

                    getJson item = response.body();

                    for (int i = 0; i < item.getOrders().size(); i++) {
                        // creating new HashMap
                        HashMap<String, String> map = new HashMap<String, String>();

                        map.put(KEY_ORDERS_ID, item.getOrders().get(i).getId());
                        map.put(KEY_ORDERS_RUNNER, item.getOrders().get(i).getRunner());
                        map.put(KEY_ORDERS_NAME, item.getOrders().get(i).getName());
                        map.put(KEY_ORDERS_ADDRESS, item.getOrders().get(i).getAddress());
                        map.put(KEY_ORDERS_DATE, item.getOrders().get(i).getDate());
                        map.put(KEY_ORDERS_STORE_ID, item.getOrders().get(i).getStore_id());
                        map.put(KEY_ORDERS_IS_CARD, item.getOrders().get(i).getIs_card());
                        map.put(KEY_ORDERS_TAKEN, item.getOrders().get(i).getTaken());
                        map.put(KEY_ORDERS_IS_BCC_CARD, item.getOrders().get(i).getIs_bcc_card());
                        map.put(KEY_ORDERS_TOTAL, item.getOrders().get(i).getTotal());
                        map.put(KEY_ORDERS_STATUS, item.getOrders().get(i).getStatus());
                        map.put(KEY_ORDERS_STATUS_ID, item.getOrders().get(i).getStatus_id());
                        map.put(KEY_ORDERS_DELIVERY_TYPE, item.getOrders().get(i).getDelivery_type());
                        map.put(KEY_ORDERS_DELIVERY_PRICE, item.getOrders().get(i).getDelivery_price());
                        map.put(KEY_ORDERS_PHONE, item.getOrders().get(i).getClient_phone());

                        HashMap<String, ArrayList<getItems>> map2 = new HashMap<String, ArrayList<getItems>>();
                        map2.put(KEY_ORDERS_ITEMS_Title, item.getOrders().get(i).getItems());
                        inItems.add(map2);

                        // adding HashList to ArrayList
                        menuItems.add(map);
                    }

                }
            }
            @Override
            public void onFailure(Call<getJson> call, Throwable t) {
                Toast toast = Toast.makeText(MainActivity.this,t.getLocalizedMessage(),Toast.LENGTH_LONG);
                toast.show();
            }
        });

        adapter = new ListViewAdapter(MainActivity.this, menuItems);
        lv.setAdapter(adapter);

        ///////sending data to SingleMenuActivity
        lv.setOnItemClickListener(new OnItemClickListener() {


            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                int pos = detailClick(view);
                String name = ((TextView) view.findViewById(R.id.title))
                        .getText().toString();
                String address = ((TextView) view.findViewById(R.id.address))
                        .getText().toString();
                String total = ((TextView) view.findViewById(R.id.total))
                        .getText().toString();
                String status = ((TextView) view.findViewById(R.id.status))
                        .getText().toString();
                String OrderId = ((TextView) view.findViewById(R.id.id))
                        .getText().toString();
                String StatusId = ((TextView) view.findViewById(R.id.status_id))
                        .getText().toString();
                String Taken = ((TextView) view.findViewById(R.id.taken))
                        .getText().toString();
                String DeliveryPrice = ((TextView) view.findViewById(R.id.delivery_price))
                        .getText().toString();
                Intent in = new Intent(MainActivity.this,
                        SingleMenuItemActivity.class);
                in.putExtra("isBusy", isBusy);
                in.putExtra("username", username);
                in.putExtra("runner",menuItems.get(position).get("runner"));
                in.putExtra(KEY_ORDERS_ITEMS, inItems);
                in.putExtra(KEY_ORDERS_NAME, name);
                in.putExtra(KEY_ORDERS_TAKEN, Taken);
                in.putExtra(KEY_ORDERS_DELIVERY_PRICE, DeliveryPrice);
                if (StatusId.length()>0){
                    in.putExtra(KEY_ORDERS_STATUS_ID, Integer.parseInt(StatusId));
                }
                in.putExtra("position", pos);
                in.putExtra("token", UserToken);
                in.putExtra(KEY_ORDERS_ADDRESS, address);
                in.putExtra(KEY_ORDERS_STATUS, status);
                in.putExtra(KEY_ORDERS_TOTAL, total);
                in.putExtra(KEY_ORDERS_ID, OrderId);
                startActivity(in);
            }
        });
    }

    public int detailClick(View v){
        ListView lv = (ListView) findViewById(R.id.list);
        int position = lv.getPositionForView(v);
        return position;
    }

    private class logOut extends AsyncTask<Void,Void,Void>{
        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(
                    MainActivity.this);
            pDialog.setMessage("Подождите..");
            pDialog.setIndeterminate(true);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            UserDb db = UserDb.getInstance(MainActivity.this);
            db.getUserDao().clearDb();
            Intent in = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(in);
            finish();
            return null;
        }
    }

    private class profile extends AsyncTask<Void,Void,Void>{
        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(
                    MainActivity.this);
            pDialog.setMessage("Подождите..");
            pDialog.setIndeterminate(true);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            Retrofit retrofit = NetworkClient.getRetrofitClient();
            JSONPlaceHolderApi jp = retrofit.create(JSONPlaceHolderApi.class);

            Call<getProfile> call = jp.getProfile(UserToken);
            call.enqueue(new Callback<getProfile>() {
                @Override
                public void onResponse(Call<getProfile> call, Response<getProfile> response) {
                    if (response.isSuccessful()){
                        String hasOrder = "false";
                        Intent in = new Intent(MainActivity.this,
                                ProfileActivity.class);
                        getProfile profile = response.body();
                        ArrayList<HashMap<String, ArrayList<getItems>>> inItems = new ArrayList<HashMap<String,ArrayList<getItems>>>();
                        ArrayList<HashMap<String, String>> order = new ArrayList<HashMap<String,String>>();
                        HashMap<String, ArrayList<getItems>> items = new HashMap<String, ArrayList<getItems>>();
                        HashMap<String, String> map = new HashMap<String, String>();
                        if(profile.getOrder()!=null){
                            map.put("order_id", profile.getOrder().getId());
                            map.put("runner", profile.getOrder().getRunner());
                            map.put("client_name", profile.getOrder().getName());
                            map.put("client_address", profile.getOrder().getAddress());
                            map.put("date", profile.getOrder().getDate());
                            map.put("taken", profile.getOrder().getTaken());
                            map.put("total", profile.getOrder().getTotal());
                            map.put("status", profile.getOrder().getStatus());
                            map.put("status_id", profile.getOrder().getStatus_id());
                            map.put("delivery_price", profile.getOrder().getDelivery_price());
                            map.put("client_phone", profile.getOrder().getClient_phone());
                            order.add(map);
                            items.put("items", profile.getOrder().getItems());
                            inItems.add(items);
                            in.putExtra("order", order);
                            in.putExtra("items", inItems);
                            hasOrder = "true";
                        }
                        in.putExtra("name", profile.getName());
                        in.putExtra("hasOrder",hasOrder);
                        in.putExtra("id", profile.getId());
                        in.putExtra("rating", profile.getRating());
                        in.putExtra("money", profile.getMoney());

                        in.putExtra("token", UserToken);
                        startActivity(in);
                        finish();
                    }
                }

                @Override
                public void onFailure(Call<getProfile> call, Throwable t) {

                }
            });
            return null;
        }
    }

    private class loadMoreListView extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(
                    MainActivity.this);
            pDialog.setMessage("Подождите..");
            pDialog.setIndeterminate(true);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected Void doInBackground(Void... unused) {
            runOnUiThread(new Runnable() {
                public void run() {
                    current_page += 1;

                    Retrofit retrofit = NetworkClient.getRetrofitClient();
                    JSONPlaceHolderApi jp = retrofit.create(JSONPlaceHolderApi.class);
                    Call<getJson> call = jp.getOrders(UserToken, current_page);

                    //check if next page exists
                    Call<getJson> call2 = jp.getOrders(UserToken, current_page+1);
                    /////
                    call.enqueue(new Callback<getJson>() {
                        @Override
                        public void onResponse(Call<getJson> call, Response<getJson> response) {
                            if (response.isSuccessful()){
                                getJson item = response.body();
                                if (!response.body().getOrders().isEmpty()){
                                    for (int i = 0; i < item.getOrders().size(); i++) {
                                        // creating new HashMap
                                        HashMap<String, String> map = new HashMap<String, String>();
                                        map.put(KEY_ORDERS_ID, item.getOrders().get(i).getId());
                                        map.put(KEY_ORDERS_RUNNER, item.getOrders().get(i).getRunner());
                                        map.put(KEY_ORDERS_NAME, item.getOrders().get(i).getName());
                                        map.put(KEY_ORDERS_ADDRESS, item.getOrders().get(i).getAddress());
                                        map.put(KEY_ORDERS_DATE, item.getOrders().get(i).getDate());
                                        map.put(KEY_ORDERS_STORE_ID, item.getOrders().get(i).getStore_id());
                                        map.put(KEY_ORDERS_IS_CARD, item.getOrders().get(i).getIs_card());
                                        map.put(KEY_ORDERS_TAKEN, item.getOrders().get(i).getTaken());
                                        map.put(KEY_ORDERS_IS_BCC_CARD, item.getOrders().get(i).getIs_bcc_card());
                                        map.put(KEY_ORDERS_TOTAL, item.getOrders().get(i).getTotal());
                                        map.put(KEY_ORDERS_STATUS, item.getOrders().get(i).getStatus());
                                        map.put(KEY_ORDERS_STATUS_ID, item.getOrders().get(i).getStatus_id());
                                        map.put(KEY_ORDERS_DELIVERY_TYPE, item.getOrders().get(i).getDelivery_type());
                                        map.put(KEY_ORDERS_DELIVERY_PRICE, item.getOrders().get(i).getDelivery_price());
                                        map.put(KEY_ORDERS_PHONE, item.getOrders().get(i).getClient_phone());

                                        HashMap<String, ArrayList<getItems>> map2 = new HashMap<String, ArrayList<getItems>>();
                                        map2.put(KEY_ORDERS_ITEMS_Title, item.getOrders().get(i).getItems());
                                        inItems.add(map2);


                                        // adding HashList to ArrayList
                                        menuItems.add(map);
                                    }
                                    int currentPosition = lv.getFirstVisiblePosition();

                                    // Appending new data to menuItems ArrayList
                                    adapter = new ListViewAdapter(
                                            MainActivity.this,
                                            menuItems);
                                    lv.setAdapter(adapter);


                                    // Setting new scroll position
                                    lv.setSelectionFromTop(currentPosition + 1, 0);
                                }
                                ///// remove LoadMore button if next page is empty
                                call2.enqueue(new Callback<getJson>() {
                                    @Override
                                    public void onResponse(Call<getJson> call, Response<getJson> response) {
                                        if (response.isSuccessful()){
                                            getJson item = response.body();
                                            if (response.body().getOrders().isEmpty()){
                                                lv.removeFooterView(btnLoadMore);
                                            }
                                            pDialog.cancel();
                                        }
                                    }
                                    @Override
                                    public void onFailure(Call<getJson> call, Throwable t) { }
                                });
                                //////////////
                            }
                        }
                        @Override
                        public void onFailure(Call<getJson> call, Throwable t) {
                            Toast toast = Toast.makeText(getApplicationContext(), t.getLocalizedMessage(), Toast.LENGTH_LONG);
                            pDialog.cancel();
                            toast.show();
                        }
                    });
                }
            });

            return (null);
        }
    }
}