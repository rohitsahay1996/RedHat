package com.example.sony.timata;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import org.w3c.dom.Text;

import java.util.HashMap;


public class RegisterActivity extends AppCompatActivity {
    private TextInputLayout mDispalyName;
    private TextInputLayout mEmail;
    private TextInputLayout mPassword;
    private Button mRegbtn;
    private FirebaseAuth mAuth;
    //Progress Dialog
    private ProgressDialog mRegProgress;
    //Toolbar
    private Toolbar mToolbar;
    //FireBase Database
    private DatabaseReference mDataBase;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();

        //Toolbar
        mToolbar = (Toolbar) findViewById(R.id.register_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Create Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //ProgressDialog
        mRegProgress = new ProgressDialog(this);


        mDispalyName = (TextInputLayout) findViewById(R.id.reg_displayame);
        mEmail = (TextInputLayout) findViewById(R.id.reg_email);
        mPassword = (TextInputLayout) findViewById(R.id.reg_password);
        mRegbtn = (Button) findViewById(R.id.reg_createacnt);
        mRegbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String display_name = mDispalyName.getEditText().getText().toString();
                String email = mEmail.getEditText().getText().toString();
                String password = mPassword.getEditText().getText().toString();

                if(!TextUtils.isEmpty(display_name) || !TextUtils.isEmpty(email) || !TextUtils.isEmpty(password)){

                    mRegProgress.setMessage("Please Wait!!");
                    mRegProgress.setTitle("Registring User!!");
                    mRegProgress.setCanceledOnTouchOutside(false);
                   mRegProgress.show();

                    register_user(display_name,email,password);

                }
                else{

                    Toast.makeText(RegisterActivity.this, "Fiels Should not be empty!!", Toast.LENGTH_SHORT).show();
                }




            }
        });


    }

    private void register_user(final String display_name, String email, String password) {
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
                    String uid = current_user.getUid();


                    //Device token
                    String device_token = FirebaseInstanceId.getInstance().getToken();


                    mDataBase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
                    HashMap<String, String> user_map = new HashMap<String, String>();
                    user_map.put("name",display_name);
                    user_map.put("status","Hi! i am using Timata!");
                    user_map.put("image","default");
                    user_map.put("thumb_image","default");
                    user_map.put("device_token",device_token);

                    mDataBase.setValue(user_map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                mRegProgress.dismiss();
                                Intent regIntent = new Intent(RegisterActivity.this,MainActivity.class);
                                regIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(regIntent);
                                finish();
                            }
                        }
                    });



                }
                else{
                    mRegProgress.hide();
                    Toast.makeText(RegisterActivity.this, "Please check email and password!!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
