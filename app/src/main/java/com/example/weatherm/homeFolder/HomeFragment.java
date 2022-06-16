package com.example.weatherm.homeFolder;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.weatherm.BluetoothConnect;
import com.example.weatherm.IntroActivity;
import com.example.weatherm.R;
import com.example.weatherm.home;


public class HomeFragment extends Fragment {
    private View view;
    Bundle bundle2 = new Bundle();
    VideoView vv;
    String whatbtn = "";
    public SharedPreferences prefs;
    //private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    //private DatabaseReference databaseReference = firebaseDatabase.getReference();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_home,container,false);

        ImageButton btn1 = view.findViewById(R.id.stress_btn);
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
                Intent intent = new Intent(getActivity(), BluetoothConnect.class);
                startActivity(intent);
            }
        });

//        ImageButton btn2 = (ImageButton) view.findViewById(R.id.record_btn);
//        btn2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//              /*  Intent intent = new Intent(home.this, fragment_whole.class);
//
//                //번들을 이용해서 어떤 버튼이 눌렸는지 써서 보내줌
//                whatbtn = "btn2";
//                bundle2.putString("whatbtn", whatbtn);
//                intent.putExtras(bundle2);
//
//                startActivity(intent);
//                finish();*/
//            }
//        });
        //home에서 part으로 연결
        ImageButton btn3 = (ImageButton) view.findViewById(R.id.weather_btn);
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), IntroActivity.class);

                //번들을 이용해서 어떤 버튼이 눌렸는지 써서 보내줌
                whatbtn = "btn3";
                bundle2.putString("whatbtn", whatbtn);
                intent.putExtras(bundle2);

                startActivity(intent);
//                finish();
            }
        });
        return view;
    }

}
