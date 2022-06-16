package com.example.weatherm.profile;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.weatherm.Model.MemberInfo;
import com.example.weatherm.R;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class FriendListAdapter extends RecyclerView.Adapter<FriendListAdapter.FriendListViewHolder> {
    private ArrayList<MemberInfo> mDataset;
    private Activity activity;
    private FirebaseFirestore firebaseFirestore;

    static class FriendListViewHolder extends RecyclerView.ViewHolder{
        public CardView cardView;
        FriendListViewHolder(CardView v){
            super(v);
            cardView = v;
        }
    }

    public FriendListAdapter(Activity activity, ArrayList<MemberInfo> memberDataset){
        mDataset = memberDataset;
        this.activity = activity;
    }

    //카드뷰를 생성하여 그곳에 데이터를 집어넣어 완성시킴
    @NotNull
    @Override
    public FriendListViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType){
        CardView cardView =(CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friendlist, parent,false);
        final FriendListViewHolder FriendListViewHolder = new FriendListViewHolder(cardView);
        Log.e("adapter 실행","adapter 실행");
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//채팅방실행
                Intent intent= new Intent(activity, MessageActivity.class);
                intent.putExtra("destUid", mDataset.get(FriendListViewHolder.getAdapterPosition()).getUid());
                activity.startActivity(intent);
            }
        });
        return FriendListViewHolder;
    }
    //카드뷰 안에 들어갈 목록
    //댓글 카드뷰에는 댓글 내용과 작성자가 들어감.
    @Override
    public void onBindViewHolder(@NotNull final FriendListViewHolder holder, int position){
        CardView cardView = holder.cardView;

        Log.e("어댑터","어댑터");

        String photoURL=mDataset.get(position).getPhotoUrl();

        TextView friend_nickName=cardView.findViewById(R.id.friend_master_textview);
        friend_nickName.setText(mDataset.get(position).getNickname());

        ImageView profileImage=cardView.findViewById(R.id.profileImageVIew_friendList);
        Glide.with(activity).load(photoURL).centerCrop().into(profileImage);





    }

    @Override
    public int getItemCount(){
        return mDataset.size();
    }
}
