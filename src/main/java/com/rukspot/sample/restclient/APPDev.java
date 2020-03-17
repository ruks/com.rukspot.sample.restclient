package com.rukspot.sample.restclient;

public class APPDev {
    Subscriber username;
    Users[] endusers;

    public APPDev(Subscriber appDev, Users[] endusers) {
        this.username = appDev;
        this.endusers = endusers;
    }

    public Subscriber getAppDev() {
        return username;
    }

    public Users[] getEndusers() {
        return endusers;
    }
}