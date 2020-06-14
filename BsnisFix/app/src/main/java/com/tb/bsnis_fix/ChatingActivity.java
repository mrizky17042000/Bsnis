package com.tb.bsnis_fix;


import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tb.bsnis_fix.Adapter.TabsAccessAdapter;
import com.tb.bsnis_fix.Model.User;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

//resolve the package to be imported
//import android.support.v7.widget.Toolbar;


public class ChatingActivity extends AppCompatActivity {

    private ViewPager myviewpager;
    private Toolbar mytoobar;
    private TabLayout mytablayout;
    private FirebaseAuth myauth;
    private DatabaseReference rooref, userref;
    private String currentUserID;
    private TabsAccessAdapter mytabaccessadapter;


    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chating);

        myauth= FirebaseAuth.getInstance();


        rooref = FirebaseDatabase.getInstance().getReference();
        mytoobar=  (Toolbar) findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mytoobar);

        String currentUserID = myauth.getCurrentUser().getUid();
        myviewpager = (ViewPager) findViewById(R.id.main_tabs_pager);
        mytabaccessadapter = new TabsAccessAdapter(getSupportFragmentManager());
        myviewpager.setAdapter(mytabaccessadapter);

        mytablayout =(TabLayout)findViewById(R.id.main_tabs);
        mytablayout.setupWithViewPager(myviewpager);

        rooref.child("Users").child(currentUserID).addValueEventListener(new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot){
                User user = dataSnapshot.getValue(User.class);
                String title = user.getUsername();
                getSupportActionBar().setTitle(title);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    protected void onStart() {
        super.onStart();
            UpdateUserStatus("Online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        FirebaseUser currentuser = myauth.getCurrentUser();
        if (currentuser != null) {
            UpdateUserStatus("Offline");
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.main_creategroup_option){

            requestNewGroup();

        }
        // to add a return statement to remove error
        return true;

    }

    private void requestNewGroup(){

        AlertDialog.Builder builder = new AlertDialog.Builder(ChatingActivity.this, R.style.AlertDialog);
        builder.setTitle("Enter gorup name");
        final EditText  groupNameField = new EditText(ChatingActivity.this);
        groupNameField.setHint("eg. Trip to XX");
        builder.setView(groupNameField);

        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String groupname = groupNameField.getText().toString();

                if(TextUtils.isEmpty(groupname)){
                    Toast.makeText(ChatingActivity.this, "Provide Group Name", Toast.LENGTH_SHORT).show();

                }
                else
                {
                    createNewGroup(groupname);
                }

            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();

            }
        });

        builder.show();

    }

    private void createNewGroup(String groupName){

        rooref.child("Group").child(groupName).setValue("")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(ChatingActivity.this, "Group Created", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }

    private void UpdateUserStatus(String state){
        String saveCurrentDate, saveCurrentTime;
        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        HashMap<String, Object> onlineStateMap = new HashMap<>();
        onlineStateMap.put("time",saveCurrentTime);
        onlineStateMap.put("date",saveCurrentDate);
        onlineStateMap.put("state",state);

        currentUserID = myauth.getCurrentUser().getUid();
        rooref.child("Users").child(currentUserID).child("userState")
                .updateChildren(onlineStateMap);
    }


}
