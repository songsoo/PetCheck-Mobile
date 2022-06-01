package com.example.weatherm.profile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.weatherm.R;
import com.example.weatherm.SignUp.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class ProfileFragment extends Fragment {
    private View rootView;
    private ArrayList<String> menuList=new ArrayList<>();
    private profileAdapter profileAdapter;
    private RecyclerView recyclerView;
    private ImageView profileImageView;
    private TextView nickNmaeTextView;
    private Button profileModifyButton;
    private FirebaseFirestore firestore;
    private FirebaseUser currentUser;
    private String profileImageUrl;
    private String nickName;

    private Toolbar myToolbar;
    private ProfileFragment profileFragment;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView=inflater.inflate(R.layout.fragment_profile,container,false);


        menuList.clear();

        menuList.add("나의 산책 기록");
        menuList.add("저장한 산책로 목록");

        Log.d("왜안되냐구","시불");

        //리사이클러뷰 작성
        recyclerView=(RecyclerView) rootView.findViewById(R.id.profile_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        RecyclerView.Adapter mAdapter1 =new profileAdapter(getActivity(),menuList);
        recyclerView.setAdapter(mAdapter1);

        profileImageView=rootView.findViewById(R.id.profileImageVIew_profile);

        nickNmaeTextView=rootView.findViewById(R.id.nickNameTextView_profile);
        profileModifyButton=rootView.findViewById(R.id.modify_profile_button);

        profileModifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(),ProfileModifyActivity.class);
                startActivity(intent);
            }
        });


        firestore=FirebaseFirestore.getInstance();
        currentUser=FirebaseAuth.getInstance().getCurrentUser();
        DocumentReference documentReference=firestore.collection("users").document(currentUser.getUid());
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot d=task.getResult();
                profileImageUrl=d.getData().get("photoUrl").toString();
                //Glide.with(getActivity()).load(profileImageUrl).centerCrop().into(profileImageView);
                nickName=d.getData().get("nickname").toString();
                nickNmaeTextView.setText(nickName);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

        // Toolbar를 생성.
        myToolbar = (Toolbar) rootView.findViewById(R.id.my_toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(myToolbar);


        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("내 프로필");  //해당 액티비티의 툴바에 있는 타이틀을 바꾸기


        setHasOptionsMenu(true);
        profileFragment=new ProfileFragment();
        return rootView;



    }

    @Override
    public void onResume(){
        super.onResume();
        FragmentTransaction ft = getParentFragmentManager().beginTransaction();
        ft.detach(this).attach(this).commit();

        firestore=FirebaseFirestore.getInstance();
        currentUser=FirebaseAuth.getInstance().getCurrentUser();
        DocumentReference documentReference=firestore.collection("users").document(currentUser.getUid());
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot d=task.getResult();
                profileImageUrl=d.getData().get("photoUrl").toString();
                Glide.with(getActivity()).load(profileImageUrl).centerCrop().into(profileImageView);
                nickName=d.getData().get("nickname").toString();
                nickNmaeTextView.setText(nickName);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }


    //ToolBar에 menu.xml을 인플레이트함
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu,inflater);
        inflater.inflate(R.menu.profile_toolbar_menu,menu);
    }

    //ToolBar에 추가된 항목의 select 이벤트를 처리하는 함수
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //return super.onOptionsItemSelected(item);
        switch (item.getItemId()) {

            case R.id.profile_logout:
                // User chose the "Settings" item, show the app settings UI...
                FirebaseAuth.getInstance().signOut();
                Intent intent=new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }



}
