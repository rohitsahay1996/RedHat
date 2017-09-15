package com.example.sony.timata;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private RecyclerView mUsersList;
    private DatabaseReference mUsersDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);
        mToolbar = (Toolbar) findViewById(R.id.users_app_layout);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("All Users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");



        mUsersList = (RecyclerView) findViewById(R.id.users_list);
        mUsersList.setHasFixedSize(true);
        mUsersList.setLayoutManager(new LinearLayoutManager(this));



    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<Users,UsersviewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Users, UsersviewHolder>(

                Users.class,
                R.layout.users_single_layout,
                UsersviewHolder.class,
                mUsersDatabase

        ) {
            @Override
            protected void populateViewHolder(UsersviewHolder viewHolder, Users model, int position) {

                              viewHolder.setName(model.getName());
                              viewHolder.setStatus(model.getStatus());
                             //  viewHolder.setThumb_image(model.getImage(),getApplicationContext());
                             //  viewHolder.setImage(model.getImage(),getApplicationContext());
                viewHolder.setUsersImage(model.getThumb_image(),getApplicationContext());

                final String user_id = getRef(position).getKey();
                viewHolder.mViesw.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent profile_intent = new Intent(UsersActivity.this,ProfileActivity.class);
                        profile_intent.putExtra("user_id",user_id);
                        startActivity(profile_intent);
                    }
                });


            }
        };

        mUsersList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class UsersviewHolder extends RecyclerView.ViewHolder{

        View mViesw;
        public UsersviewHolder(View itemView) {
            super(itemView);

            mViesw = itemView;
        }

        public void setName(String name) {

            TextView mUserNameView = (TextView) mViesw.findViewById(R.id.User_single_name);
            mUserNameView.setText(name);
        }

        public void setStatus(String status) {
            TextView mUserStatusView = (TextView) mViesw.findViewById(R.id.User_single_status);
            mUserStatusView.setText(status);
        }

       /* public void setThumb_image(String thumb_image, Context ctx) {

            CircleImageView userImageView = (CircleImageView) mViesw.findViewById(R.id.User_single_img);
            Picasso.with(ctx).load(thumb_image).placeholder(R.drawable.images).into(userImageView);
        }*/

        //public void setImage(String image,Context ctx) {
          //  CircleImageView userImageView = (CircleImageView) mViesw.findViewById(R.id.User_single_img);
            //Picasso.with(ctx).load(image).placeholder(R.drawable.images).into(userImageView);
        //}

        public void setUsersImage(String thumb_image,Context ctx) {
            CircleImageView userImageView = (CircleImageView) mViesw.findViewById(R.id.User_single_img);
            Picasso.with(ctx).load(thumb_image).placeholder(R.drawable.images).into(userImageView);

        }
    }

}
