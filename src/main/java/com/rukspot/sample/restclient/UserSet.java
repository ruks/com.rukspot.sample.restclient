package com.rukspot.sample.restclient;

public class UserSet {
    Publisher publisher;
    APPDev[] subscribers;

    public UserSet(Publisher apiDev, APPDev[] appDevs) {
        this.publisher = apiDev;
        this.subscribers = appDevs;
    }

    public Publisher getPublisher() {
        return publisher;
    }

    public APPDev[] getSubscribers() {
        return subscribers;
    }
}