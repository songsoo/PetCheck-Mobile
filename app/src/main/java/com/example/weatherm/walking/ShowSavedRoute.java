package com.example.weatherm.walking;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.weatherm.MainActivity2;
import com.example.weatherm.sharing.SharingFragment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.weatherm.R;
import com.example.weatherm.sharing.WritingPostActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class ShowSavedRoute extends AppCompatActivity implements OnMapReadyCallback{

    private GoogleMap mMap;
    private List<Polyline> polylines;
    private FirebaseFirestore firebaseFirestore;


    private static final String TAG = "googlemap_example";

    private String documentId;
    private TextView dateTextView;
    private TextView totalDistanceTextView;
    private TextView totalTimeTextView;
    private EditText walkingContentTextView;
    private Button uploadButton;
    private ArrayList<HashMap<String,Double>> ListStartLatLng=new ArrayList<>();
    private ArrayList<HashMap<String,Double>> ListEndLatLng=new ArrayList<>();
    private ArrayList<HashMap<String,Double>> ListTrashLatLng=new ArrayList<>();
    private ArrayList<HashMap<String,Double>> ListWarningLatLng=new ArrayList<>();
    private ArrayList<LatLng> ListStartLatLng2=new ArrayList<>();
    private ArrayList<LatLng> ListEndLatLng2=new ArrayList<>();
    private ArrayList<LatLng> ListTrashLatLng2=new ArrayList<>();
    private ArrayList<LatLng> ListWarningLatLng2=new ArrayList<>();
    private boolean isKeyboardOn=false;

    public FragmentManager fm;
    public FragmentTransaction ft;

    private  double totalDistance;
    private long totalTime;
    private Date date;
    private String content;
    private Toolbar myToolbar;
    @Override
    protected void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_savedroute);


        //파이어베이스에서 데이터베이스 가져옴
        firebaseFirestore= FirebaseFirestore.getInstance();

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        polylines=new ArrayList<Polyline>();//폴리라인 초기화

        //onmapreadycallback 함수 부르기
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        final SoftKeyboardDectectorView softKeyboardDecector = new SoftKeyboardDectectorView(this);
        addContentView(softKeyboardDecector, new FrameLayout.LayoutParams(-1, -1));

        softKeyboardDecector.setOnShownKeyboard(new SoftKeyboardDectectorView.OnShownKeyboardListener() {

            @Override
            public void onShowSoftKeyboard() {
                //키보드 등장할 때
                Toast.makeText(getApplicationContext(),"키보드 등장",Toast.LENGTH_LONG);
                uploadButton.setText("입력 완료");
                dateTextView.setVisibility(View.GONE);
                Log.d("키보드","키보드등장");
                isKeyboardOn=true;
            }
        });

        softKeyboardDecector.setOnHiddenKeyboard(new SoftKeyboardDectectorView.OnHiddenKeyboardListener() {

            @Override
            public void onHiddenSoftKeyboard() {
                // 키보드 사라질 때
                Toast.makeText(getApplicationContext(),"키보드 사라짐",Toast.LENGTH_LONG);
                uploadButton.setText("게시글에 공유하기");
                dateTextView.setVisibility(View.VISIBLE);
                Log.d("키보드","키보드삭제");
                isKeyboardOn=false;
            }
        });

        // Toolbar를 생성.
         myToolbar = (Toolbar) findViewById(R.id.showRoute_toolbar2);
        setSupportActionBar(myToolbar);

        //Toolbar의 왼쪽에 버튼을 추가하고 버튼의 아이콘을 바꾼다.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.left_arrow_24);
        getSupportActionBar().setTitle("산책 정보");  //당 액티비티의 툴바에 있는 타이틀을 바꾸기


    }
    // ToolBar에 menu.xml을 인플레이트함
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //return super.onCreateOptionsMenu(menu);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.information_toolbar_menu, menu);
        return true;
    }

    //ToolBar에 추가된 항목의 select 이벤트를 처리하는 함수
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //return super.onOptionsItemSelected(item);
        switch (item.getItemId()) {

            //android.R.id.home 이 툴바의 맨 왼쪽버튼 아이디 입니다.
            case android.R.id.home:
                finish();
                return true;

            default:
                return true;
        }
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        Log.d(TAG, "onMapReady :");

        mMap = googleMap;

        Intent intent=getIntent();
        documentId= intent.getStringExtra("routeInfo");

        totalDistanceTextView=findViewById(R.id.totalDistanceTextView_showRoute);
        totalTimeTextView=findViewById(R.id.timeTextView_showRoute);
        dateTextView=findViewById(R.id.dateTextView_showRoute);
        walkingContentTextView=findViewById(R.id.contentEditText_showRoute);
        uploadButton=findViewById(R.id.shareRouteButton);


        firebaseFirestore=FirebaseFirestore.getInstance();
        CollectionReference collectionReference = firebaseFirestore.collection("RouteInfo");
        collectionReference.document(documentId).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){

                            DocumentSnapshot d = task.getResult();

                            totalDistance=(double) d.getData().get("totalDistance");
                            totalTime= (long) d.getData().get("totalTime");
                            Log.e("앙",""+totalDistance);
                            Log.e("앙",""+totalTime);
                            date=new Date(d.getDate("walkingDate").getTime());
                            dateTextView.setText(new SimpleDateFormat("MM월 dd일의 산책", Locale.KOREA).format((d.getDate("walkingDate").getTime())));
                            if(d.getData().get("walkingContent")!=null) {
                                content = d.getData().get("walkingContent").toString();
                                walkingContentTextView.setText(content);


                            }
                            int seconds=(int)totalTime%60;
                            int minutes=(int)totalTime/60;
                            Log.e("앙",""+seconds);
                            Log.e("앙",""+minutes);
                            totalTimeTextView.setText(""+String.format("%02d",minutes) + ":"+  String.format("%02d",seconds));
                            int meter=(int)totalDistance%1000;
                            int kilMmeter=(int)totalDistance/1000;
                            totalDistanceTextView.setText(""+String.format("%02d",kilMmeter)+"."+String.format("%03d",meter)+"km");

                            ListStartLatLng=(ArrayList<HashMap<String,Double>>) d.getData().get("listStartLatLng");
                            ListStartLatLng2=(ArrayList<LatLng>) d.getData().get("listStartLatLng");
                            ListEndLatLng=(ArrayList<HashMap<String,Double>>) d.getData().get("listEndLatLng");
                            ListEndLatLng2=(ArrayList<LatLng>) d.getData().get("listEndLatLng");
                            if(d.getData().get("listTrashLatLng")!=null) {
                                ListTrashLatLng = (ArrayList<HashMap<String, Double>>) d.getData().get("listTrashLatLng");
                                ListTrashLatLng2=(ArrayList<LatLng>) d.getData().get("listTrashLatLng");

                            }
                            if(d.getData().get("listWarningLatLng")!=null) {
                                ListWarningLatLng = (ArrayList<HashMap<String, Double>>) d.getData().get("listWarningLatLng");
                                ListWarningLatLng2= (ArrayList<LatLng>) d.getData().get("listWarningLatLng");
                            }


                            Double startLatitude, endLatitude;
                            Double startLongtitude, endLongtitude;
                            Double trashLatitude,trashLongtitude;
                            Double warningLatitude,warningLongtitude;
                            LatLng startLatLng,endLatLng,trashLatLng,warningLatLng;
                            startLatLng=new LatLng(0,0);
                            endLatLng=new LatLng(0,0);
                            trashLatLng=new LatLng(0,0);
                            warningLatLng=new LatLng(0,0);

                            for(int i=0; i<ListEndLatLng.size();i++)
                            {
                                startLatitude=ListStartLatLng.get(i).get("latitude");
                                startLongtitude=ListStartLatLng.get(i).get("longitude");
                                startLatLng=new LatLng(startLatitude,startLongtitude);

                                endLatitude=ListEndLatLng.get(i).get("latitude");
                                endLongtitude=ListEndLatLng.get(i).get("longitude");
                                endLatLng=new LatLng(endLatitude,endLongtitude);

                                PolylineOptions options = new PolylineOptions().add(startLatLng).add(endLatLng).width(10).color(Color.RED).geodesic(true);
                                polylines.add(mMap.addPolyline(options));
                                Log.d(TAG, "onLocationResult : " + polylines.toString());

                            }
                            for(int i=0; i< ListTrashLatLng.size(); i++)
                            {
                                trashLatitude=ListTrashLatLng.get(i).get("latitude");
                                trashLongtitude=ListTrashLatLng.get(i).get("longitude");
                                trashLatLng=new LatLng(trashLatitude,trashLongtitude);
                                BitmapDrawable bd = (BitmapDrawable) getApplicationContext().getResources().getDrawable(R.drawable.baseline_delete_black_48);
                                Bitmap b = bd.getBitmap();
                                Bitmap bitMapImage = Bitmap.createScaledBitmap(b,20,20,false);
                                MarkerOptions markerOptions = new MarkerOptions();
                                markerOptions.position(trashLatLng);
                                markerOptions.title("배변 봉투함");
                                markerOptions.draggable(true);
                                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(bitMapImage));
                                mMap.addMarker(markerOptions);

                            }
                            for(int i=0; i< ListWarningLatLng.size(); i++)
                            {
                                warningLatitude=ListWarningLatLng.get(i).get("latitude");
                                warningLongtitude=ListWarningLatLng.get(i).get("longitude");
                                warningLatLng=new LatLng(warningLatitude,warningLongtitude);
                                BitmapDrawable bd2 = (BitmapDrawable) getApplicationContext().getResources().getDrawable(R.drawable.baseline_warning_black_48);
                                Bitmap b2 = bd2.getBitmap();
                                Bitmap bitMapImage2 = Bitmap.createScaledBitmap(b2,20,20,false);
                                MarkerOptions markerOptions2 = new MarkerOptions();
                                markerOptions2.position(warningLatLng);
                                markerOptions2.title("위험 지역");
                                markerOptions2.draggable(true);
                                markerOptions2.icon(BitmapDescriptorFactory.fromBitmap(bitMapImage2));
                                mMap.addMarker(markerOptions2);
                            }
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startLatLng, 16));

                        }
                        else{

                            Toast.makeText(getApplicationContext(), "루트 읽기 실패", Toast.LENGTH_LONG).show();
                        }
                    }
                });
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isKeyboardOn) {
                    firebaseFirestore = FirebaseFirestore.getInstance();
                    DocumentReference documentReference = firebaseFirestore.collection("RouteInfo").document(documentId);
                    documentReference.update("walkingContent", walkingContentTextView.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Log.d("메모 입력","성공");

                            InputMethodManager manager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                            manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("메모 입력","실패");
                        }
                    });

                } else {
//                    RouteInfo routeInfo=new RouteInfo(ListStartLatLng2,ListEndLatLng2,totalDistance,totalTime,date,content,ListTrashLatLng2,ListWarningLatLng2);
                    Intent intent=new Intent(getApplicationContext(),WritingPostActivity.class);
                    intent.putExtra("flag",1);
                    intent.putExtra("RouteInfo", documentId);
                    startActivity(intent);
                    finish();
                }
            }
        });

                }





    }






