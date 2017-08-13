package com.fenderw.samsungremote.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

public class Authorize extends Fragment implements AdapterView.OnItemClickListener {

    private ListView lvDevices;
    private ArrayAdapter<Device> adapter;
    private List<Device> devices;
    private final Search search = Service.search(getActivity());
    private ProgressDialog progressDialog;

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
        View v = inflater.inflate(R.layout.fragment_authorize, container, false);
        lvDevices = (ListView) v.findViewById(R.id.device_list);
        devices = new ArrayList<>();
        // temp
        setDemoDevices();
        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, devices);
        lvDevices.setAdapter(adapter);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getString(R.string.authorize_searching_message));
        progressDialog.setOnCancelListener(dialog -> {
            Toast.makeText(getActivity(), "Canceled", Toast.LENGTH_SHORT).show();
            if (search != null && search.isSearching()) {
                search.stop();
            }
        });
        return v;
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
        if (search != null) {
            search.setOnServiceFoundListener(service -> {
                devices.add(new Device(service.getName(), service.getId()));
            });
            search.setOnServiceLostListener(service -> {
                removeFromListById(service.getId());
            });
            progressDialog.show();
            search.start();
        }
    }

    /**
     * Remove device from the list by its id
     *
     * @param id
     */
    private void removeFromListById(String id) {
        for (Device device : devices)
            if (device.getId().equals(id))
                devices.remove(device);
    }

    @Override
    public void onPause() {
        super.onPause();
        progressDialog.dismiss();
        lvDevices.setOnItemClickListener(null);
        if (search != null) {
            search.stop();
            search.setOnServiceFoundListener(null);
            search.setOnServiceLostListener(null);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(getActivity(), "Id : " + devices.get(position).getId(), Toast.LENGTH_SHORT).show();
    }
}
