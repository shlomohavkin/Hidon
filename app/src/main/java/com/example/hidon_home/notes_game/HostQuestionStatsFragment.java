package com.example.hidon_home.notes_game;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import com.example.hidon_home.Game;
import com.example.hidon_home.R;
import com.example.hidon_home.notes_game.adapters.QuestionStatsPagerAdapter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HostQuestionStatsFragment extends Fragment {
    public static Questioneer questioneer;
    FirebaseDatabase database;
    DatabaseReference kahootGameRef;
    LinearLayout dotIndicatorContainer;
    ViewPager2 viewPager;
    View[] dots;
    View view;
    private ArrayList<QuestionStatsCardFragment> fragmentReferences = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        questioneer = WaitingRoomActivity.pickedQuestioner;
        database = FirebaseDatabase.getInstance();
        kahootGameRef = database.getReference("kahoot_games").child(String.valueOf(JoinScreenActivity.roomCode));

        // Inflate the fragment's layout
        view = inflater.inflate(R.layout.activity_host_question_stats, container, false);

        setUpViewpager();

        return view;
    }

    /**
     * This method sets up the ViewPager2 with a custom adapter and initializes the dot indicators.
     * It also sets up a custom page transformer for a fade effect during page transitions.
     */
    private void setUpViewpager() {
        viewPager = view.findViewById(R.id.viewPager);
        viewPager.setOrientation(ViewPager2.ORIENTATION_VERTICAL);

        dotIndicatorContainer = view.findViewById(R.id.dotIndicatorContainer);

        // Set up the ViewPager with a custom adapter
        QuestionStatsPagerAdapter adapter = new QuestionStatsPagerAdapter(getActivity(), questioneer.getQuestioneer()) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                QuestionStatsCardFragment fragment = (QuestionStatsCardFragment) super.createFragment(position);

                if (fragmentReferences.size() <= position) {
                    fragmentReferences.add(fragment);
                } else {
                    fragmentReferences.set(position, fragment);
                }

                return fragment;
            }
        };
        viewPager.setAdapter(adapter);

        viewPager.setOffscreenPageLimit(1); // Load adjacent pages
        viewPager.setClipToPadding(false); // Allow pages to extend beyond padding
        viewPager.setClipChildren(false); // Allow children to extend beyond bounds

        viewPager.setPageTransformer(new FadePageTransformer());

        viewPager.setPadding(0, dpToPx(110), 0, dpToPx(110));

        int pageCount = adapter.getItemCount();
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
    }

    /**
     * This class implements a custom page transformer for the ViewPager2.
     * It creates a fade and scale effect for the pages as they are swiped to create
     * a more dynamic and visually appealing transition.
     */
    private class FadePageTransformer implements ViewPager2.PageTransformer {
        private static final float MIN_SCALE = 0.80f;
        private static final float MIN_ALPHA = 0.4f;

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
                float verticalPosition = position * pageHeight * 0.02f;
                page.setTranslationY(verticalPosition);
            } else { // Page is way off-screen to the bottom
                page.setAlpha(0f);
                page.setScaleX(MIN_SCALE);
                page.setScaleY(MIN_SCALE);
            }
        }
    }

    /**
     * This method sets up the dot indicators based on the number of pages.
     * It creates a dot for each page and adds them to the dot indicator container.
     * @param count The number of pages in the ViewPager.
     */
    private void setupDotIndicators(int count) {
        dots = new View[count];
        int dotSize = dpToPx(13); // Size of each dot in pixels (adjustable)
        int dotMarginTopBottom = dpToPx(4); // Margin between dots (adjustable)
        // int dotMarginSides = dpToPx(0.30f);

        for (int i = 0; i < count; i++) {
            dots[i] = new View(view.getContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dotSize, dotSize);
            params.setMargins(0, dotMarginTopBottom, 0, dotMarginTopBottom);
            dots[i].setLayoutParams(params);
            dots[i].setBackgroundResource(android.R.drawable.btn_radio); // Use a dot-like drawable
            dots[i].setAlpha(0.5f); // Unselected dots are semi-transparent
            dotIndicatorContainer.addView(dots[i]);
        }
    }

    /**
     * This method updates the dot indicators based on the selected position.
     * It sets the selected dot to be seen and others to be semi-transparent.
     * @param selectedPosition The position of the currently selected dot.
     */
    private void updateDotIndicators(int selectedPosition) {
        for (int i = 0; i < dots.length; i++) {
            if (i == selectedPosition) {
                dots[i].setAlpha(1.0f); // Selected dot is fully opaque
            } else {
                dots[i].setAlpha(0.5f); // Unselected dots are semi-transparent
            }
        }
    }

    /**
     * This method converts dp to pixels based on the device's screen density.
     * @param dp The value in dp to be converted to pixels.
     * @return The equivalent value in pixels.
     */
    private int dpToPx(float dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        updateStats();
    }


    /**
     * This method sets up a listener to update the stats in real-time.
     * It listens for changes in the players' states and updates the relevant fragments.
     */
    public void updateStats() {
        kahootGameRef.child("game").child("playersState").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (HostGameActivity.isEnded) {
                    kahootGameRef.removeEventListener(this);
                }
                Game.PlayerState playerState = snapshot.getValue(Game.PlayerState.class);

                // Find the fragment for the current question
                int questionIndex = HostGameActivity.currentQuestion - 1;
                if (questionIndex >= 0 && questionIndex < fragmentReferences.size()) {
                    QuestionStatsCardFragment fragment = fragmentReferences.get(questionIndex);

                    // Update the fragment if it's been created
                    if (fragment != null && fragment.isAdded()) {
                        fragment.updateStats(playerState);
                    } else {
                        // Store the update for when the fragment becomes available
                        storeUpdateForLater(questionIndex, playerState);
                    }
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

    /**
     * This method stores updates for a specific question index in a map.
     * It allows for later retrieval and application of these updates when the corresponding fragment is available.
     * @param questionIndex The index of the question for which the update is stored.
     * @param playerState The player state to be stored.
     */
    private Map<Integer, ArrayList<Game.PlayerState>> pendingUpdates = new HashMap<>();

    private void storeUpdateForLater(int questionIndex, Game.PlayerState playerState) {
        if (!pendingUpdates.containsKey(questionIndex)) {
            pendingUpdates.put(questionIndex, new ArrayList<>());
        }
        pendingUpdates.get(questionIndex).add(playerState);
    }

    /**
     * This method applies any pending updates to the fragment at the given position.
     * It checks if there are any updates stored for that position and applies them if available.
     * @param position The position of the fragment to apply updates to.
     */
    public void applyPendingUpdates(int position) {
        if (pendingUpdates.containsKey(position)) {
            QuestionStatsCardFragment fragment = fragmentReferences.get(position);
            if (fragment != null && fragment.isAdded()) {
                for (Game.PlayerState state : pendingUpdates.get(position)) {
                    fragment.updateStats(state);
                }
                pendingUpdates.remove(position);
            }
        }
    }
}