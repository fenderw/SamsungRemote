package com.fenderw.samsungremote.objects;

/**
 * Created by Fender on 8/13/2017.
 */

public class Device {

    private String name;
    private String id;

    public Device(String name, String id) {
        this.name = name;
        this.id = id;
    }

    @Override
    public String toString() {
        return new String(name + "\n" + id);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
