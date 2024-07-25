package com.baiye959.mufts.network;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Baiye959
 */
public class MessageBroadcast {
    private List<Observer> observers = new ArrayList<>();

    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    public void notifyObservers(byte[] data) {
        for (Observer observer : observers) {
            observer.update(data);
        }
    }
}
