package com.fenderw.samsungremote;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.fenderw.samsungremote.fragments.Authorize;

/**
 * Created by Fender on 8/12/2017.
 */

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Fragment authorizeFragment = new Authorize();
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, authorizeFragment, null).addToBackStack("Authorize").commit();
    }
}
