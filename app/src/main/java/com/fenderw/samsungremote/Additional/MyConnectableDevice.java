package com.fenderw.samsungremote.Additional;

import com.connectsdk.device.ConnectableDevice;

/**
 * Created by Fender on 8/17/2017.
 */

public class MyConnectableDevice extends ConnectableDevice {

    @Override
    public String toString() {
        return new String(getFriendlyName() + "\n" + getId());
    }
}
