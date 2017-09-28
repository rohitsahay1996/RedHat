package com.example.sony.timata;

import android.app.ProgressDialog;
import android.icu.text.DateFormat;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {
    private ImageView mProfileImage;
    private TextView mProfileName,mProfileStatus,mProfileFriendsCount;
    private Button mProfileSendRequestBtn;
    private Button mDeclinebtn;

    private DatabaseReference mUserDatabase;
    private DatabaseReference mFriendDatabase;
    private DatabaseReference mFriendRequestDatabase;
    private ProgressDialog mProgressDialog;
    private FirebaseUser mCurrentUser;
    private DatabaseReference mNotificationDatabase;
    private String mCurrent_State;
    private DatabaseReference mRootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
       final String user_id = getIntent().getStringExtra("user_id");
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
        mFriendDatabase = FirebaseDatabase.getInstance().getReference().child("Friends");
        mFriendRequestDatabase = FirebaseDatabase.getInstance().getReference().child("friend_request");
        mNotificationDatabase = FirebaseDatabase.getInstance().getReference().child("notifications");


        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        mProfileImage = (ImageView)findViewById(R.id.Profile_image);
        mProfileName = (TextView) findViewById(R.id.profile_Display_name);
        mProfileStatus = (TextView)findViewById(R.id.Profile_sratus);
        mProfileSendRequestBtn=(Button)findViewById(R.id.profile_send_req_btn);
        mDeclinebtn = (Button) findViewById(R.id.profile_decline_friend_btn);
        mProfileFriendsCount = (TextView)findViewById(R.id.Profile_TotalFriend);

       mCurrent_State = "not_friends";
        mDeclinebtn.setVisibility(View.INVISIBLE);
        mDeclinebtn.setEnabled(false);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("Loading User Data");
        mProgressDialog.setMessage("Please wait while we loading user data.");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();



        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
               String display_name = dataSnapshot.child("name").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();

                mProfileName.setText(display_name);
                mProfileStatus.setText(status);
                Picasso.with(ProfileActivity.this).load(image).placeholder(R.drawable.images).into(mProfileImage);
                mProgressDialog.dismiss();
                /* ------------ FRIEND lIST / REQUEST FEATURE------------ */
                mFriendRequestDatabase.child(mCurrentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild(user_id)){

                            String req_type = dataSnapshot.child(user_id).child("request_type").getValue().toString();
                            if(req_type.equals("Received")){
                                mCurrent_State="req_received";
                                mProfileSendRequestBtn.setText("Accept Friend request");
                                mDeclinebtn.setVisibility(View.VISIBLE);
                                mDeclinebtn.setEnabled(true);
                            }
                            else if(req_type.equals("sent")) {
                                mCurrent_State = "req_sent";
                                mProfileSendRequestBtn.setText("Cancel Friend request");
                                mDeclinebtn.setVisibility(View.INVISIBLE);
                                mDeclinebtn.setEnabled(false);



                            }
                            mProgressDialog.dismiss();
                        }
                        else{
                            mFriendDatabase.child(mCurrentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.hasChild(user_id)){

                                        mCurrent_State="friends";
                                        mProfileSendRequestBtn.setText("Unfriend this person");
                                        mDeclinebtn.setVisibility(View.INVISIBLE);
                                        mDeclinebtn.setEnabled(false);

                                    }
                                    mProgressDialog.dismiss();
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    mProgressDialog.dismiss();
                                }
                            });

                        }




                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });




            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mProfileSendRequestBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                mProfileSendRequestBtn.setEnabled(false);
                                   /* ------------ NOT FRIEND STATE------------ */
                 if(mCurrent_State.equals("not_friends")){

                     DatabaseReference newNotificationref = mRootRef.child("notifications").child(user_id).push();
                     String newNptificationId = newNotificationref.getKey();
                     HashMap<String , String> notificationData = new HashMap<String, String>();
                     notificationData.put("from", mCurrentUser.getUid());
                     notificationData.put("type","request");

                     Map requestMap = new HashMap();
                     requestMap.put("friend_request/" +mCurrentUser.getUid() + "/"+ user_id + "/request_type", "sent");
                     requestMap.put( "friend_request/" +user_id + "/"+ mCurrentUser.getUid() + "/request_type","Received");
                     requestMap.put("notifications/"+ user_id + "/"+ newNptificationId, notificationData );

                    mRootRef.updateChildren(requestMap, new DatabaseReference.CompletionListener() {
                         @Override
                         public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                             if(databaseError!=null){

                                 Toast.makeText(ProfileActivity.this, "There was some error in sending request!!", Toast.LENGTH_SHORT).show();
                             }
                 mProfileSendRequestBtn.setEnabled(true);

                             mCurrent_State="req_sent";
                             mProfileSendRequestBtn.setText("cancel Friend Request");
                         }
                     });


                 }
                   /* ----------Friends Received State----------- */
                if (mCurrent_State.equals("req_received")) {

                    final String currentDate = DateFormat.getDateTimeInstance().format(new Date());

                    Map friendsMap = new HashMap();
                    friendsMap.put("Friends/" + mCurrentUser.getUid() + "/" + user_id + "/date", currentDate);
                    friendsMap.put("Friends/" + user_id + "/" + mCurrentUser.getUid() + "/date", currentDate);

                    friendsMap.put("Friend_req/" + mCurrentUser.getUid() + "/" + user_id, null);
                    friendsMap.put("Friend_req/" + user_id + "/" + mCurrentUser.getUid(), null);

                    mRootRef.updateChildren(friendsMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                            if (databaseError == null) {

                                mProfileSendRequestBtn.setEnabled(true);
                                mCurrent_State = "friends";
                                mProfileSendRequestBtn.setText("unfriend this person");


                                mDeclinebtn.setVisibility(View.INVISIBLE);
                                mDeclinebtn.setEnabled(false);
                            } else {

                                String error = databaseError.getMessage();
                                Toast.makeText(ProfileActivity.this, "error", Toast.LENGTH_SHORT).show();
                            }


                        }
                    });

                }
                           /* ------------ CANCEL FRIEND STATE------------ */

                   if(mCurrent_State.equals("req_sent")){

                       mFriendRequestDatabase.child(mCurrentUser.getUid()).child(user_id).removeValue().
                               addOnSuccessListener(new OnSuccessListener<Void>() {
                           @Override
                           public void onSuccess(Void aVoid) {
                               mFriendRequestDatabase.child(user_id).child(mCurrentUser.getUid()).removeValue().
                                       addOnSuccessListener(new OnSuccessListener<Void>() {
                                   @Override
                                   public void onSuccess(Void aVoid) {
                                  mProfileSendRequestBtn.setEnabled(true);
                                       mCurrent_State="not_friends";
                                       mProfileSendRequestBtn.setText("Send Friend Request");

                                       mDeclinebtn.setVisibility(View.INVISIBLE);
                                       mDeclinebtn.setEnabled(false);
                                   }
                               });
                           }
                       });
                   }


                   //  ---------------- Unfriend

                if(mCurrent_State.equals("friends")){

                    Map unFriendMap = new HashMap();
                    unFriendMap.put("Friends/" + mCurrentUser.getUid() + "/" + user_id, null);
                    unFriendMap.put("Friends/" + user_id + "/" +mCurrentUser.getUid(), null);

                    mRootRef.updateChildren(unFriendMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                            if(databaseError == null){


                                mProfileSendRequestBtn.setEnabled(true);
                                mCurrent_State = "friends";
                                mProfileSendRequestBtn.setText("Unfriend this person");
                                mProfileSendRequestBtn.setText("send Friend Req");


                                mDeclinebtn.setVisibility(View.INVISIBLE);
                                mDeclinebtn.setEnabled(false);
                            }
                            else{

                                String error = databaseError.getMessage();
                                Toast.makeText(ProfileActivity.this, "error", Toast.LENGTH_SHORT).show();
                            }
                            mProfileSendRequestBtn.setEnabled(true);


                        }
                    });




                }


            }
        });
    }


}
