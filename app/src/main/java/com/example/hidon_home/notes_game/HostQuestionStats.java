package com.example.hidon_home.notes_game;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.hidon_home.Game;
import com.example.hidon_home.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HostQuestionStats extends Fragment {
    private ProgressBar progressBarA, progressBarB, progressBarC, progressBarD;

    private TextView tvPercentA, tvPercentB, tvPercentC, tvPercentD, tvResponseCount, tvAverageTime;
    FirebaseDatabase database;
    DatabaseReference kahootGameRef;
    int[] optionCounts = new int[] {0, 0, 0, 0};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_host_question_stats, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvResponseCount = view.findViewById(R.id.tvResponseCount);
        tvAverageTime = view.findViewById(R.id.tvAverageTime);

        progressBarA = view.findViewById(R.id.progressBarA);
        progressBarB = view.findViewById(R.id.progressBarB);
        progressBarC = view.findViewById(R.id.progressBarC);
        progressBarD = view.findViewById(R.id.progressBarD);

        tvPercentA = view.findViewById(R.id.tvPercentA);
        tvPercentB = view.findViewById(R.id.tvPercentB);
        tvPercentC = view.findViewById(R.id.tvPercentC);
        tvPercentD = view.findViewById(R.id.tvPercentD);

        database = FirebaseDatabase.getInstance();
        kahootGameRef = database.getReference("kahoot_games").child(String.valueOf(JoinScreen.roomCode));
        updateStats();
    }

    public void updateStats() {

        kahootGameRef.child("game").child("playerState").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot playerStateSnap : snapshot.getChildren()) {
                    Game.PlayerState playerState = playerStateSnap.getValue(Game.PlayerState.class);
                    optionCounts[playerState.getAnswerChosen()]++;
                }

                tvPercentA.setText(String.valueOf(optionCounts[0]));
                tvPercentB.setText(String.valueOf(optionCounts[1]));
                tvPercentC.setText(String.valueOf(optionCounts[2]));
                tvPercentD.setText(String.valueOf(optionCounts[3]));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
//        tvResponseCount.setText("Responses: " +  + " players");
//        tvAverageTime.setText("Average response time: " + stats.getAverageResponseTime() + "s");
//
//
//        int totalResponses = stats.getResponseCount();
//
//        // Update progress bars and percentage texts
//        if (totalResponses > 0) {
//            int percentA = optionCounts[0] * 100 / totalResponses;
//            int percentB = optionCounts[1] * 100 / totalResponses;
//            int percentC = optionCounts[2] * 100 / totalResponses;
//            int percentD = optionCounts[3] * 100 / totalResponses;
//
//            progressBarA.setProgress(percentA);
//            progressBarB.setProgress(percentB);
//            progressBarC.setProgress(percentC);
//            progressBarD.setProgress(percentD);
       // }
    }
}