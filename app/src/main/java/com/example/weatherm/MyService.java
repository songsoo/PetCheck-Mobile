package com.example.weatherm;
import android.app.Service;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.util.Random;

public class MyService extends Service {
    String TAG = "movmov";
    ConnectedThread connectedThread;
    FirebaseDatabase database;
    DatabaseReference rmssd_ref;
    public static double res;
    public static int RMSSD_count;

    SharedPreferences sf;
    SharedPreferences.Editor editor;

    private Messenger mClient = null;

    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return mMessenger.getBinder();    }


    public void onCreate(){
        super.onCreate();
    }

    public int onStartCommand(Intent intent, int flags, int startId){
        if(intent == null){
            return Service.START_STICKY;
        }else{
            String command = intent.getStringExtra("command");
            String name = intent.getStringExtra("name");String btString = intent.getStringExtra("btSocket");

            connectedThread = new ConnectedThread(BluetoothConnect.btSocket);
            connectedThread.start();
        }

        IntentFilter date = new IntentFilter(Intent.ACTION_DATE_CHANGED);
        DateTickReceiver receiver = new DateTickReceiver();
        registerReceiver(receiver,date);

        sf = getSharedPreferences("prevData",MODE_PRIVATE);
        editor = sf.edit();

        return super.onStartCommand(intent,flags,startId);
    }

    private class DateTickReceiver extends BroadcastReceiver{
        @RequiresApi(api = Build.VERSION_CODES.O)
        public void onReceive(Context arg0, Intent intent){
            if(intent.getAction().equals(Intent.ACTION_DATE_CHANGED)){
                changeData(1);
            }
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onDestroy(){
        super.onDestroy();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void saveData(int prev,int count, double res){
        database = FirebaseDatabase.getInstance();
        rmssd_ref = database.getReference("UserData");

        LocalDate now = LocalDate.now();
        rmssd_ref.child("myIDExample").child("RMSSD").child(now.minusDays(prev).toString()).setValue("/"+Integer.toString(count)+"/"+ Double.toString(res));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void changeData(int prev){
        database = FirebaseDatabase.getInstance();
        rmssd_ref = database.getReference("UserData");

        LocalDate now = LocalDate.now();
        rmssd_ref.child("myIDExample").child("RMSSD").addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getValue() != null) {
                    String result[] = snapshot.getValue().toString().split("/");
                    int server_count = Integer.valueOf(result[0]);
                    double server_rmssd = Double.valueOf(result[1]);
                    double server_res = server_count*server_rmssd;
                    int totalCount = server_count + RMSSD_count;
                    double totalRes = server_res + res;
                    saveData(prev,totalCount,totalRes/totalCount);
                }else{
                    int totalCount = RMSSD_count;
                    double totalRes = res;
                    saveData(prev,totalCount,totalRes/totalCount);
                }
                RMSSD_count = 0;
                res = 0;
                editor.putInt("count",0);
                editor.commit();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("movmov","nein");
            }
        });
    }

    final Handler handler = new Handler(){
        public void handleMessage(Message msg){
            Bundle bundle = msg.getData();
            sendMsgToActivity(0,bundle.getString("result"));
        }
    };

    private final Messenger mMessenger = new Messenger(new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            Log.w(TAG,"ControlService - message what : "+msg.what +" , msg.obj "+ msg.obj);
            mClient = msg.replyTo;
            return false;
        }
    }));

    private void sendMsgToActivity(int sendValue,String value) {
        try {
            Bundle bundle = new Bundle();
            bundle.putString("test",value);
            Message msg = Message.obtain(null, 0);
            msg.setData(bundle);
            mClient.send(msg);
        } catch (RemoteException e) {
        }
    }
    public class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
            write("1");
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void run() {
            byte[] buffer = new byte[1024];  // buffer store for the stream
            int bytes; // bytes returned from read()


            // Keep listening to the InputStream until an exception occurs

            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.available();
                    if (bytes != 0) {
                        buffer = new byte[1024];
                        SystemClock.sleep(100); //pause and wait for rest of data. Adjust this depending on your sending speed.
                        bytes = mmInStream.available(); // how many bytes are ready to be read
                        bytes = mmInStream.read(buffer, 0, bytes); // record how many bytes we actually read
                        String result = new String(buffer, 0, bytes);
                        Message msg = handler.obtainMessage();
                        Bundle bundle = new Bundle();
                        bundle.putString("result",result);
                        msg.setData(bundle);
                        handler.sendMessage(msg);
                        write("1");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }

        /* Call this from the main activity to send data to the remote device */
        public void write(String input) {
            byte[] bytes = input.getBytes();           //converts entered String into bytes
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) {
            }
        }

        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
            }
        }
    }

}