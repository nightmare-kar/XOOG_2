package com.karrit.xoog;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MyFirebaseInstanceIdService extends FirebaseMessagingService {
    private String TAG="FirebaseMessaging";
    shared share;
    private RemoteMessage remote;
    account_details accountDetails;


    @Override
    public void onNewToken(@NonNull String s) {
        String referredToken=s;
    Log.i(TAG, "Refreshed token: " + s);
    sendTokenToServer(this,referredToken);
        share=new shared(this);
    share.setToken(s);
    share.apply();
    Log.i(TAG,"share" +share.getToken());

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
       // sendRegistrationToServer(s);

    }
    @SuppressLint("WrongThread")
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        Log.i(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.i(TAG, "Message data payload: " + remoteMessage.getData());

            if (/* Check if data needs to be processed by long running job */ true) {
                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
                // scheduleJob();
            } else {
                // Handle message within 10 seconds
                //  handleNow();
            }

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            remote=remoteMessage;
            Log.i(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            download download_task=new download();
            download_task.execute(remoteMessage.getNotification().getImageUrl().toString());

        }


    }
    public void DisplayFn(RemoteMessage remoteMessage,Bitmap result){
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        String channelId = "Default";
        NotificationCompat.Builder builder = new  NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.mipmap.xoog)
                .setContentTitle(remoteMessage.getNotification().getTitle())
                .setLargeIcon(result)
                .setStyle(new NotificationCompat.BigPictureStyle()
                        .bigPicture(result)
                        .bigLargeIcon(null))
                .setContentText(remoteMessage.getNotification().getBody()).setAutoCancel(true).setContentIntent(pendingIntent);;
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "Default channel", NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(channel);
        }
        manager.notify(0, builder.build());
    }
    public void sendTokenToServer(Context context,final String token){
       share=new shared(context);
       Log.i(TAG,"share kid"+share.getKid1_id());
        account_details accountDetails=new account_details(context,share.getKid1_id());
        Log.i(TAG,"phone"+accountDetails.getPhone_number());

        FirebaseFirestore.getInstance().collection("phone_num").whereEqualTo("phone_number",accountDetails.getPhone_number()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.getResult().isEmpty()){
                      Log.i(TAG,"result is empty");
                }else {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d(TAG, document.getId() + " => " + document.getData());
                        document.getReference().update("fcm_token",token).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Log.i(TAG,"update completed");
                            }
                        });
                        }
                    }
                }
            }
        );
    }

  /*  private void sendNotification(String messageBody) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code , intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_stat_ic_notification)
                        .setContentTitle(getString(R.string.fcm_message))
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0 /* ID of notification , notificationBuilder.build());
    }*/
  public class download extends AsyncTask<String, Void, Bitmap>{
      String message;
      @Override
      protected Bitmap doInBackground(String... params) {
          InputStream in;

          try {

              URL url = new URL(params[0]);
              HttpURLConnection connection = (HttpURLConnection)url.openConnection();
              connection.setDoInput(true);
              connection.connect();
              in = connection.getInputStream();
              Bitmap myBitmap = BitmapFactory.decodeStream(in);
              return myBitmap;
          } catch (MalformedURLException e) {
              e.printStackTrace();
          } catch (IOException e) {
              e.printStackTrace();
          }
         return null;
      }

      @Override
      protected void onPostExecute(Bitmap bitmap) {
          DisplayFn(remote,bitmap);
          super.onPostExecute(bitmap);
      }
  }

}
