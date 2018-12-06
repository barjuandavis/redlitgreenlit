package com.google.location.nearby.apps.redlitgreenlit;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class RoomFragment extends Fragment {
    //primitives

    MainActivity mainActivity;
    private LinearLayout playerListLayout;

    private Button broadcastButton;
    private Button[] playerButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.gameroom_fragment, container, false);
        mainActivity = (MainActivity) getActivity();
        playerButton = new Button[5];
        broadcastButton.findViewById(R.id.broadcast);
        playerListLayout.findViewById(R.id.player_list);
        playerButton[0].findViewById(R.id.player1);
        playerButton[1].findViewById(R.id.player2);
        playerButton[2].findViewById(R.id.player3);
        playerButton[3].findViewById(R.id.player4);
        playerButton[4].findViewById(R.id.player5);
        return v;
    }
}
