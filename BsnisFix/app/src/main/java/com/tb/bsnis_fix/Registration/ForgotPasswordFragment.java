package com.tb.bsnis_fix.Registration;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.tb.bsnis_fix.LoginActivity;
import com.tb.bsnis_fix.R;
import com.tb.bsnis_fix.StartActivity;

import static com.tb.bsnis_fix.Registration.CreateAccountFragment.VALID_EMAIL_ADDRESS_REGEX;

/**
 * A simple {@link Fragment} subclass.
 */
public class ForgotPasswordFragment extends Fragment {

    public ForgotPasswordFragment() {
        // Required empty public constructor
    }

    private EditText email;
    private Button ressetBtn;
    private TextView forgot;

    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_forgot_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init(view);

        mAuth = FirebaseAuth.getInstance();

        ressetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email.setError(null);

                if (VALID_EMAIL_ADDRESS_REGEX.matcher(email.getText().toString()).find()){
                   // progressbar.setVisibility(View.VISIBLE);
                    ressetBtn.setEnabled(false);
                    mAuth.sendPasswordResetEmail(email.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                //progressbar.setVisibility(View.VISIBLE);
                                Toast.makeText(getContext(),"Password reset Email sent Succes!",Toast.LENGTH_LONG).show();
                                startActivity(new Intent(getContext(), LoginActivity.class));
                            }else {
                                String error = task.getException().getMessage();
                                email.setError(error);
                               // progressbar.setVisibility(View.INVISIBLE);
                            }
                            ressetBtn.setEnabled(true);
                        }
                    });
                }else {
                    email.setError("Please provide a valid email");
            //        progressbar.setVisibility(View.INVISIBLE);
                }
            }
        });


    }

    private void init(View view) {
        email = view.findViewById(R.id.email);
        forgot = view.findViewById(R.id.textView3);
        ressetBtn = view.findViewById(R.id.reset_btn);
    }
}
