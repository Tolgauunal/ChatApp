package com.unallapps.chatapp;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class GlobalChat extends Fragment {

    FirebaseAuth firebaseAuth;
    RecyclerView chatRecyclerview;
    EditText message;
    Button sendMessage;
    private ArrayList<String> chatMessagesFirebase = new ArrayList<>();
    private ArrayList<String> chatImagesFirebase = new ArrayList<>();
    private ArrayList<String> emailId = new ArrayList<>();
    SendMessageRecylerViewAdapter recylerViewAdapter;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    String firebaseImageUrl = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tabbed1, container, false);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        message = view.findViewById(R.id.chatActtivityMessageEdit);
        sendMessage = view.findViewById(R.id.chatActivitySendButton);
        chatRecyclerview = view.findViewById(R.id.chatActivityRecylerview);
        recylerViewAdapter = new SendMessageRecylerViewAdapter(emailId, chatMessagesFirebase, chatImagesFirebase);
        RecyclerView.LayoutManager recyclerViewManager = new LinearLayoutManager(getContext());
        chatRecyclerview.setLayoutManager(recyclerViewManager);
        chatRecyclerview.setAdapter(recylerViewAdapter);
        sendMessage.setOnClickListener(view1 -> send());
        getData();
        getProfilImage();
        return view;
    }

    private void send() {
        String messagesCheck = message.getText().toString();
        if (TextUtils.isEmpty(messagesCheck)) {
            message.setError("Mesaj Boş Gönderilemez");
        } else {
            String messageToSend = message.getText().toString();
            UUID uuid = UUID.randomUUID();
            String uuidString = uuid.toString();
            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
            String userEmail = firebaseUser.getEmail();
            databaseReference.child("Chats").child(uuidString).child("usermessage").setValue(messageToSend);
            databaseReference.child("Chats").child(uuidString).child("useremail").setValue(userEmail);
            if (firebaseImageUrl.matches("")) {
                databaseReference.child("Chats").child(uuidString).child("userimageurl").setValue("https://firebasestorage.googleapis.com/v0/b/chatapplication-173a3.appspot.com/o/defaultimage.png?alt=media&token=1255a0ff-b11e-4995-b7b9-60905a58618e");
            } else {
                databaseReference.child("Chats").child(uuidString).child("userimageurl").setValue(firebaseImageUrl);
            }
            databaseReference.child("Chats").child(uuidString).child("usermessagetime").setValue(ServerValue.TIMESTAMP);
            message.setText("");
            getData();
        }
    }

    public void getData() {
        DatabaseReference newReference = firebaseDatabase.getReference("Chats");
        Query query = newReference.orderByChild("usermessagetime");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatMessagesFirebase.clear();
                emailId.clear();
                chatImagesFirebase.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    HashMap<String, String> hashMap = (HashMap<String, String>) ds.getValue();
                    String userEmail = hashMap.get("useremail");
                    String userMessage = hashMap.get("usermessage");
                    String userImageUrl = hashMap.get("userimageurl");
                    emailId.add(userEmail);
                    chatMessagesFirebase.add(userMessage);
                    chatImagesFirebase.add(userImageUrl);
                    recylerViewAdapter.notifyDataSetChanged();
                    chatRecyclerview.smoothScrollToPosition(recylerViewAdapter.getItemCount());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), error.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getProfilImage() {
        DatabaseReference databaseReference2 = firebaseDatabase.getReference("Profiles");
        databaseReference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    HashMap<String, String> hashMap = (HashMap<String, String>) snapshot1.getValue();
                    String userName = hashMap.get("useremail");
                    if (userName.matches(firebaseAuth.getCurrentUser().getEmail())) {
                        firebaseImageUrl = hashMap.get("userimageurl");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}