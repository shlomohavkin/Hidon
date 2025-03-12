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

import com.example.hidon_home.Game;
import com.example.hidon_home.R;

public class QuestionStatsCardFragment extends Fragment {
    LinearLayout llAnswer1, llAnswer2, llAnswer3, llAnswer4;
    private static int realTimeResponses = 0, sumForAvg = 0;
    public static int[] optionCounts = new int[] {0, 0, 0, 0, 0};

    ProgressBar progressBarA, progressBarB, progressBarC, progressBarD;

    TextView tvResponseCount, tvAverageTime, tvPercentA, tvPercentB, tvPercentC, tvPercentD;
    int prevQuestion = -1, currentQuestion;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.question_stats_card, container, false);

        TextView tvQuestion = view.findViewById(R.id.tvQuestion);
        TextView tvAnswer1 = view.findViewById(R.id.tvAnswer1);
        TextView tvAnswer2 = view.findViewById(R.id.tvAnswer2);
        TextView tvAnswer3 = view.findViewById(R.id.tvAnswer3);
        TextView tvAnswer4 = view.findViewById(R.id.tvAnswer4);
        llAnswer1 = view.findViewById(R.id.answer1Layout);
        llAnswer2 = view.findViewById(R.id.answer2Layout);
        llAnswer3 = view.findViewById(R.id.answer3Layout);
        llAnswer4 = view.findViewById(R.id.answer4Layout);
        tvResponseCount = view.findViewById(R.id.tvResponseCount);

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

        // Get data from arguments that are passed from the adapter
        Bundle args = getArguments();
        if (args != null) {
            currentQuestion = args.getInt("questionIndex") + 1;
            int questionNumber = args.getInt("questionNumber");
            String questionText = args.getString("questionText");
            int totalPlayers = args.getInt("totalPlayers");
            String answer1 = args.getString("Answer1");
            String answer2 = args.getString("Answer2");
            String answer3 = args.getString("Answer3");
            String answer4 = args.getString("Answer4");
            int correctAnswer = Integer.parseInt(args.getString("CorrectAnswer"));

            switch (correctAnswer) {
                case 0:
                    llAnswer1.setBackground(getResources().getDrawable(R.drawable.correct_answer_background));
                    llAnswer1.setPadding(8,8,8,8);
                    break;
                case 1:
                    llAnswer2.setBackground(getResources().getDrawable(R.drawable.correct_answer_background));
                    llAnswer2.setPadding(8,8,8,8);
                    break;
                case 2:
                    llAnswer3.setBackground(getResources().getDrawable(R.drawable.correct_answer_background));
                    llAnswer3.setPadding(8,8,8,8);
                    break;
                case 3:
                    llAnswer4.setBackground(getResources().getDrawable(R.drawable.correct_answer_background));
                    llAnswer4.setPadding(8,8,8,8);
                    break;
            }

            // Set the data to the views
            tvQuestion.setText(questionNumber + ": " + questionText);
            tvAnswer1.setText(answer1);
            tvAnswer2.setText(answer2);
            tvAnswer3.setText(answer3);
            tvAnswer4.setText(answer4);

            tvResponseCount.setText("Responses: 0/" + totalPlayers + " players");

        }
//        resetStats();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Notify the parent fragment that this fragment is ready
        Fragment parentFragment = getParentFragment();
        if (parentFragment instanceof HostQuestionStats) {
            int position = getArguments().getInt("questionIndex");
            ((HostQuestionStats) parentFragment).applyPendingUpdates(position);
        }
    }

    public void updateStats(Game.PlayerState playerState) {
        if (playerState.getAnswerChosen() != 4) {
            if (prevQuestion != HostGameActivity.currentQuestion) {
                resetStats();
                prevQuestion = HostGameActivity.currentQuestion;
            }
            realTimeResponses++;
            sumForAvg += playerState.getTimeStamp() / 1000.0;
        }
        optionCounts[playerState.getAnswerChosen()]++;

        int playerCount = getArguments().getInt("totalPlayers", 0);
        tvResponseCount.setText("Responses: " + realTimeResponses + "/" + playerCount + " players");

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

            if (realTimeResponses == playerCount && HostGameActivity.currentQuestion == WaitingRoom.pickedQuestioner.getQuestioneer().size()) {
                HostGameActivity.isEnded = true;
            }
        }
    }

    public void resetStats() {
        realTimeResponses = 0;
        sumForAvg = 0;
        optionCounts = new int[] {0, 0, 0, 0, 0};
    }


}
