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
import com.example.hidon_home.MainActivity;
import com.example.hidon_home.R;
import com.example.hidon_home.hidon.AmericanQuestionActivity;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HostQuestionStats extends Fragment {
    private static ProgressBar progressBarA, progressBarB, progressBarC, progressBarD;

    private static TextView tvPercentA, tvPercentB, tvPercentC, tvPercentD, tvResponseCount, tvAverageTime;
    FirebaseDatabase database;
    DatabaseReference kahootGameRef;
    public static int[] optionCounts = new int[] {0, 0, 0, 0, 0};
    private static int realTimeResponses = 0;

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

        resetStats();
        updateStats();
    }

    public void updateStats() {
        kahootGameRef.child("game").child("playersState").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Game.PlayerState playerState = snapshot.getValue(Game.PlayerState.class);

                double sumForAvg = 0;
                if (playerState.getAnswerChosen() != 4) {
                    realTimeResponses++;
                    sumForAvg += playerState.getTimeStamp() / 1000.0;
                }
                optionCounts[playerState.getAnswerChosen()]++;

                tvResponseCount.setText("Responses: " + realTimeResponses + "/" + (WaitingRoom.notesGame.getPlayerCount() - 1) + " players");

                if (realTimeResponses > 0) {
                    int percentA = optionCounts[0] * 100 / realTimeResponses;
                    int percentB = optionCounts[1] * 100 / realTimeResponses;
                    int percentC = optionCounts[2] * 100 / realTimeResponses;
                    int percentD = optionCounts[3] * 100 / realTimeResponses;

                    tvPercentA.setText(optionCounts[0] + " (" + percentA + "%)");
                    tvPercentB.setText(optionCounts[1] + " (" + percentB + "%)");
                    tvPercentC.setText(optionCounts[2] + " (" + percentC + "%)");
                    tvPercentD.setText(optionCounts[3] + " (" + percentD + "%)");

                    progressBarA.setProgress(percentA);
                    progressBarB.setProgress(percentB);
                    progressBarC.setProgress(percentC);
                    progressBarD.setProgress(percentD);

                    tvAverageTime.setText("Average time: " + (sumForAvg / realTimeResponses) + "s");
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {}
            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    public static void resetStats() {
        optionCounts = new int[] {0, 0, 0, 0, 0};
        realTimeResponses = 0;

        tvResponseCount.setText("Responses: " + realTimeResponses + "/" + (WaitingRoom.notesGame.getPlayerCount() - 1) + " players");
        tvAverageTime.setText("Average time: 0.000s");


        tvPercentA.setText("0 (0%)");
        tvPercentB.setText("0 (0%)");
        tvPercentC.setText("0 (0%)");
        tvPercentD.setText("0 (0%)");

        progressBarA.setProgress(0);
        progressBarB.setProgress(0);
        progressBarC.setProgress(0);
        progressBarD.setProgress(0);
    }
}