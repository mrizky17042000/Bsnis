package com.tb.bsnis_fix;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;

import com.tb.bsnis_fix.Registration.ForgotPasswordFragment;
import com.tb.bsnis_fix.Registration.OTPFragment;

public class ForgotPasswordActivity extends AppCompatActivity {

    private View frameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpassword);

        frameLayout = findViewById(R.id.framelayout);
        setFragment(new ForgotPasswordFragment());
    }

    public void setFragment(Fragment fragment){

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
        if (fragment instanceof ForgotPasswordFragment || fragment instanceof OTPFragment){
            fragmentTransaction.addToBackStack(null);
        }
        fragmentTransaction.replace(frameLayout.getId(),fragment);
        fragmentTransaction.commit();
    }
}
