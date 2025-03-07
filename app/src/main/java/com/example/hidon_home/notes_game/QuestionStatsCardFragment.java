package com.example.hidon_home.notes_game;

import android.os.Bundle;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.ProgressBar;
    import android.widget.TextView;
    import androidx.fragment.app.Fragment;

import com.example.hidon_home.R;

public class QuestionStatsCardFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the questionstats.xml layout
        View view = inflater.inflate(R.layout.question_stats_card, container, false);

        // Find the views
        TextView tvQuestion = view.findViewById(R.id.tvQuestion);
        ProgressBar progressBarA = view.findViewById(R.id.progressBarA);

        // Get data from arguments
        Bundle args = getArguments();
        if (args != null) {
            int questionNumber = args.getInt("questionNumber");
            String questionText = args.getString("questionText");
            int totalPlayers = args.getInt("totalPlayers");
            String answer1 = args.getString("Answer1");
            String answer2 = args.getString("Answer2");
            String answer3 = args.getString("Answer3");
            String answer4 = args.getString("Answer4");
            int correctAnswer = Integer.parseInt(args.getString("CorrectAnswer"));

            // Set the data to the views
            tvQuestion.setText(questionNumber + ": " + questionText);
        }

        return view;
    }
}
