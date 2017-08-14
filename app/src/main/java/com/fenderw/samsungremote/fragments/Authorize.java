package com.fenderw.samsungremote.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.fenderw.samsungremote.R;
import com.fenderw.samsungremote.objects.Device;
import com.samsung.multiscreen.Search;
import com.samsung.multiscreen.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Fender on 8/12/2017.
 */

public class Authorize extends Fragment implements SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener {

    private final String TAG = getClass().getSimpleName();

    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView lvDevices;
    private ArrayAdapter<Device> adapter;
    private List<Device> devices;
    private Search search;
    private ProgressDialog progressDialog;
    // handler to handle the search timeout
    private Handler mHandler;

    /**
     * Temporary method for test purposes
     */
    private void setDemoDevices() {
        if (devices != null) {
            devices.add(new Device("device1", "id1"));
            devices.add(new Device("device2", "id2"));
            devices.add(new Device("device3", "id3"));
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        swipeRefreshLayout = (SwipeRefreshLayout) inflater.inflate(R.layout.fragment_authorize_refresh, container, false);
        lvDevices = (ListView) swipeRefreshLayout.findViewById(R.id.device_list);
        //
        mHandler = new Handler();
        devices = new ArrayList<>();
        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, devices);
        lvDevices.setAdapter(adapter);
        // progress dialog setup
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getString(R.string.authorize_searching_message));
        if (search == null)
            search = Service.search(getActivity());
        progressDialog.setOnCancelListener(dialog -> cleanUpSearch());
        swipeRefreshLayout.setOnRefreshListener(this);
        return swipeRefreshLayout;
    }

    @Override
    public void onStart() {
        super.onStart();
        lvDevices.setOnItemClickListener(this);
        startSearchForDevices();
    }

    /**
     * Main search for devices method
     */
    private void startSearchForDevices() {
        devices.clear();
        // temp
        //setDemoDevices();
        adapter.notifyDataSetChanged();
        if (search != null) {
            search.setOnServiceFoundListener(service -> {
                devices.add(new Device(service.getName(), service.getId()));
                adapter.notifyDataSetChanged();
            });
            search.setOnServiceLostListener(service -> {
                removeFromListById(service.getId());
            });
            progressDialog.show();
            search.start();
            // set the handler to stop the search in 10 seconds
            mHandler.postDelayed(() -> cleanUpSearch(), 10000);
        }
    }

    /**
     * Remove device from the list by its id
     *
     * @param id
     */
    private void removeFromListById(String id) {
        for (Device device : devices)
            if (device.getId().equals(id)) {
                devices.remove(device);
                adapter.notifyDataSetChanged();
            }
    }

    @Override
    public void onPause() {
        super.onPause();
        lvDevices.setOnItemClickListener(null);
        cleanUpSearch();
    }

    /**
     * Stop search and clean up the listeners and other stuff
     */
    private void cleanUpSearch() {
        mHandler.removeCallbacksAndMessages(null);
        if (search != null) {
            search.stop();
            search.setOnServiceFoundListener(null);
            search.setOnServiceLostListener(null);
        }
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        // check if we found anything
        if (!thereAreDevices()) {
            Toast.makeText(getActivity(), R.string.authorize_no_devices_found_message, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Simple check method
     *
     * @return true if there are devices in the list
     */
    private boolean thereAreDevices() {
        return devices.size() > 0;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        search = null;
        swipeRefreshLayout.setOnRefreshListener(null);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(getActivity(), "Id : " + devices.get(position).getId(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(false);
        startSearchForDevices();
    }
}
