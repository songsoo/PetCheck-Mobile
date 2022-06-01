package com.example.weatherm.SignUp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.weatherm.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";

    EditText mEmailText, mPasswordText, mPasswordcheckText,mName,mNickname;
    Button mregisterBtn;
    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);



        //파이어베이스 접근 설정
        // user = firebaseAuth.getCurrentUser();
        firebaseAuth =  FirebaseAuth.getInstance();
        //firebaseDatabase = FirebaseDatabase.getInstance().getReference();


        mEmailText = (EditText) findViewById(R.id.emailEt);
        mPasswordText = (EditText)findViewById(R.id.passwordEdt);
        mPasswordcheckText = (EditText)findViewById(R.id.passwordcheckEdt);
        mregisterBtn = findViewById(R.id.register2_btn);



        //파이어베이스 user 로 접글

        //가입버튼 클릭리스너   -->  firebase에 데이터를 저장한다.
        mregisterBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                //가입 정보 가져오기
                String email = mEmailText.getText().toString();
                String pwd = mPasswordText.getText().toString();
                String pwdcheck = mPasswordcheckText.getText().toString();

                if(pwd.equals(pwdcheck)) {
                    Log.d(TAG, "등록 버튼 " + email + " , " + pwd);

                    if (email.length() > 0 && pwd.length() > 0 && pwdcheck.length() > 0) {

                        //파이어베이스에 신규계정 등록하기
                        firebaseAuth.createUserWithEmailAndPassword(email, pwd).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                //가입 성공시
                                if (task.isSuccessful()) {
                                    //가입이 이루어져을시 가입 화면을 빠져나감.

                                    Toast.makeText(RegisterActivity.this, "회원가입에 성공하셨습니다.", Toast.LENGTH_SHORT).show();

                                    myStartActivity(MemberInitActivity.class);


                                } else {
                                    Toast.makeText(RegisterActivity.this, "이미 존재하는 아이디거나, 비밀번호가 너무 짧습니다", Toast.LENGTH_SHORT).show();
                                }

                            }
                        });
                        //비밀번호 입력안했을시
                    } else {
                        startToast("이메일 또는 비밀번호를 입력해주세요");
                    }
                }else{ Toast.makeText(RegisterActivity.this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();}


            }
        });

    }

    private void startToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void myStartActivity(Class c){
        Intent intent=new Intent( this, c);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public boolean onSupportNavigateUp(){
        onBackPressed();; // 뒤로가기 버튼이 눌렸을시
        return super.onSupportNavigateUp(); // 뒤로가기 버튼
    }
}
