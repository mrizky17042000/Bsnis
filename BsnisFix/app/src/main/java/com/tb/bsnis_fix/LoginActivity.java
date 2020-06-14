package com.tb.bsnis_fix;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tb.bsnis_fix.Model.User;
import com.tb.bsnis_fix.Registration.RegisterActivity;

import static com.tb.bsnis_fix.Registration.CreateAccountFragment.VALID_EMAIL_ADDRESS_REGEX;


public class LoginActivity extends AppCompatActivity {

    EditText emailORphone, password;
    Button login;
    TextView txt_signup, forgot_txt;

    FirebaseAuth auth;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailORphone = findViewById(R.id.emailorphone);
        login = findViewById(R.id.login_btn);
        password = findViewById(R.id.password);
        txt_signup = findViewById(R.id.txt_signup);
        forgot_txt = findViewById(R.id.forgot_password_TV);

        auth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        txt_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

        forgot_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ProgressDialog pd = new ProgressDialog(LoginActivity.this);
                pd.setMessage("Please wait...");
                pd.show();

                String str_email = emailORphone.getText().toString();
                String str_password = password.getText().toString();

                if(TextUtils.isEmpty(str_email) || TextUtils.isEmpty(str_password)){
                    Toast.makeText(LoginActivity.this, "All fields are required!", Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                } else {
                    if (VALID_EMAIL_ADDRESS_REGEX.matcher(emailORphone.getText().toString()).find()){
                        Login(str_email);
                    }else {
                        emailORphone.setError("please Enter a valid Email or Phone!");
                        pd.dismiss();
                    }
                }
            }
        });
    }

    private void Login(String email){
        auth = FirebaseAuth.getInstance();
        final ProgressDialog pd = new ProgressDialog(LoginActivity.this);
        auth.signInWithEmailAndPassword(email,password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    pd.dismiss();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }else {
                    Toast.makeText(LoginActivity.this, "Your Email or Password is wrong!", Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            }
        });

    }
}
