package com.example.weatherm.sharing;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.weatherm.OnItemClick;
import com.example.weatherm.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class MultiImageAdapter2 extends RecyclerView.Adapter<MultiImageAdapter2.ViewHolder>{
    private ArrayList<Uri> mData = null ;
    private ArrayList<String> mfileData=null;
    private Context mContext = null ;
    private OnItemClick mCallback;

    public MultiImageAdapter2(Context mContext) {
        this.mData=null;
        this.mContext = mContext;
    }

    // 생성자에서 데이터 리스트 객체, Context를 전달받음.
    MultiImageAdapter2(ArrayList<Uri> list, ArrayList<String> fileList,Context context,OnItemClick listener) {
        mData = list ;
        mContext = context;
        mfileData = fileList;
        this.mCallback = listener;

    }

    // 아이템 뷰를 저장하는 뷰홀더 클래스.
    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView image;

        ViewHolder(View itemView) {
            super(itemView) ;
            ImageButton DeleteButton=itemView.findViewById(R.id.imageButton4);
            DeleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition() ;
                    if (pos != RecyclerView.NO_POSITION) {
                        String filename=mfileData.get(pos);
                        FirebaseStorage storage = FirebaseStorage.getInstance();
                        StorageReference storageRef=storage.getReference();

                        StorageReference desertRef = storageRef.child("freepost/"+ filename);

                        desertRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(mContext,"삭제완료",Toast.LENGTH_LONG);
                                increment(pos);

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });



                        mfileData.remove(pos);
                        mData.remove(pos);
                        notifyItemRemoved(pos);
                        notifyItemRangeChanged(pos,mData.size());
                        notifyItemChanged(pos) ;
                    }
                }
            });
            // 뷰 객체에 대한 참조.
            image = itemView.findViewById(R.id.image);



        }
    }


    // onCreateViewHolder() - 아이템 뷰를 위한 뷰홀더 객체 생성하여 리턴.
    // LayoutInflater - XML에 정의된 Resource(자원) 들을 View의 형태로 반환.
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext() ;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;    // context에서 LayoutInflater 객체를 얻는다.
        View view = inflater.inflate(R.layout.multi_image_item, parent, false) ;	// 리사이클러뷰에 들어갈 아이템뷰의 레이아웃을 inflate.
        ViewHolder vh = new ViewHolder(view) ;

        return vh ;
    }

    // onBindViewHolder() - position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시.
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if(mData!=null) {
            Uri image_uri = mData.get(position);

            Glide.with(mContext)
                    .load(image_uri)
                    .centerCrop()
                    .into(holder.image);
        }
    }

    public void increment(int pos){

        mCallback.onClick(pos);
    }

    public void decrement(int pos){

        mCallback.onClick(pos);
    }


    // getItemCount() - 전체 데이터 갯수 리턴.
    @Override
    public int getItemCount() {
        return mData.size() ;
    }

}