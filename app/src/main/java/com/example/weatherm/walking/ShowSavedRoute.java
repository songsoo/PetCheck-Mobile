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


        //???????????????????????? ?????????????????? ?????????
        firebaseFirestore= FirebaseFirestore.getInstance();

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        polylines=new ArrayList<Polyline>();//???????????? ?????????

        //onmapreadycallback ?????? ?????????
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        final SoftKeyboardDectectorView softKeyboardDecector = new SoftKeyboardDectectorView(this);
        addContentView(softKeyboardDecector, new FrameLayout.LayoutParams(-1, -1));

        softKeyboardDecector.setOnShownKeyboard(new SoftKeyboardDectectorView.OnShownKeyboardListener() {

            @Override
            public void onShowSoftKeyboard() {
                //????????? ????????? ???
                Toast.makeText(getApplicationContext(),"????????? ??????",Toast.LENGTH_LONG);
                uploadButton.setText("?????? ??????");
                dateTextView.setVisibility(View.GONE);
                Log.d("?????????","???????????????");
                isKeyboardOn=true;
            }
        });

        softKeyboardDecector.setOnHiddenKeyboard(new SoftKeyboardDectectorView.OnHiddenKeyboardListener() {

            @Override
            public void onHiddenSoftKeyboard() {
                // ????????? ????????? ???
                Toast.makeText(getApplicationContext(),"????????? ?????????",Toast.LENGTH_LONG);
                uploadButton.setText("???????????? ????????????");
                dateTextView.setVisibility(View.VISIBLE);
                Log.d("?????????","???????????????");
                isKeyboardOn=false;
            }
        });

        // Toolbar??? ??????.
         myToolbar = (Toolbar) findViewById(R.id.showRoute_toolbar2);
        setSupportActionBar(myToolbar);

        //Toolbar??? ????????? ????????? ???????????? ????????? ???????????? ?????????.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.left_arrow_24);
        getSupportActionBar().setTitle("?????? ??????");  //??? ??????????????? ????????? ?????? ???????????? ?????????


    }
    // ToolBar??? menu.xml??? ??????????????????
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //return super.onCreateOptionsMenu(menu);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.information_toolbar_menu, menu);
        return true;
    }

    //ToolBar??? ????????? ????????? select ???????????? ???????????? ??????
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //return super.onOptionsItemSelected(item);
        switch (item.getItemId()) {

            //android.R.id.home ??? ????????? ??? ???????????? ????????? ?????????.
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
                            Log.e("???",""+totalDistance);
                            Log.e("???",""+totalTime);
                            date=new Date(d.getDate("walkingDate").getTime());
                            dateTextView.setText(new SimpleDateFormat("MM??? dd?????? ??????", Locale.KOREA).format((d.getDate("walkingDate").getTime())));
                            if(d.getData().get("walkingContent")!=null) {
                                content = d.getData().get("walkingContent").toString();
                                walkingContentTextView.setText(content);


                            }
                            int seconds=(int)totalTime%60;
                            int minutes=(int)totalTime/60;
                            Log.e("???",""+seconds);
                            Log.e("???",""+minutes);
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
                                markerOptions.title("?????? ?????????");
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
                                markerOptions2.title("?????? ??????");
                                markerOptions2.draggable(true);
                                markerOptions2.icon(BitmapDescriptorFactory.fromBitmap(bitMapImage2));
                                mMap.addMarker(markerOptions2);
                            }
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startLatLng, 16));

                        }
                        else{

                            Toast.makeText(getApplicationContext(), "?????? ?????? ??????", Toast.LENGTH_LONG).show();
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
                            Log.d("?????? ??????","??????");

                            InputMethodManager manager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                            manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("?????? ??????","??????");
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






