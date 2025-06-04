package com.example.hidon_home.notes_game.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.hidon_home.R;
import com.example.hidon_home.notes_game.Questioneer;
import com.example.hidon_home.notes_game.WaitingRoomActivity;

import java.util.List;


// This class is used to create a RecyclerView adapter for displaying a list of questionnaires.
public class QuestionnaireAdapter extends RecyclerView.Adapter<QuestionnaireAdapter.ViewHolder> {
    private List<Questioneer> questionnaireList;
    private Context context;

    public QuestionnaireAdapter(List<Questioneer> questionnaireList, Context context) {
        this.questionnaireList = questionnaireList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.questionnaire_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Questioneer questionnaire = questionnaireList.get(position);
        holder.questionnaireTitle.setText(questionnaire.getTitle());

        holder.cardView.setOnClickListener(v -> {
            WaitingRoomActivity.pickedQuestioner = questionnaire;
            context.startActivity(new Intent(context, WaitingRoomActivity.class));
        });
    }

    @Override
    public int getItemCount() {
        return questionnaireList.size();
    }

    // ViewHolder class to hold the views for each item in the RecyclerView
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView questionnaireTitle;
        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            questionnaireTitle = itemView.findViewById(R.id.questionnaireTitle);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }
}

