package com.example.sony.timata;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class FriendFragment extends Fragment {


    private RecyclerView mFriendsList;
    private DatabaseReference mFriendDatabase;
    private FirebaseAuth mAuth; //For getting current user_id
    private View mView;
    private String mCurrent_user_id;
    private DatabaseReference mUsersDatabase;



    public FriendFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_friend, container, false);
        mFriendsList = (RecyclerView) mView.findViewById(R.id.friend_recycle);
        mAuth = FirebaseAuth.getInstance();
        mCurrent_user_id = mAuth.getCurrentUser().getUid();
        mFriendDatabase = FirebaseDatabase.getInstance().getReference().child("Friends").child(mCurrent_user_id);
        mFriendDatabase.keepSynced(true);
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mUsersDatabase.keepSynced(true);

        mFriendsList.setHasFixedSize(true);
        mFriendsList.setLayoutManager(new LinearLayoutManager(getContext()));
        // Inflate the layout for this fragment
        return mView;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<Friends, FriendsViewHolder> friendsRecyclerViewAdapter = new FirebaseRecyclerAdapter<Friends, FriendsViewHolder>(

                Friends.class,
                R.layout.users_single_layout,
                FriendsViewHolder.class,
                mFriendDatabase

        ) {
            @Override
            protected void populateViewHolder(final FriendsViewHolder viewHolder, Friends model, int position) {
                viewHolder.setDate(model.getDate());
                final String list_user_id = getRef(position).getKey();
                mUsersDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        final String user_name = dataSnapshot.child("name").getValue().toString();
                        String ThumbImage = dataSnapshot.child("thumb_image").getValue().toString();

                        if (dataSnapshot.hasChild("online")) {
                            String userOnline = dataSnapshot.child("online").getValue().toString();
                            viewHolder.setUserOnline(userOnline);
                        }
                        viewHolder.setName(user_name);
                        viewHolder.setUsersImage(ThumbImage, getContext());

                        viewHolder.mViewHolder.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                CharSequence option[] = new CharSequence[]{"open profile", "send message"};

                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                builder.setTitle("Select Options");
                                builder.setItems(option, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        //Click Event
                                        if (which == 0) {
                                            Intent ProfileIntent = new Intent(getContext(), ProfileActivity.class);
                                            ProfileIntent.putExtra("user_id", list_user_id);
                                            startActivity(ProfileIntent);

                                        }

                                        if (which == 1) {

                                            //Sending message
                                            Intent chatIntent = new Intent(getContext(), ChatActivity.class);
                                            chatIntent.putExtra("user_id", list_user_id);
                                            chatIntent.putExtra("user_name", user_name);
                                            startActivity(chatIntent);
                                        }

                                    }
                                });
                                builder.show();
                            }
                        });


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        };


        mFriendsList.setAdapter(friendsRecyclerViewAdapter);
        //
    }

    public static class FriendsViewHolder extends RecyclerView.ViewHolder {
        View mViewHolder;

        public FriendsViewHolder(View itemView) {
            super(itemView);
            mViewHolder = itemView;
        }

        public void setDate(String date) {

            TextView userNameView = (TextView) mViewHolder.findViewById(R.id.User_single_status);
            userNameView.setText(date);
        }

        public void setName(String name) {

            TextView userNameView = (TextView) mViewHolder.findViewById(R.id.User_single_name);
            userNameView.setText(name);
        }

        public void setUsersImage(String thumb_image, Context ctx) {
            CircleImageView userImageView = (CircleImageView) mViewHolder.findViewById(R.id.User_single_img);
            Picasso.with(ctx).load(thumb_image).placeholder(R.drawable.images).into(userImageView);

        }

        public void setUserOnline(String online_status) {
            ImageView userOnlineImg = (ImageView) mViewHolder.findViewById(R.id.user_single_online_icon);

            if (online_status.equals("true")) {

                userOnlineImg.setVisibility(View.VISIBLE);

            } else userOnlineImg.setVisibility(View.INVISIBLE);


        }
    }





}
