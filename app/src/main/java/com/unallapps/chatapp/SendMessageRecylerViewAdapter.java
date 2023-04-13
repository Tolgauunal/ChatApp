package com.unallapps.chatapp;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.List;

public class SendMessageRecylerViewAdapter extends RecyclerView.Adapter<SendMessageRecylerViewAdapter.ViewHolder> {
    private List<String> chatMessages;
    private List<String> emailMessage;
    private List<String> chatImage;
    private FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth;
    private String loginEmail;

    public SendMessageRecylerViewAdapter(List<String> emailMessages, List<String> chatMessages, List<String> chatImages) {
        this.chatMessages = chatMessages;
        this.emailMessage = emailMessages;
        this.chatImage = chatImages;

    }

    @NonNull
    @Override
    public SendMessageRecylerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemview = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyler_list_row, parent, false);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        loginEmail = firebaseUser.getEmail();
        return new ViewHolder(itemview);
    }

    @Override
    public void onBindViewHolder(@NonNull SendMessageRecylerViewAdapter.ViewHolder holder, int position) {
        String chatMessage = chatMessages.get(position);
        String emailId = emailMessage.get(position);
        String imageUrl = chatImage.get(position);
        holder.chatMessage.setText(chatMessage);
        holder.emailMessages.setText(emailId);
        Picasso.get().load(imageUrl).into(holder.imageView);
        if (emailMessage != null && emailId.matches(loginEmail)) {
            holder.linearLayout.setGravity(Gravity.RIGHT | Gravity.END);
        }
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView chatMessage;
        ImageView imageView;
        TextView emailMessages;
        LinearLayout linearLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            chatMessage = itemView.findViewById(R.id.recyler_view_test_message);
            emailMessages = itemView.findViewById(R.id.email_text);
            imageView = itemView.findViewById(R.id.message_image);
            linearLayout=itemView.findViewById(R.id.chatLinear);
        }
    }
}
