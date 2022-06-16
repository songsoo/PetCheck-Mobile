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


    //갤러리 resultcode
    private final int GALLERY_CODE = 10;

    private static final String TAG ="writingFreePostActivity";
    //유저 선언
    private FirebaseUser user;
    //로딩창 선언
    private RelativeLayout loaderLayout;
    //Firebase 선언
    private FirebaseFirestore firebaseFirestore;

    ArrayList<Uri> uriList = new ArrayList<>();     // 이미지의 uri를 담을 ArrayList 객체
    ArrayList<String> filenameList= new ArrayList<>();
    RecyclerView recyclerView;  // 이미지를 보여줄 리사이클러뷰
    MultiImageAdapter2 adapter;  // 리사이클러뷰에 적용시킬 어댑터

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


    //타이틀 이미지 경로값
    private ArrayList<String> titleImagePathList;
    //dbUploader

    private RouteInfo routeInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        titleImagePathList=new ArrayList<String>();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writing_post);

        loaderLayout = findViewById(R.id.loaderLayout);
        //현재의 User data 받아옴.
        user = FirebaseAuth.getInstance().getCurrentUser();
        //파이어베이스 가져옴
        firebaseFirestore = FirebaseFirestore.getInstance();
        editContent_Free = findViewById(R.id.editContent_Free);
        freePostInfo = (FreePostInfo)getIntent().getSerializableExtra("freePostInfo");

        // Toolbar를 생성.
        myToolbar = (Toolbar) findViewById(R.id.writingpost_toolbar);
        setSupportActionBar(myToolbar);

        //Toolbar의 왼쪽에 버튼을 추가하고 버튼의 아이콘을 바꾼다.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.left_arrow_24);
        getSupportActionBar().setTitle("게시판 글쓰기");  //당 액티비티의 툴바에 있는 타이틀을 바꾸기

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

        //액티비티 콜백 함수, 사진 등록시 필요
        resultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == RESULT_OK) {
                            Intent data = result.getData();
                            int CallType = data.getIntExtra("CallType", 0);
                            if (CallType == 0) {

                                Log.d("콜타입 통과", "콜타입 통과");
                                //실행될 코드
                                //타이틀 이미지 관련 코드
                                //타이틀 이미지를 이미지 버튼에 출력
                                FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
                                final DocumentReference documentReference = firebaseFirestore.collection("freePost").document();

                                if (data == null) {   // 어떤 이미지도 선택하지 않은 경우
                                    Toast.makeText(getApplicationContext(), "이미지를 선택하지 않았습니다.", Toast.LENGTH_LONG).show();
                                }  else {      // 이미지를 여러장 선택한 경우
                                        ClipData clipData = data.getClipData();
                                        Log.e("clipData", String.valueOf(clipData.getItemCount()));



                                    if (clipData.getItemCount() > 10) {   // 선택한 이미지가 11장 이상인 경우
                                            Toast.makeText(getApplicationContext(), "사진은 10장까지 선택 가능합니다.", Toast.LENGTH_LONG).show();
                                        } else {   // 선택한 이미지가 1장 이상 10장 이하인 경우

                                            Log.e(TAG, "multiple choice");
                                            uriList.clear();
                                            filenameList.clear();
                                            titleImagePathList.clear();
                                            count=0;
                                            for (int i = 0; i < clipData.getItemCount(); i++) {
                                                Uri imageUri = clipData.getItemAt(i).getUri();  // 선택한 이미지들의 uri를 가져온다.
                                                String filename =documentReference.getId()+"/"+i + ".jpg"; //파일명 만들기
                                                StorageReference storageRef = storage.getReferenceFromUrl("gs://joggingtracker-6df54.appspot.com");//storage 서버로 이동
                                                //Storage에 사진 저장
                                                final StorageReference riversRef = storageRef.child("freePost/" + filename);


                                                    uriList.add(imageUri);  //uri를 list에 담는다.
                                                    filenameList.add(filename);

                                                    recyclerView.setVisibility(View.VISIBLE);
                                            adapter = new MultiImageAdapter2(uriList,filenameList, getApplicationContext(),onItemClick);
                                            recyclerView.setAdapter(adapter);   // 리사이클러뷰에 어댑터 세팅
                                            int numberOfColumns = 5; // 한줄에 5개의 컬럼을 추가합니다.
                                            mGridLayoutManager = new GridLayoutManager(getApplicationContext(), numberOfColumns);
                                            recyclerView.setLayoutManager(mGridLayoutManager);


//                                            recyclerView.addItemDecoration(new ItemDecoration(getApplicationContext()));

                                        }
                                    }

                                }

                            } else {
                                Log.d("시발", "왜 calltype 1에서 막힘");
                            }
                        }
                    }

                });


        Intent intent=getIntent();
        int flag=intent.getIntExtra("flag",0);
        //산책부분에서 넘어올 때
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

    //사진 삭제 리스너입니다.
    @Override
    public void onClick(int value){
        uriList.remove(value);
    }
//    //Uri 저장하기
//    private void geturi(Uri uri){
//        this.titleImagePathList.add(uri.toString());
//    }
    //클릭시 발생하는 리스너
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
    // ToolBar에 menu.xml을 인플레이트함
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //return super.onCreateOptionsMenu(menu);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.writing_post__toolbar_menu, menu);
        return true;
    }

    //ToolBar에 추가된 항목의 select 이벤트를 처리하는 함수
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //return super.onOptionsItemSelected(item);
        switch (item.getItemId()) {

            //완료를 클릭했을때 게시판을 업로드 합니다
            case R.id.writingpost_toolbar_complete:
                bulletinUpload();
                return true;

                //android.R.id.home 이 툴바의 맨 왼쪽버튼 아이디 입니다.
            case android.R.id.home:
               finish();
               return true;

            default:
               return true;
        }
    }

    //게시글 FireBase에 업로드 하기위함
    private void bulletinUpload(){

        //내용 받아옴
        final String content = editContent_Free.getText().toString();

        //저장할 위치 선언
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
                        StorageReference storageRef = storage.getReferenceFromUrl("gs://joggingtracker-6df54.appspot.com");//storage 서버로 이동
                        //Storage에 사진 저장
                        final StorageReference riversRef = storageRef.child("freePost/" + filename);
                        //firestore에 storage uri 저장
                        UploadTask uploadTask = riversRef.putFile(imageUri);

                        // Register observers to listen for when the download is done or if it fails
                        uploadTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e("업로드 태스크 실패", filename);

                            }
                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                count++;
                                if (count == uriList.size())
                                    loaderLayout.setVisibility(View.GONE);
                                Log.e("업로드 태스크 성공", filename);
                                Toast.makeText(getApplicationContext(), "사진 업로드" + count + "성공", Toast.LENGTH_LONG).show();
                                storageRef.child("freePost/" + filename).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        titleImagePathList.add(uri.toString());
                                        flag++;
                                        if (flag == filenameList.size()) {

                                            //나중에 댓글 삽입 위해서 arrayList 선언
                                            //만약 제목 또는 내용이 공백아닐경우
                                            if (content.length() > 0 && category != null) {
                                                //데이터가 firebase에 업로드 될때까지 로딩창 띄움

                                                Log.d(TAG, "게시글 업로드 중");


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
                                                    //유저 이름을 받아오기 위하여 데이터베이스에 연결하여 유저 아이디 이용 검색
                                                    firebaseFirestore.collection("users").document(user.getUid()).get()
                                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                    Log.d(TAG, "다큐먼트 실행");
                                                                    if (task.isSuccessful()) {
                                                                        DocumentSnapshot document = task.getResult();
                                                                        HashMap<String ,Double> hashMap=(HashMap<String, Double>) document.getData().get("address");
                                                                        if (document.exists()) {
                                                                            MemberInfo userInfo = new MemberInfo(
                                                                                    document.getData().get("uid").toString(),
                                                                                    document.getData().get("date").toString(),
                                                                                    document.getData().get("photoUrl").toString(),
                                                                                    document.getData().get("nickname").toString(),
                                                                                    (ArrayList<String>) document.getData().get("walkingList"),
                                                                                    (ArrayList<String>) document.getData().get("bookmarkRouteList"),
                                                                                    (ArrayList<String>) document.getData().get("routeNameList"),
                                                                                    (ArrayList<String>) document.getData().get("friendList"),
                                                                                    document.getData().get("gender").toString(),
                                                                                    document.getData().get("myProfile").toString(),
                                                                                    getLatLng(hashMap)

                                                                            );
                                                                            Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                                                                            //검색하여 얻은 유저 이름을 이용하여 freePost 데이터 베이스에 게시글 저장.
                                                                            //freepostInfo 형식으로 저장.
                                                                            FreePostInfo freePostInfo = new FreePostInfo(content, user.getUid(), new Date(), recom, comment, documentReference.getId(), recomUser, titleImagePathList, category, userInfo.getNickname(), documentId);
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
                                                    //유저 이름을 받아오기 위하여 데이터베이스에 연결하여 유저 아이디 이용 검색

                                                }
                                            } else {
                                                showToast(WritingPostActivity.this, "내용을 정확히 입력해주세요!");
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
                                Log.d(TAG, "다큐먼트 실행");
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    HashMap<String ,Double> hashMap=(HashMap<String, Double>) document.getData().get("address");
                                    if (document.exists()) {
                                        MemberInfo userInfo = new MemberInfo(
                                                document.getData().get("uid").toString(),
                                                document.getData().get("date").toString(),
                                                document.getData().get("photoUrl").toString(),
                                                document.getData().get("nickname").toString(),
                                                (ArrayList<String>) document.getData().get("walkingList"),
                                                (ArrayList<String>) document.getData().get("bookmarkRouteList"),
                                                (ArrayList<String>) document.getData().get("routeNameList"),
                                                (ArrayList<String>) document.getData().get("friendList"),
                                                document.getData().get("gender").toString(),
                                                document.getData().get("myProfile").toString(),
                                                getLatLng(hashMap)

                                        );
                                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                                        //검색하여 얻은 유저 이름을 이용하여 freePost 데이터 베이스에 게시글 저장.
                                        //freepostInfo 형식으로 저장.
                                        FreePostInfo freePostInfo = new FreePostInfo(content, user.getUid(), new Date(), recom, comment, documentReference.getId(), recomUser, titleImagePathList, category, userInfo.getNickname(), documentId);
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
                            Log.d(TAG, "다큐먼트 실행");
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                HashMap<String ,Double> hashMap=(HashMap<String, Double>) document.getData().get("address");
                                if (document.exists()) {
                                    MemberInfo userInfo = new MemberInfo(
                                            document.getData().get("uid").toString(),
                                            document.getData().get("date").toString(),
                                            document.getData().get("photoUrl").toString(),
                                            document.getData().get("nickname").toString(),
                                            (ArrayList<String>) document.getData().get("walkingList"),
                                            (ArrayList<String>) document.getData().get("bookmarkRouteList"),
                                            (ArrayList<String>) document.getData().get("routeNameList"),
                                            (ArrayList<String>) document.getData().get("friendList"),
                                            document.getData().get("gender").toString(),
                                            document.getData().get("myProfile").toString(),
                                            getLatLng(hashMap)

                                    );
                                    Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                                    //검색하여 얻은 유저 이름을 이용하여 freePost 데이터 베이스에 게시글 저장.
                                    //freepostInfo 형식으로 저장.
                                    FreePostInfo freePostInfo = new FreePostInfo(content, user.getUid(), new Date(), recom, comment, documentReference.getId(), recomUser, titleImagePathList, category, userInfo.getNickname(), documentId);
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

    //게시글 등록
    private void dbUploader(DocumentReference documentReference , FreePostInfo freePostInfo){
        documentReference.set(freePostInfo)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        loaderLayout.setVisibility(View.GONE);
                        showToast(WritingPostActivity.this ,"게시글 등록 성공!");
                        Log.w(TAG,"Success writing document" + documentReference.getId());
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                loaderLayout.setVisibility(View.GONE);
                showToast(WritingPostActivity.this ,"게시글 등록 실패.");
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
//    //Uri 저장하기
//    private void geturi(Uri uri){
//        this.downloadUri= uri;
//        this.titleImagePath=this.downloadUri.toString();
//    }

    //앨범 사진 경로 가져오기!
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