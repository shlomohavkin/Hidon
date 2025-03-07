package com.example.hidon_home.notes_game;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.hidon_home.Game;
import com.example.hidon_home.MainActivity;
import com.example.hidon_home.Question;
import com.example.hidon_home.R;
import com.example.hidon_home.hidon.AmericanQuestionActivity;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HostQuestionStats extends Fragment {
    private static ProgressBar progressBarA, progressBarB, progressBarC, progressBarD;
    public static Questioneer questioneer;

    private static TextView tvPercentA, tvPercentB, tvPercentC, tvPercentD, tvResponseCount, tvAverageTime;
    FirebaseDatabase database;
    DatabaseReference kahootGameRef;
    public static int[] optionCounts = new int[] {0, 0, 0, 0, 0};
    private static int realTimeResponses = 0;
    LinearLayout dotIndicatorContainer;
    ViewPager2 viewPager;
    View[] dots;
    View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        questioneer = WaitingRoom.pickedQuestioner;

        // Inflate the fragment's layout
        view = inflater.inflate(R.layout.activity_host_question_stats, container, false);

        // Find the UI components
        viewPager = view.findViewById(R.id.viewPager);
        viewPager.setOrientation(ViewPager2.ORIENTATION_VERTICAL);

        dotIndicatorContainer = view.findViewById(R.id.dotIndicatorContainer);

        // Set up the adapter for ViewPager2
        QuestionStatsPagerAdapter adapter = new QuestionStatsPagerAdapter(getActivity(), questioneer.getQuestioneer());
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(1); // Load adjacent pages
        viewPager.setClipToPadding(false); // Allow pages to extend beyond padding
        viewPager.setClipChildren(false); // Allow children to extend beyond bounds

        viewPager.setPageTransformer(new FadePageTransformer());

        viewPager.setPadding(0, dpToPx(135), 0, dpToPx(135));

        int pageCount = adapter.getItemCount(); // e.g., 3 pages
        setupDotIndicators(pageCount);
        updateDotIndicators(0);

        // Listen for page changes to update dots
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                updateDotIndicators(position);
            }
        });


        return view;
    }

    private class FadePageTransformer implements ViewPager2.PageTransformer {
        private static final float MIN_SCALE = 0.90f;
        private static final float MIN_ALPHA = 0.3f;

        @Override
        public void transformPage(@NonNull View page, float position) {
            int pageHeight = page.getHeight();

            if (position < -1) { // Page is way off-screen to the top
                page.setAlpha(0f);
                page.setScaleX(MIN_SCALE);
                page.setScaleY(MIN_SCALE);
            } else if (position <= 1) { // Page is visible or nearby
                // Calculate fade effect (alpha) based on position
                float alphaFactor = Math.max(MIN_ALPHA, 1 - Math.abs(position));
                page.setAlpha(alphaFactor);

                // Add a subtle scale effect
                float scaleFactor = MIN_SCALE + (1 - MIN_SCALE) * (1 - Math.abs(position));
                page.setScaleX(scaleFactor);
                page.setScaleY(scaleFactor);

                // Add slight vertical translation based on position
                float verticalPosition = position * pageHeight * 0.05f;
                page.setTranslationY(verticalPosition);
            } else { // Page is way off-screen to the bottom
                page.setAlpha(0f);
                page.setScaleX(MIN_SCALE);
                page.setScaleY(MIN_SCALE);
            }
        }
    }

    private void setupDotIndicators(int count) {
        dots = new View[count];
        int dotSize = dpToPx(20); // Size of each dot in pixels (adjustable)
        int dotMargin = dpToPx(8); // Margin between dots (adjustable)

        for (int i = 0; i < count; i++) {
            dots[i] = new View(view.getContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dotSize, dotSize);
            dots[i].setLayoutParams(params);
            dots[i].setBackgroundResource(android.R.drawable.btn_radio); // Use a dot-like drawable
            dots[i].setAlpha(0.4f); // Unselected dots are semi-transparent
            dotIndicatorContainer.addView(dots[i]);
        }
    }

    private void updateDotIndicators(int selectedPosition) {
        for (int i = 0; i < dots.length; i++) {
            if (i == selectedPosition) {
                dots[i].setAlpha(1.0f); // Selected dot is fully opaque
            } else {
                dots[i].setAlpha(0.4f); // Unselected dots are semi-transparent
            }
        }
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        tvResponseCount = view.findViewById(R.id.tvResponseCount);
//        tvAverageTime = view.findViewById(R.id.tvAverageTime);
//
//        progressBarA = view.findViewById(R.id.progressBarA);
//        progressBarB = view.findViewById(R.id.progressBarB);
//        progressBarC = view.findViewById(R.id.progressBarC);
//        progressBarD = view.findViewById(R.id.progressBarD);
//
//        tvPercentA = view.findViewById(R.id.tvPercentA);
//        tvPercentB = view.findViewById(R.id.tvPercentB);
//        tvPercentC = view.findViewById(R.id.tvPercentC);
//        tvPercentD = view.findViewById(R.id.tvPercentD);
//
//
//        database = FirebaseDatabase.getInstance();
//        kahootGameRef = database.getReference("kahoot_games").child(String.valueOf(JoinScreen.roomCode));
//
//        resetStats();
//        updateStats();
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
        tvAverageTime.setText("Average time: 0.00s");


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