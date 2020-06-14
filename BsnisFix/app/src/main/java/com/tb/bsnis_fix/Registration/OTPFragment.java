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
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.tb.bsnis_fix.EditProfileActivity;
import com.tb.bsnis_fix.R;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * A simple {@link Fragment} subclass.
 */
public class OTPFragment extends Fragment {

    public OTPFragment(String email, String username, String idCountry, String phone, String password) {
        this.email = email;
        this.username = username;
        this.idCountry = idCountry;
        this.phone = phone;
        this.password = password;
    }

    private TextView tvPhone;
    private Button resendBtn;
    private EditText otp;
    private ProgressBar progressbar;
    private Button verifyBtn;
    private String email,username,idCountry,phone,password;

    private Timer timer;
    private int count = 60;

    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback;
    private FirebaseAuth mAuth;
    private DatabaseReference reference;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_o_t_p, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
        mAuth = FirebaseAuth.getInstance();

        tvPhone.setText("Verification code has been sent to "+ phone +""+ idCountry);

        sendOTP();

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (count == 0){
                            resendBtn.setText("RESEND");
                            resendBtn.setEnabled(true);
                            resendBtn.setAlpha(1f);
                        }else {
                            resendBtn.setText("Resend in "+count);
                            count--;
                        }
                    }
                });
            }
        }, 0, 1000);

        resendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resendOTP();
                resendBtn.setEnabled(false);
                resendBtn.setAlpha(0.5f);
                count = 60;
            }
        });

        verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (otp.getText() == null || otp.getText().toString().isEmpty()){
                    return;
                }
                otp.setError(null);
                String code = otp.getText().toString();
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
                signInWithPhoneAuthCredential(credential);
                progressbar.setVisibility(View.VISIBLE);
            }
        });
    }

    private void init(View view) {
        otp = view.findViewById(R.id.otp);
        progressbar = view.findViewById(R.id.progressbar);
        verifyBtn = view.findViewById(R.id.verify_btn);
        resendBtn = view.findViewById(R.id.resend_btn);
        tvPhone = view.findViewById(R.id.tv_phone);
    }

    private void sendOTP(){

        mCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                //Log.d(TAG, "onVerificationCompleted:" + credential);

                //signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                //Log.w(TAG, "onVerificationFailed", e);

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    otp.setError(e.getMessage());
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    otp.setError(e.getMessage());
                    progressbar.setVisibility(View.INVISIBLE);
                }
                // Show a message and update the UI
                // ...
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                //Log.d(TAG, "onCodeSent:" + verificationId);

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;

                // ...
            }
        };

        String complete_phone_number = phone +""+ idCountry;

        if(idCountry.isEmpty() || phone.isEmpty()){

        } else {
            progressbar.setVisibility(View.VISIBLE);
            verifyBtn.setEnabled(true);

            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    complete_phone_number,
                    60,
                    TimeUnit.SECONDS,
                    getActivity(),
                    mCallback);
        }
    };

    private void resendOTP(){
        String complete_phone_number = phone +""+ idCountry;

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                complete_phone_number,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                getActivity(),               // Activity (for callback binding)
                mCallback, mResendToken);        // OnVerificationStateChangedCallbacks
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            final String userid = user.getUid();
                            AuthCredential credential = EmailAuthProvider.getCredential(email,password);
                            user.linkWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()){

                                        reference = FirebaseDatabase.getInstance().getReference().child("Users").child(userid);

                                        Map<String, Object> map = new HashMap<>();
                                        map.put("id", userid);
                                        map.put("email",email);
                                        map.put("phone",phone+""+idCountry);
                                        map.put("username", username.toLowerCase());
                                        map.put("fullname", "");
                                        map.put("bio", "");
                                        map.put("imageurl", "https://firebasestorage.googleapis.com/v0/b/instagram7.appspot.com/o/profileimage.jpg?alt=media&token=267b6175-7b56-4c82-91cf-702a167f3b9c");


                                        reference.setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    Intent usernameIntent = new Intent(getContext(), EditProfileActivity.class);
                                                    startActivity(usernameIntent);
                                                    getActivity().finish();
                                                }else {
                                                    String error = task.getException().getMessage();
                                                    Toast.makeText(getContext(),error, Toast.LENGTH_SHORT).show();
                                                    progressbar.setVisibility(View.INVISIBLE);
                                                }
                                            }
                                        });
                                        progressbar.setVisibility(View.VISIBLE);

                                    }else {
                                        String error = task.getException().getMessage();
                                        Toast.makeText(getContext(),error, Toast.LENGTH_SHORT).show();
                                        progressbar.setVisibility(View.INVISIBLE);
                                    }
                                }
                            });
                        } else {
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                otp.setError("Invalid OTP");
                                progressbar.setVisibility(View.INVISIBLE);
                            }
                        }
                    }
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }
}
