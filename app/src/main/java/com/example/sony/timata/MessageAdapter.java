package com.example.sony.timata;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

/**
 * Created by Rohit on 9/25/2017.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {


    private List<messages> mMessageList;
    private FirebaseAuth mAuth;


    public MessageAdapter(List<messages> mMessageList) {
        this.mMessageList = mMessageList;
    }


    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_single_layout, parent, false);
        return new MessageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {


        mAuth = FirebaseAuth.getInstance();
        String current_user_id = mAuth.getCurrentUser().getUid();

        messages c = mMessageList.get(position);
        String from_user = c.getFrom();
        if (from_user.equals(current_user_id)) {

            holder.messageText.setBackgroundColor(Color.WHITE);
            holder.messageText.setTextColor(Color.BLACK);
            holder.mMessageLayout.setGravity(Gravity.LEFT);


        } else {

            holder.messageText.setBackgroundResource(R.drawable.message_text_background);
            holder.messageText.setTextColor(Color.WHITE);
            holder.mMessageLayout.setGravity(Gravity.RIGHT);
        }


        holder.messageText.setText(c.getMessage());
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {

        public TextView messageText;
        // public CircleImageView mProfileImage;
        public RelativeLayout mMessageLayout;

        public MessageViewHolder(View view) {
            super(view);

            messageText = (TextView) view.findViewById(R.id.message_text_layput);
            //mProfileImage = (CircleImageView) view.findViewById(R.id.message_profile_layout);
            mMessageLayout = (RelativeLayout) view.findViewById(R.id.message_single_layout);


        }
    }


}
