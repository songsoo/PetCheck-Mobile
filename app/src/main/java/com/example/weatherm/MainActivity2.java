package com.example.weatherm;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.weatherm.home2.HomeFragment;
import com.example.weatherm.matching_friends.Matching_friednsFragment;
import com.example.weatherm.profile.ProfileFragment;
import com.example.weatherm.sharing.SharingFragment;
import com.example.weatherm.walking.WalkingFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;


public class MainActivity2 extends AppCompatActivity {

    public static Context context_main2;

    private BottomNavigationView bottomNavigationView;
    public FragmentManager fm;
    public FragmentTransaction ft;
    private WalkingFragment walkingFragment;
    private HomeFragment homeFragment;
    private Matching_friednsFragment matching_friednsFragment;
    private ProfileFragment profileFragment;
    private SharingFragment sharingFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        onCheckPermision();
       super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_main2);

       context_main2 = this;
       bottomNavigationView=findViewById(R.id.bottomNavi);
       bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
           @Override
           public boolean onNavigationItemSelected(@NonNull MenuItem item) {
               int id=item.getItemId();
               switch (id){
                   case R.id.action_home:
                       setFrag(0);
                       break;
                   case R.id.action_walking:
                       setFrag(1);
                       break;
                   case R.id.action_share:
                       setFrag(3);
                       break;
                   case R.id.action_profile:
                       setFrag(4);
                       break;

               }
               return true;
           }
       });
       walkingFragment=new WalkingFragment();
       homeFragment=new HomeFragment();
       matching_friednsFragment=new Matching_friednsFragment();
       profileFragment=new ProfileFragment();
       sharingFragment=new SharingFragment();
       Intent intent=getIntent();



       int flag=intent.getIntExtra("flag",1);
       setFrag(1);
     /*
    if(flag==1)
        setFrag(1);
    else
        setFrag(0);
               String documentId = intent.getStringExtra("documentId");
               if(documentId!=null) {
                   Bundle bundle = new Bundle();
                   bundle.putString("documentId", documentId); //fragment1로 번들 전달 fragment1.setArguments(bundle);
                   walkingFragment.setArguments(bundle);
                   setFrag(1);
              }
      */


    }



   public void setFrag(int n){

       fm=getSupportFragmentManager();
       ft=fm.beginTransaction();

        switch(n){
            case 0:
                finish();
                break;
            case 1:
                ft.replace(R.id.main_frame,walkingFragment);
                ft.commit();
                break;
            case 3:
                ft.replace(R.id.main_frame,sharingFragment);
                ft.commit();
                break;
            case 4:
                ft.replace(R.id.main_frame,profileFragment);
                ft.commit();
                break;

        }
   }

   //위치 권환 요청
    ActivityResultLauncher<String[]> locationPermissionRequest =
            registerForActivityResult(new ActivityResultContracts
                            .RequestMultiplePermissions(), result -> {
                        Boolean fineLocationGranted = null;
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                            fineLocationGranted = result.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false);
                        }
                        Boolean coarseLocationGranted = null;
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                            coarseLocationGranted = result.getOrDefault(
                                    Manifest.permission.ACCESS_COARSE_LOCATION,false);
                        }
                        if (fineLocationGranted != null && fineLocationGranted) {

                            // Precise location access granted.
                        } else if (coarseLocationGranted != null && coarseLocationGranted) {

                            // Only approximate location access granted.
                        } else {
                            // No location access granted.
                        }
                    }
            );

    public void onCheckPermision(){
        if (ContextCompat.checkSelfPermission(
                getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            // You can use the API that requires the permission.
        } else {
            // You can directly ask for the permission.
            locationPermissionRequest.launch(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            });


        }

    }
   public void replaceFragment(Fragment fragment){
       fm=getSupportFragmentManager();
       ft=fm.beginTransaction();
       ft.replace(R.id.main_frame,fragment);
       ft.commit();
   }

}

