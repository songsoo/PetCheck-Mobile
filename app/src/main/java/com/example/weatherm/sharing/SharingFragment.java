package com.example.weatherm.sharing;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherm.MainActivity;
import com.example.weatherm.Model.FreePostInfo;
import com.example.weatherm.R;
import com.example.weatherm.homeFolder.HomeFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class SharingFragment extends Fragment {
    private View rootView;
    private HomeFragment homeFragment;
    //파이어베이스 선언
    private FirebaseFirestore firebaseFirestore;
    //레시피 글을 카드뷰로 띄워주기 위한 리사이클러 뷰 선언
    private RecyclerView postRecyclerView;

    private Button writeButton;

    MainActivity activity;

    // Toolbar 생성
    Toolbar myToolbar;

    //현재 유저 위치
    private LatLng currentUserLatLng;
    private ArrayList<FreePostInfo> free_postList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView=inflater.inflate(R.layout.fragment_sharing,container,false);

        rootView.findViewById(R.id.writePostButton).setOnClickListener(onClickListener);



        //파이어베이스에서 데이터베이스 가져옴
        firebaseFirestore= FirebaseFirestore.getInstance();

        //MainActivity 객체 생성
        activity=(MainActivity)getActivity();

        //리사이클러뷰 작성
        postRecyclerView=(RecyclerView) rootView.findViewById(R.id.post_recyclerView);
        postRecyclerView.setHasFixedSize(true);
        postRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Toolbar를 생성.
        myToolbar = (Toolbar) rootView.findViewById(R.id.my_toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(myToolbar);

        Button walkingRouteButton =rootView.findViewById(R.id.walkingRouteButton);
        walkingRouteButton.setOnClickListener(onClickListener);
        Button dailyButton =rootView.findViewById(R.id.DailyButton);
        dailyButton.setOnClickListener(onClickListener);
        Button tipButton =rootView.findViewById(R.id.TipButton);
        tipButton.setOnClickListener(onClickListener);


        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("동네정보");  //해당 액티비티의 툴바에 있는 타이틀을 바꾸기

        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore=FirebaseFirestore.getInstance();
        DocumentReference documentReference=firebaseFirestore.collection("users").document(user.getUid());
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot result=task.getResult();
                HashMap<String,Double> temp=(HashMap<String,Double>) result.getData().get("address");
                Double latitude,longitude;
                latitude=temp.get("latitude");
                longitude=temp.get("longitude");
                currentUserLatLng=new LatLng(latitude,longitude);
            }
        });

    setHasOptionsMenu(true);
        homeFragment=new HomeFragment();
        return rootView;
    }


    //ToolBar에 menu.xml을 인플레이트함
    @Override
    public void onCreateOptionsMenu(Menu menu,MenuInflater inflater) {
        super.onCreateOptionsMenu(menu,inflater);
        inflater.inflate(R.menu.sharing_toolbar_menu,menu);
    }

    //ToolBar에 추가된 항목의 select 이벤트를 처리하는 함수
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //return super.onOptionsItemSelected(item);
        switch (item.getItemId()) {

            case R.id.sharing_toolbar_search:
                // User chose the "Settings" item, show the app settings UI...
                Toast.makeText(getContext(), "검색 버튼 클릭됨", Toast.LENGTH_LONG).show();
                return true;

            case R.id.sharing_toolbar_notification:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                Toast.makeText(getContext(), "알림 버튼 클릭됨", Toast.LENGTH_LONG).show();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

   View.OnClickListener onClickListener=new View.OnClickListener() {
       @Override
       public void onClick(View view) {
           CollectionReference collectionReference = firebaseFirestore.collection("freePost");
           switch (view.getId()){
               case R.id.writePostButton:
                   Intent intent=new Intent(getActivity(),WritingPostActivity.class);
                   intent.putExtra("flag",0);
                   startActivity(intent);
                   break;

               case R.id.walkingRouteButton:

                   collectionReference
                           //작성일자 내림차순을 정렬
                           .orderBy("createdAt", Query.Direction.DESCENDING)
                           .whereEqualTo("category","산책루트")
                           .get()
                           .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                               @Override
                               public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                   if (task.isSuccessful()) {
                                       free_postList = new ArrayList<>();
                                       for (QueryDocumentSnapshot document : task.getResult()) {
                                           //각 게시글의 정보를 가져와 arrayList에 저장.
                                           Log.d("로그: ", document.getId() + " => " + document.getData());
                                           String routeInfoId;
                                           if(document.getData().get("routeInfoId")==null)
                                               routeInfoId=null;
                                           else
                                               routeInfoId=document.getData().get("routeInfoId").toString();
                                           FreePostInfo freePostInfo=new FreePostInfo(

                                                   document.getData().get("content").toString(),
                                                   document.getData().get("publisherId").toString(),
                                                   new Date(document.getDate("createdAt").getTime()),
                                                   (Long) document.getData().get("recom"),
                                                   (ArrayList<String>) document.getData().get("comment"),
                                                   document.getData().get("postId").toString(),
                                                   (ArrayList<String>) document.getData().get("recomUserId"),
                                                   (ArrayList<String>)  document.getData().get("imageList"),
                                                   document.getData().get("category").toString(),
                                                   document.getData().get("nickname").toString(),
                                                   routeInfoId
                                           );
                                           FirebaseFirestore firestore=FirebaseFirestore.getInstance();
                                           DocumentReference documentReference=firestore.collection("users").document(freePostInfo.getPublisherId());
                                           documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                               @Override
                                               public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                   DocumentSnapshot result=task.getResult();
                                                   HashMap<String,Double> temp=(HashMap<String,Double>) result.getData().get("address");
                                                   Double latitude,longitude;
                                                   latitude=temp.get("latitude");
                                                   longitude=temp.get("longitude");
                                                   LatLng latLng=new LatLng(latitude,longitude);
                                                   if(getDistance(currentUserLatLng,latLng)<2000)
                                                   {
                                                       free_postList.add(freePostInfo);
                                                       //freeAdapter를 이용하여 리사이클러 뷰로 내용 띄움.
                                                       RecyclerView.Adapter mAdapter1 = new freeAdapter(getActivity(), free_postList);
                                                       postRecyclerView.setAdapter(mAdapter1);
                                                   }

                                               }
                                           });
                                       }


                                   } else {
                                       Log.d("로그: ", "Error getting documents: ", task.getException());
                                   }
                               }
                           });
                   break;

               case R.id.DailyButton:
                   collectionReference
                           //작성일자 내림차순을 정렬
                           .orderBy("createdAt", Query.Direction.DESCENDING)
                           .whereEqualTo("category","일상")
                           .get()
                           .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                               @Override
                               public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                   if (task.isSuccessful()) {
                                       free_postList = new ArrayList<>();
                                       for (QueryDocumentSnapshot document : task.getResult()) {
                                           //각 게시글의 정보를 가져와 arrayList에 저장.
                                           Log.d("로그: ", document.getId() + " => " + document.getData());
                                           String routeInfoId;
                                           if(document.getData().get("routeInfoId")==null)
                                               routeInfoId=null;
                                           else
                                               routeInfoId=document.getData().get("routeInfoId").toString();
                                           FreePostInfo freePostInfo=new FreePostInfo(
                                                   document.getData().get("content").toString(),
                                                   document.getData().get("publisherId").toString(),
                                                   new Date(document.getDate("createdAt").getTime()),
                                                   (Long) document.getData().get("recom"),
                                                   (ArrayList<String>) document.getData().get("comment"),
                                                   document.getData().get("postId").toString(),
                                                   (ArrayList<String>) document.getData().get("recomUserId"),
                                                   (ArrayList<String>)  document.getData().get("imageList"),
                                                   document.getData().get("category").toString(),
                                                   document.getData().get("nickname").toString(),
                                                   routeInfoId
                                           );
                                           FirebaseFirestore firestore=FirebaseFirestore.getInstance();
                                           DocumentReference documentReference=firestore.collection("users").document(freePostInfo.getPublisherId());
                                           documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                               @Override
                                               public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                   DocumentSnapshot result=task.getResult();
                                                   HashMap<String,Double> temp=(HashMap<String,Double>) result.getData().get("address");
                                                   Double latitude,longitude;
                                                   latitude=temp.get("latitude");
                                                   longitude=temp.get("longitude");
                                                   LatLng latLng=new LatLng(latitude,longitude);
                                                   if(getDistance(currentUserLatLng,latLng)<2000)
                                                   {
                                                       free_postList.add(freePostInfo);
                                                       //freeAdapter를 이용하여 리사이클러 뷰로 내용 띄움.
                                                       RecyclerView.Adapter mAdapter1 = new freeAdapter(getActivity(), free_postList);
                                                       postRecyclerView.setAdapter(mAdapter1);
                                                   }

                                               }
                                           });
                                       }


                                   } else {
                                       Log.d("로그: ", "Error getting documents: ", task.getException());
                                   }
                               }
                           });
                   break;

               case R.id.TipButton:

                   collectionReference
                           //작성일자 내림차순을 정렬
                           .orderBy("createdAt", Query.Direction.DESCENDING)
                           .whereEqualTo("category","꿀팁")
                           .get()
                           .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                               @Override
                               public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                   if (task.isSuccessful()) {
                                       free_postList = new ArrayList<>();
                                       for (QueryDocumentSnapshot document : task.getResult()) {
                                           //각 게시글의 정보를 가져와 arrayList에 저장.
                                           Log.d("로그: ", document.getId() + " => " + document.getData());
                                           String routeInfoId;
                                           if(document.getData().get("routeInfoId")==null)
                                               routeInfoId=null;
                                           else
                                               routeInfoId=document.getData().get("routeInfoId").toString();
                                           FreePostInfo freePostInfo=new FreePostInfo(
                                                   document.getData().get("content").toString(),
                                                   document.getData().get("publisherId").toString(),
                                                   new Date(document.getDate("createdAt").getTime()),
                                                   (Long) document.getData().get("recom"),
                                                   (ArrayList<String>) document.getData().get("comment"),
                                                   document.getData().get("postId").toString(),
                                                   (ArrayList<String>) document.getData().get("recomUserId"),
                                                   (ArrayList<String>)  document.getData().get("imageList"),
                                                   document.getData().get("category").toString(),
                                                   document.getData().get("nickname").toString(),
                                                   routeInfoId
                                           );
                                           FirebaseFirestore firestore=FirebaseFirestore.getInstance();
                                           DocumentReference documentReference=firestore.collection("users").document(freePostInfo.getPublisherId());
                                           documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                               @Override
                                               public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                   DocumentSnapshot result=task.getResult();
                                                   HashMap<String,Double> temp=(HashMap<String,Double>) result.getData().get("address");
                                                   Double latitude,longitude;
                                                   latitude=temp.get("latitude");
                                                   longitude=temp.get("longitude");
                                                   LatLng latLng=new LatLng(latitude,longitude);
                                                   if(getDistance(currentUserLatLng,latLng)<2000)//2km이내 게시물만띄움
                                                   {
                                                       free_postList.add(freePostInfo);
                                                       //freeAdapter를 이용하여 리사이클러 뷰로 내용 띄움.
                                                       RecyclerView.Adapter mAdapter1 = new freeAdapter(getActivity(), free_postList);
                                                       postRecyclerView.setAdapter(mAdapter1);
                                                   }

                                               }
                                           });
                                       }


                                   } else {
                                       Log.d("로그: ", "Error getting documents: ", task.getException());
                                   }
                               }
                           });
                   break;
               default:
                   break;


           }
       }
   };

    //레시피게시판에 내용이 추가가 될 경우 바로바로 업데이트 해주기 위해 resume함수에 넣어 관리.
    @Override
    public void onResume(){
        super.onResume();

        //freePost에 있는 data를 가져오기 위함.
        CollectionReference collectionReference = firebaseFirestore.collection("freePost");
        collectionReference
                //작성일자 내림차순을 정렬
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            free_postList = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                //각 게시글의 정보를 가져와 arrayList에 저장.
                                Log.d("로그: ", document.getId() + " => " + document.getData());
                                String routeInfoId,content,publisherId,publisherName,postId,category,nickname;
                                ArrayList<String> imageList,recomUserId,comment;
                                Long recom;
                                Date createdAt;
                                if(document.getData().get("routeInfoId")==null)
                                    routeInfoId=null;
                                else
                                    routeInfoId=document.getData().get("routeInfoId").toString();
                                FreePostInfo freePostInfo=new FreePostInfo(
                                        document.getData().get("content").toString(),
                                        document.getData().get("publisherId").toString(),
                                        new Date(document.getDate("createdAt").getTime()),
                                        (Long) document.getData().get("recom"),
                                        (ArrayList<String>) document.getData().get("comment"),
                                        document.getData().get("postId").toString(),
                                        (ArrayList<String>) document.getData().get("recomUserId"),
                                        (ArrayList<String>)  document.getData().get("imageList"),
                                        document.getData().get("category").toString(),
                                        document.getData().get("nickname").toString(),
                                        routeInfoId
                                );
                                FirebaseFirestore firestore=FirebaseFirestore.getInstance();
                                DocumentReference documentReference=firestore.collection("users").document(freePostInfo.getPublisherId());
                                documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        DocumentSnapshot result=task.getResult();
                                        HashMap<String,Double> temp=(HashMap<String,Double>) result.getData().get("address");
                                        Double latitude,longitude;
                                        latitude=temp.get("latitude");
                                        longitude=temp.get("longitude");
                                        LatLng latLng=new LatLng(latitude,longitude);
                                        if(getDistance(currentUserLatLng,latLng)<2000) {
                                            free_postList.add(freePostInfo);
                                            //freeAdapter를 이용하여 리사이클러 뷰로 내용 띄움.
                                        }
                                        RecyclerView.Adapter mAdapter1 = new freeAdapter(getActivity(), free_postList);
                                        postRecyclerView.setAdapter(mAdapter1);
                                    }
                                });
                            }


                        } else {
                            Log.d("로그: ", "Error getting documents: ", task.getException());
                        }
                    }
                });

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
    private void myStartActivity(Class c){
        Intent intent=new Intent( getActivity(), c);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

}
