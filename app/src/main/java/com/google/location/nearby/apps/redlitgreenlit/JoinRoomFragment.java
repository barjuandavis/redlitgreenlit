package com.google.location.nearby.apps.redlitgreenlit;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class JoinRoomFragment extends Fragment {

    private Button findRoom;
    private MainActivity mainActivity;
    private TextView roomName;
    private RelativeLayout backgrounder;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.join_room, container, false);
        findRoom = v.findViewById(R.id.find_room);
        roomName = v.findViewById(R.id.roomName);
        backgrounder = v.findViewById(R.id.backgrounder);
        mainActivity = (MainActivity)getActivity();
        findRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.findRoom();
                if (mainActivity.isFinding()) {
                    findRoom.setText(R.string.finding);
                } else {
                    findRoom.setText(R.string.start_finding);
                }
            }
        });
        return v;
    }

    public void setRoomName(String roomName) {
        String s = String.format(roomName, R.string.joined_to_x_room);
        this.roomName.setText("You're in: " + s);
        this.roomName.setBackgroundColor(getResources().getColor(R.color.colorPrimary, null));
        this.backgrounder.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark, null));
    }

    public void commandResponse(MainActivity.Commands c) {
        switch (c) {
            case RED_LIGHT:
                this.roomName.setText(R.string.red_light);
                this.roomName.setBackgroundColor(getResources().getColor(R.color.red, null));
                this.backgrounder.setBackgroundColor(getResources().getColor(R.color.red, null));
                break;
            case GREEN_LIGHT:
                this.roomName.setText(R.string.green_light);
                this.roomName.setBackgroundColor(getResources().getColor(R.color.green, null));
                this.backgrounder.setBackgroundColor(getResources().getColor(R.color.green, null));
                break;
            case KICK_PLAYER:
                this.roomName.setText(R.string.you_kicked);
                this.roomName.setBackgroundColor(getResources().getColor(R.color.red, null));
                this.backgrounder.setBackgroundColor(getResources().getColor(R.color.red, null));
                break;
            case PLAYER_WINS:
                this.roomName.setText(R.string.you_win);
                this.roomName.setBackgroundColor(getResources().getColor(R.color.green, null));
                this.backgrounder.setBackgroundColor(getResources().getColor(R.color.green, null));
                break;
        }
    }


}