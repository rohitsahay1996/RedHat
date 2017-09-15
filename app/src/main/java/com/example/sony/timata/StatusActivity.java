package com.example.sony.timata;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StatusActivity extends AppCompatActivity {
    private Toolbar mToolBar;
    private TextInputLayout mChangeStatus;
    private Button mChange;
   private DatabaseReference mDatabase;
    private FirebaseUser mCurrentUser;

    //Progress Dialog
    private ProgressDialog mProgress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);
        //progress




        mChange = (Button) findViewById(R.id.status_change_status_btn);
        mChangeStatus = (TextInputLayout) findViewById(R.id.status_change_status);
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String current_uid = mCurrentUser.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference("Users").child(current_uid);

        String status_value =getIntent().getStringExtra("status_value");
        mChangeStatus.getEditText().setText(status_value);


        mChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgress = new ProgressDialog(StatusActivity.this);

                mProgress.setTitle("Saving Changes");
                mProgress.setMessage("Please wait..");
                mProgress.setCanceledOnTouchOutside(false);
                mProgress.show();
                String status = mChangeStatus.getEditText().getText().toString();
                mDatabase.child("status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            mProgress.dismiss();
                        }
                        else {
                            Toast.makeText(StatusActivity.this, "There are some errors!!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
        mToolBar = (Toolbar) findViewById(R.id.status_app_bar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setTitle("Account Status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }
}
