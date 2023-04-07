package com.unallapps.chatapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class SendMessageRecylerViewAdapter extends RecyclerView.Adapter<SendMessageRecylerViewAdapter.ViewHolder> {
    private List<String> chatMessages;
    private List<String> chatImage;

    public SendMessageRecylerViewAdapter(List<String> chatMessages, List<String> chatImages) {
        this.chatMessages = chatMessages;
        this.chatImage = chatImages;
    }

    @NonNull
    @Override
    public SendMessageRecylerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemview = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyler_list_row, parent, false);

        return new ViewHolder(itemview);
    }

    @Override
    public void onBindViewHolder(@NonNull SendMessageRecylerViewAdapter.ViewHolder holder, int position) {
        String chatMessage = chatMessages.get(position);
        String imageUrl = chatImage.get(position);
        holder.chatMessage.setText(chatMessage);
        Picasso.get().load(imageUrl).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView chatMessage;
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            chatMessage = itemView.findViewById(R.id.recyler_view_test_message);
            imageView = itemView.findViewById(R.id.message_image);
        }
    }
}
