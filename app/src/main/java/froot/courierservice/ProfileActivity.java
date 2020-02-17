package froot.courierservice;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.Nullable;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import froot.courierservice.retorfit.JSONPlaceHolderApi;
import froot.courierservice.retorfit.NetworkClient;
import froot.courierservice.retorfit.PostOrder;
import froot.courierservice.retorfit.getItems;
import froot.courierservice.retorfit.getOrders;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static android.view.View.GONE;

public class ProfileActivity extends Activity {
    TextView lblStatus;
    ProgressDialog pDialog;
    String token;
    int id;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_view);

        Intent in = getIntent();
        String rating = in.getStringExtra("rating");
        String money = in.getStringExtra("money");
        String name = in.getStringExtra("name");

        lblStatus = findViewById(R.id.profile_order_status);
        TextView runner_id = findViewById(R.id.runner_id);
        TextView order_title = findViewById(R.id.profile_order_title);
        TextView taken = findViewById(R.id.profile_taken);
        TextView total = findViewById(R.id.profile_sum);
        TextView delivery = findViewById(R.id.profile_delivery_price);
        TextView runner_money = findViewById(R.id.runner_money);
        TextView runner_rating = findViewById(R.id.runner_rating);
        TextView client_name = findViewById(R.id.profile_client_name);
        TextView client_phone = findViewById(R.id.profile_client_phone);
        TextView client_address = findViewById(R.id.profile_client_address);
        ListView lv = findViewById(R.id.profile_list);

        Button done = findViewById(R.id.profile_done);
        Button refuse = findViewById(R.id.profile_refuse);

        token = in.getStringExtra("token");
        if (in.getStringExtra("hasOrder").equals("true")){
            ArrayList<HashMap<String, ArrayList<getItems>>> inItems = (ArrayList<HashMap<String, ArrayList<getItems>>>) in.getSerializableExtra("items");
            ArrayList<HashMap<String,String>> order = (ArrayList<HashMap<String, String>>) in.getSerializableExtra("order");
            id = Integer.parseInt(order.get(0).get("order_id"));

            if (!order.isEmpty()){
                order_title.setText("Информация о заказе:");
                client_name.setText("Имя клиента: " + order.get(0).get("client_name"));
                client_phone.setText("Номер клиента: " + order.get(0).get("client_phone"));
                client_address.setText("Адресс клиента: " + order.get(0).get("client_address"));
                taken.setText("Сдача с: " + order.get(0).get("taken"));
                delivery.setText("Доставка: " + order.get(0).get("delivery_price"));
                total.setText("Сумма: " + order.get(0).get("total"));

                ArrayAdapter adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1);
                for (int i = 0; i<inItems.get(0).get("items").size();i++){
                    String str = inItems.get(0).get("items").get(i).getTitle() + "\n"
                            + "Кол-во:" + inItems.get(0).get("items").get(i).getCnt() + "\n"
                            + "Штрих-код: " + inItems.get(0).get("items").get(i).getBarcode();
                    adapter.add(str);
                }
                lv.setAdapter(adapter);
            }
        }
        else {
            done.setVisibility(GONE);
            refuse.setVisibility(GONE);
        }
        runner_id.setText("Курьер: " + name);
        runner_rating.setText("Рейтинг: " + rating);
        runner_money.setText("Баланс: " + money);

        findViewById(R.id.cross_line).setVisibility(GONE);

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new doneBtn().execute();
            }
        });

    }
    public class doneBtn extends AsyncTask<Void,Void,Void> {
        @Override
        protected void onPostExecute(Void aVoid) {
            pDialog = new ProgressDialog(
                    ProfileActivity.this);
            pDialog.setMessage("Подождите..");
            pDialog.setIndeterminate(true);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            final Button btnAccept = (Button) findViewById(R.id.profile_done);
            final Button btnRefuse = (Button) findViewById(R.id.profile_refuse);
            Retrofit retrofit = NetworkClient.getRetrofitClient();
            JSONPlaceHolderApi jp = retrofit.create(JSONPlaceHolderApi.class);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    PostOrder post = new PostOrder(id,3);
                    Call<PostOrder> call = jp.postOrderStatus(token, post);
                    call.enqueue(new Callback<PostOrder>() {
                        @Override
                        public void onResponse(Call<PostOrder> call, Response<PostOrder> response) {
                            btnAccept.setVisibility(GONE);
                            btnRefuse.setVisibility(GONE);
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

    private class refuseBtn extends AsyncTask<Void,Void,Void> {
        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(
                    ProfileActivity.this);
            pDialog.setMessage("Подождите..");
            pDialog.setIndeterminate(true);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            final Button btnAccept = (Button) findViewById(R.id.profile_done);
            final Button btnRefuse = (Button) findViewById(R.id.profile_refuse);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    PostOrder post = new PostOrder(id,4);
                    Retrofit retrofit = NetworkClient.getRetrofitClient();
                    JSONPlaceHolderApi jp = retrofit.create(JSONPlaceHolderApi.class);
                    Call<PostOrder> call = jp.postOrderStatus(token,post);
                    call.enqueue(new Callback<PostOrder>() {
                        @Override
                        public void onResponse(Call<PostOrder> call, Response<PostOrder> response) {
                            btnRefuse.setVisibility(GONE);
                            btnAccept.setVisibility(GONE);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent in = new Intent(this,MainActivity.class);
        in.putExtra("token", token);
        startActivity(in);
    }
}
