package com.example.weatherm.profile;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.weatherm.Model.MemberInfo;
import com.example.weatherm.R;
import com.example.weatherm.sharing.freeAdapter;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;

public class FriendListActivity  extends AppCompatActivity {
    private ArrayList<String> friendList=new ArrayList<>();
    private ArrayList<MemberInfo> memberInfoList=new ArrayList<>();
    private FirebaseFirestore firestore;
    private FirebaseUser currentUser;
    private Toolbar myToolbar;
    private RecyclerView recyclerView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friendlist);

        //리사이클러뷰 작성
        recyclerView=(RecyclerView) findViewById(R.id.friend_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(FriendListActivity.this));


        firestore=FirebaseFirestore.getInstance();
        currentUser= FirebaseAuth.getInstance().getCurrentUser();
        DocumentReference documentReference=firestore.collection("users").document(currentUser.getUid());
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot d=task.getResult();
                friendList=(ArrayList<String>) d.getData().get("friendList");
//                Log.d("친구 수",""+friendList.size());
//                Log.d("친구 목록",friendList.get(0));
                if(friendList!=null) {
                    for (int i = 0; i < friendList.size(); i++) {
                        DocumentReference documentReference1 = firestore.collection("users").document(friendList.get(i));
                        documentReference1.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                Log.d("친구 목록", friendList.get(0));
                                DocumentSnapshot result = task.getResult();
                                HashMap<String, Double> hashMap = (HashMap<String, Double>) result.getData().get("address");
                                MemberInfo memberInfo = new MemberInfo(

                                        result.getData().get("uid").toString(),
                                        result.getData().get("date").toString(),
                                        result.getData().get("photoUrl").toString(),
                                        result.getData().get("nickname").toString(),
                                        (ArrayList<String>) result.getData().get("walkingList"),
                                        (ArrayList<String>) result.getData().get("bookmarkRouteList"),
                                        (ArrayList<String>) result.getData().get("routeNameList"),
                                        (ArrayList<String>) result.getData().get("friendList"),
                                        result.getData().get("gender").toString(),
                                        result.getData().get("myProfile").toString(),
                                        getLatLng(hashMap)
                                );
                                memberInfoList.add(memberInfo);
                                RecyclerView.Adapter mAdapter1 = new FriendListAdapter(FriendListActivity.this, memberInfoList);
                                recyclerView.setAdapter(mAdapter1);
                            }
                        });
                    }
                }



            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }

        });




        // Toolbar를 생성.
        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);


        //Toolbar의 왼쪽에 버튼을 추가하고 버튼의 아이콘을 바꾼다.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.left_arrow_24);
        getSupportActionBar().setTitle("친구 목록");  //당 액티비티의 툴바에 있는 타이틀을 바꾸기



    }

    public LatLng getLatLng(HashMap<String,Double> hashmap){
        double latitude=hashmap.get("latitude");
        double longitude=hashmap.get("longitude");
        LatLng latLng=new LatLng(latitude,longitude);
        return latLng;
    }
}
