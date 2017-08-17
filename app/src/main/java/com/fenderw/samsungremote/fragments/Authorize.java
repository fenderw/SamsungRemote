package com.fenderw.samsungremote.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.connectsdk.device.ConnectableDevice;
import com.connectsdk.device.ConnectableDeviceListener;
import com.connectsdk.discovery.DiscoveryManager;
import com.connectsdk.discovery.DiscoveryManagerListener;
import com.connectsdk.service.DeviceService;
import com.connectsdk.service.command.ServiceCommandError;
import com.fenderw.samsungremote.Additional.MyConnectableDevice;
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
    private final int SEARCH_TIME_OUT = 60 * 1000; // 1 minute

    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView lvDevices;
    private ArrayAdapter<MyConnectableDevice> adapter;
    private List<MyConnectableDevice> devices;
    //private Search search;
    private ProgressDialog progressDialog;
    // handler to handle the search timeout
    private Handler mHandler;
    //
    MyConnectableDevice mTV;

    /**
     * Temporary method for test purposes
     */
    /*private void setDemoDevices() {
        if (devices != null) {
            devices.add(new Device("device1", "id1"));
            devices.add(new Device("device2", "id2"));
            devices.add(new Device("device3", "id3"));
        }
    }*/

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
        //
        DiscoveryManager.getInstance().registerDefaultDeviceTypes();
        DiscoveryManager.getInstance().setPairingLevel(DiscoveryManager.PairingLevel.ON);
        //
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
        Toast.makeText(getActivity(), "search started", Toast.LENGTH_SHORT).show();
        DiscoveryManager.getInstance().addListener(discoveryManagerListener);
        progressDialog.show();
        DiscoveryManager.getInstance().start();

        // set the handler to stop the search in SEARCH_TIME_OUT
        mHandler.postDelayed(() -> cleanUpSearch(), SEARCH_TIME_OUT);

    }

    /**
     * Just a debug method
     */
    /*private void checkIfSearching() {
        if (search.isSearching()) {
            Toast.makeText(getActivity(), "is searching..", Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(getActivity(), "is not searching", Toast.LENGTH_SHORT).show();
    }*/

    /**
     * Remove device from the list by its id
     *
     * @param id
     */
    /*private void removeFromListById(String id) {
        for (Device device : devices)
            if (device.getId().equals(id)) {
                devices.remove(device);
                adapter.notifyDataSetChanged();
            }
    }*/

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
        Toast.makeText(getActivity(), "Listeners cleaned up!", Toast.LENGTH_SHORT).show();
        DiscoveryManager.getInstance().removeListener(discoveryManagerListener);
        DiscoveryManager.getInstance().stop();
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
        swipeRefreshLayout.setOnRefreshListener(null);
    }

    /**
     * Switch to the remote control fragment
     */
    private void nextFragment() {
        Fragment fragment = new RemoteControl();
        String fragmentTag = "remote";
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
        ft.replace(R.id.fragment_container, fragment, fragmentTag).addToBackStack(fragmentTag).commit();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(getActivity(), "Id : " + devices.get(position).getId(), Toast.LENGTH_SHORT).show();
        nextFragment();
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(false);
        startSearchForDevices();
    }

    private DiscoveryManagerListener discoveryManagerListener = new DiscoveryManagerListener() {
        @Override
        public void onDeviceAdded(DiscoveryManager manager, ConnectableDevice device) {
            Toast.makeText(getActivity(), "Found " + device.getFriendlyName(), Toast.LENGTH_SHORT).show();
            devices.add((MyConnectableDevice) device);
            adapter.notifyDataSetChanged();
        }

        @Override
        public void onDeviceUpdated(DiscoveryManager manager, ConnectableDevice device) {

        }

        @Override
        public void onDeviceRemoved(DiscoveryManager manager, ConnectableDevice device) {
            devices.remove(device);
            adapter.notifyDataSetChanged();
        }

        @Override
        public void onDiscoveryFailed(DiscoveryManager manager, ServiceCommandError error) {

        }
    };

    private ConnectableDeviceListener deviceListener = new ConnectableDeviceListener() {

        @Override
        public void onPairingRequired(ConnectableDevice device, DeviceService service, DeviceService.PairingType pairingType) {
            Log.d("2ndScreenAPP", "Connected to " + mTV.getIpAddress());

            switch (pairingType) {
                case FIRST_SCREEN:
                    Log.d("2ndScreenAPP", "First Screen");
                    //pairingAlertDialog.show();
                    break;

                case PIN_CODE:
                case MIXED:
                    Log.d("2ndScreenAPP", "Pin Code");
                    //pairingCodeDialog.show();
                    break;

                case NONE:
                default:
                    break;
            }
        }

        @Override
        public void onConnectionFailed(ConnectableDevice device, ServiceCommandError error) {
            Log.d("2ndScreenAPP", "onConnectFailed");
            //connectFailed(mTV);
        }

        @Override
        public void onDeviceReady(ConnectableDevice device) {
            Log.d("2ndScreenAPP", "onPairingSuccess");
            /*if (pairingAlertDialog.isShowing()) {
                pairingAlertDialog.dismiss();
            }
            if (pairingCodeDialog.isShowing()) {
                pairingCodeDialog.dismiss();
            }
            registerSuccess(mTV);*/
        }

        @Override
        public void onDeviceDisconnected(ConnectableDevice device) {
            Log.d("2ndScreenAPP", "Device Disconnected");
            /*connectEnded(mTV);
            connectItem.setTitle("Connect");

            BaseFragment frag = mSectionsPagerAdapter.getFragment(mViewPager.getCurrentItem());
            if (frag != null) {
                Toast.makeText(getApplicationContext(), "Device Disconnected", Toast.LENGTH_SHORT).show();
                frag.disableButtons();
            }*/
        }

        @Override
        public void onCapabilityUpdated(ConnectableDevice device, List<String> added, List<String> removed) {

        }
    };
}
