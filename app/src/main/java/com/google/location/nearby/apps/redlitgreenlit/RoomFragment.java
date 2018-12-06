package com.google.location.nearby.apps.redlitgreenlit;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class RoomFragment extends Fragment {
    //primitives

    private MainActivity mainActivity;
    private GridLayout playerListLayout;

    private Button broadcastButton;
    private Button[] playerButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.gameroom_fragment, container, false);

        playerButton = new Button[5];
        broadcastButton = v.findViewById(R.id.broadcast);

        playerListLayout = v.findViewById(R.id.player_list);
            playerButton[0] = v.findViewById(R.id.player1);
            playerButton[1] = v.findViewById(R.id.player2);
            playerButton[2] = v.findViewById(R.id.player3);
            playerButton[3] = v.findViewById(R.id.player4);
            playerButton[4] = v.findViewById(R.id.player5);

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        mainActivity = (MainActivity) getActivity();
        broadcastButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.broadcastRoom();
                if (!mainActivity.isSearching()) {
                    broadcastButton.setText(R.string.searching);
                } else {
                    broadcastButton.setText(R.string.start_searching);
                }
            }
        });
        for (int i = 0; i<5; i++) {
            playerButton[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        broadcastButton.performClick();

    }
    public void setPlayerSlot(int id, String playerName) {
        String s = String.format(playerName, R.string.filled_slot);
        playerButton[id].setText(s);
    }
    public void clearPlayerSlot(int id) {
        playerButton[id].setText(R.string.empty_slot);
    }
}
