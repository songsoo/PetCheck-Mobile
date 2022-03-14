package com.example.weatherm;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

//import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.SignInButton;
import com.google.firebase.auth.FirebaseAuth;

public class login extends AppCompatActivity {

        private FirebaseAuth mAuth = null;
        //private GoogleSignInClient mGoogleSignInClient;
        private static final int RC_SIGN_IN = 9001;
        private SignInButton signInButton;

        //Email Login
        //define view objects
        EditText editTextEmail;
        EditText editTextPassword;
        TextView textviewMessage;
        Button buttonLogin;
        Button bntSingin;
        Button bntFindpw;
        ProgressDialog progressDialog;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.login);

            //여기서부터
            //initializig firebase auth object
            //mAuth = FirebaseAuth.getInstance();

            //if(mAuth.getCurrentUser() != null) {
                //Exit this activity if you already have a history of log in
            //    finish();
                //And open the home activity
            //    startActivity(new Intent(getApplicationContext(), home.class));
            //}
            //initializing views
            editTextEmail = (EditText) findViewById(R.id.editTextEmail);
            editTextPassword = (EditText) findViewById(R.id.editTextPassword);
            buttonLogin = (Button) findViewById(R.id.buttonLogin);

            bntSingin= (Button) findViewById(R.id.btnSignin);
            bntFindpw = (Button) findViewById(R.id.btnFindpassword);

            textviewMessage = (TextView) findViewById(R.id.textviewMessage);
            progressDialog = new ProgressDialog(this);

            //button click event
            buttonLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getApplicationContext(), home.class));
                }
            });
            bntSingin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getApplicationContext(), NewId.class));
                }
            });
            bntFindpw.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getApplicationContext(), FindPw.class));
                }
            });
            //여기까지




         /*  // signInButton = findViewById(R.id.signInButton);
            mAuth = FirebaseAuth.getInstance();

            //If you have a record of logging in before, log in automatically.
            if (mAuth.getCurrentUser() != null) {
                SharedPreferences pref = getSharedPreferences("Service socket info", MODE_PRIVATE);

                String server_ip = pref.getString("server_ip", null);
                Log.d("LoginActivity", "server ip --->" + server_ip);

                Toast.makeText(getApplicationContext(), "자동 로그인", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplication(), home.class);
                startActivity(intent);
                finish();
            }


            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.firebase_web_client_id))
                    .requestEmail()
                    .build();

            mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
            signInButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    signIn();
                }
            });

        } //onCreate 끝


    //여기부터
    //firebase userLogin method
    private void userLogin(){
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if(TextUtils.isEmpty(email)){
            Toast.makeText(this, "email을 입력해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(password)){
            Toast.makeText(this, "password를 입력해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setMessage("로그인중입니다. 잠시 기다려 주세요...");
        progressDialog.show();

        //logging in the user
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if(task.isSuccessful()) {
                            finish();
                            startActivity(new Intent(getApplicationContext(), home.class));
                        } else {
                            Toast.makeText(getApplicationContext(), "로그인 실패!", Toast.LENGTH_LONG).show();
                            textviewMessage.setText("로그인 실패 유형\n - 회원이 아닙니다.\n - password가 맞지 않습니다.\n -서버에러");
                        }
                    }
                });
    }



    //Google Login
        private void signIn() {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
            if (requestCode == RC_SIGN_IN) {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                try {
                    // Google Sign In was successful, authenticate with Firebase
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    firebaseAuthWithGoogle(account);
                } catch (ApiException e) {
                }
            }
        }

        private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
            AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
            mAuth.signInWithCredential(credential)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "구글 로그인 성공", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplication(), home.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(getApplicationContext(), "구글 로그인 실패", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }*/
}}