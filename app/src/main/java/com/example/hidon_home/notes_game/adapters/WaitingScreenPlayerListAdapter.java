package com.example.hidon_home.notes_game.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.hidon_home.R;
import java.util.ArrayList;

// This class is used to create a custom adapter for displaying a list of players in the waiting screen.
public class WaitingScreenPlayerListAdapter extends ArrayAdapter<String> {
    private Context context;
    private ArrayList<String> players;

    public WaitingScreenPlayerListAdapter(Context context, ArrayList<String> players) {
        super(context, 0, players);
        this.context = context;
        this.players = players;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.waiting_room_player_list_item, parent, false);
        }

        String playerName = getItem(position);
        TextView nameTextView = convertView.findViewById(R.id.playerName);
        nameTextView.setText(playerName);

        // if you are the host, make your name purple
        if (playerName.equals("Host")) {
            nameTextView.setTextColor(context.getResources().getColor(R.color.purple_500));
            ImageView playerIcon = convertView.findViewById(R.id.playerIcon);
            playerIcon.setImageResource(R.drawable.ic_host);
        }

        return convertView;
    }
}