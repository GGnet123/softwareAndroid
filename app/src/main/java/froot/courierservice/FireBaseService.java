package froot.courierservice;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import froot.courierservice.retorfit.JSONPlaceHolderApi;
import froot.courierservice.retorfit.NetworkClient;
import froot.courierservice.retorfit.PostToken;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static android.content.ContentValues.TAG;

public class FireBaseService extends FirebaseMessagingService {
    String t;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        showNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
        MainActivity activity = MainActivity.instance;
        SingleMenuItemActivity single_activity = SingleMenuItemActivity.instance;
        try {
            if (!single_activity.isFront){
                activity.finish();
                startActivity(activity.getIntent());
            }
            single_activity.notification();
        } catch (NullPointerException e){
            activity.finish();
            startActivity(activity.getIntent());
        }
    }
    public void showNotification(String title, String message){
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel("1", "default",
                        NotificationManager.IMPORTANCE_HIGH);
                channel.setDescription("default channel");
                channel.enableVibration(true);
                notificationManager.createNotificationChannel(channel);
            }

            Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        final long[] DEFAULT_VIBRATE_PATTERN = {250, 250, 250, 250};
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,"1")
                .setContentTitle(title)
                .setSmallIcon(R.drawable.frooticon)
                .setAutoCancel(true)
                .setSound(uri)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVibrate(DEFAULT_VIBRATE_PATTERN)
                .setContentText(message);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1, builder.build());
    }
}
