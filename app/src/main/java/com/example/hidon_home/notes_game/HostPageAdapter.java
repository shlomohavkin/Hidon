package com.example.hidon_home.notes_game;

import androidx.fragment.app.Fragment;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class HostPageAdapter extends FragmentStateAdapter {

    public HostPageAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Return a new fragment instance for each position
        switch (position) {
            case 0:
                return new HostLeaderboard();
            case 1:
                return new HostQuestionStats();
            default:
                return new HostLeaderboard();
        }
    }

    @Override
    public int getItemCount() {
        return 2; // Number of tabs
    }
}
