package com.example.weatherm.sharing;

import android.app.Activity;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.weatherm.Model.CommentInfo;
import com.example.weatherm.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

//자유게시판에서 댓글을 가져오기 위한 어댑터
public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.commentViewHolder> {
    //게시글의 댓글을 저장하기 위한 arraylist
    private ArrayList<CommentInfo> mDataset;
    private Activity activity;
    private FirebaseFirestore firebaseFirestore;
    private MultiImageAdapter multiImageAdapter;
    private ArrayList<Uri> uriList;
    private ArrayList<String> stringUriList;
    private GridLayoutManager mGridLayoutManager;
    //파이어베이스에서 유저 정보 가져오기위해 선언.
    FirebaseUser firebaseUser;


    static class commentViewHolder extends RecyclerView.ViewHolder{
        public CardView cardView;
        commentViewHolder(CardView v){
            super(v);
            cardView = v;
        }
    }

    public CommentAdapter(Activity activity, ArrayList<CommentInfo> commentDataset){
        mDataset = commentDataset;
        this.activity = activity;
    }


    //카드뷰를 생성하여 그곳에 데이터를 집어넣어 완성시킴
    @NotNull
    @Override
    public commentViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType){
        CardView cardView =(CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment_post, parent,false);
        final commentViewHolder commentViewHolder = new commentViewHolder(cardView);
        Log.e("adapter 실행","adapter 실행");
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        return commentViewHolder;
    }

    //카드뷰 안에 들어갈 목록
    //댓글 카드뷰에는 댓글 내용과 작성자가 들어감.
    @Override
    public void onBindViewHolder(@NotNull final commentViewHolder holder, int position){


        CardView cardView = holder.cardView;

        Log.e("어댑터","어댑터");

        mDataset.get(position).getProfileImage();


        TextView contentTextView= cardView.findViewById(R.id.commentContent);
        contentTextView.setText(mDataset.get(position).getContent().toString());

        TextView publisherTextView = cardView.findViewById(R.id.freePublisher1_comment);
        String publisher=mDataset.get(position).getPublisher();
        publisherTextView.setText(publisher);

        ImageView profileImage=cardView.findViewById(R.id.profileImageVIew_comment);
        String publisherId=mDataset.get(position).getPublisherId();

        firebaseFirestore= FirebaseFirestore.getInstance();
        DocumentReference dr = firebaseFirestore.collection("users").document(publisherId);
        dr.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String photoURL=document.getData().get("photoUrl").toString();
                        Glide.with(activity).load(photoURL).centerCrop().into(profileImage);
                    }
                }
            }
        });

        RecyclerView recyclerView=cardView.findViewById(R.id.recyclerview3_comment);
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
            mGridLayoutManager = new GridLayoutManager(activity, numberOfColumns);
            recyclerView.setLayoutManager(mGridLayoutManager);
        }
        else{

        }
        TextView createdTimeTextView= cardView.findViewById(R.id.createdTime_comment);
        createdTimeTextView.setText(new SimpleDateFormat("MM-dd hh:mm", Locale.KOREA).format(mDataset.get(position).getCreatedTime()));

    }

    @Override
    public int getItemCount(){
        return mDataset.size();
    }

}
