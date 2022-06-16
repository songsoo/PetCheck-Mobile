package com.example.weatherm.profile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.weatherm.Model.ChatModel;
import com.example.weatherm.Model.MemberInfo;
import com.example.weatherm.R;
import com.example.weatherm.homeFolder.HomeFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

public class MessageActivity extends AppCompatActivity {
    private String chatRoomUid; //채팅방 하나 id
    private String myuid;       //나의 id
    private String destUid;     //상대방 uid

    private RecyclerView recyclerView;
    private Button button;
    private EditText editText;

    private FirebaseDatabase firebaseDatabase;

    private MemberInfo destUser;

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyy.MM.dd HH:mm");


//    @Override
////    public void onBackPressed() {
////        Intent intent = new Intent(MessageActivity.this, HomeFragment.class);
////
////        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK
////
////                | Intent.FLAG_ACTIVITY_NEW_TASK);
////
//////        startActivity(intent);
//////        overridePendingTransition(R.anim.in_left,R.anim.out_right);
////    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        init();
        sendMsg();
    }

    private void init()
    {
        myuid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        destUid = getIntent().getStringExtra("destUid");        //채팅 상대
        FirebaseFirestore firestore= FirebaseFirestore.getInstance();
        DocumentReference documentReference=firestore.collection("users").document(destUid);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot d=task.getResult();
                DocumentSnapshot result=task.getResult();
                HashMap<String ,Double> hashMap=(HashMap<String, Double>) result.getData().get("address");
                MemberInfo memberInfo=new MemberInfo(

                        result.getData().get("uid").toString(),
                        result.getData().get("date").toString(),
                        result.getData().get("photoUrl").toString(),
                        result.getData().get("nickname").toString(),
                        (ArrayList<String>) result.getData().get("walkingList"),
                        (ArrayList<String>) result.getData().get("bookmarkRouteList"),
                        (ArrayList<String>) result.getData().get("routeNameList"),
                        (ArrayList<String>) result.getData().get("friendList"),
                        result.getData().get("gender").toString(),
                        result.getData().get("myProfile").toString(),
                        getLatLng(hashMap)
                );
                destUser=memberInfo;



            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });


        recyclerView = (RecyclerView)findViewById(R.id.message_recyclerview);
        button=(Button)findViewById(R.id.message_btn);
        editText = (EditText)findViewById(R.id.message_editText);

        firebaseDatabase = FirebaseDatabase.getInstance();

        if(editText.getText().toString() == null) button.setEnabled(false);
        else button.setEnabled(true);

        checkChatRoom();
    }

//    //푸시알람...구현x
//    private void sendGcm()
//    {
//        final Gson gson = new Gson();
//
//        final NotificationModel notificationModel = new NotificationModel();
//
//        firebaseDatabase.getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                for(DataSnapshot dataSnapshot : snapshot.getChildren())
//                {
//                    String name = dataSnapshot.child("name").getValue().toString();
//                    notificationModel.notification.title = name;
//                    notificationModel.to = destUser.pushToken;
//                    notificationModel.notification.text = editText.getText().toString();
//
//                    notificationModel.data.title = name;
//                    notificationModel.data.text = editText.getText().toString();
//                }
//
//                //서버에 알림 데이터를 json 형태로
//                RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf8"),gson.toJson(notificationModel));
//                Request request = new Request.Builder()
//                        .header("Context-Type","application/json")
//                        .addHeader("Authorization", "서버키")
//                        .url("서버 url")
//                        .post(requestBody)
//                        .build();
//
//                OkHttpClient okHttpClient = new OkHttpClient();
//                okHttpClient.newCall(request).enqueue(new Callback() {
//                    @Override
//                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
//                    }
//
//                    @Override
//                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
//                    }
//                });
//
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//            }
//        });
//
//        //서버에 토큰을 db에 저장하고, 저장한 토큰을 가지고 서버에서 FirebaseMessagingService 에 메시지를 보낸다.
//    }

    private void sendMsg()
    {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChatModel chatModel = new ChatModel();
                chatModel.users.put(myuid,true);
                chatModel.users.put(destUid,true);

                //push() 데이터가 쌓이기 위해 채팅방 key가 생성
                if(chatRoomUid == null){
                    Toast.makeText(MessageActivity.this, "채팅방 생성", Toast.LENGTH_SHORT).show();
                    button.setEnabled(false);
                    firebaseDatabase.getReference().child("chatrooms").push().setValue(chatModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            checkChatRoom();
                        }
                    });
                }else{
                    sendMsgToDataBase();
                }
            }
        });
    }

    //작성한 메시지를 데이터베이스에 보낸다.
    private void sendMsgToDataBase()
    {
        if(!editText.getText().toString().equals(""))
        {
            ChatModel.Comment comment = new ChatModel.Comment();
            comment.uid = myuid;
            comment.message = editText.getText().toString();
            comment.timestamp = ServerValue.TIMESTAMP;
            firebaseDatabase.getReference().child("chatrooms").child(chatRoomUid).child("comments").push().setValue(comment).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    editText.setText("");
                }
            });
        }
    }

    private void checkChatRoom()
    {
        //자신 key == true 일때 chatModel 가져온다.
        /* chatModel
        public Map<String,Boolean> users = new HashMap<>(); //채팅방 유저
        public Map<String, ChatModel.Comment> comments = new HashMap<>(); //채팅 메시지
        */
        firebaseDatabase.getReference().child("chatrooms").orderByChild("users/"+myuid).equalTo(true).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot:snapshot.getChildren()) //나, 상대방 id 가져온다.
                {
                    ChatModel chatModel = dataSnapshot.getValue(ChatModel.class);
                    if(chatModel.users.containsKey(destUid)){           //상대방 id 포함돼 있을때 채팅방 key 가져옴
                        chatRoomUid = dataSnapshot.getKey();
                        button.setEnabled(true);

                        //동기화
                        recyclerView.setLayoutManager(new LinearLayoutManager(MessageActivity.this));
                        recyclerView.setAdapter(new RecyclerViewAdapter());

                        //메시지 보내기
                        sendMsgToDataBase();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    //===============채팅 창===============//
    class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>
    {
        List<ChatModel.Comment> comments;

        public RecyclerViewAdapter(){
            comments = new ArrayList<>();

            getDestUid();
        }

        //상대방 uid 하나(single) 읽기
        private void getDestUid()
        {
            firebaseDatabase.getReference().child("users").child(destUid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                    destUser = snapshot.getValue(MemberInfo.class);

                    //채팅 내용 읽어들임
                    getMessageList();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }

        //채팅 내용 읽어들임
        private void getMessageList()
        {
            FirebaseDatabase.getInstance().getReference().child("chatrooms").child(chatRoomUid).child("comments").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    comments.clear();

                    for(DataSnapshot dataSnapshot : snapshot.getChildren())
                    {
                        comments.add(dataSnapshot.getValue(ChatModel.Comment.class));
                    }
                    notifyDataSetChanged();

                    recyclerView.scrollToPosition(comments.size()-1);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) { }
            });
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_messagebox,parent,false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            ViewHolder viewHolder = ((ViewHolder)holder);

            if(comments.get(position).uid.equals(myuid)) //나의 uid 이면
            {
                //나의 말풍선 오른쪽으로
                viewHolder.textViewMsg.setText(comments.get(position).message);
//                viewHolder.textViewMsg.setBackgroundResource(R.drawable.rightbubble);
                viewHolder.linearLayoutDest.setVisibility(View.INVISIBLE);        //상대방 레이아웃
                viewHolder.linearLayoutRoot.setGravity(Gravity.RIGHT);
                viewHolder.linearLayoutTime.setGravity(Gravity.RIGHT);
            }else{
//                상대방 말풍선 왼쪽
                Glide.with(holder.itemView.getContext())
                        .load(destUser.getPhotoUrl())
                        .apply(new RequestOptions().circleCrop())
                        .into(holder.imageViewProfile);
                viewHolder.textViewName.setText(destUser.getNickname());
                viewHolder.linearLayoutDest.setVisibility(View.VISIBLE);
//                viewHolder.textViewMsg.setBackgroundResource(R.drawable.leftbubble);
                viewHolder.textViewMsg.setText(comments.get(position).message);
                viewHolder.linearLayoutRoot.setGravity(Gravity.LEFT);
                viewHolder.linearLayoutTime.setGravity(Gravity.LEFT);
            }
            viewHolder.textViewTimeStamp.setText(getDateTime(position));

        }

        public String getDateTime(int position)
        {
            long unixTime=(long) comments.get(position).timestamp;
            Date date = new Date(unixTime);
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
            String time = simpleDateFormat.format(date);
            return time;
        }

        @Override
        public int getItemCount() {
            return comments.size();
        }

        private class ViewHolder extends RecyclerView.ViewHolder
        {
            public TextView textViewMsg;   //메시지 내용
            public TextView textViewName;
            public TextView textViewTimeStamp;
            public ImageView imageViewProfile;
            public LinearLayout linearLayoutDest;
            public LinearLayout linearLayoutRoot;
            public LinearLayout linearLayoutTime;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                textViewMsg = (TextView)itemView.findViewById(R.id.item_messagebox_textview_msg);
                textViewName = (TextView)itemView.findViewById(R.id.item_messagebox_TextView_name);
                textViewTimeStamp = (TextView)itemView.findViewById(R.id.item_messagebox_textview_timestamp);
                imageViewProfile = (ImageView)itemView.findViewById(R.id.item_messagebox_ImageView_profile);
                linearLayoutDest = (LinearLayout)itemView.findViewById(R.id.item_messagebox_LinearLayout);
                linearLayoutRoot = (LinearLayout)itemView.findViewById(R.id.item_messagebox_root);
                linearLayoutTime = (LinearLayout)itemView.findViewById(R.id.item_messagebox_layout_timestamp);
            }
        }
    }
    public LatLng getLatLng(HashMap<String,Double> hashmap){
        double latitude=hashmap.get("latitude");
        double longitude=hashmap.get("longitude");
        LatLng latLng=new LatLng(latitude,longitude);
        return latLng;
    }
}