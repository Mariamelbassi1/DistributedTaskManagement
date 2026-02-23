package com.monprojet.server;

import com.monprojet.common.Observer;
import com.monprojet.common.Subject;
import com.monprojet.models.Task;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class TaskSubject implements Subject {
    // CopyOnWriteArrayList est thread-safe pour les observers
    private List<Observer> observers = new CopyOnWriteArrayList<>();

    @Override
    public void registerObserver(Observer observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
            System.out.println("Nouvel observateur enregistré");
        }
    }

    @Override
    public void removeObserver(Observer observer) {
        observers.remove(observer);
        System.out.println("Observateur retiré");
    }

    @Override
    public void notifyObservers(Object object, String action) {
        Task task = (Task) object; // On sait que c'est une Task
        System.out.println("Notification à " + observers.size() + " observateurs");

        for (Observer observer : observers) {
            observer.update(task, action);
        }
    }
}
