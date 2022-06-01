package com.example.weatherm.sharing;//package com.example.jogging_tracker.sharing;
//
//import android.app.Activity;
//import android.content.Intent;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.cardview.widget.CardView;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.bumptech.glide.Glide;
//import com.example.jogging_tracker.R;
//import com.example.jogging_tracker.Util.Model.PostInfo;
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.database.annotations.NotNull;
//import com.google.firebase.firestore.DocumentReference;
//import com.google.firebase.firestore.DocumentSnapshot;
//import com.google.firebase.firestore.FirebaseFirestore;
//
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Locale;
//
//import static com.example.jogging_tracker.Util.Util.isStorageUrl;
//
////레시피 게시판의 글을 카드뷰로 보여주기 위한 어댑터
//public class PostAdapter extends RecyclerView.Adapter<PostAdapter.recipeViewHolder> {
//    //레시피게시판 글 데이터
//    private ArrayList<Free> mDataset;
//    private Activity activity;
//    private FirebaseFirestore firebaseFirestore;
//    //파이어베이스에서 유저 정보 가져오기위해 선언.
//    FirebaseUser firebaseUser;
//    //유저 아이디
//    String user;
//    //레시피 아이디
//    String id;
//
//
//
//
//    static class recipeViewHolder extends RecyclerView.ViewHolder{
//        public CardView cardView;
//        recipeViewHolder(Activity activity, CardView v, PostInfo recipePostInfo){
//            super(v);
//            cardView = v;
//        }
//    }
//
//    public PostAdapter(Activity activity, ArrayList<PostInfo> postDataset){
//        mDataset = postDataset;
//        this.activity = activity;
//    }
//
//    @Override
//    public int getItemViewType(int position){
//        return position;
//    }
//
//    //카드뷰를 생성하여 그곳에 데이터를 집어넣어 완성시킴
//    @NotNull
//    @Override
//    public PostAdapter.recipeViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType){
//        CardView cardView =(CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent,false);
//        final recipeViewHolder recipeViewHolder = new recipeViewHolder(activity, cardView, mDataset.get(viewType));
//        //카드뷰를 클릭할경우, 그 게시글로 activity가 넘어감.
////        cardView.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                Intent intent= new Intent(activity, recipeInformationActivity.class);
////                intent.putExtra("recipePostInfo", mDataset.get(recipeViewHolder.getAdapterPosition()));
////                activity.startActivity(intent);
////            }
////        });
//
//
//
//        return recipeViewHolder;
//    }
//
//    //카드뷰 안에 들어갈 목록
//    //레시피게시판 게시글 카드뷰에는 제목, 타이틀 이미지 , 작성자, 작성 날짜, 추천수가 저장되어 띄워짐.
//    @Override
//    public void onBindViewHolder(@NotNull final recipeViewHolder holder, int position){
//        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//
//
//        CardView cardView = holder.cardView;
//        cardView.setLayoutParams(layoutParams);
//        ImageView titleImage = cardView.findViewById(R.id.recipeTitleImage);
//        String titleImagePath = mDataset.get(position).getTitleImage();
//        if(isStorageUrl(titleImagePath)){
//            Glide.with(activity).load(titleImagePath).centerCrop().into(titleImage);
//        }
//
//        ImageView profileImage=cardView.findViewById(R.id.profileImageVIew2);
//        String publisher=mDataset.get(position).getPublisher();
//
//        firebaseFirestore= FirebaseFirestore.getInstance();
//        DocumentReference dr = firebaseFirestore.collection("users").document(publisher);
//        dr.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if (task.isSuccessful()) {
//                    DocumentSnapshot document = task.getResult();
//                    if (document.exists()) {
//                        String photoURL=document.getData().get("photoUrl").toString();
//                        Glide.with(activity).load(photoURL).centerCrop().into(profileImage);
//                    }
//                }
//            }
//        });
//
//        TextView recipetag=cardView.findViewById(R.id.RecipeTag);
//        recipetag.setText("#"+(mDataset.get(position).getTagCategory()));
//
//        TextView title = cardView.findViewById(R.id.recipeTitle);
//        title.setText(mDataset.get(position).getTitle());
//
//        TextView userName = cardView.findViewById(R.id.recipePublisher);
//        userName.setText(mDataset.get(position).getUserName());
//
//        TextView createdAt = cardView.findViewById(R.id.recipeCreatedAt);
//        createdAt.setText(new SimpleDateFormat("MM-dd hh:mm", Locale.KOREA).format(mDataset.get(position).getCreatedAt()));
//
//        TextView recom = cardView.findViewById(R.id.recipeRecom);
//        recom.setText("추천수 : " + (int) mDataset.get(position).getRecom());
//    }
//
//    @Override
//    public int getItemCount(){
//        return mDataset.size();
//    }
//
//
//
//
//}
