package com.example.weatherm;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.example.weatherm.MainActivity;
import com.example.weatherm.MyService;

public class MyService2 extends Service {
   static  public  String MESSAGE_KEY="";
    String NOTIFICATION_CHANNEL_ID = "my_channel_id_01";
    NotificationManager notificationManager;
    public MyService2() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Intent intent1=new Intent(this, MainActivity.class);
        intent1.putExtra("flag",1);
        PendingIntent pendingIntent=PendingIntent.getActivity(this,1,intent1,PendingIntent.FLAG_IMMUTABLE);
        createNotificationChannel();
        boolean message=intent.getExtras().getBoolean(MyService2.MESSAGE_KEY);
        if(message){
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
            notificationBuilder.setSmallIcon(android.R.drawable.btn_radio)
                     .setContentTitle("Dog_Check")
                     .setContentText("산책을 하고있어요!!!")
                     .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                     .setAutoCancel(true);



            notificationManager.notify(0,notificationBuilder.build());

            Toast.makeText(MyService2.this,"서비스실행",Toast.LENGTH_SHORT);
            Log.d("서비스","서비스");
        }else
        {
            Toast.makeText(MyService2.this,"서비스중지",Toast.LENGTH_SHORT);
            Log.d("서비스","서비스중지");
        }

        return START_NOT_STICKY;
    }
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "good", importance);
            channel.setDescription("description");
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}