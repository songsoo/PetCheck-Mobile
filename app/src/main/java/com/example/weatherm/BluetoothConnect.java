package com.example.weatherm;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.KeyboardShortcutGroup;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.Utils;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.UUID;

public class BluetoothConnect extends AppCompatActivity implements OnChartValueSelectedListener, SwipeRefreshLayout.OnRefreshListener {

    private static final int PERMISSION_ACCESS_FINE_LOCATION = 0;
    String TAG = "movmov";
    UUID BT_MODULE_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); // "random" unique identifier

    TextView textStatus, stressText, RMSSDText, runningDogText;
    Button btnMenu,getDefaultRMSSDBtn;
    ImageButton btnStateSnack, btnStateWalking, btnStateFood, btnStateAngry;
    ListView listView1, listView2;
    LineChart bpmChart, RMSSDChart;
    ImageView stressImg;
    Boolean isBluetoothConnected = false;
    Boolean isGettingDefaultRMSSD = false;
    Boolean isStartMeasure = false;
    Boolean wilcoxonMode = true;
    //int isGettingState=-1;
    int numGetDefaultRMSSD;
    //int numGetStateRMSSD;
    int RMSSDArrNum = 10;
    int totalRMSSDCount = 0;
    int totalBPMCount= 0;
    int BPMCount = 0;
    int BPMSum = 0;
    int[] BPMArr = new int[RMSSDArrNum];

    double BPMAvg = 0;
    double[] rmssdArr = new double[RMSSDArrNum];
    double[] defaultRMSSDArr = new double[RMSSDArrNum];
    double[] recentRMSSDArr = new double[RMSSDArrNum];
    double RMSSDSum=0;

    String[] stateString = {"Snack","Walk","Food","Angry"};

    double RMSSDAvg;
    int[] criticalRegion = {10,13,17,21,25,30,35,41,47,53,60,67,75,83,91,100,119,130,140,151};
    int criticalRegionDiff = 20;




    AppCompatDialog progressDialog;

    //Chart에 표시될 값 개수
    int count = 15;
    public static int bpm_i = 0;
    public static int RMSSD_i = 0;


    private Messenger mServiceMessenger = null;

    BluetoothAdapter btAdapter;
    Set<BluetoothDevice> pairedDevices;
    ArrayAdapter<String> btArrayAdapter1, btArrayAdapter2;
    ArrayList<String> deviceAddressArray1, deviceAddressArray2;
    DrawerLayout drawerLayout;
    SwipeRefreshLayout swipeRefreshLayout;

    AnimationDrawable animationDrawable;
    ImageView mProgressBar;


    public static ArrayList<Entry> bpm_values = new ArrayList<>();
    public static ArrayList<Entry> RMSSD_values = new ArrayList<>();


    private final static int REQUEST_ENABLE_BT = 1;
    public static BluetoothSocket btSocket = null;
    public static boolean flag = false;
    public static String name;
    public static double RMSSD = -1;
    public static int stressStatus = -1;

    SharedPreferences sf;
    SharedPreferences.Editor editor;

    FirebaseDatabase database;
    DatabaseReference rmssd_ref;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_connect);

        database = FirebaseDatabase.getInstance();
        rmssd_ref = database.getReference("UserData");

        checkPrev();

        setVariables();
        setBluetooth();
        createChart();
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        this.registerReceiver(mReceiver, filter);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (this.checkSelfPermission(Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setTitle("블루투스에 대한 액세스가 필요합니다");
                builder.setMessage("어플리케이션이 블루투스를 감지 할 수 있도록 위치 정보 액세스 권한을 부여하십시오.");
                builder.setPositiveButton(android.R.string.ok, null);

                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        requestPermissions(new String[]{Manifest.permission.BLUETOOTH_SCAN}, 2 );
                    }
                });
                builder.show();
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (this.checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setTitle("블루투스에 대한 액세스가 필요합니다");
                builder.setMessage("어플리케이션이 블루투스를 연결 할 수 있도록 위치 정보 액세스 권한을 부여하십시오.");
                builder.setPositiveButton(android.R.string.ok, null);

                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        requestPermissions(new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 3 );
                    }
                });
                builder.show();
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_ACCESS_FINE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("디버깅", "coarse location permission granted");
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("권한 제한");
                    builder.setMessage("위치 정보 및 액세스 권한이 허용되지 않았으므로 블루투스를 검색 및 연결할수 없습니다.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                        @Override
                        public void onDismiss(DialogInterface dialog) {
                        }

                    });
                    builder.show();
                }
                break;
            }
            case 2: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("디버깅", "coarse location permission granted");
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("권한 제한");
                    builder.setMessage("블루투스 스캔권한이 허용되지 않았습니다.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                        @Override
                        public void onDismiss(DialogInterface dialog) {
                        }

                    });
                    builder.show();
                }
                break;
            }
            case 3: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("디버깅", "coarse location permission granted");
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("권한 제한");
                    builder.setMessage("블루투스 연결 권한이 허용되지 않았습니다.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                        @Override
                        public void onDismiss(DialogInterface dialog) {
                        }

                    });
                    builder.show();
                }
                break;
            }
        }
        return;
    }





    public void checkPrev() {
        Log.d(TAG,"Check Prev");
        //SF 확인 후 아직 업로드되지 않은 값이 있다면 업로드
        sf = getSharedPreferences("prevData", MODE_PRIVATE);
        editor = sf.edit();
        int count = sf.getInt("RMSSDCount", 0);

        Log.d(TAG,"Check Prev, RMSSDCount"+Integer.toString(count));

        checkDefaultRMSSD();
        if (count != 0) {
            double res = Double.valueOf(sf.getString("rmssd", ""));
            Log.d(TAG,"Check Prev, Res"+Double.toString(res));
            updateRMSSD(count, res);
        }
        int BPMCount = sf.getInt("BPMCount", 0);
        if (BPMCount != 0) {
            double res = Double.valueOf(sf.getString("bpm", ""));
            updateBPM(count, res);
        }




        checkBPMAvg();

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void uploadRMSSD(int count, double res) {
        database = FirebaseDatabase.getInstance();
        rmssd_ref = database.getReference("UserData");
        Log.d(TAG,Double.toString(res));
        rmssd_ref.child("myIDExample").child("RMSSD").setValue("/"+Integer.toString(count) + "/" + String.format("%.2f",res)+"/");
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void uploadBPM(int count, double res) {
        database = FirebaseDatabase.getInstance();
        rmssd_ref = database.getReference("UserData");
        Log.d(TAG,Double.toString(res));
        rmssd_ref.child("myIDExample").child("BPM").setValue("/"+Integer.toString(count) + "/" + String.format("%.2f",res)+"/");
    }

    public void updateRMSSD(int count, double rest) {

        database = FirebaseDatabase.getInstance();
        rmssd_ref = database.getReference("UserData");
        rmssd_ref.child("myIDExample").child("RMSSD").addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    String result[] = snapshot.getValue().toString().split("/");
                    int server_count = Integer.valueOf(result[1]);
                    double server_rmssd = Double.valueOf(result[2]);
                    double server_res = server_count * server_rmssd;
                    totalRMSSDCount = server_count + count;
                    double totalRes = server_res + rest*count;
                    double upRMSSDAvg =totalRes / totalRMSSDCount;
                    if(totalRMSSDCount>10000){
                        RMSSDAvg = upRMSSDAvg;
                        wilcoxonMode = false;
                    }else{
                        RMSSDAvg = getDefaultRMSSDAvg();
                    }
                    uploadRMSSD(totalRMSSDCount, upRMSSDAvg);

                    //총 얻은 RMSSD값이 크다면 평균값 활용하기
                } else {
                    uploadRMSSD(count, rest);
                    totalRMSSDCount = count;
                }
                // 전반적인 값들 모두 업데이트했으니 초기화하기
                Log.d(TAG,"reset sf Main");
                editor.putInt("RMSSDCount", 0);
                editor.putString("rmssd", "0");
                editor.commit();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "nein");
            }

        });


    }

    public void updateBPM(int count, double rest) {

        database = FirebaseDatabase.getInstance();
        rmssd_ref = database.getReference("UserData");
        rmssd_ref.child("myIDExample").child("BPM").addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    String result[] = snapshot.getValue().toString().split("/");
                    int server_count = Integer.valueOf(result[1]);
                    double server_BPM = Double.valueOf(result[2]);
                    double server_res = server_count * server_BPM;
                    totalBPMCount = server_count + count;
                    double totalRes = server_res + rest*count;
                    BPMAvg =totalRes / totalBPMCount;
                    uploadBPM(totalBPMCount, BPMAvg);

                    //총 얻은 RMSSD값이 크다면 평균값 활용하기
                } else {
                    uploadBPM(count, rest);
                    totalBPMCount = count;
                }
                // 전반적인 값들 모두 업데이트했으니 초기화하기
                Log.d(TAG,"reset sf Main");
                editor.putInt("BPMCount", 0);
                editor.putString("bpm", "0");
                editor.commit();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "nein");
            }

        });
    }

    public void checkDefaultRMSSD(){

        database = FirebaseDatabase.getInstance();
        rmssd_ref = database.getReference("UserData");

        rmssd_ref.child("myIDExample").child("defaultRMSSD").addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    isStartMeasure = true;
                    String[] defaultRMSSDStr = snapshot.getValue().toString().split("/");
                    for(int i=0;i<defaultRMSSDStr.length-1;i++){
                        if(defaultRMSSDStr[i].length()>0) {
                            defaultRMSSDArr[i] = Double.parseDouble(defaultRMSSDStr[i]);
                            recentRMSSDArr[i] = defaultRMSSDArr[i];
                        }
                    }
                } else {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "nein");
            }

        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void updateStateRMSSD(int state){
        if(RMSSD_i>10) {
            database = FirebaseDatabase.getInstance();
            rmssd_ref = database.getReference("UserData");
            String DefaultRMSSDString = defaultRMSSDDoubleToString(recentRMSSDArr);
            rmssd_ref.child("myIDExample").child("State|"+stateString[state]).child(LocalDate.now().toString()+LocalTime.now().toString().replace('.','|')).setValue(DefaultRMSSDString);
        }
    }

    public void checkBPMAvg(){

        database = FirebaseDatabase.getInstance();
        rmssd_ref = database.getReference("UserData");

        rmssd_ref.child("myIDExample").child("BPM").addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    String BPMAVGString = snapshot.getValue().toString();
                    String[] BPMAVGArr = BPMAVGString.split("/");
                    BPMAvg = Double.parseDouble(BPMAVGArr[1]);
                } else {
                    //BPMAvg = 100;
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "nein");
            }

        });
    }


    @SuppressLint("UseCompatLoadingForDrawables")
    public void setVariables() {

        textStatus = (TextView) findViewById(R.id.text_status);
        stressText = (TextView) findViewById(R.id.stressText);
        RMSSDText = findViewById(R.id.RMSSDText);
        btnMenu = (Button) findViewById(R.id.btn_menu);
        getDefaultRMSSDBtn = (Button) findViewById(R.id.getDefaultRMSSDBtn);
        listView1 = (ListView) findViewById(R.id.listview1);
        listView2 = (ListView) findViewById(R.id.listview2);
        stressImg = findViewById(R.id.stressImg);
        btnStateSnack = findViewById(R.id.btn_state_snack);
        btnStateWalking = findViewById(R.id.btn_state_walking);
        btnStateFood = findViewById(R.id.btn_state_food);
        btnStateAngry = findViewById(R.id.btn_state_angry);
        runningDogText = findViewById(R.id.running_dog_text);

        bpmChart = findViewById(R.id.bpm_chart);
        RMSSDChart = findViewById(R.id.RMSSD_chart);
        drawerLayout = findViewById(R.id.bluetooth_Layout);




        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pairedDevices();
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        // 초기 RMSSD 설정
        getDefaultRMSSDBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isBluetoothConnected){
                    startProgress();
                    //그래프, 서비스 내에서도 모든 정보 제거
                }else{
                    //Alert화면 추가
                }}
        });

        btnStateSnack.setOnClickListener(new View.OnClickListener(){
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                if(isBluetoothConnected){
                    //startStateProgress(0);
                    updateStateRMSSD(0);
                }else{

                }
            }
        });

        btnStateWalking.setOnClickListener(new View.OnClickListener(){
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                if(isBluetoothConnected){
                    //startStateProgress(1);
                    updateStateRMSSD(1);
                }else{

                }
            }
        });

        btnStateFood.setOnClickListener(new View.OnClickListener(){
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                if(isBluetoothConnected){
                    //startStateProgress(2);
                    updateStateRMSSD(2);
                }else{

                }
            }
        });

        btnStateAngry.setOnClickListener(new View.OnClickListener(){
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                if(isBluetoothConnected){
                    //startStateProgress(3);
                    updateStateRMSSD(3);
                }else{

                }
            }
        });




        swipeRefreshLayout = findViewById(R.id.swipeLayout);
        swipeRefreshLayout.setOnRefreshListener(this);

        NavigationView navigationview = findViewById(R.id.navigationView);
        navigationview.setItemIconTintList(null);

        if (RMSSD != -1) {
            RMSSDText.setText(Double.toString(RMSSD));
        }
        if (stressStatus != -1) {
            switch (stressStatus) {
                case 0:
                    stressImg.setImageResource(R.drawable.happy);
                    stressText.setText("Low");
                    break;
                case 1:
                    stressImg.setImageResource(R.drawable.normal);
                    stressText.setText("Normal");
                    break;
                case 2:
                    stressImg.setImageResource(R.drawable.sad);
                    stressText.setText("High");
                    break;
            }
        }





    }

    public void setBluetooth() {

        String[] permission_list = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        };
        ActivityCompat.requestPermissions(BluetoothConnect.this, permission_list, 1);

        // Show paired devices
        btArrayAdapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1){
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position,convertView,parent);
                TextView tv = (TextView) view.findViewById(android.R.id.text1);
                tv.setTextColor(Color.GRAY);
                return view;
            }
        };
        btArrayAdapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1){
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position,convertView,parent);
                TextView tv = (TextView) view.findViewById(android.R.id.text1);
                tv.setTextColor(Color.GRAY);
                return view;
            }
        };

        deviceAddressArray1 = new ArrayList<>();
        deviceAddressArray2 = new ArrayList<>();

        listView1.setAdapter(btArrayAdapter1);
        listView2.setAdapter(btArrayAdapter2);

        listView1.setOnItemClickListener(new myOnItemClickListener());
        listView2.setOnItemClickListener(new myOnItemClickListener());

        // Enable bluetooth
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!btAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }


        if (flag) {
            textStatus.setText("connected to " + name);
            Intent intent = new Intent(getApplicationContext(), MyService.class);
            startService(intent);
            bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
            isBluetoothConnected = true;
        }

    }

    final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
        }
    };

    public void pairedDevices() {
        btArrayAdapter1.clear();
        if (deviceAddressArray1 != null && !deviceAddressArray1.isEmpty()) {
            deviceAddressArray1.clear();
        }
        pairedDevices = btAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            // There are paired devices. Get the name and address of each paired device.
            for (BluetoothDevice device : pairedDevices) {
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                btArrayAdapter1.add(deviceName);
                deviceAddressArray1.add(deviceHardwareAddress);
            }
        }

    }


    public void searchDevices() {
        if (btAdapter.isDiscovering()) {
            btAdapter.cancelDiscovery();
        } else {
            if (btAdapter.isEnabled()) {
                btAdapter.startDiscovery();
                btArrayAdapter2.clear();
                if (deviceAddressArray2 != null && !deviceAddressArray2.isEmpty()) {
                    deviceAddressArray2.clear();
                }
                IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                registerReceiver(receiver, filter);
            } else {
                Toast.makeText(getApplicationContext(), "bluetooth not on", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();

                if (deviceName != null) {
                    String deviceHardwareAddress = device.getAddress(); // MAC address
                    btArrayAdapter2.add(deviceName);
                    deviceAddressArray2.add(deviceHardwareAddress);
                    btArrayAdapter2.notifyDataSetChanged();
                }
            }
        }
    };
    //
    //The BroadcastReceiver that listens for bluetooth broadcasts
    public final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

            if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                isBluetoothConnected = false;
                stressImg.setImageResource(R.drawable.not_yet);
                textStatus.setText("Disconnected...");



                //테스트
                NotificationManager mNotificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
                mNotificationManager.createNotificationChannel(new NotificationChannel("bluetooth","0",NotificationManager.IMPORTANCE_HIGH));

                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(),"bluetooth")
                        .setSmallIcon(R.drawable.logo_main_dc)
                        .setContentTitle("블루투스 연결 해제")
                        .setContentText("블루투스를 다시 연결해주세요")
                        .setDefaults(Notification.DEFAULT_VIBRATE)
                        .setPriority(Notification.PRIORITY_HIGH)
                        .setSmallIcon(R.drawable.normal);

                mNotificationManager.notify(1,mBuilder.build());


            }
        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //unregisterReceiver(mBroadcastReceiver1);
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }

    @Override
    public void onRefresh() {
        searchDevices();
        swipeRefreshLayout.setRefreshing(false);
    }

    public class myOnItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            Toast.makeText(getApplicationContext(), btArrayAdapter1.getItem(position), Toast.LENGTH_SHORT).show();


            name = btArrayAdapter1.getItem(position); // get name
            final String address = deviceAddressArray1.get(position); // get address
            if (address.length() != 0) {
                textStatus.setText("try...");
                flag = true;
                BluetoothDevice device = btAdapter.getRemoteDevice(address);

                // create & connect socket
                try {
                    btSocket = createBluetoothSocket(device);
                    btSocket.connect();
                } catch (IOException e) {
                    flag = false;
                    textStatus.setText("connection failed!");
                    e.printStackTrace();
                }

                // start bluetooth communication
                if (flag) {
                    if(!isStartMeasure) {
                        textStatus.setText("Get Default RMSSD First ");
                        textStatus.setTextSize(10);
                    }else {
                        textStatus.setText("connected to " + name);
                        textStatus.setTextSize(17);
                    }
                    Intent intent = new Intent(getApplicationContext(), MyService.class);
                    startService(intent);
                    bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
                    isBluetoothConnected = true;
                    drawerLayout.closeDrawer(GravityCompat.START);
                }
            }
        }
    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        try {
            final Method m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", UUID.class);
            return (BluetoothSocket) m.invoke(device, BT_MODULE_UUID);
        } catch (Exception e) {
            Log.e(TAG, "Could not create Insecure RFComm Connection", e);
        }
        return device.createRfcommSocketToServiceRecord(BT_MODULE_UUID);
    }

    private Messenger mMessenger = new Messenger(new Handler(new Handler.Callback() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public boolean handleMessage(Message msg) {
            String value = msg.getData().getString("test");
            String protoArr[] = value.split("#");
            for (String protocols : protoArr) {
                Log.d(TAG, protocols);
                String resultArr[] = protocols.split("/");
                int resultLength = resultArr.length;
                if (resultLength > 1) {

                    /*
                    sf = getSharedPreferences("prevData", MODE_PRIVATE);
                            editor = sf.edit();
                            int count = sf.getInt("count", 0);
                            if (count != 0) {
                                double res = Double.valueOf(sf.getString("res", ""));
                                String date = sf.getString("date", "");
                                updateRMSSD(count, res);
                            }
                            int BPMCount = sf.getInt("BPMCount", 0);
                            if (BPMCount != 0) {
                                double res = Double.valueOf(sf.getString("BPMRes", ""));
                                //updateBPM(count, res);
                            }

                            editor.putString("date", now.toString());
                            editor.putString("res", Double.toString(MyService.res));
                            editor.putInt("count", MyService.RMSSD_count);
                            editor.putString("bpm",Double.toString(BPMAvg));
                            editor.commit();
                     */

                    switch (resultArr[0]) {
                        case "beat":
                            int bpm = Integer.parseInt(resultArr[1]);
                            if (bpm > 10) {
                                Log.d(TAG, "BPM: " + bpm);
                                setData(count, bpm, 0);
                                bpmChart.invalidate();
                            }
                            BPMCount++;
                            BPMSum += bpm;

                            if(BPMCount%5==0){
                                setSFBPMAvg();
                            }

                            break;
                        case "RMSSD":
                            double CurRMSSD = Double.parseDouble(resultArr[1]);
                            CurRMSSD = Math.round(CurRMSSD);
                            Log.d(TAG, "RMSSD: " + CurRMSSD);
                            RMSSD = CurRMSSD;
                            rightShift(recentRMSSDArr);
                            recentRMSSDArr[0] = RMSSD;

                            RMSSDSum += RMSSD;

                            if(RMSSD_i%5==0){
                                setSFRMSSDAvg();
                            }



                            if(isStartMeasure) {
                                setData(count, (int) CurRMSSD, 1);
                                RMSSDChart.invalidate();

                                if (CurRMSSD > RMSSDAvg*1.3 && (wilcoxonSignedRankTest(defaultRMSSDArr, recentRMSSDArr) && wilcoxonMode)) {
                                    stressImg.setImageResource(R.drawable.sad);
                                    stressText.setText("HIGH");
                                    stressStatus = 2;
                                } else if (CurRMSSD > RMSSDAvg*0.7) {
                                    stressImg.setImageResource(R.drawable.normal);
                                    stressText.setText("Normal");
                                    stressStatus = 1;
                                } else {
                                    stressImg.setImageResource(R.drawable.happy);
                                    stressText.setText("LOW");
                                    stressStatus = 0;
                                }
                            }else{
                                //if(!isGettingDefaultRMSSD && isGettingState == -1){
                                if(!isGettingDefaultRMSSD){
                                    stressText.setText("measure default RMSSD First...");
                                }

                                if(isGettingDefaultRMSSD && numGetDefaultRMSSD<RMSSDArrNum){
                                    rmssdArr[numGetDefaultRMSSD] = CurRMSSD;
                                    recentRMSSDArr[numGetDefaultRMSSD] = CurRMSSD;
                                    numGetDefaultRMSSD++;
                                    //runningDogText.setText("checking... ( "+numGetDefaultRMSSD+" / "+RMSSDArrNum+" )...");
                                    //10번을 모두 측정하면 데이터베이스에 업데이트하고 측정 종료
                                    database = FirebaseDatabase.getInstance();
                                    rmssd_ref = database.getReference("UserData");
                                    if(numGetDefaultRMSSD>=RMSSDArrNum){
                                        String DefaultRMSSDString = defaultRMSSDDoubleToString(rmssdArr);
                                        rmssd_ref.child("myIDExample").child("defaultRMSSD").setValue(DefaultRMSSDString);
                                        isStartMeasure = true;
                                        progressOFF();
                                    }
                                    RMSSDAvg = getDefaultRMSSDAvg();
                                }
                                /*
                                if(isGettingState!=-1 && numGetStateRMSSD<RMSSDArrNum){
                                    rmssdArr[numGetStateRMSSD] = CurRMSSD;
                                    numGetStateRMSSD++;
                                    //runningDogText.setText("checking... ( "+numGetStateRMSSD+" / "+RMSSDArrNum+" )...");
                                    //10번을 모두 측정하면 데이터베이스에 업데이트하고 측정 종료
                                    if(numGetStateRMSSD>=RMSSDArrNum){
                                        String DefaultRMSSDString = Arrays.toString(rmssdArr);
                                        DefaultRMSSDString = DefaultRMSSDString.replace("[","");
                                        DefaultRMSSDString = DefaultRMSSDString.replace("]","");
                                        DefaultRMSSDString = DefaultRMSSDString.replaceAll(",","/");

                                        String stateString="";
                                        switch(isGettingState){
                                            case 0:
                                                stateString = "stateSnack";
                                                break;
                                            case 1:
                                                stateString = "stateWalking";
                                                break;
                                            case 2:
                                                stateString = "stateFood";
                                                break;
                                            case 3:
                                                stateString = "stateAngry";
                                                break;
                                            default:
                                                break;
                                        }

                                        rmssd_ref.child("myIDExample").child(stateString).child(LocalDate.now().toString()+ LocalTime.now().toString()).setValue(DefaultRMSSDString);
                                        progressOFF();
                                    }


                                }
                                */
                            }
                            RMSSDText.setText(Double.toString(CurRMSSD));



                            break;
                        case "GYRO":
                            break;
                    }
                }
            }
            return false;
        }
    }));

    public void setSFRMSSDAvg(){
        int SFRMSSDCount = sf.getInt("RMSSDCount", 0);
        if(SFRMSSDCount !=0 || RMSSD_i !=0) {
            double SFRMSSDAvg = Double.parseDouble(sf.getString("rmssd", "0"));

            Log.d(TAG, "SFRMSSDAvg: " + Double.toString(SFRMSSDAvg) + "/ SFRMSSDCount: " + SFRMSSDCount + "/ RMSSDSum: " + RMSSDSum + "/ RMSSDCount: " + RMSSD_i);
            SFRMSSDAvg = (SFRMSSDAvg * SFRMSSDCount + RMSSDSum) / (SFRMSSDCount + RMSSD_i);

            editor.putInt("RMSSDCount", SFRMSSDCount + RMSSD_i);
            editor.putString("rmssd", Double.toString(SFRMSSDAvg));
            editor.commit();

            Log.d("SF", "RMSSD" + Integer.toString(SFRMSSDCount + RMSSD_i));
            Log.d("SF", "RMSSD" + Double.toString(SFRMSSDAvg));
        }
    }

    public void setSFBPMAvg(){
        int SFBPMCount = sf.getInt("BPMCount", 0);
        if(SFBPMCount !=0 || BPMCount !=0) {
            double BPMAvg = Double.parseDouble(sf.getString("bpm", "0"));

            BPMAvg = (BPMAvg * SFBPMCount + BPMSum) / (SFBPMCount + BPMCount);

            editor.putInt("BPMCount", SFBPMCount + BPMCount);
            editor.putString("bpm", Double.toString(BPMAvg));
            editor.commit();

            Log.d("SF", "BPM" + Integer.toString(SFBPMCount + BPMCount));
            Log.d("SF", "BPM" + Double.toString(BPMAvg));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setSF() {
        LocalDate now = LocalDate.now();
        editor.putString("date", now.toString());
        editor.putString("res", Double.toString(MyService.res));
        editor.putInt("count", MyService.RMSSD_count);
        editor.putString("bpm",Double.toString(BPMAvg));
        editor.commit();
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mServiceMessenger = new Messenger(iBinder);
            try {
                Message msg = Message.obtain(null, 0);
                msg.replyTo = mMessenger;
                mServiceMessenger.send(msg);
            } catch (RemoteException e) {
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
        }
    };

    private void createChart() {
        {   // // Chart Style // //
            bpmChart.setBackgroundColor(Color.WHITE);
            bpmChart.getDescription().setEnabled(false);
            bpmChart.setTouchEnabled(true);
            bpmChart.setOnChartValueSelectedListener(this);
            bpmChart.setDrawGridBackground(false);
            bpmChart.setDragEnabled(true);
            bpmChart.setScaleEnabled(true);
            bpmChart.setPinchZoom(true);

            RMSSDChart.setBackgroundColor(Color.WHITE);
            RMSSDChart.getDescription().setEnabled(false);
            RMSSDChart.setTouchEnabled(true);
            RMSSDChart.setOnChartValueSelectedListener(this);
            RMSSDChart.setDrawGridBackground(false);
            RMSSDChart.setDragEnabled(true);
            RMSSDChart.setScaleEnabled(true);
            RMSSDChart.setPinchZoom(true);
        }

        XAxis bpm_xAxis = bpmChart.getXAxis();
        bpm_xAxis.setEnabled(false);

        YAxis bpm_yAxis;
        {
            bpm_yAxis = bpmChart.getAxisLeft();
            bpmChart.getAxisRight().setEnabled(false);
            bpm_yAxis.enableGridDashedLine(10f, 10f, 0f);
            bpm_yAxis.setAxisMaximum(250f);
            bpm_yAxis.setAxisMinimum(0f);
        }

        XAxis RMSSD_xAxis = RMSSDChart.getXAxis();
        RMSSD_xAxis.setEnabled(false);


        YAxis RMSSD_yAxis;
        {
            RMSSD_yAxis = RMSSDChart.getAxisLeft();
            RMSSDChart.getAxisRight().setEnabled(false);
            RMSSD_yAxis.enableGridDashedLine(10f, 10f, 0f);
            RMSSD_yAxis.setAxisMaximum(300f);
            RMSSD_yAxis.setAxisMinimum(0f);
        }

        if (bpm_values.size() == 0) {

        } else {
            setData(count, -1, 0);
            setData(count, -1, 1);
        }

        bpmChart.animateX(1500);

        Legend bpm_l = bpmChart.getLegend();
        bpm_l.setForm(Legend.LegendForm.LINE);

        RMSSDChart.animateX(1500);
        Legend RMSSD_l = RMSSDChart.getLegend();
        RMSSD_l.setForm(Legend.LegendForm.LINE);
    }

    private void setChart() {
        LineChart lineChart = bpmChart;
        lineChart.invalidate(); //차트 초기화 작업
        lineChart.clear();
        ArrayList<Entry> values = new ArrayList<>();
    }

    private void setData(int count, int val, int chartNum) {

        ArrayList<Entry> values = new ArrayList<>();
        LineChart chart;
        String Label;
        int line_color;
        Drawable graph_color;
        int i;

        if (chartNum == 0) {
            chart = bpmChart;
            values = bpm_values;
            Label = "BPM";
            line_color = ContextCompat.getColor(this, R.color.red);
            graph_color = ContextCompat.getDrawable(this, R.drawable.fade_red);
            i = bpm_i;
        } else {
            chart = RMSSDChart;
            values = RMSSD_values;
            Label = "RMSSD";
            line_color = ContextCompat.getColor(this, R.color.teal_line);
            graph_color = ContextCompat.getDrawable(this, R.drawable.fade_teal);
            i = RMSSD_i;


        }
        if (val != -1) {
            values.add(new Entry(i, val, getResources().getDrawable(R.drawable.star)));
            if (values.size() > count) {
                values.remove(0);
            }
            if (chartNum == 0) {
                bpm_i++;
            } else {
                RMSSD_i++;
                Log.d(TAG,"RMSSD_i Increased");
            }
        }

        LineDataSet set1;

        if (chart.getData() != null &&
                chart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) chart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            set1.notifyDataSetChanged();
            chart.getData().notifyDataChanged();
            chart.notifyDataSetChanged();
        } else {

            set1 = new LineDataSet(values, Label);

            set1.setMode(LineDataSet.Mode.CUBIC_BEZIER);
            set1.setCubicIntensity(0.1f);
            set1.setDrawIcons(false);

            set1.setColor(line_color);
            set1.setCircleColor(line_color);

            // line thickness and point size
            set1.setLineWidth(2f);
            set1.setCircleRadius(2f);


            // draw points as solid circles
            set1.setDrawCircleHole(false);

            // customize legend entry
            set1.setFormLineWidth(1f);
            set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
            set1.setFormSize(15.f);

            // text size of values
            set1.setValueTextSize(9f);
            set1.setValueTextColor(Color.BLACK);

            set1.enableDashedHighlightLine(10f, 5f, 0f);

            set1.setDrawFilled(true);
            set1.setFillFormatter(new IFillFormatter() {
                @Override
                public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                    return chart.getAxisLeft().getAxisMinimum();
                }
            });


            if (Utils.getSDKInt() >= 18) {
                Drawable drawable = graph_color;
                set1.setFillDrawable(drawable);
            } else {
                set1.setFillColor(Color.BLACK);
            }

            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);

            LineData data = new LineData(dataSets);

            chart.setData(data);
        }
    }

    private void startProgress() {

        progressON();
        isGettingDefaultRMSSD = true;
        numGetDefaultRMSSD = 0;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG,"Running start Progress");
            }
        }, 100);

    }
    /*
    //강아지 상태 측정
    private void startStateProgress(int i) {

        progressON();
        isGettingState = i;
        numGetStateRMSSD = 0;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG,"Running state Progress");
            }
        }, 100);

    }
    */



    public void progressON() {

        if (progressDialog != null && progressDialog.isShowing()) {
            progressSET();
        } else {

            progressDialog = new AppCompatDialog(this);
            progressDialog.setCancelable(false);
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            progressDialog.setContentView(R.layout.loading_dialog);
            progressDialog.setOnKeyListener(new DialogInterface.OnKeyListener(){
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    if(keyCode== KeyEvent.KEYCODE_BACK){
                        Log.d(TAG,"back pressed");
                        progressOFF();
                    }
                    return false;
                }
            });
            progressDialog.show();
        }

        final ImageView img_loading_frame = (ImageView) progressDialog.findViewById(R.id.running_dog_view);
        final AnimationDrawable frameAnimation = (AnimationDrawable) img_loading_frame.getBackground();
        img_loading_frame.post(new Runnable() {
            @Override
            public void run() {
                frameAnimation.start();
            }
        });


    }

    public void progressSET() {

        if (progressDialog == null || !progressDialog.isShowing()) {
            return;
        }

    }

    public void progressOFF() {
        if (progressDialog != null && progressDialog.isShowing()) {
            isGettingDefaultRMSSD = false;
            numGetDefaultRMSSD = 0;
            //numGetStateRMSSD = 0;
            //isGettingState = -1;
            progressDialog.dismiss();
            //runningDogText.setText("Loading...");
        }
    }

    public String defaultRMSSDDoubleToString(double[] rmssdArr){
        String DefaultRMSSDString = Arrays.toString(rmssdArr);
        DefaultRMSSDString = DefaultRMSSDString.replace("[","");
        DefaultRMSSDString = DefaultRMSSDString.replace("]","");
        DefaultRMSSDString = DefaultRMSSDString.replaceAll(",","/");
        DefaultRMSSDString = DefaultRMSSDString.trim();
        DefaultRMSSDString = "/"+DefaultRMSSDString;
        return DefaultRMSSDString;
    }

    public boolean wilcoxonSignedRankTest(double[] wilDefaultRMSSDArr, double[] wilRMSSDArr){

        double[] RMSSDDiff = new double[RMSSDArrNum];
        boolean[] signPlus = new boolean[RMSSDArrNum];
        int rankSumPlus=0, rankSumMinus=0;

        for(int i=0;i<RMSSDArrNum;i++){
            RMSSDDiff[i] = wilDefaultRMSSDArr[i] - wilRMSSDArr[i];
            if(RMSSDDiff[i]>=0){
                signPlus[i] = true;
            }else{
                signPlus[i] = false;
            }
            RMSSDDiff[i] = Math.abs(RMSSDDiff[i]);
        }

        for(int i = 1; i < RMSSDArrNum; i++) {
            double temp = RMSSDDiff[i];
            int prev = i - 1;
            while(prev >= 0 && RMSSDDiff[prev] > temp) {
                RMSSDDiff[prev + 1] = RMSSDDiff[prev];
                prev--;
            }
            RMSSDDiff[prev + 1] = temp;
        }

        for(int i=0;i<RMSSDArrNum;i++){
            if(signPlus[i]){
                rankSumPlus += i;
            }else{
                rankSumMinus += i;
            }
        }

        if(rankSumMinus<(criticalRegion[RMSSDArrNum]-criticalRegionDiff)){
            return true;
        }else{
            return false;
        }

    }

    public void rightShift(double[] array){
        for(int i=RMSSDArrNum-1;i>0;i--){
            array[i] = array[i-1];
        }
    }

    public double getDefaultRMSSDAvg(){
        double sum = 0;
        for(int i=0;i<RMSSDArrNum-1;i++){
            sum += defaultRMSSDArr[i];
        }
        return sum/RMSSDArrNum;
    }







}