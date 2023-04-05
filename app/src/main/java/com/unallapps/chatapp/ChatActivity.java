package com.unallapps.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
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
import com.unallapps.chatapp.databinding.ActivityChatBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Queue;
import java.util.UUID;

public class ChatActivity extends AppCompatActivity {
    ActivityChatBinding binding;
    FirebaseAuth firebaseAuth;
    RecyclerView chatRecyclerview;
    EditText message;
    Button sendMessage;
    private ArrayList<String> chatMessagesFirebase = new ArrayList<>();
    SendMessageRecylerViewAdapter recylerViewAdapter;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        message = binding.chatActtivityMessageEdit;
        sendMessage = binding.chatActivitySendButton;

        chatRecyclerview = binding.chatActivityRecylerview;
        recylerViewAdapter = new SendMessageRecylerViewAdapter(chatMessagesFirebase);
        RecyclerView.LayoutManager recyclerViewManager = new LinearLayoutManager(getApplicationContext());
        chatRecyclerview.setLayoutManager(recyclerViewManager);
        chatRecyclerview.setAdapter(recylerViewAdapter);
        sendMessage.setOnClickListener(view1 -> send());

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        getData();
    }

    private void send() {
        String messageToSend = message.getText().toString();
        UUID uuid = UUID.randomUUID();
        String uuidString = uuid.toString();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        String userEmail = firebaseUser.getEmail();
        databaseReference.child("Chats").child(uuidString).child("usermessage").setValue(messageToSend);
        databaseReference.child("Chats").child(uuidString).child("useremail").setValue(userEmail);
        databaseReference.child("Chats").child(uuidString).child("usermessagetime").setValue(ServerValue.TIMESTAMP);
        message.setText("");
        getData();
    }

    public void getData() {
        DatabaseReference newReference = firebaseDatabase.getReference("Chats");
        Query query = newReference.orderByChild("usermessagetime");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatMessagesFirebase.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    HashMap<String, String> hashMap = (HashMap<String, String>) ds.getValue();
                    String userEmail = hashMap.get("useremail");
                    String userMessage = hashMap.get("usermessage");
                    chatMessagesFirebase.add(userEmail + ": " + userMessage);
                    recylerViewAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), error.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        MenuInflater getMenuInflater = getMenuInflater();
        getMenuInflater.inflate(R.menu.option_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.option_sign_out) {
            firebaseAuth.signOut();
            Intent intent = new Intent(ChatActivity.this, SignUpActivity.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.option_menu_profile) {
            Intent intent = new Intent(ChatActivity.this, ProfileActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}