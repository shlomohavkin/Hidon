package com.example.hidon_home.notes_game;

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

import java.util.List;

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
            WaitingRoom.pickedQuestioner = questionnaire;
            context.startActivity(new Intent(context, WaitingRoom.class));
        });
    }

    @Override
    public int getItemCount() {
        return questionnaireList.size();
    }

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

