package com.example.hidon_home;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class NotesGameQuestionsGen extends AppCompatActivity {
    private EditText etQuestion, etAnswer1, etAnswer2, etAnswer3, etAnswer4;
    private CheckBox cbAnswer1, cbAnswer2, cbAnswer3, cbAnswer4;
    private Button btnAddQuestion;
    private LinearLayout questionNavigationLayout;
    private ArrayList<Question> questions = new ArrayList<>();
    private ArrayList<Button> navigationButtons = new ArrayList<>();
    private int currentQuestionIndex = -1;
    public ArrayList<Questioneer> questioners = new ArrayList<>();
    private TextWatcher textWatcher = new FieldTextWatcher();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_game_questions_gen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize views
        etQuestion = findViewById(R.id.etQuestion);
        etAnswer1 = findViewById(R.id.etAnswer1);
        etAnswer2 = findViewById(R.id.etAnswer2);
        etAnswer3 = findViewById(R.id.etAnswer3);
        etAnswer4 = findViewById(R.id.etAnswer4);

        cbAnswer1 = findViewById(R.id.cbAnswer1);
        cbAnswer2 = findViewById(R.id.cbAnswer2);
        cbAnswer3 = findViewById(R.id.cbAnswer3);
        cbAnswer4 = findViewById(R.id.cbAnswer4);

        btnAddQuestion = findViewById(R.id.btnAddQuestion);
        questionNavigationLayout = findViewById(R.id.questionNavigationLayout);

        etQuestion.addTextChangedListener(textWatcher);
        etAnswer1.addTextChangedListener(textWatcher);
        etAnswer2.addTextChangedListener(textWatcher);
        etAnswer3.addTextChangedListener(textWatcher);
        etAnswer4.addTextChangedListener(textWatcher);

        // Add a new question
        btnAddQuestion.setOnClickListener(v -> {
            addNewQuestion();
            Toast.makeText(this, "New question added!", Toast.LENGTH_SHORT).show();
        });

        // Automatically add the first question on activity start
        addNewQuestion();
    }

    /**
     * Add a new question to the list and create a navigation button.
     */
    private void addNewQuestion() {
        ArrayList<String> answers = new ArrayList<>();
        answers.add("");
        answers.add("");
        answers.add("");
        answers.add("");
        Question question = new Question("", answers, -1);
        questions.add(question);

        int index = questions.size();
        Button navButton = new Button(this);
        navButton.setText(String.valueOf(index));
        navButton.setBackgroundColor(Color.parseColor("#E74C3C")); // Start as red (empty)

        // Set margins to add spacing between buttons
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics()),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics())
        );
        params.setMargins(8, 0, 8, 0); // Left, Top, Right, Bottom margin (8dp horizontal spacing)
        navButton.setLayoutParams(params);

        navButton.setOnClickListener(v -> {
            saveCurrentQuestion();
            loadQuestion(index - 1);
        });

        questionNavigationLayout.addView(navButton);
        navigationButtons.add(navButton);
        loadQuestion(index - 1);

        if (currentQuestionIndex == -1) {
            loadQuestion(0); // Automatically load the first question
        }
    }

    /**
     * Save the current question to the list.
     */
    private void saveCurrentQuestion() {
        if (currentQuestionIndex == -1) return;

        String questionText = etQuestion.getText().toString().trim();
        String answer1 = etAnswer1.getText().toString().trim();
        String answer2 = etAnswer2.getText().toString().trim();
        String answer3 = etAnswer3.getText().toString().trim();
        String answer4 = etAnswer4.getText().toString().trim();

        int correctAnswerIndex = -1;
        if (cbAnswer1.isChecked()) correctAnswerIndex = 0;
        if (cbAnswer2.isChecked()) correctAnswerIndex = 1;
        if (cbAnswer3.isChecked()) correctAnswerIndex = 2;
        if (cbAnswer4.isChecked()) correctAnswerIndex = 3;

        // Save data only to the current question object
        Question question = questions.get(currentQuestionIndex);
        question.setQuestionContent(questionText);
        ArrayList<String> answers = new ArrayList<>();
        answers.add(answer1);
        answers.add(answer2);
        answers.add(answer3);
        answers.add(answer4);
        question.setAnswers(answers);

        question.setCorrectAnswer(correctAnswerIndex);

        // Update button color
        updateNavigationButtonColor(currentQuestionIndex);
    }
    private int areAllFieldsFilled() {
        if (!(etQuestion.getText().toString().trim().isEmpty()
                && !etAnswer1.getText().toString().trim().isEmpty()
                && !etAnswer2.getText().toString().trim().isEmpty()
                && !etAnswer3.getText().toString().trim().isEmpty()
                && !etAnswer4.getText().toString().trim().isEmpty())) {
            return 1;
        } else if ((etQuestion.getText().toString().trim().isEmpty()
                && !etAnswer1.getText().toString().trim().isEmpty()
                && !etAnswer2.getText().toString().trim().isEmpty()
                && !etAnswer3.getText().toString().trim().isEmpty()
                && !etAnswer4.getText().toString().trim().isEmpty())) {
            return 0;
        } else {
            return -1;
        }
    }

    // TextWatcher for EditText fields
    private class FieldTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // No action needed
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            Button button = navigationButtons.get(currentQuestionIndex);
            // Check if all fields are filled
            if (areAllFieldsFilled() == 1) {
                // Change navigation button to green
                button.setBackgroundColor(Color.parseColor("#2ECC71")); // all fields valid - green
                saveCurrentQuestion();
            } else if (areAllFieldsFilled() == 0) {
                button.setBackgroundColor(Color.parseColor("#F1C40F")); // Some fields are filled - yellow
            } else {
                // If not filled, change navigation button to red
                button.setBackgroundColor(Color.parseColor("#E74C3C")); // All fields are empty - red
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            // No action needed
        }
    }

    /**
     * Update the color of the navigation button based on the question's completion status.
     *
     * @param index The index of the question to evaluate.
     */
    private void updateNavigationButtonColor(int index) {
        if (index < 0 || index >= questions.size()) return;

        Question question = questions.get(index);
        Button button = navigationButtons.get(index);

        boolean allFieldsEmpty = question.getQuestionContent().isEmpty() &&
                question.getAnswers().get(0).isEmpty() &&
                question.getAnswers().get(1).isEmpty() &&
                question.getAnswers().get(2).isEmpty() &&
                question.getAnswers().get(3).isEmpty();

        boolean allFieldsFilled = !question.getQuestionContent().isEmpty() &&
                !question.getAnswers().get(0).isEmpty() &&
                !question.getAnswers().get(1).isEmpty() &&
                !question.getAnswers().get(2).isEmpty() &&
                !question.getAnswers().get(3).isEmpty() &&
                question.getCorrectAnswer() != -1;

        if (allFieldsEmpty) {
            button.setBackgroundColor(Color.parseColor("#E74C3C")); // All fields are empty - red
        } else if (allFieldsFilled) {
            button.setBackgroundColor(Color.parseColor("#2ECC71")); // all fields valid - green
        } else {
            button.setBackgroundColor(Color.parseColor("#F1C40F")); // Some fields are filled - yellow
        }
    }

    /**
     * Load a question into the fields for editing.
     *
     * @param index The index of the question to load.
     */
    private void loadQuestion(int index) {
        if (index < 0 || index >= questions.size()) return;

        currentQuestionIndex = index;
        Question question = questions.get(index);

        // Temporarily disable TextWatchers to prevent unnecessary interference
        etQuestion.removeTextChangedListener(textWatcher);
        etAnswer1.removeTextChangedListener(textWatcher);
        etAnswer2.removeTextChangedListener(textWatcher);
        etAnswer3.removeTextChangedListener(textWatcher);
        etAnswer4.removeTextChangedListener(textWatcher);

        // Reset fields
        etQuestion.setText(question.getQuestionContent());
        etAnswer1.setText(question.getAnswers().get(0));
        etAnswer2.setText(question.getAnswers().get(1));
        etAnswer3.setText(question.getAnswers().get(2));
        etAnswer4.setText(question.getAnswers().get(3));

        cbAnswer1.setChecked(question.getCorrectAnswer() == 0);
        cbAnswer2.setChecked(question.getCorrectAnswer() == 1);
        cbAnswer3.setChecked(question.getCorrectAnswer() == 2);
        cbAnswer4.setChecked(question.getCorrectAnswer() == 3);

        // Re-enable TextWatchers after resetting fields
        etQuestion.addTextChangedListener(textWatcher);
        etAnswer1.addTextChangedListener(textWatcher);
        etAnswer2.addTextChangedListener(textWatcher);
        etAnswer3.addTextChangedListener(textWatcher);
        etAnswer4.addTextChangedListener(textWatcher);
    }

    public void onSaveQuestionsClick(View view) {
        if (!isValidQuestioneer()) {
            Toast.makeText(this, "Please fill all the questions.", Toast.LENGTH_SHORT).show();
            return;
        }
        // Create an EditText for user input
        EditText input = new EditText(this);
        input.setHint("Enter questionnaire name");

        // Create a dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Save Questionnaire");
        builder.setMessage("Enter the name for your questionnaire:");
        builder.setView(input);

        // Add "Save" button to dialog
        builder.setPositiveButton("Save", (dialog, which) -> {
            String questionnaireName = input.getText().toString().trim();

            if (questionnaireName.isEmpty()) {
                Toast.makeText(this, "Please enter a valid name", Toast.LENGTH_SHORT).show();
            } else {
                // Handle saving the questionnaire name
                questioners.add(new Questioneer(questions, questionnaireName));
                Toast.makeText(this, "Saved as: " + questionnaireName, Toast.LENGTH_SHORT).show();
                Log.d("Questioneer Saved", questioners.get(0).toString());
                dialog.dismiss();
                startActivity(new Intent(this, MainActivity.class));
            }
        });

        // Add "Cancel" button to dialog
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        // Show the dialog
        builder.create().show();

    }

    public boolean isValidQuestioneer() {
        Question question;
        for (int i = 0; i < questions.size(); i++) {
            question = questions.get(i);

            boolean allFieldsFilled = !question.getQuestionContent().isEmpty() &&
                    !question.getAnswers().get(0).isEmpty() &&
                    !question.getAnswers().get(1).isEmpty() &&
                    !question.getAnswers().get(2).isEmpty() &&
                    !question.getAnswers().get(3).isEmpty() &&
                    question.getCorrectAnswer() != -1;

            if (!allFieldsFilled)
                return false;
        }
        return true;
    }
}