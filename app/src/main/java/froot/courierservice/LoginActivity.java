package froot.courierservice;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.io.IOException;
import java.util.List;

import froot.courierservice.LocationService.LocationService;
import froot.courierservice.db.User;
import froot.courierservice.db.UserDao;
import froot.courierservice.db.UserDb;
import froot.courierservice.retorfit.JSONPlaceHolderApi;
import froot.courierservice.retorfit.NetworkClient;
import froot.courierservice.retorfit.Post;
import froot.courierservice.retorfit.PostToken;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static android.content.ContentValues.TAG;

public class LoginActivity extends Activity {
    String token = null;
    ProgressDialog pDialog;
    String name, pass;
    UserDb userDB;
    User user;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        userDB = UserDb.getInstance(this);
        new autoLogin().execute();
    }

    public void login(View view) throws IOException, InterruptedException
    {
        new loginAction().execute();
    }

    private class autoLogin extends AsyncTask<Void, Void, Void>{
        @Override
        public void onPreExecute(){
            pDialog = new ProgressDialog(
                    LoginActivity.this);
            pDialog.setMessage("Подождите..");
            pDialog.setIndeterminate(true);
            pDialog.setCancelable(false);
            pDialog.show();
        }
        public Void doInBackground(Void...unused){
            if (userDB.getUserDao().getUsers().isEmpty()){
                pDialog.cancel();
                this.cancel(true);
            }

            List<User> users = userDB.getUserDao().getUsers();

            name = users.get(0).getLogin();
            pass = users.get(0).getPassword();

            Retrofit retrofit = NetworkClient.getRetrofitClient();
            JSONPlaceHolderApi jp = retrofit.create(JSONPlaceHolderApi.class);
            Post post = new Post(name, pass);
            Call<Post> call = jp.postLogin(post);

            call.enqueue(new Callback<Post>() {
                @Override
                public void onResponse(Call<Post> call, Response<Post> response) {
                    if (response.isSuccessful()) {
                        Post postResponce = response.body();
                        token = postResponce.getToken();

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra("token", token);
                        startActivity(intent);
                        finish();
                    } else {
                        Log.i("msg",response.toString());
                        pDialog.cancel();
                        Toast.makeText(LoginActivity.this, "Неверное имя пользователя или пароль!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Post> call, Throwable t) {
                    Log.i("error", t.getMessage());
                    Log.d("here","here");
                }
            });
            return null;
        }
    }

    private class loginAction extends AsyncTask<Void, Void, Void> {
        @Override
        public void onPreExecute(){

            final EditText username = findViewById(R.id.username);
            final EditText password = findViewById(R.id.password);

            name = username.getText().toString();
            pass = password.getText().toString();

            pDialog = new ProgressDialog(
                    LoginActivity.this);
            pDialog.setMessage("Подождите..");
            pDialog.setIndeterminate(true);
            pDialog.setCancelable(false);
            pDialog.show();
        }
        public Void doInBackground(Void...unused){

            Retrofit retrofit = NetworkClient.getRetrofitClient();
            JSONPlaceHolderApi jp = retrofit.create(JSONPlaceHolderApi.class);

            Post post = new Post(name, pass);
            user = new User(1,name,pass);

            userDB.getUserDao().insert(user);
            Call<Post> call = jp.postLogin(post);

            call.enqueue(new Callback<Post>() {
                @Override
                public void onResponse(Call<Post> call, Response<Post> response) {
                    if (response.isSuccessful()) {
                        Post postResponce = response.body();
                        token = postResponce.getToken();
                        deviceToken(token);
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra("token", token);
                        startActivity(intent);
                        finish();
                    } else {
                        Log.i("msg",response.toString());
                        pDialog.cancel();
                        Toast.makeText(LoginActivity.this, "Неверное имя пользователя или пароль!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Post> call, Throwable t) {
                    Log.i("error", t.getMessage());
                }
            });
            return null;
        }
        protected void onPostExecute(Void unused) {
            // closing progress dialog
            pDialog.dismiss();
        }

        public void deviceToken(String t){
            FirebaseInstanceId.getInstance().getInstanceId()
                    .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                        @Override
                        public void onComplete(@NonNull Task<InstanceIdResult> task) {
                            if (!task.isSuccessful()) {
                                Log.w(TAG, "getInstanceId failed", task.getException());
                                return;
                            }
                            // Get new Instance ID token
                            String token = task.getResult().getToken();
                            PostToken deviceToken = new PostToken(token);
                            Retrofit retrofit = NetworkClient.getRetrofitClient();
                            JSONPlaceHolderApi jp = retrofit.create(JSONPlaceHolderApi.class);
                            Call<PostToken> call = jp.postDeviceToken(t, deviceToken);
                            Log.i("devicetoken", t+" /// "+deviceToken);
                            call.enqueue(new Callback<PostToken>() {
                                @Override
                                public void onResponse(Call<PostToken> call, Response<PostToken> response) {
                                    Log.i("response", response.message());
                                }
                                @Override
                                public void onFailure(Call<PostToken> call, Throwable t) { }
                            });
                        }
                    });
        }
    }
}


