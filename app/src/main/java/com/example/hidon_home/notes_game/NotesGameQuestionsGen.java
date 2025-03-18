package com.example.hidon_home.notes_game;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.hidon_home.LoginActivity;
import com.example.hidon_home.MainActivity;
import com.example.hidon_home.Question;
import com.example.hidon_home.R;
import com.example.hidon_home.notes_game.Questioneer;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class NotesGameQuestionsGen extends AppCompatActivity {
    private com.google.android.material.textfield.TextInputEditText etQuestion, etAnswer1, etAnswer2, etAnswer3, etAnswer4;
    private com.google.android.material.checkbox.MaterialCheckBox checkBoxAnswer1, checkBoxAnswer2, checkBoxAnswer3, checkBoxAnswer4;
    private com.google.android.material.button.MaterialButton btnAddQuestion;
    private com.google.android.material.chip.ChipGroup questionsNavigation;
    private ArrayList<Question> questions;
    private ArrayList<Button> navigationButtons;
    private int currentQuestionIndex = -1;
    private TextWatcher textWatcher = new FieldTextWatcher();
    private CheckBoxChangedListener checkboxWatcher = new CheckBoxChangedListener();
    FirebaseDatabase database;
    DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_game_questions_gen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        database = FirebaseDatabase.getInstance();
        usersRef = database.getReference("users");

        questions = new ArrayList<>();
        navigationButtons = new ArrayList<>();

        // Initialize views
        etQuestion = findViewById(R.id.etQuestion);
        etAnswer1 = findViewById(R.id.etAnswer1);
        etAnswer2 = findViewById(R.id.etAnswer2);
        etAnswer3 = findViewById(R.id.etAnswer3);
        etAnswer4 = findViewById(R.id.etAnswer4);

        checkBoxAnswer1 = findViewById(R.id.cbAnswer1);
        checkBoxAnswer2 = findViewById(R.id.cbAnswer2);
        checkBoxAnswer3 = findViewById(R.id.cbAnswer3);
        checkBoxAnswer4 = findViewById(R.id.cbAnswer4);

        btnAddQuestion = findViewById(R.id.btnAddQuestion);
        questionsNavigation = findViewById(R.id.questionNavigationChips);

        etQuestion.addTextChangedListener(textWatcher);
        etAnswer1.addTextChangedListener(textWatcher);
        etAnswer2.addTextChangedListener(textWatcher);
        etAnswer3.addTextChangedListener(textWatcher);
        etAnswer4.addTextChangedListener(textWatcher);

        checkBoxAnswer1.setOnCheckedChangeListener(checkboxWatcher);
        checkBoxAnswer2.setOnCheckedChangeListener(checkboxWatcher);
        checkBoxAnswer3.setOnCheckedChangeListener(checkboxWatcher);
        checkBoxAnswer4.setOnCheckedChangeListener(checkboxWatcher);



        // Add a new question
        btnAddQuestion.setOnClickListener(v -> {
            addNewQuestion();
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
        Chip chip = new Chip(this);

        // Configure the chip appearance
        chip.setText(String.valueOf(index));
        chip.setChipBackgroundColor(ColorStateList.valueOf(Color.parseColor("#E74C3C"))); // Start as red (empty)
        chip.setTextColor(Color.WHITE);
        chip.setChipStrokeWidth(6);
        chip.setClickable(true);
        chip.setCheckable(true);
        questionsNavigation.clearCheck();
        chip.setChecked(true);

        int chipSize = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                50,
                getResources().getDisplayMetrics()
        );

        // Set margins to add spacing between chips
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(chipSize, chipSize);
        chip.setLayoutParams(params);

        // Set click listener
        chip.setOnClickListener(v -> {
            questionsNavigation.clearCheck();
            chip.setChecked(true);
            saveCurrentQuestion();
            loadQuestion(index - 1);
        });

        // Add to your ChipGroup or whatever parent you're using
        questionsNavigation.addView(chip);
        navigationButtons.add(chip);
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
        if (checkBoxAnswer1.isChecked()) correctAnswerIndex = 0;
        if (checkBoxAnswer2.isChecked()) correctAnswerIndex = 1;
        if (checkBoxAnswer3.isChecked()) correctAnswerIndex = 2;
        if (checkBoxAnswer4.isChecked()) correctAnswerIndex = 3;

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
        boolean isOneChecked = checkBoxAnswer1.isChecked() || checkBoxAnswer2.isChecked() || checkBoxAnswer3.isChecked() || checkBoxAnswer4.isChecked();

        if (!etQuestion.getText().toString().trim().isEmpty()
                && !etAnswer1.getText().toString().trim().isEmpty()
                && !etAnswer2.getText().toString().trim().isEmpty()
                && !etAnswer3.getText().toString().trim().isEmpty()
                && !etAnswer4.getText().toString().trim().isEmpty()
                && isOneChecked) {
            return 1; // return 1 if all fields are filled
        } else if (etQuestion.getText().toString().trim().isEmpty()
                && etAnswer1.getText().toString().trim().isEmpty()
                && etAnswer2.getText().toString().trim().isEmpty()
                && etAnswer3.getText().toString().trim().isEmpty()
                && etAnswer4.getText().toString().trim().isEmpty()
                && !isOneChecked) {
            return 0; // return 0 if all fields are empty
        } else {
            return -1; // return -1 if some fields are empty
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
            Chip chip = (Chip) questionsNavigation.getChildAt(currentQuestionIndex);
            // Check if all fields are filled
            if (areAllFieldsFilled() == 1) {
                // Change navigation button to green if all fields filled
                chip.setChipBackgroundColor(ColorStateList.valueOf(Color.parseColor("#339900"))); // change to green
                saveCurrentQuestion();
            } else if (areAllFieldsFilled() == 0) {
                // If all fields are empty, change navigation button to red
                chip.setChipBackgroundColor(ColorStateList.valueOf(Color.parseColor("#E74C3C"))); // all fields arent filled - red
            } else {
                // If not all filled, change navigation button to yellow
                chip.setChipBackgroundColor(ColorStateList.valueOf(Color.parseColor("#ffcc00"))); // some fields are empty - yellow
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            // No action needed
        }
    }

    private class CheckBoxChangedListener implements CompoundButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            // if a checkbox is checked then the other should be unchecked
            if (isChecked) {
                if (buttonView.getId() == R.id.cbAnswer1) {
                    checkBoxAnswer2.setChecked(false);
                    checkBoxAnswer3.setChecked(false);
                    checkBoxAnswer4.setChecked(false);
                } else if (buttonView.getId() == R.id.cbAnswer2) {
                    checkBoxAnswer1.setChecked(false);
                    checkBoxAnswer3.setChecked(false);
                    checkBoxAnswer4.setChecked(false);
                } else if (buttonView.getId() == R.id.cbAnswer3) {
                    checkBoxAnswer1.setChecked(false);
                    checkBoxAnswer2.setChecked(false);
                    checkBoxAnswer4.setChecked(false);
                } else if (buttonView.getId() == R.id.cbAnswer4) {
                    checkBoxAnswer1.setChecked(false);
                    checkBoxAnswer2.setChecked(false);
                    checkBoxAnswer3.setChecked(false);
                }
            }

            // Update the navigation button color
            Chip chip = (Chip) questionsNavigation.getChildAt(currentQuestionIndex);
            if (areAllFieldsFilled() == 1) {
                chip.setChipBackgroundColor(ColorStateList.valueOf(Color.parseColor("#339900"))); // change to green
                saveCurrentQuestion();
            } else if (areAllFieldsFilled() == 0) {
                chip.setChipBackgroundColor(ColorStateList.valueOf(Color.parseColor("#E74C3C"))); // all fields arent filled - red
            } else {
                chip.setChipBackgroundColor(ColorStateList.valueOf(Color.parseColor("#ffcc00"))); // some fields are empty - yellow
            }
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
            button.setBackgroundColor(Color.parseColor("#339900")); // all fields valid - green
        } else {
            button.setBackgroundColor(Color.parseColor("#ffcc00")); // Some fields are filled - yellow
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

        checkBoxAnswer1.setChecked(question.getCorrectAnswer() == 0);
        checkBoxAnswer2.setChecked(question.getCorrectAnswer() == 1);
        checkBoxAnswer3.setChecked(question.getCorrectAnswer() == 2);
        checkBoxAnswer4.setChecked(question.getCorrectAnswer() == 3);

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
                MainActivity.user.addQuestioneer(new Questioneer(questions, questionnaireName));
                LoginActivity.user.addQuestioneer(new Questioneer(questions, questionnaireName));
                usersRef.child(MainActivity.user.getName()).setValue(MainActivity.user);
                Toast.makeText(this, "Saved as: " + questionnaireName, Toast.LENGTH_SHORT).show();
                Log.d("Questioneer Saved", MainActivity.user.getQuestioners().get(0).toString());
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