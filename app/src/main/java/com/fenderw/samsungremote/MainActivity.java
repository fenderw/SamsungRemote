package com.fenderw.samsungremote;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.fenderw.samsungremote.fragments.Authorize;

/**
 * Created by Fender on 8/12/2017.
 */

public class MainActivity extends AppCompatActivity {

    private final String TAG = getClass().getSimpleName();
    private final int REQUEST_STORAGE_PERMISSION = 10005;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermissions();
    }

    /**
     * Check and request WRITE_EXTERNAL_STORAGE permission
     */
    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_STORAGE_PERMISSION);
        } else
            startFragment();
    }

    // TODO Strings
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_STORAGE_PERMISSION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "Permission granted");
                    startFragment();
                } else {
                    Log.d(TAG, "Permission denied");
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    /**
     * Open the discovery (Authorize) fragment
     */
    private void startFragment() {
        Fragment authorizeFragment = new Authorize();
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, authorizeFragment, null).commit();
    }
}
