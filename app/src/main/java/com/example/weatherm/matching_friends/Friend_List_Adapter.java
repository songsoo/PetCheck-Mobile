package com.example.weatherm.matching_friends;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.weatherm.Model.MemberInfo;
import com.example.weatherm.R;
import com.example.weatherm.profile.MessageActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class Friend_List_Adapter extends RecyclerView.Adapter<Friend_List_Adapter.Friend_List_ViewHolder> {
    private ArrayList<MemberInfo> mDataset;
    private Activity activity;
    private FirebaseFirestore firebaseFirestore;

    static class Friend_List_ViewHolder extends RecyclerView.ViewHolder{
        public CardView cardView;
        Friend_List_ViewHolder(CardView v){
            super(v);
            cardView = v;
        }
    }

    public Friend_List_Adapter(Activity activity, ArrayList<MemberInfo> memberDataset){
        mDataset = memberDataset;
        this.activity = activity;
    }

    //카드뷰를 생성하여 그곳에 데이터를 집어넣어 완성시킴
    @NotNull
    @Override
    public Friend_List_ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType){
        CardView cardView =(CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friendlist, parent,false);
        final Friend_List_ViewHolder Friend_List_ViewHolder = new Friend_List_Adapter.Friend_List_ViewHolder(cardView);
        Log.e("adapter 실행","adapter 실행");
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//채팅방실행
                Intent intent= new Intent(activity, MessageActivity.class);
                intent.putExtra("destUid", mDataset.get(Friend_List_ViewHolder.getAdapterPosition()).getUid());
                activity.startActivity(intent);
            }
        });
        return Friend_List_ViewHolder;
    }
    //카드뷰 안에 들어갈 목록
    //댓글 카드뷰에는 댓글 내용과 작성자가 들어감.
    @Override
    public void onBindViewHolder(@NotNull final Friend_List_ViewHolder holder, int position){
        CardView cardView = holder.cardView;

        Log.e("어댑터","어댑터");

        String photoURL=mDataset.get(position).getPhotoUrl();

        TextView friend_nickName_textview=cardView.findViewById(R.id.friend_master_textview);
        TextView pets_infromation_textview=cardView.findViewById(R.id.friend_pet_textview);



        String gender=mDataset.get(position).getGender();
        String birth=mDataset.get(position).getDate();
        String year_birth=birth.substring(0,2);
        int year=Integer.parseInt(year_birth);
        int age=122-year;

        friend_nickName_textview.setText(mDataset.get(position).getNickname()+"("+gender+","+age+")");

        ImageView profileImage=cardView.findViewById(R.id.profileImageVIew_friendList);
        Glide.with(activity).load(photoURL).centerCrop().into(profileImage);

        FirebaseFirestore firebaseFirestore;
        firebaseFirestore=FirebaseFirestore.getInstance();
        DocumentReference documentReference=firebaseFirestore.collection("pets").document(mDataset.get(position).getUid());
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot result = task.getResult();
                String petName=result.getData().get("petName").toString();
                String petAge=result.getData().get("age").toString();
                String petType=result.getData().get("petType").toString();

                pets_infromation_textview.setText("("+petName+","+petAge+","+petType+")");
            }
        });
    }



    @Override
    public int getItemCount(){
        return mDataset.size();
    }
}