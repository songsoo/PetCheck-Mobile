package com.example.weatherm.sharing;

import static com.example.weatherm.Util.Util.showToast;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.loader.content.CursorLoader;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherm.Model.FreePostInfo;
import com.example.weatherm.Model.MemberInfo;
import com.example.weatherm.Model.RouteInfo;
import com.example.weatherm.OnItemClick;
import com.example.weatherm.R;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;


public class WritingPostActivity extends AppCompatActivity  implements OnItemClick {


    //????????? resultcode
    private final int GALLERY_CODE = 10;

    private static final String TAG ="writingFreePostActivity";
    //?????? ??????
    private FirebaseUser user;
    //????????? ??????
    private RelativeLayout loaderLayout;
    //Firebase ??????
    private FirebaseFirestore firebaseFirestore;

    ArrayList<Uri> uriList = new ArrayList<>();     // ???????????? uri??? ?????? ArrayList ??????
    ArrayList<String> filenameList= new ArrayList<>();
    RecyclerView recyclerView;  // ???????????? ????????? ??????????????????
    MultiImageAdapter2 adapter;  // ????????????????????? ???????????? ?????????

    private EditText editTitle_Free;

    private EditText editContent_Free;

    private ImageButton titleImageButton;
    private Spinner categorySpinner;
    private ConstraintLayout mapLayout;

    private Toolbar myToolbar;

    private String category;

    private FreePostInfo freePostInfo;
    private ArrayList<String> comment;
    private ArrayList<String> recomUser;
    private long recom;
    public String imagepath;
    private int count;
    private int flag;
    private String documentId;

    //
    private ActivityResultLauncher<Intent> resultLauncher;

    private FirebaseStorage storage;

    private Uri downloadUri;

    private GridLayoutManager mGridLayoutManager;

    private OnItemClick onItemClick;


    //????????? ????????? ?????????
    private ArrayList<String> titleImagePathList;
    //dbUploader

    private RouteInfo routeInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        titleImagePathList=new ArrayList<String>();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writing_post);

        loaderLayout = findViewById(R.id.loaderLayout);
        //????????? User data ?????????.
        user = FirebaseAuth.getInstance().getCurrentUser();
        //?????????????????? ?????????
        firebaseFirestore = FirebaseFirestore.getInstance();
        editContent_Free = findViewById(R.id.editContent_Free);
        freePostInfo = (FreePostInfo)getIntent().getSerializableExtra("freePostInfo");

        // Toolbar??? ??????.
        myToolbar = (Toolbar) findViewById(R.id.writingpost_toolbar);
        setSupportActionBar(myToolbar);

        //Toolbar??? ????????? ????????? ???????????? ????????? ???????????? ?????????.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.left_arrow_24);
        getSupportActionBar().setTitle("????????? ?????????");  //??? ??????????????? ????????? ?????? ???????????? ?????????

        titleImageButton=findViewById(R.id.titleImageButton);
        titleImageButton.setOnClickListener(onClickListener);
        categorySpinner=findViewById(R.id.category_Spinner);
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                category = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        recyclerView=findViewById(R.id.recyclerView_information_photo);


        storage = FirebaseStorage.getInstance();
        verifyStoragePermissions(this);

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
                                            titleImagePathList.clear();
                                            count=0;
                                            for (int i = 0; i < clipData.getItemCount(); i++) {
                                                Uri imageUri = clipData.getItemAt(i).getUri();  // ????????? ??????????????? uri??? ????????????.
                                                String filename =documentReference.getId()+"/"+i + ".jpg"; //????????? ?????????
                                                StorageReference storageRef = storage.getReferenceFromUrl("gs://joggingtracker-6df54.appspot.com");//storage ????????? ??????
                                                //Storage??? ?????? ??????
                                                final StorageReference riversRef = storageRef.child("freePost/" + filename);


                                                    uriList.add(imageUri);  //uri??? list??? ?????????.
                                                    filenameList.add(filename);

                                                    recyclerView.setVisibility(View.VISIBLE);
                                            adapter = new MultiImageAdapter2(uriList,filenameList, getApplicationContext(),onItemClick);
                                            recyclerView.setAdapter(adapter);   // ????????????????????? ????????? ??????
                                            int numberOfColumns = 5; // ????????? 5?????? ????????? ???????????????.
                                            mGridLayoutManager = new GridLayoutManager(getApplicationContext(), numberOfColumns);
                                            recyclerView.setLayoutManager(mGridLayoutManager);


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


        Intent intent=getIntent();
        int flag=intent.getIntExtra("flag",0);
        //?????????????????? ????????? ???
        if(flag==1)
        {
            documentId=intent.getStringExtra("RouteInfo");
            mapLayout=findViewById(R.id.mapShowLayout_information);
            mapLayout.setVisibility(View.VISIBLE);
            CollectionReference collectionReference = firebaseFirestore.collection("RouteInfo");
            collectionReference.document(documentId).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            DocumentSnapshot d = task.getResult();
                            if(d.getData().get("walkingContent")!=null)
                            editContent_Free.setText(d.getData().get("walkingContent").toString());
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });

        }
        else
        {
            documentId=null;
        }




        postInit();
    }

    //?????? ?????? ??????????????????.
    @Override
    public void onClick(int value){
        uriList.remove(value);
    }
//    //Uri ????????????
//    private void geturi(Uri uri){
//        this.titleImagePathList.add(uri.toString());
//    }
    //????????? ???????????? ?????????
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {

                case R.id.titleImageButton:

                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.putExtra("CallType", 1);
                    intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    resultLauncher.launch(intent);
                    break;

            }
        }

    };
    // ToolBar??? menu.xml??? ??????????????????
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //return super.onCreateOptionsMenu(menu);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.writing_post__toolbar_menu, menu);
        return true;
    }

    //ToolBar??? ????????? ????????? select ???????????? ???????????? ??????
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //return super.onOptionsItemSelected(item);
        switch (item.getItemId()) {

            //????????? ??????????????? ???????????? ????????? ?????????
            case R.id.writingpost_toolbar_complete:
                bulletinUpload();
                return true;

                //android.R.id.home ??? ????????? ??? ???????????? ????????? ?????????.
            case android.R.id.home:
               finish();
               return true;

            default:
               return true;
        }
    }

    //????????? FireBase??? ????????? ????????????
    private void bulletinUpload(){

        //?????? ?????????
        final String content = editContent_Free.getText().toString();

        //????????? ?????? ??????
        final DocumentReference documentReference = freePostInfo
                == null ? firebaseFirestore.collection("freePost").document()
                : firebaseFirestore.collection("freePost").document(freePostInfo.getPostId());
        loaderLayout.setVisibility(View.VISIBLE);
        flag=0;
        if(documentId==null) {
            if (uriList.size() != 0) {
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
                                        titleImagePathList.add(uri.toString());
                                        flag++;
                                        if (flag == filenameList.size()) {

                                            //????????? ?????? ?????? ????????? arrayList ??????
                                            //?????? ?????? ?????? ????????? ??????????????????
                                            if (content.length() > 0 && category != null) {
                                                //???????????? firebase??? ????????? ???????????? ????????? ??????

                                                Log.d(TAG, "????????? ????????? ???");


                                                if (freePostInfo != null) {
                                                    recom = freePostInfo.getRecom();
                                                    recomUser = freePostInfo.getRecomUserId();
                                                    comment = freePostInfo.getComment();

                                                } else {
                                                    recom = 0;
                                                    comment = new ArrayList<>();
                                                    recomUser = new ArrayList<>();
                                                }


                                                if (titleImagePathList != null) {
                                                    //?????? ????????? ???????????? ????????? ????????????????????? ???????????? ?????? ????????? ?????? ??????
                                                    firebaseFirestore.collection("users").document(user.getUid()).get()
                                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                    Log.d(TAG, "???????????? ??????");
                                                                    if (task.isSuccessful()) {
                                                                        DocumentSnapshot document = task.getResult();
                                                                        HashMap<String ,Double> hashMap=(HashMap<String, Double>) document.getData().get("address");
                                                                        if (document.exists()) {
                                                                            MemberInfo userInfo = new MemberInfo(
                                                                                    document.getData().get("name").toString(),
                                                                                    document.getData().get("phoneNumber").toString(),
                                                                                    document.getData().get("adress").toString(),
                                                                                    document.getData().get("date").toString(),
                                                                                    document.getData().get("photoUrl").toString(),
                                                                                    document.getData().get("nickname").toString(),
                                                                                    (ArrayList<String>) document.getData().get("bookmarkRouteList"),
                                                                                    (ArrayList<String>) document.getData().get("walkingList"),
                                                                                    (ArrayList<String>) document.getData().get("routeNameList"),
                                                                                    getLatLng(hashMap)

                                                                            );
                                                                            Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                                                                            //???????????? ?????? ?????? ????????? ???????????? freePost ????????? ???????????? ????????? ??????.
                                                                            //freepostInfo ???????????? ??????.
                                                                            FreePostInfo freePostInfo = new FreePostInfo(content, user.getUid(), userInfo.getName(), new Date(), recom, comment, documentReference.getId(), recomUser, titleImagePathList, category, userInfo.getNickname(), documentId);
                                                                            dbUploader(documentReference, freePostInfo);
                                                                        } else {
                                                                            Log.d(TAG, "No such document");
                                                                        }
                                                                    } else {
                                                                        Log.d(TAG, "get failed with ", task.getException());
                                                                    }
                                                                }
                                                            });
                                                } else {
                                                    //?????? ????????? ???????????? ????????? ????????????????????? ???????????? ?????? ????????? ?????? ??????

                                                }
                                            } else {
                                                showToast(WritingPostActivity.this, "????????? ????????? ??????????????????!");
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
            } else {
                firebaseFirestore.collection("users").document(user.getUid()).get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                Log.d(TAG, "???????????? ??????");
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    HashMap<String ,Double> hashMap=(HashMap<String, Double>) document.getData().get("address");
                                    if (document.exists()) {
                                        MemberInfo userInfo = new MemberInfo(
                                                document.getData().get("name").toString(),
                                                document.getData().get("phoneNumber").toString(),
                                                document.getData().get("adress").toString(),
                                                document.getData().get("date").toString(),
                                                document.getData().get("photoUrl").toString(),
                                                document.getData().get("nickname").toString(),
                                                (ArrayList<String>) document.getData().get("bookmarkRouteList"),
                                                (ArrayList<String>) document.getData().get("walkingList"),
                                                (ArrayList<String>) document.getData().get("routeNameList"),
                                                getLatLng(hashMap)


                                        );
                                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                                        //???????????? ?????? ?????? ????????? ???????????? freePost ????????? ???????????? ????????? ??????.
                                        //freepostInfo ???????????? ??????.
                                        FreePostInfo freePostInfo = new FreePostInfo(content, user.getUid(), userInfo.getName(), new Date(), recom, comment, documentReference.getId(), recomUser, category, userInfo.getNickname(), documentId);
                                        dbUploader(documentReference, freePostInfo);
                                    } else {
                                        Log.d(TAG, "No such document");
                                    }
                                } else {
                                    Log.d(TAG, "get failed with ", task.getException());
                                }
                            }
                        });

            }
        }//document id==null
        else{
            firebaseFirestore.collection("users").document(user.getUid()).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            Log.d(TAG, "???????????? ??????");
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                HashMap<String ,Double> hashMap=(HashMap<String, Double>) document.getData().get("address");
                                if (document.exists()) {
                                    MemberInfo userInfo = new MemberInfo(
                                            document.getData().get("name").toString(),
                                            document.getData().get("phoneNumber").toString(),
                                            document.getData().get("adress").toString(),
                                            document.getData().get("date").toString(),
                                            document.getData().get("photoUrl").toString(),
                                            document.getData().get("nickname").toString(),
                                            (ArrayList<String>) document.getData().get("bookmarkRouteList"),
                                            (ArrayList<String>) document.getData().get("walkingList"),
                                            (ArrayList<String>) document.getData().get("routeNameList"),
                                            getLatLng(hashMap)

                                    );
                                    Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                                    //???????????? ?????? ?????? ????????? ???????????? freePost ????????? ???????????? ????????? ??????.
                                    //freepostInfo ???????????? ??????.
                                    FreePostInfo freePostInfo = new FreePostInfo(content, user.getUid(), userInfo.getName(), new Date(), recom, comment, documentReference.getId(), recomUser, category, userInfo.getNickname(), documentId);
                                    dbUploader(documentReference, freePostInfo);
                                } else {
                                    Log.d(TAG, "No such document");
                                }
                            } else {
                                Log.d(TAG, "get failed with ", task.getException());
                            }
                        }
                    });
        }


    }

    //????????? ??????
    private void dbUploader(DocumentReference documentReference , FreePostInfo freePostInfo){
        documentReference.set(freePostInfo)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        loaderLayout.setVisibility(View.GONE);
                        showToast(WritingPostActivity.this ,"????????? ?????? ??????!");
                        Log.w(TAG,"Success writing document" + documentReference.getId());
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                loaderLayout.setVisibility(View.GONE);
                showToast(WritingPostActivity.this ,"????????? ?????? ??????.");
                Log.w(TAG,"Error writing document", e);
            }
        });
    }

    private void postInit(){
        if(freePostInfo != null){

            editContent_Free.setText(freePostInfo.getContent());
        }
    }


    private void myStartActivity(Class c){
        Intent intent=new Intent( this, c);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
//
//    //Uri ????????????
//    private void geturi(Uri uri){
//        this.downloadUri= uri;
//        this.titleImagePath=this.downloadUri.toString();
//    }

    //?????? ?????? ?????? ????????????!
    public String getPath(Uri uri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        CursorLoader cursorLoader = new CursorLoader(this, uri, proj, null, null, null);
        Cursor cursor = cursorLoader.loadInBackground();
        int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(index);
    }


    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    public LatLng getLatLng(HashMap<String,Double> hashmap){
        double latitude=hashmap.get("latitude");
        double longitude=hashmap.get("longitude");
        LatLng latLng=new LatLng(latitude,longitude);
        return latLng;
    }


}