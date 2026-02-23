package com.monprojet.common;

public interface Subject {
    void registerObserver(Observer observer);   // Ajouter un observateur
    void removeObserver(Observer observer);     // Enlever un observateur
    void notifyObservers(Object object, String action); // Notifier tout le monde
}