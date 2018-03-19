package com.example.kaihuynh.part_timejob;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity{

    private Button mLoginButton;
    private TextView mToRegisterTextView;
    private EditText mEmail, mPassword;
    private ProgressDialog mProgress;

    public static LoginActivity sInstance = null;

    //Firebase instance variables
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mUserDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        addComponents();
        initialize();
        addEvents();
    }

    private void initialize() {
        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Đang xác nhận thông tin...");
        mProgress.setCancelable(false);
        mProgress.setIndeterminate(true);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mUserDatabaseReference = mFirebaseDatabase.getReference().child("users");
        mAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user!= null){
                    startActivity(new Intent(LoginActivity.this, HomePageActivity.class));
                    finish();
                }else {

                }
            }
        };
    }

    private void addEvents() {
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mProgress.show();
                mAuth.signInWithEmailAndPassword(mEmail.getText().toString(), mPassword.getText().toString())
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    mProgress.dismiss();
                                    // Sign in success, update UI with the signed-in user's information
                                    finish();
                                } else {
                                    mProgress.dismiss();
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(LoginActivity.this, "Thông tin đăng nhập không chính xác.",
                                            Toast.LENGTH_SHORT).show();
                                }

                                // ...
                            }
                        });
            }
        });

        mEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(mEmail.getText().length()>0 && mPassword.getText().length()>0){
                    mLoginButton.setEnabled(true);
                }else {
                    mLoginButton.setEnabled(false);
                }
            }
        });

        mPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(mEmail.getText().length()>0 && mPassword.getText().length()>0){
                    mLoginButton.setEnabled(true);
                }else {
                    mLoginButton.setEnabled(false);
                }
            }
        });

        mToRegisterTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, LoginMethodActivity.class));
            }
        });
    }


    private void addComponents() {
        mEmail = findViewById(R.id.editText_email_login);
        mPassword = findViewById(R.id.editText_password_login);
        mLoginButton = findViewById(R.id.btn_login);
        mToRegisterTextView = findViewById(R.id.tv_to_register);
        sInstance = this;
    }

    public static LoginActivity getInstance(){
        if (sInstance == null) {
            sInstance = new LoginActivity();
        }
        return sInstance;
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mAuth.removeAuthStateListener(mAuthStateListener);
    }
}
