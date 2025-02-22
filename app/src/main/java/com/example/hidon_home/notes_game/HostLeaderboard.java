package com.example.hidon_home.notes_game;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hidon_home.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HostLeaderboard extends Fragment {

    private RecyclerView rvLeaderboard;
    private LeaderboardAdapter adapter;
    FirebaseDatabase database;
    List<Map.Entry<String, Integer>> leaderboard = new ArrayList<>();
    DatabaseReference kahootGamesRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_host_leaderboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        database = FirebaseDatabase.getInstance();
        kahootGamesRef = database.getReference("kahoot_games");

        rvLeaderboard = view.findViewById(R.id.rvLeaderboard);
        rvLeaderboard.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new LeaderboardAdapter(leaderboard);
        rvLeaderboard.setAdapter(adapter);

        // Get leaderboard from database
        kahootGamesRef.child(String.valueOf(JoinScreen.roomCode)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    ArrayList<Integer> scores = new ArrayList<>();
                    for (DataSnapshot snapshot : dataSnapshot.child("game").child("playersScore").getChildren()) {
                        scores.add(snapshot.getValue(Integer.class));
                    }
                    ArrayList<String> names = new ArrayList<>();
                    for (DataSnapshot snapshot : dataSnapshot.child("names").getChildren()) {
                        names.add(snapshot.getValue(String.class));
                    }

                    leaderboard.clear();
                    for (int i = 0; i < scores.size(); i++) {
                        leaderboard.add(new AbstractMap.SimpleEntry<>(names.get(i), scores.get(i)));
                    }
                    leaderboard.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));

                    updatePlayerList(new ArrayList<>(leaderboard));
                } else {
                    // Handle error
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void updatePlayerList(List<Map.Entry<String, Integer>> updatedLeaderboard) {
        leaderboard.clear();
        leaderboard.addAll(updatedLeaderboard);
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }
}