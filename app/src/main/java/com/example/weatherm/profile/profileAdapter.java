package com.example.weatherm.profile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherm.R;
import com.google.firebase.database.annotations.NotNull;

import java.util.ArrayList;

public class profileAdapter extends RecyclerView.Adapter<profileAdapter.profileViewHolder>{
    private Context mContext = null;
    private LayoutInflater mLayoutInflater = null;
    private ArrayList<String> mMenuList;
    private Activity activity;


    static class profileViewHolder extends RecyclerView.ViewHolder{
        public CardView cardView;
        profileViewHolder(Activity activity, CardView v, ArrayList<String> menuList){
            super(v);
            cardView = v;
        }
    }
    public profileAdapter(Activity activity, ArrayList<String> menuList){
        mMenuList = menuList;
        this.activity = activity;
    }

    //카드뷰를 생성하여 그곳에 데이터를 집어넣어 완성시킴
    @NotNull
    @Override
    public profileViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType){
        CardView cardView =(CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_item, parent,false);
        final profileViewHolder profileViewHolder = new profileViewHolder(activity, cardView, mMenuList);
        //카드뷰를 클릭할경우, 그 게시글로 activity가 넘어감.
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

              String menuTitle= mMenuList.get(profileViewHolder.getAdapterPosition());
              switch (menuTitle){
                  case "나의 산책 기록":
                      Intent intent=new Intent(activity,MyWalkingRecordActivity.class);
                      activity.startActivity(intent);
                      break;
                  case "저장한 산책로 목록":
                      break;
                  case "친구 목록":
                      Intent intent2=new Intent(activity,FriendListActivity.class);
                      activity.startActivity(intent2);
                  default:
                      break;
              }

            }
        });
        return profileViewHolder;
    }

    //카드뷰 안에 들어갈 목록
    //자유게시판 게시글 카드뷰에는 제목, 작성자, 작성 날짜, 추천수가 저장되어 띄워짐.
    @Override
    public void onBindViewHolder(@NotNull final profileViewHolder holder, int position){
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        CardView cardView = holder.cardView;
        cardView.setLayoutParams(layoutParams);

        TextView textView=cardView.findViewById(R.id.profile_item_menuTitle);
        textView.setText(mMenuList.get(position));
        Log.d("메뉴",mMenuList.get(position));

    }

    @Override
    public int getItemCount(){
        return mMenuList.size();
    }
}
