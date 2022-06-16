package com.example.weatherm.Gallery;//package com.example.mobileprogramming_termproject.Gallery;
//
//import android.app.Activity;
//import android.content.Intent;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//
//import androidx.cardview.widget.CardView;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.bumptech.glide.Glide;
//
//import java.util.ArrayList;
//
////Recycle View의 adapter
//public class GalleryAdapter extends RecyclerView.Adapter<com.example.jogging_tracker.Gallery.GalleryAdapter.GalleryViewHolder> {
//    private ArrayList<String> mDataset;
//    private Activity activty;
//
//    public static class GalleryViewHolder extends RecyclerView.ViewHolder {
//        public CardView cardView;
//        public GalleryViewHolder(CardView v) {
//            super(v);
//            cardView = v;
//        }
//    }
//
//    public GalleryAdapter(Activity activity, ArrayList<String> myDataset) {
//        mDataset = myDataset;
//        this.activty=activity;
//    }
//
//
//    @Override
//    public com.example.jogging_tracker.Gallery.GalleryAdapter.GalleryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        CardView cardView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_gallery, parent, false);
//        GalleryViewHolder galleryViewHolder = new GalleryViewHolder(cardView);
//        return galleryViewHolder;
//    }
//
//    @Override
//    public void onBindViewHolder(final GalleryViewHolder holder,int position) {
//        CardView cardView=holder.cardView;
//        cardView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //사진 경로를 memberactivity로 보내고 꺼짐
//
//                Intent resultIntent = new Intent();
//                resultIntent.putExtra("profilePath",mDataset.get(holder.getAdapterPosition()));
//                activty.setResult(Activity.RESULT_OK, resultIntent);
//                activty.finish();
//
//            }
//        });
//
//        ImageView imageView=cardView.findViewById(R.id.imageView);
//        Glide.with(activty).load(mDataset.get(position)).centerCrop().override(600).into(imageView);
//    }
//
//    // Return the size of your dataset (invoked by the layout manager)
//    @Override
//    public int getItemCount() {
//        return mDataset.size();
//    }
//}
