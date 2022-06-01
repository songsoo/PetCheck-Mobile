package com.example.weatherm.sharing;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherm.Model.FreePostInfo;
import com.example.weatherm.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

//자유게시판의 글을 카드뷰로 보여주기 위한 어댑터
public class freeAdapter extends RecyclerView.Adapter<freeAdapter.freeViewHolder> implements OnMapReadyCallback {
    //자유게시판 글 데이터
    private ArrayList<FreePostInfo> mDataset;
    private Context mContext;
    private Activity activity;
    private FirebaseFirestore firebaseFirestore;
    //파이어베이스에서 유저 정보 가져오기위해 선언.
    FirebaseUser firebaseUser;
    private MultiImageAdapter multiImageAdapter;
    private ArrayList<Uri> uriList;
    private ArrayList<String> stringUriList;
    private GridLayoutManager mGridLayoutManager;
    private MapView mapView;
    private GoogleMap mMap;
    private String documentId;
    private ArrayList<Polyline> polylines=new ArrayList<>();

    private ArrayList<HashMap<String,Double>> ListStartLatLng=new ArrayList<>();
    private ArrayList<HashMap<String,Double>> ListEndLatLng=new ArrayList<>();
    private ArrayList<HashMap<String,Double>> ListTrashLatLng=new ArrayList<>();
    private ArrayList<HashMap<String,Double>> ListWarningLatLng=new ArrayList<>();
    private ArrayList<LatLng> ListStartLatLng2=new ArrayList<>();
    private ArrayList<LatLng> ListEndLatLng2=new ArrayList<>();
    private ArrayList<LatLng> ListTrashLatLng2=new ArrayList<>();
    private ArrayList<LatLng> ListWarningLatLng2=new ArrayList<>();

    static class freeViewHolder extends RecyclerView.ViewHolder {
        public CardView cardView;
        freeViewHolder(Activity activity, CardView v, FreePostInfo freePostInfo){
            super(v);
            cardView = v;
        }
    }

    public freeAdapter(Activity activity, ArrayList<FreePostInfo> freeDataset){
        mDataset = freeDataset;
        this.activity = activity;
    }

    //카드뷰를 생성하여 그곳에 데이터를 집어넣어 완성시킴
    @NotNull
    @Override
    public freeViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType){
        CardView cardView =(CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent,false);
        final freeViewHolder freeViewHolder = new freeViewHolder(activity, cardView, mDataset.get(viewType));
        //카드뷰를 클릭할경우, 그 게시글로 activity가 넘어감.
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(activity, InformationActivity.class);
                intent.putExtra("freePostInfo", mDataset.get(freeViewHolder.getAdapterPosition()));
                activity.startActivity(intent);
            }
        });
        return freeViewHolder;
    }

    //카드뷰 안에 들어갈 목록
    //자유게시판 게시글 카드뷰에는 제목, 작성자, 작성 날짜, 추천수가 저장되어 띄워짐.
    @Override
    public void onBindViewHolder(@NotNull final freeViewHolder holder, int position){

        Log.e("쉐어링어댑터","쉐어링어댑터");
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        CardView cardView = holder.cardView;
        cardView.setLayoutParams(layoutParams);


        RecyclerView recyclerView=cardView.findViewById(R.id.recyclerview3);
        if(mDataset.get(position).getImageList()!=null) {
            uriList = new ArrayList<>();
            stringUriList = mDataset.get(position).getImageList();
            String tempString;
            for (int i = 0; i < stringUriList.size(); i++) {
                tempString = stringUriList.get(i);
                uriList.add(Uri.parse(tempString));
            }
            multiImageAdapter = new MultiImageAdapter(uriList, activity);
            recyclerView.setAdapter(multiImageAdapter);
            int numberOfColumns = uriList.size(); // 한줄에 5개의 컬럼을 추가합니다.
            if(numberOfColumns==0){
                numberOfColumns=1;
            }
            mGridLayoutManager = new GridLayoutManager(activity, numberOfColumns);
            recyclerView.setLayoutManager(mGridLayoutManager);
        }
        else{

        }

        TextView createdAt = cardView.findViewById(R.id.freeCreatedAt);
        createdAt.setText(new SimpleDateFormat("MM-dd hh:mm", Locale.KOREA).format(mDataset.get(position).getCreatedAt()));

//        TextView freePublisher = cardView.findViewById(R.id.freePublisher1);
//        freePublisher.setText(mDataset.get(position).getUserName());

        Button category=cardView.findViewById(R.id.category_button_post);
        category.setText((String) mDataset.get(position).getCategory());

        TextView recom = cardView.findViewById(R.id.freeRecom);
        recom.setText("공감수 : " + (int) mDataset.get(position).getRecom());

        TextView content = cardView.findViewById(R.id.postContent);
        content.setText((String)  mDataset.get(position).getContent());

        TextView commentNum=cardView.findViewById(R.id.comment_number_textVeiw);
        if(mDataset.get(position).getComment()!=null)
        commentNum.setText("댓글 "+mDataset.get(position).getComment().size());

        documentId=mDataset.get(position).getRouteInfoId();

        if(documentId!=null)
        {
            //onmapreadycallback 함수 부르기
            mapView = (MapView) cardView.findViewById(R.id.map);
            mapView.onCreate(null);
            mapView.setVisibility(View.VISIBLE);
            mapView.getMapAsync(this);

        }

    }

    @Override
    public int getItemCount(){
        return mDataset.size();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if(documentId!=null){
        MapsInitializer.initialize(activity.getApplicationContext());
        mMap = googleMap;

        firebaseFirestore=FirebaseFirestore.getInstance();

        DocumentReference documentReference = firebaseFirestore.collection("RouteInfo").document(documentId);
        documentReference.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){

                            Log.d("하하","하하");
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
                                BitmapDrawable bd = (BitmapDrawable) activity.getResources().getDrawable(R.drawable.baseline_delete_black_48);
                                Bitmap b = bd.getBitmap();
                                Bitmap bitMapImage = Bitmap.createScaledBitmap(b,20,20,false);
                                MarkerOptions markerOptions = new MarkerOptions();
                                markerOptions.position(trashLatLng);
                                markerOptions.title("배변 봉투함");
                                markerOptions.draggable(true);
                                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(bitMapImage));
                                mMap.addMarker(markerOptions);

                            }
                            for(int i=0; i< ListWarningLatLng.size(); i++)
                            {
                                warningLatitude=ListWarningLatLng.get(i).get("latitude");
                                warningLongtitude=ListWarningLatLng.get(i).get("longitude");
                                warningLatLng=new LatLng(warningLatitude,warningLongtitude);
                                BitmapDrawable bd2 = (BitmapDrawable) activity.getResources().getDrawable(R.drawable.baseline_warning_black_48);
                                Bitmap b2 = bd2.getBitmap();
                                Bitmap bitMapImage2 = Bitmap.createScaledBitmap(b2,20,20,false);
                                MarkerOptions markerOptions2 = new MarkerOptions();
                                markerOptions2.position(warningLatLng);
                                markerOptions2.title("위험 지역");
                                markerOptions2.draggable(true);
                                markerOptions2.icon(BitmapDescriptorFactory.fromBitmap(bitMapImage2));
                                mMap.addMarker(markerOptions2);
                            }
                            Log.d("하하","하하2");
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startLatLng, 16));

                        }
                        else{

                            Toast.makeText(activity, "루트 읽기 실패", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }}

}
