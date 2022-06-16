package com.example.weatherm.SignUp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.weatherm.Model.MemberInfo;
import com.example.weatherm.Model.PetInfo;
import com.example.weatherm.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class PetinitActivity extends AppCompatActivity {
    private static final String TAG = "PetInitActivity";
    private Button men_button;
    private Button woman_button;
    private String gender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_init);

        findViewById(R.id.checkButton).setOnClickListener(onClickListener);
        findViewById(R.id.Men_button).setOnClickListener(onClickListener);
        findViewById(R.id.woman_button).setOnClickListener(onClickListener);

        woman_button=(Button) findViewById(R.id.woman_button);
        men_button=(Button) findViewById(R.id.Men_button);
        gender="남아";
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.checkButton:
                    profileUpdate();
                    break;
                case R.id.Men_button:
                    gender="남아";
                    men_button.setBackground(getDrawable(R.drawable.background_black));
                    men_button.setTextColor(Color.WHITE);
                    woman_button.setBackground(getDrawable(R.drawable.background_white));
                    woman_button.setTextColor(Color.BLACK);

                    break;
                case R.id.woman_button:
                    gender="여아";
                    men_button.setBackground(getDrawable(R.drawable.background_white));
                    men_button.setTextColor(Color.BLACK);
                    woman_button.setBackground(getDrawable(R.drawable.background_black));
                    woman_button.setTextColor(Color.WHITE);
                    break;
            }
        }
    };

    private void profileUpdate() {


        String age = ((EditText)findViewById(R.id.petAgeEditText)).getText().toString();
        String petName=((EditText)findViewById(R.id.editTextPetname)).getText().toString();
        String character=((EditText)findViewById(R.id.character_editText)).getText().toString();
        String petType=((EditText)findViewById(R.id.petTypeEditText)).getText().toString();
        String weight=((EditText)findViewById(R.id.petWeightEditText)).getText().toString();
        String uid= FirebaseAuth.getInstance().getUid();
        ArrayList<String> bookmarkRecipe=new ArrayList<>();


        if(petName.length()>0&& age.length() >1){
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            PetInfo petInfo = new PetInfo(uid, petName,petType,age,weight,character,gender

                    );
            if(user != null){
                db.collection("pets").document(user.getUid()).set(petInfo)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                startToast("강아지 등록을 성공하였습니다.");
                                Intent intent = new Intent(PetinitActivity.this, LocationValidationActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                startToast("강아지 등록에 실패하였습니다.");
                                Log.w(TAG, "Error writing document", e);
                            }
                        });
            }

        }else {
            startToast("회원정보를 입력해주세요.");
        }
    }

    private void startToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
