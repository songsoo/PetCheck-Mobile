package com.example.weatherm.sharing;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.weatherm.R;

public class PictureExpansionActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_expansion);

        ImageView myImageView;
        myImageView=findViewById(R.id.expansion_imageview);

       Uri imageUri=getIntent().getData();
       Toast.makeText(getApplicationContext(),imageUri.toString(),Toast.LENGTH_LONG).show();


        Glide.with(getApplicationContext()).load(imageUri).into(myImageView);
    }


}
