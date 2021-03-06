package com.example.weatherm.walking;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.example.weatherm.MainActivity;
import com.example.weatherm.MainActivity2;
import com.example.weatherm.Model.RouteInfo;
import com.example.weatherm.MyService2;
import com.example.weatherm.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


public class WalkingFragment extends Fragment implements OnMapReadyCallback {
    View rootView;
    MapView mapView;

    List<Marker> previous_marker = null;

    LatLng startLatLng;
    LatLng endLatLng;
    private  LatLng currentLatLng;
    private ArrayList<LatLng> ListStartLatLng;
    private ArrayList<LatLng> ListEndLatLng;
    private ArrayList<LatLng> ListTrashLatLng;
    private ArrayList<LatLng> ListWarningLatLng;
    ///////////////////////////////////////////////////////////////////////////////////////
    private FragmentActivity mContext;

    private static final String TAG = "hi";
    private GoogleMap mMap;
    private Marker currentMarker = null;
    private List<Polyline> polylines;

    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient mFusedLocationProviderClient; // Deprecated??? FusedLocationApi??? ??????
    private LocationRequest locationRequest;
    private Location mCurrentLocatiion;

    private final LatLng mDefaultLocation = new LatLng(37.56, 126.97);
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;

    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int UPDATE_INTERVAL_MS = 1000 ;  // 1??? ?????? ?????? ??????
    private static final int FASTEST_UPDATE_INTERVAL_MS = 1000; // 30??? ????????? ?????? ??????

    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";
    LatLng currentPosition;

    private FloatingActionButton startButton;
    private FloatingActionButton warningButton;
    private FloatingActionButton pause_stopButton;
    private FloatingActionButton trash_Button;
    private TextView timeTextView;
    private  TextView distacneTextView;
    private Boolean walkState;
    private Boolean isOnceStart=false;


    private TimeHandler timer;
    int minutes,seconds;
    int timerTime=0;
    double totalDistance=0;

    private boolean isStartExist;

    public WalkingFragment() {
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) { // Fragment ??? Activity??? attach ??? ??? ????????????.
        mContext = (FragmentActivity) context;
        super.onAttach(context);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {


        isOnceStart=false;
        // Layout ??? inflate ?????? ?????????.
        if (savedInstanceState != null) {
            mCurrentLocatiion = savedInstanceState.getParcelable(KEY_LOCATION);
            Log.d("hello","Location: "+mCurrentLocatiion.toString());
            CameraPosition mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }
        rootView = inflater.inflate(
                R.layout.fragment_walking, container, false);
        mapView = (MapView) rootView.findViewById(R.id.map);
        if (mapView != null) {
            mapView.onCreate(savedInstanceState);
        }
        mapView.getMapAsync(this);
        previous_marker = new ArrayList<Marker>();

        ListStartLatLng=new ArrayList<>();
        ListEndLatLng=new ArrayList<>();
        ListTrashLatLng=new ArrayList<>();
        ListWarningLatLng=new ArrayList<>();
        walkState=false;
        polylines=new ArrayList<Polyline>();//???????????? ?????????
        startButton = (FloatingActionButton) rootView.findViewById(R.id.trackingStartButton);
        pause_stopButton = (FloatingActionButton)rootView.findViewById(R.id.trackingPauseStopButton);
        pause_stopButton.setScaleType(ImageView.ScaleType.CENTER);
        warningButton = (FloatingActionButton)rootView.findViewById(R.id.trackingWarningButton);
        trash_Button=(FloatingActionButton)rootView.findViewById(R.id.trackingGarbageButton);
        timeTextView=(TextView)rootView.findViewById(R.id.timeTextView);
        Log.d("hello",timeTextView.getText().toString());
        distacneTextView=(TextView)rootView.findViewById(R.id.distanceTextView);

        timer=new TimeHandler();
        startButton.setOnClickListener(new Button.OnClickListener() {
            @Override public void onClick(View v) {
                Intent intent=new Intent(getActivity(), MyService2.class);
                if(walkState==false) {
                   intent.putExtra(MyService2.MESSAGE_KEY,true);
                   getActivity().startService(intent);

                    isOnceStart=true;
                    timer.sendEmptyMessage(0);
                    try {
                        if (mLocationPermissionGranted) {
                            if(isStartExist=true)
                                isStartExist=false;
                            mFusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
                        }
                    } catch (SecurityException e) {
                        Log.e("Exception: %s", e.getMessage());
                    }
                    changeWalkState();
                    pause_stopButton.setImageResource(R.drawable.baseline_pause_24);
                }else{
                    intent.putExtra(MyService2.MESSAGE_KEY,false);
                    getActivity().startService(intent);
                }
            }
        });
        pause_stopButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                if(isOnceStart==true) {

                    if (walkState == false) {
                        mFusedLocationProviderClient.removeLocationUpdates(locationCallback);

                        RouteInfo routeInfo = new RouteInfo(ListStartLatLng, ListEndLatLng,totalDistance,timerTime,new Date(),
                                null,ListTrashLatLng,ListWarningLatLng);
                        Double latitude = ListStartLatLng.get(0).latitude;
                        Log.d(TAG, latitude.toString() + "haha");
                        Log.d(TAG, "haha");

                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        DocumentReference dr = db.collection("RouteInfo").document();
                        dr.set(routeInfo)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(mContext, "????????? ?????????????????????!", Toast.LENGTH_SHORT).show();
                                        Log.d(TAG, "??????????????????!!");

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(mContext, "??????????????? ?????????????????????!", Toast.LENGTH_SHORT).show();
                                        Log.w(TAG, "Error writing document", e);
                                    }
                                });
                        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                        String uid = firebaseUser.getUid();
                        DocumentReference dr2 = db.collection("users").document(uid);
                        dr2.update("walkingList", FieldValue.arrayUnion(dr.getId().toString()));

                        Log.e("????????????",dr.getId().toString());
                        Intent intent = new Intent(getActivity(), ShowSavedRoute.class);
                        intent.putExtra("routeInfo", dr.getId().toString());
                        startActivity(intent);
                        ((MainActivity2)getActivity()).setFrag(3);
                    } else {
                        timer.sendEmptyMessage(1);
                        mFusedLocationProviderClient.removeLocationUpdates(locationCallback);
                        pause_stopButton.setImageResource(R.drawable.baseline_stop_black_36);
                    }
                    //?????? ?????? ??????
                    changeWalkState();
                }
            }

        });

        warningButton.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v){
                final CharSequence[] oItems = {"?????? ????????? ??????","?????? ?????? ??????","??????"};

                AlertDialog.Builder oDialog = new AlertDialog.Builder(mContext,
                        android.R.style.Theme_DeviceDefault_Light_Dialog_Alert);

                oDialog.setTitle("????????? ???????????????")
                        .setItems(oItems, new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                               switch (which){
                                   case 0:
                                       BitmapDrawable bd = (BitmapDrawable) mContext.getResources().getDrawable(R.drawable.baseline_delete_black_48);
                                       Bitmap b = bd.getBitmap();
                                       Bitmap bitMapImage = Bitmap.createScaledBitmap(b,50,50,false);
                                       MarkerOptions markerOptions = new MarkerOptions();
                                       markerOptions.position(currentLatLng);
                                       markerOptions.title("?????? ?????????");
                                       markerOptions.draggable(true);
                                       markerOptions.icon(BitmapDescriptorFactory.fromBitmap(bitMapImage));
                                       mMap.addMarker(markerOptions);
                                       ListTrashLatLng.add(currentLatLng);
                                       break;
                                   case 1:
                                       BitmapDrawable bd2 = (BitmapDrawable) mContext.getResources().getDrawable(R.drawable.baseline_warning_black_48);
                                       Bitmap b2 = bd2.getBitmap();
                                       Bitmap bitMapImage2 = Bitmap.createScaledBitmap(b2,50,50,false);
                                       MarkerOptions markerOptions2 = new MarkerOptions();
                                       markerOptions2.position(currentLatLng);
                                       markerOptions2.title("?????? ??????");
                                       markerOptions2.draggable(true);
                                       markerOptions2.icon(BitmapDescriptorFactory.fromBitmap(bitMapImage2));
                                       mMap.addMarker(markerOptions2);
                                       ListWarningLatLng.add(currentLatLng);
                                       break;
                                   case 2:
                                       break;
                               }
                            }
                        })
                        .setCancelable(false)
                        .show();
            }
        });
        trash_Button.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {



            }
        });
        ////////////////////

        //????????? ?????? ?????????

        return rootView;
    }


    private class TimeHandler extends Handler{
        @Override
        public void handleMessage(@NonNull Message msg){
            super.handleMessage(msg);

            switch (msg.what){
                case 0:
                   timerTime++;
                   seconds=timerTime%60;
                   minutes=timerTime/60;
                   timeTextView.setText(""+String.format("%02d",minutes) + ":"+ String.format("%02d", seconds));
                   sendEmptyMessageDelayed(0,1000);
                   break;
                case 1:
                    removeMessages(0);
                    seconds=timerTime%60;
                    minutes=timerTime/60;
                    timeTextView.setText(""+String.format("%02d",minutes) + ":"+ String.format("%02d", seconds));
                    break;

            }
        }
    }

    @Override
    public void onViewCreated(View view,@Nullable Bundle savedInstanceState) {
        // Fragement????????? OnCreateView??? ?????????, Activity?????? onCreate()??? ???????????? ?????? ???????????? ???????????????.
        // Activity??? Fragment??? ?????? ?????? ????????? ?????????, View??? ???????????? ????????? ????????? ?????????.
        super.onViewCreated(view,savedInstanceState);

        //??????????????? ?????? ????????? ??? ???????????? ??????
        MapsInitializer.initialize(mContext);

        locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY) // ???????????? ?????????????????? ??????
                .setInterval(UPDATE_INTERVAL_MS) // ????????? Update ?????? ??????
                .setFastestInterval(FASTEST_UPDATE_INTERVAL_MS); // ?????? ????????? ?????????????????? ??????


        LocationSettingsRequest.Builder builder =
                new LocationSettingsRequest.Builder();

        builder.addLocationRequest(locationRequest);

        // FusedLocationProviderClient ?????? ??????
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(mContext);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
//        FragmentTransaction ft = getParentFragmentManager().beginTransaction();
//        ft.detach(this).attach(this).commit();

       Bundle bundle = getArguments();
        Log.d("haha","haha2");
       if(bundle!=null) {
           Log.d("haha","haha");
           String documentId = bundle.getString("documentId", "haha");
           if (documentId != "haha")
               drawWalkingRoute(documentId);
       }



//        setDefaultLocation(); // GPS??? ?????? ????????? ????????? ?????? ?????? ????????? ?????? ????????? ?????????.

        getLocationPermission();

        updateLocationUI();

        getDeviceLocation();







    }
    private void changeWalkState(){
        if(walkState==false) {
            Toast.makeText(mContext, "?????? ??????", Toast.LENGTH_SHORT).show();
            walkState = true;
            startLatLng = new LatLng(mCurrentLocatiion.getLatitude(), mCurrentLocatiion.getLongitude());//?????? ????????? ??????????????? ??????
            endLatLng=new LatLng(0,0);
//            ListStartLatLng.add(startLatLng);
        }else{
            Toast.makeText(mContext, "?????? ??????", Toast.LENGTH_SHORT).show();
            walkState = false;
        }
    }
    private void drawPath(){        //polyline??? ???????????? ?????????
        Log.v(TAG,startLatLng.toString()+endLatLng.toString());
        PolylineOptions options = new PolylineOptions().add(startLatLng).add(endLatLng).width(10).color(Color.BLACK).geodesic(true);
        polylines.add(mMap.addPolyline(options));
        double distance=getDistance(startLatLng,endLatLng);
        totalDistance=totalDistance+distance;
        int meter=(int)totalDistance%1000;
        int kilMmeter=(int)totalDistance/1000;
        distacneTextView.setText(""+String.format("%02d",kilMmeter)+"."+String.format("%03d",meter)+"km");
        Log.d("??????",""+totalDistance);
        ListStartLatLng.add(startLatLng);
        ListEndLatLng.add(endLatLng);
        startLatLng=endLatLng;
        isStartExist=true;


//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startLatLng, 15));
    }


    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mCurrentLocatiion = null;
                getLocationPermission();
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void setDefaultLocation() {
        if (currentMarker != null) currentMarker.remove();

//        MarkerOptions markerOptions = new MarkerOptions();
//        markerOptions.position(mDefaultLocation);
//        markerOptions.title("???????????? ????????? ??? ??????");
//        markerOptions.snippet("?????? ???????????? GPS ?????? ?????? ???????????????");
//        markerOptions.draggable(true);
//        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
//        currentMarker = mMap.addMarker(markerOptions);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(mDefaultLocation, 15);
        mMap.moveCamera(cameraUpdate);
    }

    String getCurrentAddress(LatLng latlng) {
        // ?????? ????????? ?????????????????? ?????? ???????????? ?????????.
        List<Address> addressList = null;
        Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());

        // ??????????????? ???????????? ?????? ???????????? ?????????.
        try {
            addressList = geocoder.getFromLocation(latlng.latitude, latlng.longitude, 1);
        } catch (IOException e) {
            Toast.makeText(mContext, "??????????????? ????????? ????????? ??? ????????????. ??????????????? ???????????? ????????? ????????? ?????????.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            return "?????? ?????? ??????";
        }

        if (addressList.size() < 1) { // ?????? ???????????? ??????????????? ?????? ?????????
            return "?????? ????????? ?????? ??????";
        }

        // ????????? ?????? ???????????? ???????????? ??????
        Address address = addressList.get(0);
        StringBuilder addressStringBuilder = new StringBuilder();
        for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
            addressStringBuilder.append(address.getAddressLine(i));
            if (i < address.getMaxAddressLineIndex())
                addressStringBuilder.append("\n");
        }

        return addressStringBuilder.toString();
    }

    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);

            List<Location> locationList = locationResult.getLocations();

            if (locationList.size() > 0) {
                Location location = locationList.get(locationList.size() - 1);

                currentPosition
                        = new LatLng(location.getLatitude(), location.getLongitude());

                String markerTitle = getCurrentAddress(currentPosition);
                String markerSnippet = "??????:" + String.valueOf(location.getLatitude())
                        + " ??????:" + String.valueOf(location.getLongitude());

                Log.d(TAG, "Time :" + CurrentTime() + " onLocationResult : " + markerSnippet);

                //?????? ????????? ?????? ???????????? ??????
                setCurrentLocation(location, markerTitle, markerSnippet);
                mCurrentLocatiion = location;
                if(walkState==true&&isStartExist==false){
                    startLatLng=currentPosition;
                    isStartExist=true;

                }else if (walkState==true&&isStartExist==true){
                    endLatLng=currentPosition;
                    drawPath();
                }else if(walkState==false)
                    mFusedLocationProviderClient.removeLocationUpdates(locationCallback);
            }
        }

    };



    private String CurrentTime() {
        Date today = new Date();
        SimpleDateFormat date = new SimpleDateFormat("yyyy/MM/dd");
        SimpleDateFormat time = new SimpleDateFormat("hh:mm:ss a");
        return time.format(today);
    }

    public void setCurrentLocation(Location location, String markerTitle, String markerSnippet) {
        if (currentMarker != null) currentMarker.remove();

        currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(currentLatLng);
        markerOptions.title(markerTitle);
        markerOptions.snippet(markerSnippet);
        markerOptions.draggable(true);

//        currentMarker = mMap.addMarker(markerOptions);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(currentLatLng,17);
        mMap.moveCamera(cameraUpdate);
    }

    private void getDeviceLocation() {
        try {
            if (mLocationPermissionGranted) {
                mFusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());

            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(mContext,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(mContext,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    public double getDistance(LatLng LatLng1, LatLng LatLng2) {
        double distance = 0;
        Location locationA = new Location("A");
        locationA.setLatitude(LatLng1.latitude);
        locationA.setLongitude(LatLng1.longitude);
        Location locationB = new Location("B");
        locationB.setLatitude(LatLng2.latitude);
        locationB.setLongitude(LatLng2.longitude);
        distance = locationA.distanceTo(locationB);

        return distance;
    }


    @Override
    public void onStart() { // ???????????? Fragment??? ???????????? ?????????.
        super.onStart();
        mapView.onStart();
        Log.d(TAG, "onStart ");
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
        if (mFusedLocationProviderClient != null) {
            Log.d(TAG, "onStop : removeLocationUpdates");
//            mFusedLocationProviderClient.removeLocationUpdates(locationCallback);
        }
    }

    @Override
    public void onResume() { // ???????????? Fragment??? ????????????, ????????? ??????????????? ???????????? ?????? ??????
        super.onResume();
        mapView.onResume();

//        if (mLocationPermissionGranted) {
//            Log.d(TAG, "onResume : requestLocationUpdates");
//            mFusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
//            if (mMap!=null)
//                mMap.setMyLocationEnabled(true);
//        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onDestroyView() { // ?????????????????? ????????? View ??? ???????????? ??????
        super.onDestroyView();
        if (mFusedLocationProviderClient != null) {
            Log.d(TAG, "onDestroyView : removeLocationUpdates");
//            mFusedLocationProviderClient.removeLocationUpdates(locationCallback);
        }
    }

    @Override
    public void onDestroy() {
        // Destroy ??? ??????, ????????? OnDestroyView?????? View??? ????????????, OnDestroy()??? ????????????.
        super.onDestroy();
        if(timer!=null){
            timer.removeMessages(0);
        }
        mapView.onDestroy();
    }

    public void drawWalkingRoute(String documentId){
        Log.e("??? ?????? ??????","??????!");
        FirebaseFirestore firebaseFirestore;
        firebaseFirestore=FirebaseFirestore.getInstance();
        CollectionReference collectionReference = firebaseFirestore.collection("RouteInfo");
        collectionReference.document(documentId).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){

                            DocumentSnapshot d = task.getResult();
                             ArrayList<HashMap<String,Double>> ListStartLatLng2=new ArrayList<>();
                             ArrayList<HashMap<String,Double>> ListEndLatLng2=new ArrayList<>();
                             ArrayList<HashMap<String,Double>> ListTrashLatLng2=new ArrayList<>();
                             ArrayList<HashMap<String,Double>> ListWarningLatLng2=new ArrayList<>();
                            List<Polyline> polylines2=new ArrayList<Polyline>();

                            ListStartLatLng2=(ArrayList<HashMap<String,Double>>) d.getData().get("listStartLatLng");

                            ListEndLatLng2=(ArrayList<HashMap<String,Double>>) d.getData().get("listEndLatLng");

                            if(d.getData().get("listTrashLatLng")!=null) {
                                ListTrashLatLng2 = (ArrayList<HashMap<String, Double>>) d.getData().get("listTrashLatLng");


                            }
                            if(d.getData().get("listWarningLatLng")!=null) {
                                ListWarningLatLng2 = (ArrayList<HashMap<String, Double>>) d.getData().get("listWarningLatLng");

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

                            for(int i=0; i<ListEndLatLng2.size();i++)
                            {
                                startLatitude=ListStartLatLng2.get(i).get("latitude");
                                startLongtitude=ListStartLatLng2.get(i).get("longitude");
                                startLatLng=new LatLng(startLatitude,startLongtitude);

                                endLatitude=ListEndLatLng2.get(i).get("latitude");
                                endLongtitude=ListEndLatLng2.get(i).get("longitude");
                                endLatLng=new LatLng(endLatitude,endLongtitude);

                                PolylineOptions options = new PolylineOptions().add(startLatLng).add(endLatLng).width(10).color(Color.RED).geodesic(true);
                                polylines2.add(mMap.addPolyline(options));
                                Log.d(TAG, "onLocationResult : " + polylines.toString());

                            }
                            for(int i=0; i< ListTrashLatLng2.size(); i++)
                            {
                                trashLatitude=ListTrashLatLng2.get(i).get("latitude");
                                trashLongtitude=ListTrashLatLng2.get(i).get("longitude");
                                trashLatLng=new LatLng(trashLatitude,trashLongtitude);
                                BitmapDrawable bd = (BitmapDrawable) getContext().getResources().getDrawable(R.drawable.baseline_delete_black_48);
                                Bitmap b = bd.getBitmap();
                                Bitmap bitMapImage = Bitmap.createScaledBitmap(b,50,50,false);
                                MarkerOptions markerOptions = new MarkerOptions();
                                markerOptions.position(trashLatLng);
                                markerOptions.title("?????? ?????????");
                                markerOptions.draggable(true);
                                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(bitMapImage));
                                mMap.addMarker(markerOptions);

                            }
                            for(int i=0; i< ListWarningLatLng2.size(); i++)
                            {
                                warningLatitude=ListWarningLatLng2.get(i).get("latitude");
                                warningLongtitude=ListWarningLatLng2.get(i).get("longitude");
                                warningLatLng=new LatLng(warningLatitude,warningLongtitude);
                                BitmapDrawable bd2 = (BitmapDrawable) getContext().getResources().getDrawable(R.drawable.baseline_warning_black_48);
                                Bitmap b2 = bd2.getBitmap();
                                Bitmap bitMapImage2 = Bitmap.createScaledBitmap(b2,50,50,false);
                                MarkerOptions markerOptions2 = new MarkerOptions();
                                markerOptions2.position(warningLatLng);
                                markerOptions2.title("?????? ??????");
                                markerOptions2.draggable(true);
                                markerOptions2.icon(BitmapDescriptorFactory.fromBitmap(bitMapImage2));
                                mMap.addMarker(markerOptions2);
                            }
//                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startLatLng, 18));

                        }
                        else{

                            Toast.makeText(getContext(), "?????? ?????? ??????", Toast.LENGTH_LONG).show();
                        }
                    }
                });


    }

}







