package com.tb.bsnis_fix.Registration;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.tb.bsnis_fix.LoginActivity;
import com.tb.bsnis_fix.R;

import java.util.regex.Pattern;

/**
 * A simple {@link Fragment} subclass.
 */
public class CreateAccountFragment extends Fragment {

    public CreateAccountFragment() {
        // Required empty public constructor
    }
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    private EditText email, username, phone, idCountry, password;
    private Button createaccountAccountBtn;
    private ProgressBar progressbar;
    private TextView loginTV;
    private FirebaseAuth mAuth;

    public final static String USERNAME_PATTERN = "^[a-z0-9_-]{3,15}$";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init(view);

        mAuth = FirebaseAuth.getInstance();

        loginTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), LoginActivity.class));
            }
        });

        createaccountAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email.setError(null);
                username.setError(null);
                idCountry.setError(null);
                phone.setError(null);
                password.setError(null);
                if (email.getText().toString().isEmpty()) {
                    email.setError("Required");
                    return;
                }
                if (!username.getText().toString().matches(USERNAME_PATTERN)) {
                    username.setError("Only \"a to z, 9 - 0, - and _\" to allowed!");
                    return;
                } else if(username.getText().toString().isEmpty()){
                    username.setError("Required!");
                    return;
                }
                if (idCountry.getText().toString().isEmpty()) {
                    idCountry.setError("Required");
                    return;
                }
                if (phone.getText().toString().isEmpty()) {
                    phone.setError("Required");
                    return;
                }
                 if (password.getText().toString().isEmpty()) {
                    password.setError("Required");
                    return;
                }
                if (!VALID_EMAIL_ADDRESS_REGEX.matcher(email.getText().toString()).find()){
                    email.setError("please Enter a valid Email!");
                    return;
                }
                if (phone.getText().toString().length() != 11){
                    phone.setError("Please enter a valid Phone Number!");
                    return;
                }
                createAccount();
            }
        });
    }

    private void init(View view) {
        email = view.findViewById(R.id.email);
        username = view.findViewById(R.id.username);
        phone = view.findViewById(R.id.phone);
        idCountry = view.findViewById(R.id.id_country);
        password = view.findViewById(R.id.password);
        createaccountAccountBtn = view.findViewById(R.id.create_account_btn);
        progressbar = view.findViewById(R.id.progressbar);
        loginTV = view.findViewById(R.id.login_text);

    }

    private void createAccount(){
        progressbar.setVisibility(View.VISIBLE);
        mAuth.fetchSignInMethodsForEmail(email.getText().toString()).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
            @Override
            public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                if (task.isSuccessful()){
                    if (task.getResult().getSignInMethods().isEmpty()){
                        ((RegisterActivity)getActivity()).setFragment(new OTPFragment(email.getText().toString(),username.getText().toString(),phone.getText().toString(),idCountry.getText().toString(),password.getText().toString()));
                    }else {
                        email.setError("Email already exists!");
                        progressbar.setVisibility(View.INVISIBLE);
                    }
                }else {
                    String error = task.getException().getMessage();
                    Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                    progressbar.setVisibility(View.INVISIBLE);
                }

            }
        });
    }
}