package com.fenderw.samsungremote.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.fenderw.samsungremote.R;

/**
 * Created by Fender on 8/14/2017.
 */

public class RemoteControl extends Fragment implements View.OnClickListener {

    private final String TAG = getClass().getSimpleName();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_remote, container, false);
        ImageButton btDisconnect = (ImageButton) v.findViewById(R.id.remote_bt_disconnect);
        //
        btDisconnect.setOnClickListener(this);
        //
        return v;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.remote_bt_disconnect:
                getFragmentManager().popBackStackImmediate();
                break;
        }
    }
}
