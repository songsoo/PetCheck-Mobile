package com.example.weatherm.sharing;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.weatherm.Model.CommentInfo;
import com.example.weatherm.Model.FreePostInfo;
import com.example.weatherm.OnItemClick;
import com.example.weatherm.R;
import com.example.weatherm.walking.ShowSavedRoute2;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class InformationActivity extends AppCompatActivity implements OnItemClick, OnMapReadyCallback {

    //?????????
    private RelativeLayout loaderLayout;

    //adapter ?????????
    private OnItemClick onItemClick;

    private FreePostInfo freePostInfo;
    private CommentInfo commentInfo;

    private FirebaseFirestore firebaseFirestore;
    private TextView publisherTextView;
    private TextView createTimeTextView;
    private TextView sympathyTextView;
    private TextView contentTextView;
    private TextView commentNumTextView;
    private EditText commentEditText;
    private ImageView profileImage;
    private ImageView sympathyImage;
    private ImageView commentImage;
    private FloatingActionButton commentCompleteImage;
    private ImageButton addImageButton;
    private RecyclerView imageRecyclerView;
    private RecyclerView commentRecyclerView;
    private RecyclerView commentImageRecyclerView;

    private ArrayList<HashMap<String,Double>> ListStartLatLng=new ArrayList<>();
    private ArrayList<HashMap<String,Double>> ListEndLatLng=new ArrayList<>();
    private ArrayList<HashMap<String,Double>> ListTrashLatLng=new ArrayList<>();
    private ArrayList<HashMap<String,Double>> ListWarningLatLng=new ArrayList<>();
    private ArrayList<LatLng> ListStartLatLng2=new ArrayList<>();
    private ArrayList<LatLng> ListEndLatLng2=new ArrayList<>();
    private ArrayList<LatLng> ListTrashLatLng2=new ArrayList<>();
    private ArrayList<LatLng> ListWarningLatLng2=new ArrayList<>();

    private String comment_content;
    private String commentProfileImage;
    private String currentUserId;
    private String userName;
    private String publisher;
    private String publisherId;
    private String postId;
    private MapView mapView;
    private GoogleMap mMap;
    private String documentId;
    private ArrayList<Polyline> polylines=new ArrayList<>();
    private long sympathyNum;
    private int commentNum;
    private Date createTime;
    private String content;
    private ArrayList<String> imageList;
    private ArrayList<String> commentImageList=new ArrayList<>();
    private ArrayList<String> commentList;
    private ArrayList<String> sympathyUserList;
    private ArrayList<CommentInfo> commentInfoArrayList;
    ArrayList<String> filenameList= new ArrayList<>();
    private ArrayList<Uri> uriList;

    private MultiImageAdapter multiImageAdapter;
    private MultiImageAdapter2 multiImageAdapter2;

    private GridLayoutManager mGridLayoutManager;
    public CommentAdapter commentAdapter;

    private int count;
    private int flag;
    private int flag2;


    //
    private ActivityResultLauncher<Intent> resultLauncher;
    private FirebaseStorage storage;

    private Toolbar myToolbar;
    @Override
    protected void onCreate(Bundle bundleSavedInstance){
        super.onCreate(bundleSavedInstance);
        setContentView(R.layout.activity_information);

        Activity activity=InformationActivity.this;

        loaderLayout = findViewById(R.id.loaderLayout);
        ConstraintLayout layout=findViewById(R.id.mapShowLayout_information);


        // Toolbar??? ??????.
        myToolbar = (Toolbar) findViewById(R.id.writingpost_toolbar);
        setSupportActionBar(myToolbar);

        //Toolbar??? ????????? ????????? ???????????? ????????? ???????????? ?????????.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.left_arrow_24);
        getSupportActionBar().setTitle("");  //??? ??????????????? ????????? ?????? ???????????? ?????????




        Intent intent=getIntent();

        freePostInfo=(FreePostInfo) intent.getSerializableExtra("freePostInfo");
        documentId=freePostInfo.getRouteInfoId();
        if(documentId!=null)
        {
            Button button=findViewById(R.id.showRouteButton);
            button.setVisibility(View.VISIBLE);
            //onmapreadycallback ?????? ?????????
            mapView = (MapView) findViewById(R.id.map);
            mapView.onCreate(null);
            mapView.setVisibility(View.VISIBLE);
            mapView.getMapAsync(this);
            if(mapView != null)
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(InformationActivity.this, ShowSavedRoute2.class);
                        intent.putExtra("routeInfo", documentId);
                        startActivity(intent);
                    }
                });
        }


        publisherId=freePostInfo.getPublisherId();//????????? ID
        publisher=freePostInfo.getNickname();//????????? ??????
        sympathyNum=freePostInfo.getRecom();// ?????????
        sympathyUserList=freePostInfo.getRecomUserId();
        if(sympathyUserList==null)
            sympathyUserList=new ArrayList<String>();
        createTime=freePostInfo.getCreatedAt();//?????????
        content=freePostInfo.getContent();//??????
        imageList=freePostInfo.getImageList();//?????? ?????????
        commentList=freePostInfo.getComment();//??????

        if(commentList==null||commentList.size()<=0)
            commentList=new ArrayList<String>();

        if(commentList!=null)
        commentNum=commentList.size();//?????? ???

        postId=freePostInfo.getPostId();





        profileImage=findViewById(R.id.profileImageVIew2);
        publisherTextView=findViewById(R.id.freePublisher1);
        createTimeTextView=findViewById(R.id.freeCreatedAt);
        sympathyTextView=findViewById(R.id.sympahty_textVeiw);
        contentTextView=findViewById(R.id.postContent);
        imageRecyclerView=findViewById(R.id.recyclerview3);
        sympathyImage=findViewById(R.id.sympathy_imageView);
        commentNumTextView=findViewById(R.id.comment_number_textVeiw);
        commentRecyclerView=findViewById(R.id.comment_List_recyclerView);
        commentRecyclerView.setHasFixedSize(true);
        commentRecyclerView.setLayoutManager(new LinearLayoutManager(InformationActivity.this));
        commentImageRecyclerView=findViewById(R.id.comment_imageList_recyclerView);
        addImageButton=findViewById(R.id.titleImageButton_information);
        commentEditText=findViewById(R.id.writing_comment);
        commentCompleteImage=findViewById(R.id.comment_completeImageVeiw);

        sympathyImage.setOnClickListener(onClickListener);
        sympathyTextView.setOnClickListener(onClickListener);
        addImageButton.setOnClickListener(onClickListener);
        commentCompleteImage.setOnClickListener(onClickListener);


        //????????? ??????
        firebaseFirestore= FirebaseFirestore.getInstance();
        DocumentReference dr = firebaseFirestore.collection("users").document(publisherId);
        dr.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()&&profileImage!=null) {
                        String photoURL=document.getData().get("photoUrl").toString();
                        Glide.with(getApplicationContext()).load(photoURL).centerCrop().into(profileImage);
                    }
                }
            }
        });


        //????????? ??????
        publisherTextView.setText(publisher);

        //?????? ??????
        createTimeTextView.setText(new SimpleDateFormat("MM-dd hh:mm", Locale.KOREA).format(createTime));

        //?????? ???
        sympathyTextView.setText("?????????: "+ (int) sympathyNum);

        //????????? ??????
        contentTextView.setText(content);

        //???????????? ?????? ??????
        uriList = new ArrayList<>();
        String tempString;
        if(imageList!=null) {
            for (int i = 0; i < imageList.size(); i++) {
                tempString = imageList.get(i);
                uriList.add(Uri.parse(tempString));
            }
            multiImageAdapter = new MultiImageAdapter(uriList, activity);
            imageRecyclerView.setAdapter(multiImageAdapter);
            int numberOfColumns = uriList.size(); // ????????? 5?????? ????????? ???????????????.
            mGridLayoutManager = new GridLayoutManager(activity, numberOfColumns);
            imageRecyclerView.setLayoutManager(mGridLayoutManager);
        }

        //?????? ??? ??????
        commentNumTextView.setText("?????? "+commentNum);



        commentInfoArrayList=new ArrayList<>();
        flag2=0;
        if(commentList!=null) {
            for (int i = 0; i < commentList.size(); i++) {
                flag2++;
                String commentId = commentList.get(i);

                DocumentReference dr2 = firebaseFirestore.collection("comment").document(commentId);
                dr2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                commentInfoArrayList.add(new CommentInfo(
                                        document.getData().get("commentId").toString(),
                                        document.getData().get("publisherId").toString(),
                                        document.getData().get("publisher").toString(),
                                        document.getData().get("content").toString(),
                                        (ArrayList<String>) document.getData().get("imageList"),
                                        new Date(document.getDate("createdTime").getTime()),
                                        document.getData().get("profileImage").toString(),
                                        document.getData().get("postId").toString()
                                ));
                                if (flag2 == commentList.size()) {
                                    commentAdapter = new CommentAdapter(InformationActivity.this, commentInfoArrayList);
                                    commentRecyclerView.setAdapter(commentAdapter);
                                }

                            }

                        }
                    }
                });
            }
        }

        storage = FirebaseStorage.getInstance();
        //???????????? ?????? ??????, ?????? ????????? ??????
        resultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == RESULT_OK) {
                            Intent data = result.getData();
                            int CallType = data.getIntExtra("CallType", 0);
                            if (CallType == 0) {

                                Log.d("????????? ??????", "????????? ??????");
                                //????????? ??????
                                //????????? ????????? ?????? ??????
                                //????????? ???????????? ????????? ????????? ??????
                                FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
                                final DocumentReference documentReference = firebaseFirestore.collection("freePost").document();

                                if (data == null) {   // ?????? ???????????? ???????????? ?????? ??????
                                    Toast.makeText(getApplicationContext(), "???????????? ???????????? ???????????????.", Toast.LENGTH_LONG).show();
                                }  else {      // ???????????? ????????? ????????? ??????
                                    ClipData clipData = data.getClipData();
                                    Log.e("clipData", String.valueOf(clipData.getItemCount()));



                                    if (clipData.getItemCount() > 10) {   // ????????? ???????????? 11??? ????????? ??????
                                        Toast.makeText(getApplicationContext(), "????????? 10????????? ?????? ???????????????.", Toast.LENGTH_LONG).show();
                                    } else {   // ????????? ???????????? 1??? ?????? 10??? ????????? ??????

                                        Log.e(TAG, "multiple choice");
                                        uriList.clear();
                                        filenameList.clear();
                                        commentImageList.clear();
                                        count=0;
                                        for (int i = 0; i < clipData.getItemCount(); i++) {
                                            Uri imageUri = clipData.getItemAt(i).getUri();  // ????????? ??????????????? uri??? ????????????.
                                            String filename =documentReference.getId()+"/"+i + ".jpg"; //????????? ?????????
                                            StorageReference storageRef = storage.getReferenceFromUrl("gs://joggingtracker-6df54.appspot.com");//storage ????????? ??????
                                            //Storage??? ?????? ??????
                                            final StorageReference riversRef = storageRef.child("freePost/" + filename);


                                            uriList.add(imageUri);  //uri??? list??? ?????????.
                                            filenameList.add(filename);


                                            commentImageRecyclerView.setVisibility(View.VISIBLE);
                                            multiImageAdapter2 = new MultiImageAdapter2(uriList,filenameList, getApplicationContext(),onItemClick);
                                            commentImageRecyclerView.setAdapter(multiImageAdapter2);   // ????????????????????? ????????? ??????
                                            int numberOfColumns = 5; // ????????? 5?????? ????????? ???????????????.
                                            mGridLayoutManager = new GridLayoutManager(activity, numberOfColumns);
                                            commentImageRecyclerView.setLayoutManager(mGridLayoutManager);


//                                            recyclerView.addItemDecoration(new ItemDecoration(getApplicationContext()));

                                        }
                                    }

                                }

                            } else {
                                Log.d("??????", "??? calltype 1?????? ??????");
                            }
                        }
                    }

                });


    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }


    @Override
    public void onResume() {
        super.onResume();

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

    //?????? ?????? ??????????????????.
    @Override
    public void onClick(int value){
        uriList.remove(value);
    }

    //????????? ???????????? ?????????
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            DocumentReference documentReference;
            switch (v.getId()) {

                case R.id.sympathy_imageView:
                    if(sympathyUserList.contains(currentUserId)) {
                        sympathyNum=sympathyNum-1;
                        freePostInfo.setRecom(sympathyNum );
                        sympathyUserList.remove(currentUserId);
                        freePostInfo.setRecomUserId(sympathyUserList);
                        sympathyTextView.setText("????????? "+ sympathyNum);


                        documentReference = firebaseFirestore.collection("freePost").document(postId);
                        documentReference.set(freePostInfo)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(InformationActivity.this, "??????????????? ???????????????!",Toast.LENGTH_LONG).show();
                                        Log.w(TAG,"Success writing document" + documentReference.getId());
//                                        onResume();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(InformationActivity.this, "??????????????? ???????????????!",Toast.LENGTH_LONG).show();
                                Log.w(TAG,"Error writing document", e);
                            }
                        });
                    }else{
                        sympathyNum=sympathyNum+1;
                        freePostInfo.setRecom(sympathyNum );
                        sympathyUserList.add(currentUserId);
                        freePostInfo.setRecomUserId(sympathyUserList);
                        sympathyTextView.setText("????????? "+ sympathyNum);


                        documentReference = firebaseFirestore.collection("freePost").document(postId);
                        documentReference.set(freePostInfo)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(InformationActivity.this, "??????????????? ???????????????!",Toast.LENGTH_LONG).show();
                                        Log.w(TAG,"Success writing document" + documentReference.getId());
//                                        onResume();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(InformationActivity.this, "??????????????? ???????????????!",Toast.LENGTH_LONG).show();
                                Log.w(TAG,"Error writing document", e);
                            }
                        });

                    }

                    break;
                case R.id.sympahty_textVeiw:
                    if(sympathyUserList.contains(currentUserId)) {

                        sympathyNum=sympathyNum-1;
                        freePostInfo.setRecom(sympathyNum);
                        sympathyUserList.remove(currentUserId);
                        freePostInfo.setRecomUserId(sympathyUserList);
                        sympathyTextView.setText("????????? "+ sympathyNum);


                        documentReference = firebaseFirestore.collection("freePost").document(postId);
                        documentReference.set(freePostInfo)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(getApplicationContext(), "????????? ?????????????????????!",Toast.LENGTH_LONG).show();
                                        Log.w(TAG,"Success writing document" + documentReference.getId());
//                                        onResume();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(), "??????????????? ???????????????!",Toast.LENGTH_LONG).show();
                                Log.w(TAG,"Error writing document", e);
                            }
                        });
                    }else{
                        sympathyNum=sympathyNum+1;
                        freePostInfo.setRecom(sympathyNum);
                        sympathyUserList.add(currentUserId);
                        freePostInfo.setRecomUserId(sympathyUserList);
                        sympathyTextView.setText("????????? "+ sympathyNum);


                        documentReference = firebaseFirestore.collection("freePost").document(postId);
                        documentReference.set(freePostInfo)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(InformationActivity.this, "?????????????????????!",Toast.LENGTH_LONG).show();
                                        Log.w(TAG,"Success writing document" + documentReference.getId());
//                                        onResume();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(InformationActivity.this, "??????????????? ???????????????!",Toast.LENGTH_LONG).show();
                                Log.w(TAG,"Error writing document", e);
                            }
                        });

                    }

                    break;
                case R.id.titleImageButton_information:
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.putExtra("CallType", 1);
                    intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    resultLauncher.launch(intent);
                    break;

                case R.id.comment_completeImageVeiw:
                    commentUpload();
                    break;

            }
        }

    };

    //?????? ????????? ??????
    private void commentUpload(){

        //????????? ?????? ??????
        final DocumentReference documentReference = commentInfo
                == null ? firebaseFirestore.collection("comment").document()
                : firebaseFirestore.collection("comment").document(commentInfo.getCommentId());

        loaderLayout.setVisibility(View.VISIBLE);
        flag=0;
        if(filenameList.size()>0) {
            for (int i = 0; i < uriList.size(); i++) {
                try {
                    String filename = filenameList.get(i);
                    Uri imageUri = uriList.get(i);
                    StorageReference storageRef = storage.getReferenceFromUrl("gs://joggingtracker-6df54.appspot.com");//storage ????????? ??????
                    //Storage??? ?????? ??????
                    final StorageReference riversRef = storageRef.child("freePost/" + filename);
                    //firestore??? storage uri ??????
                    UploadTask uploadTask = riversRef.putFile(imageUri);

                    // Register observers to listen for when the download is done or if it fails
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("????????? ????????? ??????", filename);

                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            count++;
                            if (count == uriList.size())
                                loaderLayout.setVisibility(View.GONE);
                            Log.e("????????? ????????? ??????", filename);
                            Toast.makeText(getApplicationContext(), "?????? ?????????" + count + "??????", Toast.LENGTH_LONG).show();
                            storageRef.child("freePost/" + filename).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    commentImageList.add(uri.toString());
                                    flag++;
                                    if (flag == filenameList.size()) {

                                        //????????? ?????? ?????? ????????? arrayList ??????
                                        //?????? ?????? ?????? ????????? ??????????????????
                                        comment_content=(String) commentEditText.getText().toString();
                                        if (comment_content.length() > 0 ) {
                                            //???????????? firebase??? ????????? ???????????? ????????? ??????

                                            Log.d(TAG, "????????? ????????? ???");

                                            if (commentImageList != null) {
                                                FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
                                                currentUserId=firebaseUser.getUid();
                                                Log.e("?????????????????????",currentUserId);

                                                DocumentReference dr = firebaseFirestore.collection("users").document(currentUserId);
                                                dr.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            DocumentSnapshot document = task.getResult();
                                                            if (document.exists()&&profileImage!=null) {


                                                                String photoURL=document.getData().get("photoUrl").toString();
                                                                commentProfileImage=photoURL;
                                                                userName=document.getData().get("nickname").toString();
                                                                Log.e("??????????????????",userName);
                                                                comment_content=(String) commentEditText.getText().toString();
                                                                commentList.add(documentReference.getId());
                                                                freePostInfo.setComment(commentList);
                                                                Log.e("??????????????????",""+commentList.size());
                                                                commentInfo=new CommentInfo(documentReference.getId(),currentUserId,userName,comment_content,commentImageList,new Date(),commentProfileImage,freePostInfo.getPostId());
                                                                dbUploader(documentReference, commentInfo);
                                                                DocumentReference documentReference2 = firebaseFirestore.collection("freePost").document(postId);
                                                                documentReference2.set(freePostInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void unused) {

                                                                    }
                                                                });

                                                            }
                                                        }
                                                    }
                                                });
                                            } else {
                                                //?????? ????????? ???????????? ????????? ????????????????????? ???????????? ?????? ????????? ?????? ??????

                                            }
                                        } else {
                                            Toast.makeText(getApplicationContext(), "????????? ???????????????", Toast.LENGTH_SHORT).show();
                                        }


                                    }
                                }
                            });
                        }
                    });

                } catch (Exception e) {
                    Log.e(TAG, "File select error", e);
                }
            }
        }
        else{
            FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
            currentUserId=firebaseUser.getUid();
            Log.e("?????????????????????",currentUserId);

            DocumentReference dr = firebaseFirestore.collection("users").document(currentUserId);
            dr.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()&&profileImage!=null) {

                            String photoURL=document.getData().get("photoUrl").toString();
                            commentProfileImage=photoURL;
                            userName=document.getData().get("nickname").toString();
                            Log.e("??????????????????",userName);
                            comment_content=(String) commentEditText.getText().toString();
                            commentList.add(documentReference.getId());
                            freePostInfo.setComment(commentList);
                            commentInfo=new CommentInfo(documentReference.getId(),currentUserId,userName,comment_content,new Date(),commentProfileImage,freePostInfo.getPostId());
                            dbUploader(documentReference, commentInfo);
                            DocumentReference documentReference2 = firebaseFirestore.collection("freePost").document(postId);
                            documentReference2.set(freePostInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {

                                }
                            });

                        }
                    }
                }
            });

        }


    }

    //????????? ????????????
    //????????? ??????
    private void dbUploader(DocumentReference documentReference , CommentInfo commentInfo){
        documentReference.set(commentInfo)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        loaderLayout.setVisibility(View.GONE);
                        Log.w(TAG,"Success writing document" + documentReference.getId()+"?????????????????????");
                        Intent intent=new Intent(getApplicationContext(),InformationActivity.class);
                        intent.putExtra("freePostInfo", freePostInfo);
                        startActivity(intent);
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                loaderLayout.setVisibility(View.GONE);
                Log.w(TAG,"Error writing document", e);
            }
        });
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        firebaseFirestore=FirebaseFirestore.getInstance();
        DocumentReference documentReference = firebaseFirestore.collection("RouteInfo").document(documentId);
        documentReference.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){

                            DocumentSnapshot d = task.getResult();

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

                            }
                            for(int i=0; i< ListTrashLatLng.size(); i++)
                            {
                                trashLatitude=ListTrashLatLng.get(i).get("latitude");
                                trashLongtitude=ListTrashLatLng.get(i).get("longitude");
                                trashLatLng=new LatLng(trashLatitude,trashLongtitude);
                                BitmapDrawable bd = (BitmapDrawable) InformationActivity.this.getResources().getDrawable(R.drawable.baseline_delete_black_48);
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
                                BitmapDrawable bd2 = (BitmapDrawable) InformationActivity.this.getResources().getDrawable(R.drawable.baseline_warning_black_48);
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

                            Toast.makeText(InformationActivity.this, "?????? ?????? ??????", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

}
