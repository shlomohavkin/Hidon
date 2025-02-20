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

import com.example.hidon_home.R;

public class HostQuestionStats extends Fragment {

    private TextView tvQuestionCategory;
    private TextView tvResponseCount;
    private TextView tvAverageTime;

    private ProgressBar progressBarA;
    private ProgressBar progressBarB;
    private ProgressBar progressBarC;
    private ProgressBar progressBarD;

    private TextView tvPercentA;
    private TextView tvPercentB;
    private TextView tvPercentC;
    private TextView tvPercentD;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_host_question_stats, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        tvQuestionCategory = view.findViewById(R.id.tvQuestionCategory);
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
    }

//    public void updateStats(QuestionStats stats) {
//        if (tvQuestionCategory == null) return; // View not initialized yet
//
//        tvQuestionCategory.setText("Category: " + stats.getCategory());
//        tvResponseCount.setText("Responses: " + stats.getResponseCount() + " players");
//        tvAverageTime.setText("Average response time: " + stats.getAverageResponseTime() + "s");
//
//        int[] optionCounts = stats.getOptionCounts();
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
//
//            tvPercentA.setText(percentA + "% (" + optionCounts[0] + ")");
//            tvPercentB.setText(percentB + "% (" + optionCounts[1] + ")");
//            tvPercentC.setText(percentC + "% (" + optionCounts[2] + ")");
//            tvPercentD.setText(percentD + "% (" + optionCounts[3] + ")");
//        }
//    }
}