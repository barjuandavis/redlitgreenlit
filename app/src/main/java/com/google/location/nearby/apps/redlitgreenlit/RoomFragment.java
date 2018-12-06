package com.google.location.nearby.apps.redlitgreenlit;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
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

    private Button broadcastButton, changeButton;
    private Button[] playerButton;

    private MainActivity.Commands command;
    private boolean gameStarted, redlight;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.gameroom_fragment, container, false);

        playerButton = new Button[5];
        broadcastButton = v.findViewById(R.id.broadcast);
        gameStarted = false;
        redlight = true;
        playerListLayout = v.findViewById(R.id.player_list);
            playerButton[0] = v.findViewById(R.id.player1);
            playerButton[1] = v.findViewById(R.id.player2);
            playerButton[2] = v.findViewById(R.id.player3);
            playerButton[3] = v.findViewById(R.id.player4);
            playerButton[4] = v.findViewById(R.id.player5);
         changeButton = v.findViewById(R.id.button_change);
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
                if (mainActivity.isSearching()) {
                    broadcastButton.setText(R.string.searching);
                } else {
                    broadcastButton.setText(R.string.start_searching);
                }
            }
        });
            playerButton[0].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    playerMenu(0);
                }
            });
            playerButton[1].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    playerMenu(1);
                }
            });
            playerButton[2].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    playerMenu(2);
                }
            });
            playerButton[3].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    playerMenu(3);
                }
            });
            playerButton[4].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    playerMenu(4);
                }
            });
        changeButton.setText(R.string.start_game);
        changeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               gameSession();
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        broadcastButton.performClick();

    }
    public void setPlayerSlot(int id, String playerName) {
        playerButton[id].setText(playerName);
        playerButton[id].setEnabled(true);
        playerButton[id].setBackgroundColor(getResources().getColor(R.color.colorSecondaryLight, null));
    }
    public void clearPlayerSlot(int id) {
        playerButton[id].setText(R.string.empty_slot);
    }
    public void gameSession() {
        if (!isGameStarted()) {
            setGameStarted(true);
        } else {
            flipLight();
        }
    }
    private void flipLight() {
        if (redlight) {
            //flip to green
            changeButton.setBackgroundColor(getResources().getColor(R.color.green,null));
            changeButton.setText(R.string.green_light);
            mainActivity.sendCommand(MainActivity.Commands.GREEN_LIGHT);
            redlight = false;
        } else {
            changeButton.setBackgroundColor(getResources().getColor(R.color.red,null));
            changeButton.setText(R.string.red_light);
            mainActivity.sendCommand(MainActivity.Commands.RED_LIGHT);
            redlight = true;
        }


    }
    public boolean isGameStarted() {
        return gameStarted;
    }
    public void setGameStarted(boolean gameStarted) {
        this.gameStarted = gameStarted;
    }
    public void playerMenu(int i) {
        final int inner = i;
        AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
        b.setMessage("What do you want to do with " + playerButton[i].getText() + "?")
                .setCancelable(true)
                .setPositiveButton("Win", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mainActivity.sendSpecificCommand(MainActivity.Commands.PLAYER_WINS,inner);
                    }
                })
                .setNegativeButton("Kick", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mainActivity.sendSpecificCommand(MainActivity.Commands.KICK_PLAYER,inner);
                    }
                });
        AlertDialog a = b.create();
        a.show();
    }

}
