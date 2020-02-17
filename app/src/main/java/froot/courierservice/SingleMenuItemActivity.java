package froot.courierservice;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.HashMap;
import froot.courierservice.retorfit.JSONPlaceHolderApi;
import froot.courierservice.retorfit.NetworkClient;
import froot.courierservice.retorfit.PostOrder;
import froot.courierservice.retorfit.getItems;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SingleMenuItemActivity  extends Activity {
    ArrayAdapter adapter;
    ProgressDialog pDialog;
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
    static final String KEY_ORDERS_ITEMS_BARCODE = "Barcode";
    static final String KEY_ORDERS_ITEMS_Price = "Price";
    static final String KEY_ORDERS_ITEMS_Cnt = "Cnt";
    static final String KEY_ORDERS_ITEMS_Title = "Title";
    static final String KEY_ORDERS_ITEMS_Grams = "Grams";
    ListView lv;
    int fId;
    String token;
    JSONPlaceHolderApi jp;
	boolean btnStatus = false;
	boolean btnClicked = false;
	boolean isFront = false;
	static SingleMenuItemActivity instance;
	///listview items
    ArrayList<HashMap<String, String>> menuItems;

    TextView lblTitle;
    TextView lblDesc;
    TextView lblTotal;
    TextView lblStatus;
    TextView lblOrderId;
    TextView lblTaken;
    TextView lblDeliveryPrice;
    int isBusy;
    @Override
    protected void onStart() {
        super.onStart();
        isFront = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        isFront = false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_list_item);

        instance = this;

        Intent in = getIntent();

        ArrayList<HashMap<String, ArrayList<getItems>>> items = (ArrayList<HashMap<String, ArrayList<getItems>>>) getIntent().getSerializableExtra(KEY_ORDERS_ITEMS);
        int position = in.getIntExtra("position",0);
        token = in.getStringExtra("token");
        String name = in.getStringExtra(KEY_ORDERS_NAME);
        String address = in.getStringExtra(KEY_ORDERS_ADDRESS);
        String total = in.getStringExtra(KEY_ORDERS_TOTAL);
        String status = in.getStringExtra(KEY_ORDERS_STATUS);
        String id = in.getStringExtra(KEY_ORDERS_ID);
        String taken = in.getStringExtra(KEY_ORDERS_TAKEN);
        String delivery_price = in.getStringExtra(KEY_ORDERS_DELIVERY_PRICE);
        int StatusId = in.getIntExtra(KEY_ORDERS_STATUS_ID,4);

        String[] nId = id.split("#");
        fId = Integer.parseInt(nId[1]);
        lblTitle = (TextView) findViewById(R.id.title_label);
        lblDesc = (TextView) findViewById(R.id.description_label);
        lblTotal = (TextView) findViewById(R.id.total_label);
        lblStatus = (TextView) findViewById(R.id.status_label);
        lblOrderId = (TextView) findViewById(R.id.headerInOrder);
        lblTaken = (TextView) findViewById(R.id.taken_label);
        lblDeliveryPrice = (TextView) findViewById(R.id.delivery_price_label);

        lblTitle.setText(name);
        lblDesc.setText(address);
        lblTotal.setText(total);
        lblStatus.setText(status);
        lblOrderId.setText("Заказ номер: " + id);
        lblTaken.setText(taken);
        lblDeliveryPrice.setText(delivery_price);

        Retrofit retrofit = NetworkClient.getRetrofitClient();
        jp = retrofit.create(JSONPlaceHolderApi.class);
        
        final Button btnAccept = (Button) findViewById(R.id.accept);
        final Button btnRefuse = (Button) findViewById(R.id.refuse);

//        TextView runner = findViewById(R.id.runner);
        String runner = in.getStringExtra("runner");
        String username = in.getStringExtra("username");

        if (isBusy==1 && !runner.equals(username)){
            btnAccept.setVisibility(View.GONE);
            btnRefuse.setVisibility(View.GONE);
        }

        if (isBusy==0 && !runner.equals(username)){
            btnAccept.setVisibility(View.GONE);
            btnRefuse.setVisibility(View.GONE);
        }

        if (StatusId==3 || StatusId==4){
            btnAccept.setVisibility(View.GONE);
            btnRefuse.setVisibility(View.GONE);
        }

        ///listview
        menuItems = new ArrayList<>();

        ////set adapter
        lv = (ListView) findViewById(R.id.list2);
        adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1);
        lv.setAdapter(adapter);

        ///get data
        for (int i = 0; i<items.get(position).get(KEY_ORDERS_ITEMS_Title).size();i++){
            String product = items.get(position).get(KEY_ORDERS_ITEMS_Title).get(i).getTitle() + "\n" + "Кол-во: " + items.get(position).get(KEY_ORDERS_ITEMS_Title).get(i).getCnt() + "шт"+"\n"+"Штрихкод: " + items.get(position).get(KEY_ORDERS_ITEMS_Title).get(i).getBarcode();
            adapter.add(product);
        }
        
        ///accept button
        if (StatusId!=2){
            btnAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new acceptBtn().execute();
                }
            });
        }

        if (StatusId==2){
            btnAccept.setText("Завершить заказ");
            btnAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new doneBtn().execute();
                }
            });

        }


        btnRefuse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new refuseBtn().execute();
            }
        });

        Button back = (Button) findViewById(R.id.backBtn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                if (btnClicked){
                    MainActivity activity = MainActivity.instance;
                    activity.finish();
                    startActivity(activity.getIntent());
                }
            }
        });

    }

    private class refuseBtn extends AsyncTask<Void,Void,Void> {
        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(
                    SingleMenuItemActivity.this);
            pDialog.setMessage("Подождите..");
            pDialog.setIndeterminate(true);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            final Button btnAccept = (Button) findViewById(R.id.accept);
            final Button btnRefuse = (Button) findViewById(R.id.refuse);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    btnClicked = true;
                    PostOrder post = new PostOrder(fId,4);
                    Call<PostOrder> call = jp.postOrderStatus(token,post);
                    call.enqueue(new Callback<PostOrder>() {
                        @Override
                        public void onResponse(Call<PostOrder> call, Response<PostOrder> response) {
                            btnRefuse.setVisibility(View.GONE);
                            btnAccept.setVisibility(View.GONE);
                            lblStatus.setText("Отменено");
                            pDialog.dismiss();
                        }

                        @Override
                        public void onFailure(Call<PostOrder> call, Throwable t) { }
                    });
                }
            });
            return null;
        }
    }

    private class acceptBtn extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(
                    SingleMenuItemActivity.this);
            pDialog.setMessage("Подождите..");
            pDialog.setIndeterminate(true);
            pDialog.setCancelable(false);
            pDialog.show();
        }
        protected Void doInBackground(Void... unused) {
            final Button btnAccept = (Button) findViewById(R.id.accept);
            final Button btnRefuse = (Button) findViewById(R.id.refuse);
            runOnUiThread(new Runnable() {
                public void run(){
                    btnClicked = true;
                    if (!btnStatus){
                        PostOrder post = new PostOrder(fId,2);
                        Call<PostOrder> call = jp.postOrderStatus(token,post);
                        call.enqueue(new Callback<PostOrder>() {
                            @Override
                            public void onResponse(Call<PostOrder> call, Response<PostOrder> response) {
                                btnStatus = !btnStatus;
                                btnAccept.setText("Завершить заказ");
                                lblStatus.setText("Принято");
                                pDialog.dismiss();
                            }
                            @Override
                            public void onFailure(Call<PostOrder> call, Throwable t) { }
                        });
                    }
                    if(btnStatus){
                        PostOrder post = new PostOrder(fId,3);
                        Call<PostOrder> call = jp.postOrderStatus(token,post);
                        call.enqueue(new Callback<PostOrder>() {
                            @Override
                            public void onResponse(Call<PostOrder> call, Response<PostOrder> response) {
                                btnAccept.setVisibility(View.GONE);
                                btnRefuse.setVisibility(View.GONE);
                                lblStatus.setText("Доставлено");
                                pDialog.dismiss();
                            }
                            @Override
                            public void onFailure(Call<PostOrder> call, Throwable t) { }
                        });
                    }

                }
            });
            return null;
        }
    }

    public class doneBtn extends AsyncTask<Void,Void,Void> {
        @Override
        protected void onPostExecute(Void aVoid) {
            pDialog = new ProgressDialog(
                    SingleMenuItemActivity.this);
            pDialog.setMessage("Подождите..");
            pDialog.setIndeterminate(true);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            final Button btnAccept = (Button) findViewById(R.id.accept);
            final Button btnRefuse = (Button) findViewById(R.id.refuse);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    btnClicked = true;
                    PostOrder post = new PostOrder(fId,3);
                    Call<PostOrder> call = jp.postOrderStatus(token,post);
                    call.enqueue(new Callback<PostOrder>() {
                        @Override
                        public void onResponse(Call<PostOrder> call, Response<PostOrder> response) {
                            btnAccept.setVisibility(View.GONE);
                            btnRefuse.setVisibility(View.GONE);
                            lblStatus.setText("Доставлено");
                            pDialog.dismiss();
                        }
                        @Override
                        public void onFailure(Call<PostOrder> call, Throwable t) { }
                    });
                }
            });
            return null;
        }
    }

    public void notification(){
        btnClicked = true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        if (btnClicked){
            MainActivity activity = MainActivity.instance;
            activity.finish();
            startActivity(activity.getIntent());
        }
    }
}
