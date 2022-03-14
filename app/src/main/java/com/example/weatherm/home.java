package com.example.weatherm;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class home extends AppCompatActivity {

    VideoView vv;
    String whatbtn = "";
    public SharedPreferences prefs;
    //private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    //private DatabaseReference databaseReference = firebaseDatabase.getReference();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        //databaseReference.child("index").setValue(0);

        // 신체 기록 최초 저장, 처음 실행시만 bodyprofile 설정하도록
        //prefs = getSharedPreferences("Pref", MODE_PRIVATE);
        //checkFirstRun();

        Bundle bundle2 = new Bundle();


        ImageButton btn1 = (ImageButton) findViewById(R.id.stress_btn);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* Intent intent = new Intent(home.this, check_bluetooth.class);

                //번들을 이용해서 어떤 버튼이 눌렸는지 써서 보내줌
                whatbtn = "btn1";
                bundle2.putString("whatbtn", whatbtn);
                intent.putExtras(bundle2);

                startActivity(intent);
                finish();*/
                Intent intent = new Intent(home.this, BluetoothConnect.class);
                startActivity(intent);
            }
        });

        ImageButton btn2 = (ImageButton) findViewById(R.id.record_btn);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              /*  Intent intent = new Intent(home.this, fragment_whole.class);

                //번들을 이용해서 어떤 버튼이 눌렸는지 써서 보내줌
                whatbtn = "btn2";
                bundle2.putString("whatbtn", whatbtn);
                intent.putExtras(bundle2);

                startActivity(intent);
                finish();*/
            }
        });
        //home에서 part으로 연결
        ImageButton btn3 = (ImageButton) findViewById(R.id.weather_btn);
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent intent = new Intent(home.this, IntroActivity.class);

                //번들을 이용해서 어떤 버튼이 눌렸는지 써서 보내줌
                whatbtn = "btn3";
                bundle2.putString("whatbtn", whatbtn);
                intent.putExtras(bundle2);

                startActivity(intent);
                finish();
            }
        });




        //하단바
     // ImageView girok = (ImageView) findViewById(R.id.scale);
     //   girok.setOnClickListener(new View.OnClickListener() {
        //      @Override
        //      public void onClick(View v) {
        //
        //  }
        //});

        // ImageView memo = (ImageView) findViewById(R.id.calendar);
        // memo.setOnClickListener(new View.OnClickListener() {
        //    @Override
        //    public void onClick(View v) {
               /* Intent intent = new Intent(home.this, Calendar.class);
                startActivity(intent);*/
        //     }
    //  });

        //home에서 mypage으로 연결
        //   ImageView mypage = (ImageView) findViewById(R.id.mypage);
        //   mypage.setOnClickListener(new View.OnClickListener() {
        //      @Override
        //      public void onClick(View v) {
        //         Intent intent = new Intent(home.this, Mypage.class);
        //         startActivity(intent);
        //     }
        //  });



        //동영상을 읽어오는데 시간이 걸리므로..
        //비디오 로딩 준비가 끝났을 때 실행하도록..
        //리스너 설정
        //  vv.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            //     @Override
        //     public void onPrepared(MediaPlayer mediaPlayer) {
            //        //비디오 시작
        //       vv.start();
        //    }
        //});

        // }

        // public void checkFirstRun() {
        // boolean isFirstRun = prefs.getBoolean("isFirstRun", true);
        // if (isFirstRun) {

        // }

    }

}
