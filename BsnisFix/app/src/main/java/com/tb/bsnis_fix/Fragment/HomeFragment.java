package com.tb.bsnis_fix.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tb.bsnis_fix.Adapter.PostAdapter;
import com.tb.bsnis_fix.Adapter.StoryAdapter;
import com.tb.bsnis_fix.AddStoryActivity;
import com.tb.bsnis_fix.ChatActivity;
import com.tb.bsnis_fix.ChatingActivity;
import com.tb.bsnis_fix.Model.Post;
import com.tb.bsnis_fix.Model.Story;
import com.tb.bsnis_fix.R;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private List<Post> postLists;

    private RecyclerView recyclerView_story;
    private StoryAdapter storyAdapter;
    private List<Story> storyList;

    private List<String> followingList;

    private ImageView addstory, chatroom;

    int countStory;
    Story story;

    ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        addstory = view.findViewById(R.id.addstory);
        chatroom = view.findViewById(R.id.chat_room);


        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        postLists = new ArrayList<>();
        postAdapter = new PostAdapter(getContext(), postLists);
        recyclerView.setAdapter(postAdapter);

        recyclerView_story = view.findViewById(R.id.recycler_view_story);
        recyclerView_story.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView_story.setLayoutManager(linearLayoutManager1);
        storyList = new ArrayList<>();
        storyAdapter = new StoryAdapter(getContext(), storyList);
        recyclerView_story.setAdapter(storyAdapter);

        progressBar = view.findViewById(R.id.progress_circular);

        checkFollowing();

        addstory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent  = new Intent(getActivity().getApplicationContext(), AddStoryActivity.class);
                startActivity(intent);
            }
        });

        chatroom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent  = new Intent(getActivity().getApplicationContext(), ChatingActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

    private void checkFollowing(){
        followingList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Follow").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("following");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                followingList.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    followingList.add(snapshot.getKey());
                }
                readPosts();
                readStory();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readPosts(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postLists.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Post post = snapshot.getValue(Post.class);
                    for(String id : followingList){
                        if(post.getPublisher().equals(id)){
                            postLists.add(post);
                        }
                    }
                }

                postAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readStory(){
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Story");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long timecurrent = System.currentTimeMillis();
                storyList.clear();
                storyList.add(new Story("",0,0,"", FirebaseAuth.getInstance().getCurrentUser().getUid()));
                for(String id : followingList){
                    countStory = 0;
                    story = null;
                    for(DataSnapshot snapshot : dataSnapshot.child(id).getChildren()){
                        story = snapshot.getValue(Story.class);
                        if(timecurrent > story.getTimestart() && timecurrent < story.getTimeend()){
                            countStory++;
                            if (timecurrent == story.getTimeend()){
                                reference.removeValue();
                            }
                        }
                    }

                    if(countStory > 0){
                        storyList.add(story);
                    }
                }
                storyAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
