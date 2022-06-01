package com.example.weatherm.profile;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.example.weatherm.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


public class ProfileModifyActivity extends AppCompatActivity {
    private ImageView profileImageView;
    private EditText  nickNameEditText;
    private Toolbar myToolbar;
    private String profileImageUrl;
    private String nickName;
    private Button completeButton;
    private ActivityResultLauncher<Intent> resultLauncher;
    private FirebaseStorage storage;
    private Uri imageUri;
    //로딩창 선언
    private RelativeLayout loaderLayout;
    @Override
    protected void onCreate(Bundle bundleSavedInstance){
        super.onCreate(bundleSavedInstance);
        setContentView(R.layout.activity_profilemodify);

        FirebaseUser currentUser=FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore firestore=FirebaseFirestore.getInstance();
        DocumentReference documentReference=firestore.collection("users").document(currentUser.getUid());
        loaderLayout = findViewById(R.id.loaderLayout);
        profileImageView=findViewById(R.id.profileImageVIew_profileModify);
        nickNameEditText=findViewById(R.id.profileModifyEditText);
        completeButton=findViewById(R.id.profileModify_completeButton);
        completeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                documentReference.update("photoUrl",profileImageUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d("이미지 업데이트 성공","성공");
                    }
                });
                nickName=nickNameEditText.getText().toString();
                documentReference.update("nickname",nickName).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d("성공","성공");
                        finish();
                    }
                });
            }
        });

        myToolbar=findViewById(R.id.my_toolbar_profileModify);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.left_arrow_24);
        getSupportActionBar().setTitle("프로필 수정");



        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                DocumentSnapshot d=task.getResult();
                profileImageUrl=d.getData().get("photoUrl").toString();
                Glide.with(getApplication()).load(profileImageUrl).centerCrop().into(profileImageView);
                nickName=d.getData().get("nickname").toString();
                nickNameEditText.setText(nickName);
            }
        });

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
                                final DocumentReference documentReference = firebaseFirestore.collection("users").document(currentUser.getUid());

                                if (data == null) {   // 어떤 이미지도 선택하지 않은 경우
                                    Toast.makeText(getApplicationContext(), "이미지를 선택하지 않았습니다.", Toast.LENGTH_LONG).show();
                                }  else {      // 이미지를 여러장 선택한 경우
                                    imageUri = data.getData();
                                    String filename = documentReference.getId() + "/" + ".jpg"; //파일명 만들기
                                    StorageReference storageRef = storage.getReferenceFromUrl("gs://joggingtracker-6df54.appspot.com");//storage 서버로 이동
                                    //Storage에 사진 저장
                                    final StorageReference riversRef = storageRef.child("users/" + filename);

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

                                            loaderLayout.setVisibility(View.VISIBLE);
                                            Log.e("업로드 태스크 성공", filename);
                                            Toast.makeText(getApplicationContext(), "사진 업로드" + "성공", Toast.LENGTH_LONG).show();
                                            storageRef.child("users/" + filename).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    profileImageUrl=uri.toString();
                                                    documentReference.update("photoUrl",profileImageUrl).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {
                                                            loaderLayout.setVisibility(View.GONE);
                                                            Log.d("성공","프로필업데이트");
                                                        }
                                                    });
                                                    Glide.with(getApplication()).load(profileImageUrl).centerCrop().into(profileImageView);

                                                }
                                            });
                                        }
                                    });
                                }

                            } else {
                                Log.d("시발", "왜 calltype 1에서 막힘");
                            }
                        }
                    }

                });
        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.putExtra("CallType", 1);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                resultLauncher.launch(intent);
            }
        });
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

}
