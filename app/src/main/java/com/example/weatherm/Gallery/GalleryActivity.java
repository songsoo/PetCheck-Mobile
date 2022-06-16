package com.example.weatherm.Gallery;//package com.example.jogging_tracker.Gallery;
//
//import static com.example.jogging_tracker.Util.Util.showToast;
//
//import android.Manifest;
//import android.app.Activity;
//import android.content.pm.PackageManager;
//import android.database.Cursor;
//import android.net.Uri;
//import android.os.Bundle;
//import android.provider.MediaStore;
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.app.ActivityCompat;
//import androidx.core.content.ContextCompat;
//import androidx.recyclerview.widget.GridLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.jogging_tracker.R;
//
//import java.util.ArrayList;
//
//import javax.annotation.Nonnull;
//
////activity gallery를 recycleview로 참조
//public class GalleryActivity extends AppCompatActivity {
//    private RecyclerView recyclerView;
//    private RecyclerView.Adapter mAdapter;
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_gallery);
//
//
//        if(ContextCompat.checkSelfPermission(GalleryActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
//                != PackageManager.PERMISSION_GRANTED){
//            ActivityCompat.requestPermissions(GalleryActivity.this,
//                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
//                    1);
//            if(ActivityCompat.shouldShowRequestPermissionRationale(GalleryActivity.this,
//                    Manifest.permission.READ_EXTERNAL_STORAGE)){
//            }else{
//                showToast(GalleryActivity.this ,"권한을 혀용해 주세요");
//            }
//        }else{
//            recyclerInit();
//        }
//
//    }
//
//    private void recyclerInit(){
//        final int numberOfColumns = 3;
//        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
//        recyclerView.setHasFixedSize(true);
//        recyclerView.setLayoutManager(new GridLayoutManager(this,numberOfColumns));
//
//        mAdapter = new GalleryAdapter(this,getImagesPath(this));
//        recyclerView.setAdapter(mAdapter);
//    }
//
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @Nonnull String permissions[], @Nonnull int[] grantResult){
//        super.onRequestPermissionsResult(requestCode,permissions,grantResult);
//
//        switch(requestCode){
//            case 1:{
//                if(grantResult.length > 0 && grantResult[0] == PackageManager.PERMISSION_GRANTED){
//                    recyclerInit();
//                }else{
//                    finish();
//                    showToast(GalleryActivity.this ,"권한을 허용해주세요");
//                }
//            }
//        }
//    }
//
//
//    public static ArrayList<String> getImagesPath(Activity activity) {
//        Uri uri;
//        ArrayList<String> listOfAllImages = new ArrayList<String>();
//        Cursor cursor;
//        int column_index_data, column_index_folder_name;
//        String PathOfImage = null;
//        uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
//
//        //갤러리의 이미지를 최신순으로 하기위한 코드
//        String orderBy = MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC";
//
//        String[] projection = { MediaStore.MediaColumns.DATA,
//                MediaStore.Images.Media.BUCKET_DISPLAY_NAME};
//
//        cursor = activity.getContentResolver().query(uri, projection, null,
//                null, orderBy);
//
//        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
//        column_index_folder_name = cursor
//                .getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
//        while (cursor.moveToNext()) {
//            PathOfImage = cursor.getString(column_index_data);
//
//            listOfAllImages.add(PathOfImage);
//        }
//        return listOfAllImages;
//    }
//}
